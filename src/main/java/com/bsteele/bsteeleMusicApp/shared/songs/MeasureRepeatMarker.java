package com.bsteele.bsteeleMusicApp.shared.songs;

import javax.annotation.Nonnull;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureRepeatMarker extends Measure {
    public MeasureRepeatMarker(int repeats) {
        this.setRepeats(repeats);
    }

    @Override
    public MeasureNodeType getMeasureNodeType() {
        return MeasureNodeType.decoration;
    }

    public final int getRepeats() {
        return repeats;
    }

    public final void setRepeats(int repeats) {
        this.repeats = repeats;
    }

    @Override
    public String transpose(@Nonnull Key key, int halfSteps) {
        return toString();
    }

    public final String getHtmlBlockId() {
        return "RX";
    }

    public int compareTo(MeasureRepeatMarker o) {
        return repeats < o.repeats ? -1 : (repeats > o.repeats ? 1 : 0);
    }

    @Override
    public String toString() {
        return "x" + getRepeats();
    }

    private int repeats;

}
