/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.client.application.BSteeleMusicIO;
import com.bsteele.bsteeleMusicApp.client.application.GWTAppOptions;
import com.bsteele.bsteeleMusicApp.client.application.events.DefaultDrumSelectEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.DefaultDrumSelectEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.MusicAnimationEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.StatusEvent;
import com.bsteele.bsteeleMusicApp.client.jsTypes.AudioFilePlayer;
import com.bsteele.bsteeleMusicApp.client.legacy.LegacyDrumMeasure;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.client.songs.SongUpdate;
import com.bsteele.bsteeleMusicApp.shared.GridCoordinate;
import com.bsteele.bsteeleMusicApp.shared.songs.AppOptions;
import com.bsteele.bsteeleMusicApp.shared.songs.SongBase;
import com.bsteele.bsteeleMusicApp.shared.songs.SongMoment;
import com.bsteele.bsteeleMusicApp.shared.songs.SongPlayer;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.json.client.JSONException;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HandlerContainerImpl;
import jsinterop.annotations.JsType;

import java.text.ParseException;
import java.util.logging.Logger;

/**
 * @author bob
 */
@JsType
public class SongPlayMasterImpl
        extends HandlerContainerImpl
        implements SongPlayMaster,
        DefaultDrumSelectEventHandler,
        AnimationScheduler.AnimationCallback {

    @Inject
    public SongPlayMasterImpl(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * A rough timer handler designed to pump commands to the audio
     * about once a measure but not intended to be synchronous.
     * The intent is to run on measure ahead to assure time to
     * load the audio properly.
     */
    @Override
    public void execute(double systemT) {
        //  do this first to get the best time alignment
        double t = audioFilePlayer.getCurrentTime();

        systemT /= 1000;      //  use units of seconds internally

        {   //  find offset from system to audio time, integrate over time
            double dt = systemT - t;
            systemToAudioOffset = (Math.abs(systemToAudioOffset - dt) > 0.1)
                    ? dt : systemToAudioOffset * pass + (1 - pass) * dt;
        }
        double tn = t + systemToAudioOffset;
        if (Math.abs(tn - systemT) > 0.005)
            logger.finer("dt: " + (tn - systemT));

        //  deal with state transitions
        switch (requestedState) {
            case idle:
                switch (state) {
                    case playing:
                        //  stop
                        audioFilePlayer.stop();
                        songOutUpdate.setState(SongUpdate.State.idle);
                        eventBus.fireEvent(new SongUpdateEvent(songOutUpdate));
                        bSteeleMusicIO.sendMessage(songOutUpdate.toJson());
                        state = requestedState;
                        break;
                }
                break;
            case playing:
                switch (state) {
                    case idle:
                        //  start
                        state = requestedState;
                        break;
                }
                break;
        }

        //  work the current state
        switch (state) {
            case playing:
                //  distribute the time update locally
                //songMomentUpdate(tn);

            {
                int mn = songPlayer.peekMomentNumberAt(t);
                int advanceMomentNumber = songPlayer.peekMomentNumberAt(t + songOutUpdate.getBeatDuration());
                if (advanceMomentNumber != mn && advanceMomentNumber != lastAdvanceMomentNumber) {
                    //logger.info("mn: " + mn + ", send next: " + advanceMomentNumber);
                    lastAdvanceMomentNumber = advanceMomentNumber;
                    beatTheDrums(defaultDrumSelection, advanceMomentNumber, songPlayer.getPeekMomentT0(advanceMomentNumber));
                }
                if (mn != lastMomentNumber) {
                    lastMomentNumber = mn;
                    logger.finest("mn: " + mn + ", next: " + advanceMomentNumber);
                }
            }

            break;
            default:
                lastSystemT = systemT;
                break;
        }

        //  get ready for next time
        timer.requestAnimationFrame(this);


        //  tell everyone else it's animation time
        scheduler.scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                switch (state) {
                    case playing:
                        //  distribute the time update locally
                        songMomentUpdate(t);
                        eventBus.fireEvent(new MusicAnimationEvent(tn,
                                (songPlayer == null ? 0 : songPlayer.getBeatCount()),
                                (songPlayer == null ? 0 : songPlayer.getBeat(t)),
                                (songPlayer == null ? 0 : songPlayer.getBeatFraction(t)),
                                songOutUpdate.getMomentNumber()));
//                        if (songPlayer != null)
//                            logger.info("anim: " + t
//                                    + ", "   + songPlayer.getBeatCount()
//                                    + ", " + songPlayer.getBeat(t)
//                                    + ", " + songPlayer.getBeatFraction(t));
                        break;
                    default:
                        //  distribute the animation time update locally
                        eventBus.fireEvent(new MusicAnimationEvent(tn,
                                0, 0, 0, 0));
                        break;
                }

            }
        });
    }

    @Override
    protected void onBind() {
        eventBus.addHandler(DefaultDrumSelectEvent.TYPE, this);
    }

    /**
     * Push the song's play forward in time based on the song's start time and the timing of the song's measures.
     *
     * @param t system time
     */
    private void songMomentUpdate(double t) {
        int lastMomentNumber = songOutUpdate.getMomentNumber();

        int momentNumber = songPlayer.getMomentNumberAt(t);

        //  distribute the change
        if (lastMomentNumber != momentNumber) {
            songOutUpdate.setMomentNumber(momentNumber);
            eventBus.fireEvent(new SongUpdateEvent(songOutUpdate));
            logger.finest("songMomentUpdate: " + songOutUpdate.toString());
            logger.finer("t: " + t + " = m#: " + momentNumber
            );
        }
        if (songPlayer.isDone())
            stopSong();
    }

    private void beatTheDrums(LegacyDrumMeasure drumSelection, int momentNumber, double myT0) {
        if (momentNumber < 0 && !appOptions.isCountIn())
            return;
        if ((drumSelection == null || drumSelection.isSilent()) && momentNumber < 0) {
            drumSelection = countInDrumSelection;
        }
        if (drumSelection == null)
            return;

        SongBase songBase = songOutUpdate.getSong();
        double measureDuration = songOutUpdate.getDefaultMeasureDuration();

        int beatsPerBar = songBase.getBeatsPerBar();
        double start = myT0 - systemAudioLatency;

        SongMoment songMoment = songBase.getSongMoment(momentNumber);
        if (songMoment != null) {
            start += songMoment.getBeatNumber() * songBase.getSecondsPerBeat();
            beatsPerBar = songMoment.getMeasure().getBeatCount();
        } else {
            //  typically count in
            start += momentNumber * beatsPerBar * songBase.getSecondsPerBeat();
        }

        //logger.info("drum: start: " + start + ", t: " + audioFilePlayer.getCurrentTime());
        final double audioMargin = 0.002;
        {
            String drum = drumSelection.getHighHat();
            if (drum != null && drum.length() > 0) {
                int divisionsPerBeat = drum.length() / songBase.getBeatsPerBar();   //  truncate by int division
                int limit = divisionsPerBeat * beatsPerBar;
                //logger.finest("divisionsPerBeat: " + divisionsPerBeat + ", limit: " + limit + ", len: " + drum.length());
                double drumbeatDivisionDuration = measureDuration / (songBase.getBeatsPerBar() * divisionsPerBeat);
                //logger.finer("drum: "+drum+", drumbeatDivisionDuration: " + drumbeatDivisionDuration);
                String highHat = highHat1;

                for (int b = 0; b < limit; b++) {
                    switch (drum.charAt(b)) {
                        case 'x':
                            audioFilePlayer.play(highHat, start + b * drumbeatDivisionDuration,
                                    drumbeatDivisionDuration - audioMargin);
                            //logger.info("b at: "+(start+ systemAudioLatency + b * drumbeatDivisionDuration));
                            highHat = highHat3;
                            break;
                        case 'X':
                            audioFilePlayer.play(highHat1, start + b * drumbeatDivisionDuration,
                                    swing * drumbeatDivisionDuration - audioMargin);
                            highHat = highHat1;
                            break;
                        case 's':
                            audioFilePlayer.play(highHat, start + (b + (swing - 1)) * drumbeatDivisionDuration,
                                    (swing - 1) * drumbeatDivisionDuration - audioMargin);
                            highHat = highHat3;
                            break;
                    }
                }
            }
        }
        {
            String drum = drumSelection.getSnare();
            if (drum != null && drum.length() > 0) {
                int divisionsPerBeat = drum.length() / songBase.getBeatsPerBar();   //  truncate by int division
                int limit = divisionsPerBeat * beatsPerBar;
                double drumbeatDivisionDuration = measureDuration / (songBase.getBeatsPerBar() * divisionsPerBeat);

                for (int b = 0; b < limit; b++) {
                    switch (drum.charAt(b)) {
                        case 'X':
                        case 'x':
                            audioFilePlayer.play(snare, start + b * drumbeatDivisionDuration,
                                    drumbeatDivisionDuration - 0.002);
                            break;
                    }
                }
            }
        }
        {
            String drum = drumSelection.getKick();
            if (drum != null && drum.length() > 0) {
                int divisionsPerBeat = drum.length() / songBase.getBeatsPerBar();   //  truncate by int division
                int limit = divisionsPerBeat * beatsPerBar;
                double drumbeatDivisionDuration = measureDuration / (songBase.getBeatsPerBar() * divisionsPerBeat);

                for (int b = 0; b < limit; b++) {
                    switch (drum.charAt(b)) {
                        case 'X':
                        case 'x':
                            audioFilePlayer.play(kick, start + b * drumbeatDivisionDuration,
                                    drumbeatDivisionDuration - 0.002);
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void onMessage(double systemT, String data) {
        try {
            SongUpdate songInUpdate = SongUpdate.fromJson(data);
            if (songInUpdate == null) {
                logger.fine("PlayMaster null songInUpdate");
                return;
            }
            logger.fine("onMessage(): update diff: " + songInUpdate.diff(songOutUpdate));

            eventBus.fireEvent(new SongUpdateEvent(songInUpdate));

            songOutUpdate = songInUpdate;
            requestedState = songOutUpdate.getState();

            logger.fine("onMessage: " + songOutUpdate.toString());
        } catch (JSONException | ParseException ex) {
            logger.info(ex.getMessage());
        }
    }

    @Override
    public void onDefaultDrumSelection(DefaultDrumSelectEvent event) {
        defaultDrumSelection = event.getDrumSelection();
    }

    public final void setBSteeleMusicIO(BSteeleMusicIO bSteeleMusicIO) {
        this.bSteeleMusicIO = bSteeleMusicIO;
    }

    public final void stopSong() {
        requestedState = SongUpdate.State.idle;
    }

    public final void issueSongUpdate(SongUpdate songUpdate) {
        if (bSteeleMusicIO == null || !bSteeleMusicIO.sendMessage(songUpdate.toJson())) {
            //  issue the song update locally if there is no communication with the server
            eventBus.fireEvent(new SongUpdateEvent(songUpdate));
            logger.fine("issueSongUpdate: " + songUpdate.toString());
        }
    }

    @Override
    public void playSongUpdate(SongUpdate songUpdate) {
        songOutUpdate = songUpdate;

        songOutUpdate.getSong().setBeatsPerMinute(songOutUpdate.getCurrentBeatsPerMinute());    //  fixme: should this be done here?
        double t0 = audioFilePlayer.getCurrentTime()
                + 1 * songOutUpdate.getBeatDuration();  //  delay just enough to get the audio rolling
        int countIn = appOptions.isCountIn() ? -preRoll : -1;
        songOutUpdate.setMomentNumber(countIn);
        songPlayer = new SongPlayer(songOutUpdate.getSong());
        songPlayer.setMomentNumber(t0, countIn);
        songOutUpdate.setState(SongUpdate.State.playing);

        issueSongUpdate(songOutUpdate);
        requestedState = SongUpdate.State.playing;
    }

    @Override
    public void playSongOffsetRowNumber(int offset) {
        if (songOutUpdate == null
                || songOutUpdate.getState() != SongUpdate.State.playing
                || songPlayer == null
                || offset == 0 //  nothing to do
        )
            return;

        int momentNumber = songOutUpdate.getMomentNumber();
        if (momentNumber <= 0)     //  not during preroll
            return;

        SongBase song = songOutUpdate.getSong();
        GridCoordinate gridCoordinate = song.getMomentGridCoordinate(songOutUpdate.getMomentNumber());
        if (gridCoordinate == null)
            return;

        int momentRow = gridCoordinate.getRow();
        SongMoment songMoment = song.getSongMomentAtRow(momentRow + offset);
        if (songMoment == null)
            return;

        logger.fine("bump from row " + gridCoordinate.getRow()
                + " to row " + song.getMomentGridCoordinate(songOutUpdate.getMomentNumber()).getRow());

        songPlayer.setMomentNumber(audioFilePlayer.getCurrentTime(), songMoment.getMomentNumber());//  fixme: this is wrong, current time should be calculated from existing t0
        songOutUpdate.setMomentNumber(songMoment.getMomentNumber());
        logger.fine("play moment: "+Integer.toString(songOutUpdate.getMomentNumber()));
        eventBus.fireEvent(new SongUpdateEvent(songOutUpdate));
    }

    @Override
    public void playSongSetRowNumber(int row) {
        if (songOutUpdate == null
                || songOutUpdate.getState() != SongUpdate.State.playing
                || songPlayer == null
        )
            return;

        int momentNumber = songOutUpdate.getMomentNumber();
        if (momentNumber <= 0)     //  not during preroll
            return;

        SongBase song = songOutUpdate.getSong();
        if (song == null)
            return;
        SongMoment songMoment = song.getSongMomentAtRow(row);
        if (songMoment == null)
            return;

        songPlayer.setMomentNumber(audioFilePlayer.getCurrentTime(), songMoment.getMomentNumber());
        songOutUpdate.setMomentNumber(songMoment.getMomentNumber());
        eventBus.fireEvent(new SongUpdateEvent(songOutUpdate));
    }

    @Override
    public Integer jumpSectionToFirstSongMomentInSection(int momentNumber) {
        if (songPlayer != null)
            return songPlayer.jumpSectionToFirstSongMomentInSection(momentNumber);
        return 0;
    }

    @Override
    public int getMomentNumber() {
        if (songPlayer == null)
            return 0;
        return songPlayer.getMomentNumber();
    }

    @Override
    public Integer getSkipToNumber() {
        if (songPlayer == null)
            return null;
        return songPlayer.getSkipToNumber();
    }


    @Override
    public void play(Song song) {
        SongUpdate songUpdate = new SongUpdate();
        songUpdate.setSong(song);
        songUpdate.setBeatsPerBar(song.getBeatsPerBar());
        songUpdate.setCurrentBeatsPerMinute(song.getBeatsPerMinute());
        songUpdate.setCurrentKey(song.getKey());
        playSongUpdate(songUpdate);
    }

    public final void continueSong() {
        requestedState = SongUpdate.State.playing;
    }


//    public void setSong(Song song) {
//        this.song = song;
//        firstSection = 0;
//        lastSection = -1;
//        beatsPerBar = song.getBeatsPerBar();
//        measureDuration = song.getBeatsPerBar() * 60.0 / song.getCurrentBeatsPerMinute();
//    }

    @Override
    public void initialize() {
        audioFilePlayer = new AudioFilePlayer();

        //  verify we can find anything
        String url = GWT.getHostPageBaseURL();
        if (!url.matches("^file://.*")) {

            //  prep the canned sounds
            for (int i = 0; i <= 27; i++) {
                audioFilePlayer.bufferFile("images/bass_" + i + ".mp3");
            }
            for (int i = 0; i <= 30; i++) {
                audioFilePlayer.bufferFile("images/guitar_" + i + ".mp3");
            }
            for (int i = 1; i <= 4; i++) {
                audioFilePlayer.bufferFile("images/count" + i + ".mp3");
            }
            audioFilePlayer.bufferFile(highHat1);
            audioFilePlayer.bufferFile(highHat3);
            audioFilePlayer.bufferFile(kick);
            audioFilePlayer.bufferFile(snare);

            systemAudioLatency = audioFilePlayer.getBaseLatency();
            logger.fine("Latency: " + systemAudioLatency
                    + ", " + audioFilePlayer.getOutputLatency()
            );

            //  fixme: should wait for end of audio buffer loading

            countInDrumSelection.setHighHat("xxxxxx");
            countInDrumSelection.setKick("x_____");
        }

        //  use the animation timer to pump commands to the audio
        timer = AnimationScheduler.get();
        timer.requestAnimationFrame(this);
    }

    private void sendMeasureDurationStatus(double songT) {
        double dur = songT - lastSystemT;
        sendStatus("measureDuration", dur);
        //double lowPassDur =  pass * lowPassDur + (1-pass) * dur;
        lastSystemT = songT;
    }

    private void sendStatus(String name, String value) {
        eventBus.fireEvent(new StatusEvent(name, value));
    }

    private void sendStatus(String name, int value) {
        eventBus.fireEvent(new StatusEvent(name, value));
    }

    private void sendStatus(String name, double value) {
        eventBus.fireEvent(new StatusEvent(name, value));
    }

    private AudioFilePlayer audioFilePlayer;
    private AnimationScheduler timer;

    private SongUpdate songOutUpdate = new SongUpdate();
    private SongUpdate.State requestedState = SongUpdate.State.idle;
    private SongUpdate.State state = SongUpdate.State.idle;
    private LegacyDrumMeasure defaultDrumSelection = new LegacyDrumMeasure();
    private LegacyDrumMeasure countInDrumSelection = new LegacyDrumMeasure();
    private static final String highHat1 = "images/hihat3.mp3";
    private static final String highHat3 = "images/hihat1.mp3";
    private static final String kick = "images/kick_4513.mp3";
    private static final String snare = "images/snare_4405.mp3";
    private static final double swing = 1.5;
    private static final int preRoll = 4;
    private final EventBus eventBus;
    private BSteeleMusicIO bSteeleMusicIO;
    private double systemToAudioOffset;
    private double systemAudioLatency;
    private static double pass = 0.95;
    private double lastSystemT;
    private static final Scheduler scheduler = Scheduler.get();
    private SongPlayer songPlayer;
    private static final AppOptions appOptions = GWTAppOptions.getInstance();

    private int lastMomentNumber;
    private int lastAdvanceMomentNumber;

    private static final Logger logger = Logger.getLogger(SongPlayMasterImpl.class.getName());

    static {
        //logger.setLevel(Level.FINE);
    }
}
