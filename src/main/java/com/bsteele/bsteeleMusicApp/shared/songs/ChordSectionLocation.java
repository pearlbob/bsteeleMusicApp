package com.bsteele.bsteeleMusicApp.shared.songs;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import javax.annotation.Nonnull;

public class ChordSectionLocation {

    ChordSectionLocation(@Nonnull ChordSection chordSection, int index) {
        this.chordSection = chordSection;
        this.index = index;
    }

    static final ChordSectionLocation parse(String s) {
        return parse(new StringBuffer(s));
    }

    /**
     * @param sb string buffer
     * @return
     */
    public static final ChordSectionLocation parse(StringBuffer sb) {

        SectionVersion sectionVersion = SectionVersion.parse(sb);
        if (sectionVersion == null)
            return null;

        if (sb.length() < 1)
            return null;

        final RegExp numberRegexp = RegExp.compile("^(\\d+)");  //  workaround for RegExp is not serializable.
        MatchResult mr = numberRegexp.exec(sb.substring(0, Math.min(sb.length(), 4)));
        if (mr != null) {
            try {
                int index = Integer.parseInt(mr.getGroup(1));
                sb.delete(0, mr.getGroup(0).length());
                return new ChordSectionLocation(new ChordSection(sectionVersion), index);
            } catch (NumberFormatException nfe) {
                return null;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return getId();
    }

    public final String getId() {
        return chordSection.getId() + ":" + index;
    }

    public final ChordSection getChordSection() {
        return chordSection;
    }

    public final int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ChordSectionLocation))
            return false;
        ChordSectionLocation o = (ChordSectionLocation) obj;
        return chordSection.equals(o.chordSection)
                && index == o.index;
    }

    private final ChordSection chordSection;
    private final int index;
}
