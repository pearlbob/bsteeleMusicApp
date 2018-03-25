/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.client.application.BSteeleMusicIO;
import com.bsteele.bsteeleMusicApp.client.application.events.*;
import com.bsteele.bsteeleMusicApp.client.jsTypes.AudioFilePlayer;
import com.bsteele.bsteeleMusicApp.client.songs.DrumMeasure;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONException;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HandlerContainerImpl;
import jsinterop.annotations.JsType;

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
     * The intent is to run on measure ahed to assure time to
     * load the audio properly.
     */
    @Override
    public void execute(double systemT) {
        //  do this first to get the best time alignment
        double t = audioFilePlayer.getCurrentTime();
        double t0 = songOutUpdate.getEventTime();

        systemT /= 1000;      //  use seconds internally

        {   //  find offset from system to audio time, integrate over time
            double dt = systemT - t;
            systemToAudioOffset = (Math.abs(systemToAudioOffset - dt) > 0.1)
                    ? dt : systemToAudioOffset * pass + (1 - pass) * dt;
        }

        if (audioFilePlayer != null) {
            switch (action) {
                case playing:
                    //  distribute the time update locally
                    songLocalUpdate(systemT);

                    double n = Math.floor((t + systemToAudioOffset - t0) / measureDuration);
                    thisMeasureStart = n * measureDuration + (t0 - systemToAudioOffset);
                    if (nextMeasureStart - thisMeasureStart < measureDuration / 2) {
                        //  schedule the audio one measure early
                        nextMeasureStart = thisMeasureStart + measureDuration;

                        beatTheDrums(defaultDrumSelection);
                    }
                    break;
                case stopSong:
                    audioFilePlayer.stop();
                    songOutUpdate.setState(SongUpdate.State.idle);
                    eventBus.fireEvent(new SongUpdateEvent(songOutUpdate));
                    bSteeleMusicIO.sendMessage(songOutUpdate.toJson());
                    action = Action.idle;
                    break;
                case idle:
                    break;
            }
        }

        //  tell everyone else it's animation time
        eventBus.fireEvent(new MusicAnimationEvent(systemT));

        //  get ready for next time
        timer.requestAnimationFrame(this);
    }

    @Override
    protected void onBind() {
        eventBus.addHandler(DefaultDrumSelectEvent.TYPE, this);
    }

    /**
     * @param t system time
     */
    private void songLocalUpdate(double t) {
        double songT = songOutUpdate.getEventTime();

        int m = (int) Math.floor((t - songT) / measureDuration);
        if (m == songOutUpdate.getMeasure())
            return;     //  that measure has already been updated

        logger.fine("t: " + t + ", m: " + m + " != " + songOutUpdate.getMeasure());

        if (m <= 0) {
            //  preroll
            songOutUpdate.setMeasure(m);
        } else {
            while (m > songOutUpdate.getMeasure()) {
                if (!songOutUpdate.nextMeasure()) {
                    stopSong();
                    break;
                }
            }
        }
        eventBus.fireEvent(new SongUpdateEvent(songOutUpdate));

        logger.fine("local update done: m: " + m + ", " + songOutUpdate.getMeasure());
    }

    private void beatTheDrums(DrumMeasure drumSelection) {
        {
            String drum = drumSelection.getHighHat();
            if (drum != null && drum.length() > 0) {
                int divisionsPerBeat = drum.length() / beatsPerBar;   //  truncate by int division
                int limit = divisionsPerBeat * beatsPerBar;
                double drumbeatDuration = measureDuration / limit;
                String highHat = highHat1;

                for (int b = 0; b < limit; b++) {
                    switch (drum.charAt(b)) {
                        case 'x':
                            audioFilePlayer.play(highHat, nextMeasureStart + b * drumbeatDuration,
                                    drumbeatDuration - 0.002);
                            highHat = highHat3;
                            break;
                        case 'X':
                            audioFilePlayer.play(highHat1, nextMeasureStart + b * drumbeatDuration,
                                    swing * drumbeatDuration - 0.002);
                            highHat = highHat3;
                            break;
                        case 's':
                            audioFilePlayer.play(highHat, nextMeasureStart + (b + (swing - 1)) * drumbeatDuration,
                                    (swing - 1) * drumbeatDuration - 0.002);
                            highHat = highHat3;
                            break;
                    }
                }
            }
        }
        {
            String drum = drumSelection.getSnare();
            if (drum != null && drum.length() > 0) {
                int divisionsPerBeat = drum.length() / beatsPerBar;   //  truncate by int division
                int limit = divisionsPerBeat * beatsPerBar;
                double drumbeatDuration = measureDuration / limit;

                for (int b = 0; b < limit; b++) {
                    switch (drum.charAt(b)) {
                        case 'X':
                        case 'x':
                            audioFilePlayer.play(snare, nextMeasureStart + b * drumbeatDuration,
                                    drumbeatDuration - 0.002);
                            break;
                    }
                }
            }
        }
        {
            String drum = drumSelection.getKick();
            if (drum != null && drum.length() > 0) {
                int divisionsPerBeat = drum.length() / beatsPerBar;   //  truncate by int division
                int limit = divisionsPerBeat * beatsPerBar;
                double drumbeatDuration = measureDuration / limit;

                for (int b = 0; b < limit; b++) {
                    switch (drum.charAt(b)) {
                        case 'X':
                        case 'x':
                            audioFilePlayer.play(kick, nextMeasureStart + b * drumbeatDuration,
                                    drumbeatDuration - 0.002);
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
            GWT.log("update diff: " + songInUpdate.diff(songOutUpdate));

            if (!songInUpdate.getSong().equals(songOutUpdate.getSong()))
                eventBus.fireEvent(new SongSelectionEvent(songInUpdate.getSong()));
            eventBus.fireEvent(new SongUpdateEvent(songInUpdate));
        } catch (JSONException jsonException) {
            GWT.log(jsonException.getMessage());
        }
    }

    @Override
    public void onDefaultDrumSelection(DefaultDrumSelectEvent event) {
        defaultDrumSelection = event.getDrumSelection();
    }

    public void setBSteeleMusicIO(BSteeleMusicIO bSteeleMusicIO) {
        this.bSteeleMusicIO = bSteeleMusicIO;
    }

    public enum Action {
        stopSong,
        playSong,
        //        continueSong,
//        loopSong,
//        loop1,
//        loop2,
//        loop4,
//        loopSelected,
        playing,
        idle;
    }

    public void stopSong() {
        requestAction(Action.stopSong);
    }

    public void play() {
        requestAction(Action.playSong);
    }

    public void playSong(Song song) {
        setSong(song);
        requestAction(Action.playSong);
    }

    public void continueSong() {
        requestAction(Action.playSong);
    }

    private void requestAction(Action action) {
        if (this.action == null) {
            return;
        }

        this.action = action;
        process();
    }

    public void setSelection(int first, int last) {
        this.firstSection = first;
        this.lastSection = last;
    }

    public void setSong(Song song) {
        this.song = song;
        firstSection = 0;
        lastSection = -1;
        beatsPerBar = song.getBeatsPerBar();
        measureDuration = song.getBeatsPerBar() * 60.0 / song.getBeatsPerMinute();
    }

    private void process() {

        switch (action) {
            case idle:
                break;
            case playSong:
                songOutUpdate.setBeatsPerBar(song.getBeatsPerBar());
                songOutUpdate.setBeatsPerMinute(song.getBeatsPerMinute());

                songOutUpdate.setEventTime(
                        Math.floor((System.currentTimeMillis() / 1000.0) / measureDuration) * measureDuration
                                //  add margin into the future
                                + (preRoll + 1) * measureDuration); //   fixme: adjust for adjustable runnup
                songOutUpdate.setSong(song);
                songOutUpdate.setMeasure(-preRoll);
                songOutUpdate.setState(SongUpdate.State.playing);

                bSteeleMusicIO.sendMessage(songOutUpdate.toJson());
                action = Action.playing;
                break;
            case playing:
                break;
        }
    }

    @Override
    public void initialize() {
        audioFilePlayer = new AudioFilePlayer();

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


        GWT.log("Latency: " + audioFilePlayer.getBaseLatency()
                + ", " + audioFilePlayer.getOutputLatency()
        );


        //  fixme: should wait for end of audio buffer loading

        //  use the animation timer to pump commands to the audio
        timer = AnimationScheduler.get();
        timer.requestAnimationFrame(this);
    }

    private int firstSection;
    private int lastSection;

    private AudioFilePlayer audioFilePlayer;
    private AnimationScheduler timer;
    private int beatsPerBar = 4;
    private double measureDuration = 120.0 / 60;
    private double thisMeasureStart;
    private double nextMeasureStart;
    private Song song;
    private final SongUpdate songOutUpdate = new SongUpdate();
    private final SongUpdate.State state = SongUpdate.State.idle;
    private Action action = Action.idle;
    private DrumMeasure defaultDrumSelection = new DrumMeasure();
    private static final String highHat1 = "images/hihat3.mp3";
    private static final String highHat3 = "images/hihat1.mp3";
    private static final String kick = "images/kick_4513.mp3";
    private static final String snare = "images/snare_4405.mp3";
    private static final double swing = 1.5;
    private double lastOffset;
    private static final int preRoll = 3;
    private final EventBus eventBus;
    private BSteeleMusicIO bSteeleMusicIO;
    private double systemToAudioOffset;
    private double pass = 0.95;

    private static final Logger logger = Logger.getLogger(SongPlayMasterImpl.class.getName());
}
