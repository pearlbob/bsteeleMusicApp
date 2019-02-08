package com.bsteele.bsteeleMusicApp.shared.songs;

import java.util.ArrayList;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class PartSection {

    public enum PartSectionEarlyEnding {
        end,
        loop;
    }

    public final Section getSection() {
        return section;
    }

    public final void setSection(Section section) {
        this.section = section;
    }

    public final ArrayList<Bar> getBars() {
        return bars;
    }

    public final void setBars(ArrayList<Bar> bars) {
        this.bars = bars;
    }

    public final PartSectionEarlyEnding getPartSectionEarlyEnding() {
        return partSectionEarlyEnding;
    }

    public final void setPartSectionEarlyEnding(PartSectionEarlyEnding partSectionEarlyEnding) {
        this.partSectionEarlyEnding = partSectionEarlyEnding;
    }

    private PartSectionEarlyEnding partSectionEarlyEnding;
    private Section section;
    private ArrayList<Bar> bars;

}
