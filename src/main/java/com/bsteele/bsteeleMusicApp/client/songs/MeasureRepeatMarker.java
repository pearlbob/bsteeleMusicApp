package com.bsteele.bsteeleMusicApp.client.songs;

import javax.annotation.Nonnull;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureRepeatMarker extends Measure
{
    public MeasureRepeatMarker(int repeats)
    {
        this.repeats = repeats;
    }

    public String getHtmlBlockId() { return "RX"; }

    @Override
    public String toString()
    {
        return "x" + repeats;
    }

    private int repeats;
}
