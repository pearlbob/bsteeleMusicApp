package com.bsteele.bsteeleMusicApp.shared.songs;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import javax.annotation.Nonnull;

public class ChordSectionLocation {

    ChordSectionLocation(@Nonnull ChordSection chordSection, int phraseIndex, int measureIndex) {
        this.chordSection = chordSection;
        this.phraseIndex = phraseIndex;
        hasPhraseIndex = true;
        this.measureIndex = measureIndex;
        hasMeasureIndex = true;
    }

    ChordSectionLocation(@Nonnull ChordSection chordSection, int phraseIndex) {
        this.chordSection = chordSection;
        this.phraseIndex = phraseIndex;
        hasPhraseIndex = true;
        this.measureIndex = 0;
        hasMeasureIndex = false;
    }

    ChordSectionLocation(@Nonnull ChordSection chordSection) {
        this.chordSection = chordSection;
        this.phraseIndex = 0;
        hasPhraseIndex = false;
        this.measureIndex = 0;
        hasMeasureIndex = false;
    }

    static final ChordSectionLocation parse(String s) {
        return parse(new StringBuffer(s));
    }

    /**
     * Parse a chord section location from the given string input
     *
     * @param sb the given string input
     * @return the chord section location, can be null
     */
    public static final ChordSectionLocation parse(StringBuffer sb) {

        SectionVersion sectionVersion = SectionVersion.parse(sb);
        if (sectionVersion == null)
            return null;

        if (sb.length() >= 3) {
            final RegExp numberRegexp = RegExp.compile("^(\\d+):(\\d+)");  //  workaround for RegExp is not serializable.
            MatchResult mr = numberRegexp.exec(sb.substring(0, Math.min(sb.length(), 6)));
            if (mr != null) {
                try {
                    int phraseIndex = Integer.parseInt(mr.getGroup(1));
                    int measureIndex = Integer.parseInt(mr.getGroup(2));
                    sb.delete(0, mr.getGroup(0).length());
                    return new ChordSectionLocation(new ChordSection(sectionVersion), phraseIndex, measureIndex);
                } catch (NumberFormatException nfe) {
                    return null;
                }
            }
        }
        if (sb.length() >= 1) {
            final RegExp numberRegexp = RegExp.compile("^(\\d+)");  //  workaround for RegExp is not serializable.
            MatchResult mr = numberRegexp.exec(sb.substring(0, Math.min(sb.length(), 2)));
            if (mr != null) {
                try {
                    int phraseIndex = Integer.parseInt(mr.getGroup(1));
                    sb.delete(0, mr.getGroup(0).length());
                    return new ChordSectionLocation(new ChordSection(sectionVersion), phraseIndex);
                } catch (NumberFormatException nfe) {
                    return null;
                }
            }
        }
        return new ChordSectionLocation(new ChordSection(sectionVersion));
    }

    @Override
    public String toString() {
        return getId();
    }

    public final String getId() {
        return chordSection.getId() + ":" + (hasPhraseIndex ? phraseIndex + (hasMeasureIndex ? ":" + measureIndex : "") : "");
    }

    public final ChordSection getChordSection() {
        return chordSection;
    }

    public final int getPhraseIndex() {
        return phraseIndex;
    }

    public final int getMeasureIndex() {
        return measureIndex;
    }

    public boolean hasPhraseIndex() {
        return hasPhraseIndex;
    }

    public boolean hasMeasureIndex() {
        return hasMeasureIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ChordSectionLocation))
            return false;
        ChordSectionLocation o = (ChordSectionLocation) obj;
        return chordSection.equals(o.chordSection)
                && phraseIndex == o.phraseIndex
                && measureIndex == o.measureIndex;
    }

    private final ChordSection chordSection;
    private final int phraseIndex;
    private final boolean hasPhraseIndex;
    private final int measureIndex;
    private final boolean hasMeasureIndex;

}
