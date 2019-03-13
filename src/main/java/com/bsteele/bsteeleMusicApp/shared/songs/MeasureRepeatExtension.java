package com.bsteele.bsteeleMusicApp.shared.songs;

import javax.annotation.Nonnull;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureRepeatExtension extends Measure {
    public String getHtmlBlockId() {
        return "RE";
    }

    @Override
    public String transpose(@Nonnull Key key, int halfSteps) {
        return toString();
    }

    @Override
    public String toString() {
        return "|";
    }

    private static final MeasureRepeatExtension defaultInstance = new MeasureRepeatExtension();

    static final MeasureRepeatExtension defaultInstance() {
        return defaultInstance;
    }
}
