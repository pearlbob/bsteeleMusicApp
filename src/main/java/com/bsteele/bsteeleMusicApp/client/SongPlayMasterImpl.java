/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.client.application.BSteeleMusicIO;
import com.bsteele.bsteeleMusicApp.client.application.songs.DefaultDrumSelectEvent;
import com.bsteele.bsteeleMusicApp.client.application.songs.DefaultDrumSelectEventHandler;
import com.bsteele.bsteeleMusicApp.client.jsTypes.AudioFilePlayer;
import com.bsteele.bsteeleMusicApp.client.songs.DrumMeasure;
import com.bsteele.bsteeleMusicApp.client.songs.Section;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HandlerContainerImpl;
import jsinterop.annotations.JsType;

import java.util.ArrayList;

/**
 * @author bob
 */
@JsType
public class SongPlayMasterImpl
        extends HandlerContainerImpl
        implements
        SongPlayMaster,

        DefaultDrumSelectEventHandler {

    @Inject
    public SongPlayMasterImpl(final EventBus eventBus) {
        this.eventBus = eventBus;

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

        //  fixme: should wait for end of audio buffer loading

        //  setup a rough timer to pump commands to the audio
        timer = new Timer() {
            @Override
            public void run() {
                oncePrettyOften();
            }
        };
        timer.scheduleRepeating(1000 / 60);
    }

    @Override
    protected void onBind() {
        eventBus.addHandler(DefaultDrumSelectEvent.TYPE, this);
    }


    /**
     * A rough timer handler designed to pump commands to the audio
     * about once a measure but not intended to be synchronous.
     * The intent is to run on measure ahed to assure time to
     * load the audio properly.
     */
    private void oncePrettyOften() {

        switch (action) {
            case playing:
                double t = audioFilePlayer.getCurrentTime();
                if (t > nextMeasureStart - measureDuration) {
                    //   time to load the next measure
                    if (nextMeasureStart < t - measureDuration)
                        nextMeasureStart = t;// fixme: modulo measureDuration?

                    beatTheDrums(defaultDrumSelection);

                    nextMeasureStart += measureDuration;
                }
                break;
            case stopSong:
                audioFilePlayer.stop();
                action = Action.idle;
                break;
            case idle:
                break;
        }
    }

    private void beatTheDrums(DrumMeasure drumSelection) {
        {
            String drum = drumSelection.getHighHat();
            double drumbeatDuration = measureDuration / drum.length();
            String highHat = highHat1;

            for (int b = 0; b < drum.length(); b++) {
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
        {
            String drum = drumSelection.getKick();
            double drumbeatDuration = measureDuration / drum.length();

            for (int b = 0; b < drum.length(); b++) {
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

    @Override
    public void onMessage(double systemT, String data) {
        //  collect timing data as soon as possible
        double t = audioFilePlayer.getCurrentTime();

        try {
            SongUpdate songInUpdate = SongUpdate.fromJson(data);
            GWT.log("update diff: " + songInUpdate.diff(songOutUpdate));
//            double t = audioContext.getCurrentTime();
//            GWT.log("diff: " + (System.currentTimeMillis() / 1000.0 - t) + ", t: " + t);
        } catch (JSONException jsonException) {
            GWT.log(jsonException.getMessage());
        }

        double audioOffset = systemT - t;
        double dOff = 0;
        if (lastOffset > 0)
            dOff = lastOffset - audioOffset;
        lastOffset = audioOffset;

        GWT.log("t: " + t
                        + ", off: " + audioOffset
                        + ", dOff: " + dOff
//                +", "+audioFilePlayer.getBaseLatency()
//                +", "+audioFilePlayer.getOutputLatency()
        );
    }

    @Override
    public void onDefaultDrumSelection(DefaultDrumSelectEvent event) {
        defaultDrumSelection = event.getDrumSelection();
    }

    public enum Action {
        stopSong,
        playSong,
        continueSong,
        loopSong,
        loop1,
        loop2,
        loop4,
        loopSelected,
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
        sectionSequence = song.getSectionSequence();
        currentSection = 0;
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
                songOutUpdate.setBeat(currentBeat);
                songOutUpdate.setBeatsPerBar(song.getBeatsPerBar());
                songOutUpdate.setBeatsPerMinute(song.getBeatsPerMinute());
                songOutUpdate.setChordSectionCurrentRepeat(0);
                songOutUpdate.setChordSectionRepeat(0);
                songOutUpdate.setChordSectionRow(0);
                songOutUpdate.setEventTime(System.currentTimeMillis());
                songOutUpdate.setMeasure(currentMeasure);
                songOutUpdate.setSectionCount(currentSection);
                songOutUpdate.setSection(sectionSequence.get(currentSection).getSection().getAbreviation());
                songOutUpdate.setSectionVersion(sectionSequence.get(currentSection).getVersion());
                songOutUpdate.setSong(song);

                BSteeleMusicIO.sendMessage(songOutUpdate.toJson());
                action = Action.playing;
                break;
            case playing:
                break;
        }
    }

    private int currentSection;
    private int currentMeasure;
    private int currentBeat;
    private int firstSection;
    private int lastSection;

    private final AudioFilePlayer audioFilePlayer;
    private final Timer timer;
    private int beatsPerBar = 4;
    private double measureDuration = 120.0 / 60;
    private double nextMeasureStart;
    private Song song;
    private ArrayList<Section.Version> sectionSequence;
    private final SongUpdate songOutUpdate = new SongUpdate();
    private final SongUpdate.State state = SongUpdate.State.idle;
    private Action action = Action.stopSong;
    private DrumMeasure defaultDrumSelection = new DrumMeasure();
    private static final String highHat1 = "images/hihat3.mp3";
    private static final String highHat3 = "images/hihat1.mp3";
    private static final String kick = "images/kick_4513.mp3";
    private static final double swing = 1.5;
    private double lastOffset;
    private final EventBus eventBus;
}
