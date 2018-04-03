package com.bsteele.bsteeleMusicApp.client.songs;

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

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public ArrayList<Bar> getBars() {
        return bars;
    }

    public void setBars(ArrayList<Bar> bars) {
        this.bars = bars;
    }

    public PartSectionEarlyEnding getPartSectionEarlyEnding() {
        return partSectionEarlyEnding;
    }

    public void setPartSectionEarlyEnding(PartSectionEarlyEnding partSectionEarlyEnding) {
        this.partSectionEarlyEnding = partSectionEarlyEnding;
    }

    private PartSectionEarlyEnding partSectionEarlyEnding;
    private Section section;
    private ArrayList<Bar> bars;

}
