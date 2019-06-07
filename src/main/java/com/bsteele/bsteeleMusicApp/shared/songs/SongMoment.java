package com.bsteele.bsteeleMusicApp.shared.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class SongMoment implements Comparable<SongMoment> {

    SongMoment(int sequenceNumber, int beatNumber, int sectionBeatNumber,
               LyricSection lyricSection,
               ChordSection chordSection, int phraseIndex, Phrase phrase, int measureIndex, Measure measure,
               int repeat, int repeatCycleBeats, int repeatMax, int sectionCount) {
        this.sequenceNumber = sequenceNumber;
        this.beatNumber = beatNumber;
        this.sectionBeatNumber = sectionBeatNumber;

        this.lyricSection = lyricSection;
        this.chordSection = chordSection;
        this.phraseIndex = phraseIndex;
        this.phrase = phrase;
        this.measureIndex = measureIndex;
        this.measure = measure;
        this.repeat = repeat;
        this.repeatCycleBeats = repeatCycleBeats;
        this.repeatMax = repeatMax;
        this.sectionCount = sectionCount;
    }

    public final int getSequenceNumber() {
        return sequenceNumber;
    }

    public final int getBeatNumber() {
        return beatNumber;
    }

    public int getSectionBeatNumber() {
        return sectionBeatNumber;
    }

    public final LyricSection getLyricSection() {
        return lyricSection;
    }

    public final ChordSection getChordSection() {
        return chordSection;
    }

    @Deprecated
    public final MeasureNode getPhrase() {
        return phrase;
    }

    public final int getPhraseIndex() {
        return phraseIndex;
    }

    public final int getMeasureIndex() {
        return measureIndex;
    }

    public final Measure getMeasure() {
        return measure;
    }

    public final int getRepeat() {
        return repeat;
    }

    public final int getRepeatCycleBeats() {
        return repeatCycleBeats;
    }

    public final int getRepeatMax() {
        return repeatMax;
    }

    public final int getSectionCount() {
        return sectionCount;
    }


    @Override
    public int compareTo(SongMoment o) {
        if (sequenceNumber == o.sequenceNumber)
            return 0;
        return sequenceNumber < o.sequenceNumber ? -1 : 1;
    }


    public final ChordSectionLocation getChordSectionLocation() {
        if (chordSectionLocation == null)
            chordSectionLocation = new ChordSectionLocation(chordSection.getSectionVersion(), phraseIndex, measureIndex);
        return chordSectionLocation;
    }

    @Override
    public String toString() {
        return sequenceNumber + ": " + getChordSectionLocation().toString() + "#" + sectionCount
                + " "+measure.toMarkup()
                + " beat "+getBeatNumber()
                ;
    }


    private transient ChordSectionLocation chordSectionLocation;

    private final int sequenceNumber;
    private final int beatNumber;   //  total beat count from start of song to the start of the moment
    private final int sectionBeatNumber;   //  total beat count from start of the current section to the start of the moment

    private final int repeat;       //  current iteration from 0 to repeatMax - 1
    private final int repeatMax;
    private final int repeatCycleBeats;   //  number of beats in one cycle of the repeat

    private final LyricSection lyricSection;
    private final ChordSection chordSection;
    private final int phraseIndex;
    private final Phrase phrase;
    private final int measureIndex;
    private final Measure measure;
    private final int sectionCount;

}
