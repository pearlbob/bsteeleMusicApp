package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.Grid;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * Base class for measure node trees.
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public abstract class MeasureNode
{
    public int getParseLength()
    {
        return parseLength;
    }

    boolean isSingleItem()
    {
        return true;
    }

    boolean isRepeat()
    {
        return false;
    }

    public abstract ArrayList<String> generateInnerHtml(@Nonnull Key key, int tran, boolean expandRepeats);

    public abstract void addToGrid(@Nonnull Grid<MeasureNode> grid, @Nonnull ChordSection chordSection);

    public String transpose(@Nonnull Key key, int halfSteps) { return toString(); }     //  default only

    public String toText() { return toString(); }     //  default only

    public abstract boolean equals(Object o);

    public abstract int hashCode();

    public String getHtmlBlockId() { return "C"; }



    protected transient int parseLength;
}
