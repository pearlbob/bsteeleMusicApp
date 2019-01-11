package com.bsteele.bsteeleMusicApp.client.songs;

import javax.annotation.Nonnull;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureRepeatExtension extends MeasureComment
{
    public String getHtmlBlockId() { return  "RE"; }

    @Override
    public String transpose(@Nonnull Key key, int halfSteps)
    {
        return toString();
    }
    
    @Override
    public String toString()
    {
        return "|";
    }
}
