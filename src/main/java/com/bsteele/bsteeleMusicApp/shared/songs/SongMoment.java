package com.bsteele.bsteeleMusicApp.shared.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class SongMoment {
    SongMoment(int sequenceNumber, LyricSection lyricSection,
               ChordSection chordSection, int phraseIndex, Phrase phrase, int measureIndex, Measure measure,
               int repeat, int repeatMax) {
        this.sequenceNumber = sequenceNumber;
        this.lyricSection = lyricSection;
        this.chordSection = chordSection;
        this.phraseIndex = phraseIndex;
        this.phrase = phrase;
        this.measureIndex = measureIndex;
        this.measure = measure;
        this.repeat = repeat;
        this.repeatMax = repeatMax;
    }

    public final int getSequenceNumber() {
        return sequenceNumber;
    }

    public final LyricSection getLyricSection() {
        return lyricSection;
    }

    public final ChordSection getChordSection() {
        return chordSection;
    }

    @Deprecated
    public MeasureNode getPhrase() {
        return phrase;
    }

    public final int getPhraseIndex() {
        return phraseIndex;
    }

    public final int getMeasureIndex() {
        return measureIndex;
    }

    public Measure getMeasure() {
        return measure;
    }

    public final int getRepeat() {
        return repeat;
    }

    public  final int getRepeatMax() {
        return repeatMax;
    }


    public final ChordSectionLocation getChordSectionLocation() {
        if ( chordSectionLocation == null )
            chordSectionLocation = new ChordSectionLocation(chordSection.getSectionVersion(), phraseIndex, measureIndex);
        return chordSectionLocation;
    }

    @Override
    public String toString() {
        return  getChordSectionLocation().toString() + "@"+sequenceNumber;
    }

    private transient ChordSectionLocation chordSectionLocation;

    private final int sequenceNumber;

    private final int repeat;
    private final int repeatMax;

    private final LyricSection lyricSection;
    private final ChordSection chordSection;
    private final int phraseIndex;
    private final Phrase phrase;
    private final int measureIndex;
    private final Measure measure;
}
