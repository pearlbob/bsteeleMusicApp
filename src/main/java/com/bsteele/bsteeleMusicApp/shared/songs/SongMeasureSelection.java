package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.client.songs.Song;

public class SongMeasureSelection {

    /**
     * Construct a song measure selection.
     *
     * @param song                song the selection is in
     * @param chordSection        chord section the selection is in
     * @param measureSequenceItem measureSequenceItem section the selection is in
     * @param measure             measure selection
     */
    SongMeasureSelection(Song song, ChordSection chordSection, MeasureSequenceItem measureSequenceItem, Measure measure) {
        this.song = song;
        this.chordSection = chordSection;
        this.measureSequenceItem = measureSequenceItem;
        this.measure = measure;
    }

    public Song getSong() {
        return song;
    }

    void setSong(Song song) {
        this.song = song;
    }

    public ChordSection getChordSection() {
        return chordSection;
    }

    void setChordSection(ChordSection chordSection) {
        this.chordSection = chordSection;
    }

    public MeasureSequenceItem getMeasureSequenceItem() {
        return measureSequenceItem;
    }

    void setMeasureSequenceItem(MeasureSequenceItem measureSequenceItem) {
        this.measureSequenceItem = measureSequenceItem;
    }

    public Measure getMeasure() {
        return measure;
    }

    void setMeasure(Measure measure) {
        this.measure = measure;
    }

    private Song song;
    private ChordSection chordSection;
    private MeasureSequenceItem measureSequenceItem;
    private Measure measure;
}
