package com.bsteele.bsteeleMusicApp.client.songs;

import javax.annotation.Nonnull;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureRepeatMarker extends MeasureComment
{
    public MeasureRepeatMarker(int repeats)
    {
        this.repeats = repeats;
    }

    @Override
    public String transpose(@Nonnull Key key, int halfSteps)
    {
        return toString();
    }

    public String getHtmlBlockId() { return "RX"; }

    @Override
    public String toString()
    {
        return "x" + repeats;
    }

    private int repeats;
}
