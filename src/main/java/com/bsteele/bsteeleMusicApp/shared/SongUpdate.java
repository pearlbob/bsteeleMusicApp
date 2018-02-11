/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.shared;

import jsinterop.annotations.JsType;

/**
 * Immutable song update data
 *
 * @author bob
 */
@JsType
public class SongUpdate {
  
  SongUpdate(){}

  SongUpdate(SongUpdate other) {
    this.eventTime = other.eventTime;
    this.title = other.title;
    this.sectionCount = other.sectionCount;
    this.sectionVersion = other.sectionVersion;
    this.chordSectionRow = other.chordSectionRow;
    this.chordSectionCurrentRepeat = other.chordSectionCurrentRepeat;
    this.chordSectionRepeat = other.chordSectionRepeat;
    this.measure = other.measure;
    this.beat = other.beat;
    this.beatsPerMeasure = other.beatsPerMeasure;
    this.beatsPerMinute = other.beatsPerMinute;
  }

  /**
   * return event time in seconds
   *
   * @return
   */
  public double getEventTime() {
    return eventTime;
  }

  public String getTitle() {
    return title;
  }

  public int getSectionCount() {
    return sectionCount;
  }

  /**
   * Id of the current section to be used by the chords display.
   *
   * @return
   */
  public String getSection() {
    return section;
  }

  /**
   * @param section
   */
  public void setSection(String section) {
    this.section = section;
  }
  
    /**
   * Id of the current section to be used by the chords display.
   *
   * @return
   */
  public int getSectionVersion() {
    return sectionVersion;
  }

  /**
   * @param sectionVersion the sectionVersion to set
   */
  public void setSectionVersion(int sectionVersion) {
    this.sectionVersion = sectionVersion;
  }

  /**
   * Current row number in the section
   *
   * @return
   */
  public int getChordSectionRow() {
    return chordSectionRow;
  }

  /**
   * Current repeat count if the row(s) repeat
   *
   * @return
   */
  public int getChordSectionCurrentRepeat() {
    return chordSectionCurrentRepeat;
  }

  /**
   * Total number of repeats in the current repeat row(s).
   *
   * @return
   */
  public int getChordSectionRepeat() {
    return chordSectionRepeat;
  }

  /**
   * Measure number from start of song Starts at zero.
   *
   * @return
   */
  public int getMeasure() {
    return measure;
  }

  /**
   * Beat number from start of the current measure. Starts at zero and goes to
   * beatsPerBar - 1
   *
   * @return
   */
  public int getBeat() {
    return beat;
  }

  /**
   * @return the beatsPerMeasure
   */
  public int getBeatsPerMeasure() {
    return beatsPerMeasure;
  }

  /**
   * @return the beatsPerMinute
   */
  public int getBeatsPerMinute() {
    return beatsPerMinute;
  }

  /**
   * @param eventTime the eventTime to set
   */
  public void setEventTime(double eventTime) {
    this.eventTime = eventTime;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @param sectionCount the sectionCount to set
   */
  public void setSectionCount(int sectionCount) {
    this.sectionCount = sectionCount;
  }

  /**
   * @param chordSectionRow the chordSectionRow to set
   */
  public void setChordSectionRow(int chordSectionRow) {
    this.chordSectionRow = chordSectionRow;
  }

  /**
   * @param chordSectionCurrentRepeat the chordSectionCurrentRepeat to set
   */
  public void setChordSectionCurrentRepeat(int chordSectionCurrentRepeat) {
    this.chordSectionCurrentRepeat = chordSectionCurrentRepeat;
  }

  /**
   * @param chordSectionRepeat the chordSectionRepeat to set
   */
  public void setChordSectionRepeat(int chordSectionRepeat) {
    this.chordSectionRepeat = chordSectionRepeat;
  }

  /**
   * @param measure the measure to set
   */
  public void setMeasure(int measure) {
    this.measure = measure;
  }

  /**
   * @param beat the beat to set
   */
  public void setBeat(int beat) {
    this.beat = beat;
  }

  /**
   * @param beatsPerMeasure the beatsPerMeasure to set
   */
  public void setBeatsPerBar(int beatsPerMeasure) {
    this.beatsPerMeasure = beatsPerMeasure;
  }

  /**
   * @param beatsPerMinute the beatsPerMinute to set
   */
  public void setBeatsPerMinute(int beatsPerMinute) {
    this.beatsPerMinute = beatsPerMinute;
  }

  private double eventTime;
  private String title;
  private int sectionCount;
  private String section;
  private int sectionVersion;
  private int chordSectionRow;
  private int chordSectionCurrentRepeat;
  private int chordSectionRepeat;
  private int measure;

  private int beat;
  private int beatsPerMeasure;
  private int beatsPerMinute;

}
