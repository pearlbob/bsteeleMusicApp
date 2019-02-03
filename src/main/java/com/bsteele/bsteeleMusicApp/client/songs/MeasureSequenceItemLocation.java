package com.bsteele.bsteeleMusicApp.client.songs;

public class MeasureSequenceItemLocation {

    MeasureSequenceItemLocation(ChordSectionLocation chordSectionLocation, int index) {
        this.chordSectionLocation = chordSectionLocation;
        this.index = index;
    }

    public String getId() {
        return chordSectionLocation.getId() + "#" + index;
    }

    public int getIndex() {
        return index;
    }

    private final ChordSectionLocation chordSectionLocation;
    private final int index;
}
