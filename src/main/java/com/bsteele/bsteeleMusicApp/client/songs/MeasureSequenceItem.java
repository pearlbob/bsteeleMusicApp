package com.bsteele.bsteeleMusicApp.client.songs;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureSequenceItem {

    public MeasureSequenceItem(@Nonnull SectionVersion sectionVersion, @Nonnull ArrayList<Measure> measures, int sequenceNumber, int repeats) {
        this.sectionVersion = sectionVersion;
        this.measures = measures;
        this.sequenceNumber = sequenceNumber;
        this.repeats = repeats;
    }

    public final int getSequenceNumber() {
        return sequenceNumber;
    }

    public final SectionVersion getSectionVersion() {
        return sectionVersion;
    }

    public final ArrayList<Measure> getMeasures() {
        return measures;
    }

    public final int getTotalMeasures() {
        return (repeats <= 1 ? 1 : repeats) * measures.size();
    }

    private final int sequenceNumber;
    private final SectionVersion sectionVersion;
    private final ArrayList<Measure> measures;
    private final int repeats;

}
