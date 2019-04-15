package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.util.MarkedString;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.util.Objects;

public class ChordSectionLocation {

    ChordSectionLocation(@Nonnull SectionVersion sectionVersion, int phraseIndex, int measureIndex) {
        this.sectionVersion = sectionVersion;
        if (phraseIndex < 0) {
            this.phraseIndex = -1;
            hasPhraseIndex = false;
            this.measureIndex = measureIndex;
            hasMeasureIndex = false;
        } else {
            this.phraseIndex = phraseIndex;
            hasPhraseIndex = true;
            if (measureIndex < 0) {
                this.measureIndex = measureIndex;
                hasMeasureIndex = false;
            } else {
                this.measureIndex = measureIndex;
                hasMeasureIndex = true;
            }
        }

        marker = Marker.none;
    }

    ChordSectionLocation(@Nonnull SectionVersion sectionVersion, int phraseIndex) {
        this.sectionVersion = sectionVersion;
        if (phraseIndex < 0) {
            this.phraseIndex = -1;
            hasPhraseIndex = false;
        } else {
            this.phraseIndex = phraseIndex;
            hasPhraseIndex = true;
        }
        this.measureIndex = 0;
        hasMeasureIndex = false;
        marker = Marker.none;
    }

    ChordSectionLocation(@Nonnull SectionVersion sectionVersion, int phraseIndex, Marker marker) {
        this.sectionVersion = sectionVersion;
        if (phraseIndex < 0) {
            this.phraseIndex = -1;
            hasPhraseIndex = false;
        } else {
            this.phraseIndex = phraseIndex;
            hasPhraseIndex = true;
        }
        this.measureIndex = 0;
        hasMeasureIndex = false;
        this.marker = marker;
    }

    ChordSectionLocation(@Nonnull SectionVersion sectionVersion) {
        this.sectionVersion = sectionVersion;
        this.phraseIndex = -1;
        hasPhraseIndex = false;
        this.measureIndex = -1;
        hasMeasureIndex = false;
        marker = Marker.none;
    }

    enum Marker {
        none,
        repeatUpperRight,
        repeatMiddleRight,
        repeatLowerRight;
    }

    static final ChordSectionLocation parse(String s) throws ParseException {
        return parse(new MarkedString(s));
    }

    /**
     * Parse a chord section location from the given string input
     *
     * @param markedString the given string input
     * @return the chord section location, can be null
     */
    static final ChordSectionLocation parse(MarkedString markedString) throws ParseException {

        SectionVersion sectionVersion = SectionVersion.parse(markedString);

        if (markedString.available() >= 3) {
            final RegExp numberRegexp = RegExp.compile("^(\\d+):(\\d+)");  //  workaround for RegExp is not serializable.
            MatchResult mr = numberRegexp.exec(markedString.remainingStringLimited(6));
            if (mr != null) {
                try {
                    int phraseIndex = Integer.parseInt(mr.getGroup(1));
                    int measureIndex = Integer.parseInt(mr.getGroup(2));
                    markedString.consume(mr.getGroup(0).length());
                    return new ChordSectionLocation(sectionVersion, phraseIndex, measureIndex);
                } catch (NumberFormatException nfe) {
                    throw new ParseException(nfe.getMessage(), 0);
                }
            }
        }
        if (!markedString.isEmpty()) {
            final RegExp numberRegexp = RegExp.compile("^(\\d+)");  //  workaround for RegExp is not serializable.
            MatchResult mr = numberRegexp.exec(markedString.remainingStringLimited(2));
            if (mr != null) {
                try {
                    int phraseIndex = Integer.parseInt(mr.getGroup(1));
                    markedString.consume(mr.getGroup(0).length());
                    return new ChordSectionLocation(sectionVersion, phraseIndex);
                } catch (NumberFormatException nfe) {
                    throw new ParseException(nfe.getMessage(), 0);
                }
            }
        }
        return new ChordSectionLocation(sectionVersion);
    }

    public final ChordSectionLocation nextMeasureIndexLocation() {
        if (!hasPhraseIndex || !hasMeasureIndex)
            return this;
        return new ChordSectionLocation(sectionVersion, phraseIndex, measureIndex + 1);
    }

    public final ChordSectionLocation nextPhraseIndexLocation() {
        if (!hasPhraseIndex)
            return this;
        return new ChordSectionLocation(sectionVersion, phraseIndex + 1);
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

    public final boolean hasPhraseIndex() {
        return hasPhraseIndex;
    }

    public final boolean hasMeasureIndex() {
        return hasMeasureIndex;
    }


    public final boolean isSection() {
        return hasPhraseIndex == false && hasMeasureIndex == false;
    }

    public final boolean isPhrase() {
        return hasPhraseIndex == true && hasMeasureIndex == false;
    }

    public final boolean isMeasure() {
        return hasPhraseIndex == true && hasMeasureIndex == true;
    }


    public Marker getMarker() {
        return marker;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = (31 * hash + sectionVersion.hashCode()) % (1 << 31);
        hash = (31 * hash + (hasPhraseIndex ? 1 : 0)) % (1 << 31);
        hash = (31 * hash + phraseIndex) % (1 << 31);
        hash = (31 * hash + (hasMeasureIndex ? 1 : 0)) % (1 << 31);
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
