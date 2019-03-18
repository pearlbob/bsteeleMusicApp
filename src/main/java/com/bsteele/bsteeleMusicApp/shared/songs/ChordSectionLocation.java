package com.bsteele.bsteeleMusicApp.shared.songs;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ChordSectionLocation {

    ChordSectionLocation(@Nonnull SectionVersion sectionVersion, int phraseIndex, int measureIndex) {
        this.sectionVersion = sectionVersion;
        this.phraseIndex = phraseIndex;
        hasPhraseIndex = true;
        this.measureIndex = measureIndex;
        hasMeasureIndex = true;
        marker = Marker.none;
    }

    ChordSectionLocation(@Nonnull SectionVersion sectionVersion, int phraseIndex) {
        this.sectionVersion = sectionVersion;
        this.phraseIndex = phraseIndex;
        hasPhraseIndex = true;
        this.measureIndex = 0;
        hasMeasureIndex = false;
        marker = Marker.none;
    }

    ChordSectionLocation(@Nonnull SectionVersion sectionVersion, int phraseIndex, Marker marker) {
        this.sectionVersion = sectionVersion;
        this.phraseIndex = phraseIndex;
        hasPhraseIndex = true;
        this.measureIndex = 0;
        hasMeasureIndex = false;
        this.marker = marker;
    }

    ChordSectionLocation(@Nonnull SectionVersion sectionVersion) {
        this.sectionVersion = sectionVersion;
        this.phraseIndex = 0;
        hasPhraseIndex = false;
        this.measureIndex = 0;
        hasMeasureIndex = false;
        marker = Marker.none;
    }

    enum Marker {
        none,
        repeatUpperRight,
        repeatMiddleRight,
        repeatLowerRight;
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
                    return new ChordSectionLocation(sectionVersion, phraseIndex, measureIndex);
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
                    return new ChordSectionLocation(sectionVersion, phraseIndex);
                } catch (NumberFormatException nfe) {
                    return null;
                }
            }
        }
        return new ChordSectionLocation(sectionVersion);
    }

    @Override
    public String toString() {
        return getId();
    }

    public final String getId() {
        return sectionVersion.toString() + (hasPhraseIndex ? phraseIndex + (hasMeasureIndex ? ":" + measureIndex : "") : "");
    }

    public final SectionVersion getSectionVersion() {
        return sectionVersion;
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


    public Marker getMarker() {
        return marker;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = (31 * hash + sectionVersion.hashCode()) % (1 << 31);
        hash = (31 * hash + (hasPhraseIndex?1:0)) % (1 << 31);
        hash = (31 * hash + phraseIndex) % (1 << 31);
        hash = (31 * hash + (hasMeasureIndex?1:0)) % (1 << 31);
        hash = (31 * hash + measureIndex) % (1 << 31);
        hash = (31 * hash + Objects.hashCode(marker)) % (1 << 31);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ChordSectionLocation))
            return false;
        ChordSectionLocation o = (ChordSectionLocation) obj;
        return sectionVersion.equals(o.sectionVersion)
                && hasPhraseIndex == o.hasPhraseIndex
                && hasMeasureIndex == o.hasMeasureIndex
                && (hasPhraseIndex ? phraseIndex == o.phraseIndex : true)
                && (hasMeasureIndex ? measureIndex == o.measureIndex : true)
                ;
    }

    private final SectionVersion sectionVersion;
    private final int phraseIndex;
    private final boolean hasPhraseIndex;
    private final int measureIndex;
    private final boolean hasMeasureIndex;
    private final Marker marker;

}
