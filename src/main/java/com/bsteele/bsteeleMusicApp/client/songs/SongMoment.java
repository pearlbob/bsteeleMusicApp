package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class SongMoment {
    SongMoment(int sequenceNumber, LyricSection lyricSection,
               ChordSection chordSection, MeasureSequenceItem measureSequenceItem, Measure measure,
               int repeat, int repeatMax) {
        this.sequenceNumber = sequenceNumber;
        this.lyricSection = lyricSection;
        this.chordSection = chordSection;
        this.measureSequenceItem = measureSequenceItem;
        this.measure = measure;
        this.repeat = repeat;
        this.repeatMax = repeatMax;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public LyricSection getLyricSection() {
        return lyricSection;
    }

    public ChordSection getChordSection() {
        return chordSection;
    }

    public MeasureNode getMeasureSequenceItem() {
        return measureSequenceItem;
    }

    public Measure getMeasure() {
        return measure;
    }

    public int getRepeat() {
        return repeat;
    }

    public int getRepeatMax() {
        return repeatMax;
    }

    private final int sequenceNumber;

    private final int repeat;
    private final int repeatMax;

    private final LyricSection lyricSection;
    private final ChordSection chordSection;
    private final MeasureSequenceItem measureSequenceItem;
    private final Measure measure;

}
