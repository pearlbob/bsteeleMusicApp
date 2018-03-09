/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.shared;

import java.util.ArrayList;
import jsinterop.annotations.JsType;

/**
 *
 * @author bob
 */
@JsType
public class SongPlayMaster {

  public enum Action {
    stopSong,
    playSong,
    continueSong,
    loopSong,
    loop1,
    loop2,
    loop4,
    loopSelected,
    idle;
  }

  public void stopSong() {
    requestAction(Action.stopSong);
  }
  
  public void playSong() {
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
  }

  public void setSelection(int first, int last) {
    this.firstSection = first;
    this.lastSection = last;
  }

  public void setSong(Song song) {
    this.song = song;
    sectionSequence = song.getSectionSequence();
    firstSection = 0;
    lastSection = -1;
  }

  public SongUpdate process(double t) {

    switch (action) {
      case idle:
        break;
      case playSong:
        songUpdate.setBeat(currentBeat);
        songUpdate.setBeatsPerBar(song.getBeatsPerBar());
        songUpdate.setBeatsPerMinute(song.getBeatsPerMinute());
        songUpdate.setChordSectionCurrentRepeat(0);
        songUpdate.setChordSectionRepeat(0);
        songUpdate.setChordSectionRow(0);
        songUpdate.setEventTime(t);
        songUpdate.setMeasure(currentMeasure);
        songUpdate.setSectionCount(currentSection);
        songUpdate.setSection(sectionSequence.get(currentSection).getSection().getAbreviation());
        songUpdate.setSectionVersion(sectionSequence.get(currentSection).getVersion());
        songUpdate.setTitle(song.getSongId());
        
        
        break;
    }

    return null;
  }

  private int currentSection;
  private int currentMeasure;
  private int currentBeat;
  private int firstSection;
  private int lastSection;

  private Song song;
  private ArrayList<Section.Version> sectionSequence;
  private final SongUpdate songUpdate = new SongUpdate();
  private final Action state = Action.stopSong;
  private Action action = Action.stopSong;
}
