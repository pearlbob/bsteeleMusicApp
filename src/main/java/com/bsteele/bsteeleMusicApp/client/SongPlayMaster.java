/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.shared.Section;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONException;
import java.util.ArrayList;
import jsinterop.annotations.JsType;

/**
 * @author bob
 */
@JsType
public class SongPlayMaster {

  /**
   * Hide the default constructor to force use of the singleton
   */
  private SongPlayMaster() {
    audioFilePlayer = new AudioFilePlayer();

    for (int i = 0; i <= 27; i++) {
      audioFilePlayer.bufferFile("images/bass_" + i + ".mp3");
    }

    audioFilePlayer.bufferFile(highHat1);
    audioFilePlayer.bufferFile(highHat3);
  }

  public static final SongPlayMaster getSongPlayMaster() {
    return songPlayMaster;
  }

  public void onMessage(String data) {
    try {
      SongUpdate songInUpdate = SongUpdate.fromJson(data);
      GWT.log("update diff: " + songInUpdate.diff(songOutUpdate));
//            double t = audioContext.getCurrentTime();
//            GWT.log("diff: " + (System.currentTimeMillis() / 1000.0 - t) + ", t: " + t);
    } catch (JSONException jsonException) {
      GWT.log(jsonException.getMessage());
    }

    GWT.log("highHat1: " + Boolean.toString(audioFilePlayer.play(highHat1, 0, 1)));
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

        BSteeleMusicApp.sendMessage(songOutUpdate.toJson());
        action = Action.playing;
        break;
      case playing:
        break;
    }
  }

  private static final SongPlayMaster songPlayMaster = new SongPlayMaster();    //  singleton
  private int currentSection;
  private int currentMeasure;
  private int currentBeat;
  private int firstSection;
  private int lastSection;

  private final AudioFilePlayer audioFilePlayer;
  private Song song;
  private ArrayList<Section.Version> sectionSequence;
  private final SongUpdate songOutUpdate = new SongUpdate();
  private final SongUpdate.State state = SongUpdate.State.idle;
  private Action action = Action.stopSong;
  private static final String highHat1 = "images/hihat3.mp3";
  private static final String highHat3 = "images/hihat3.mp3";
}
