package com.bsteele.bsteeleMusicApp.client.songs;

import javax.annotation.Nonnull;

public class ChordSectionLocation {

    ChordSectionLocation(@Nonnull ChordSection chordSection, int index) {
        this.chordSection = chordSection;
        this.index = index;
    }

    public static final ChordSectionLocation parseLocation(String s) {
        StringBuffer sb = new StringBuffer(s);
        SectionVersion sectionVersion = Section.parse(sb);
        if (sectionVersion == null)
            return null;
        try {
            int index = Integer.parseInt(s);
            return new ChordSectionLocation(new ChordSection(sectionVersion), index);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }


    public String getId() {
        return chordSection.getId() + "#" + index;
    }

    public ChordSection getChordSection() {
        return chordSection;
    }

    public int getIndex() {
        return index;
    }

    private final ChordSection chordSection;
    private final int index;

}
