package com.bsteele.bsteeleMusicApp.client.songs;

import javax.annotation.Nonnull;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureRepeatExtension extends Measure
{
    public String getHtmlBlockId() { return  "RE"; }

    @Override
    public String toString()
    {
        return "|";
    }
}
