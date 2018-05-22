package com.bsteele.bsteeleMusicApp.client.songs;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureRepeat extends MeasureSequenceItem {

    MeasureRepeat(@Nonnull SectionVersion sectionVersion, @Nonnull ArrayList<MeasureNode> measureNodes, int repeats) {
        super(sectionVersion, measureNodes );
        this.repeats = repeats;
    }

    @Override
    public int getTotalMeasures() {
        return repeats * super.getTotalMeasures();
    }


    public int getRepeats() {
        return repeats;
    }


    public void setRepeats(int repeats) {
        this.repeats = repeats;
    }

    @Override
    public String toString() {
        return super.toString() + "x"+repeats+" ";
    }

    private int repeats;
}
