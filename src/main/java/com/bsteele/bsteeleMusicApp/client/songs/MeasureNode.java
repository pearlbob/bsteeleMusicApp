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
    int getParseLength()
    {
        return parseLength;
    }

    int getTotalMoments()
    {
        return (measures != null ? measures.size() : 0);
    }

    ArrayList<MeasureNode> getMeasureNodes()
    {
        return null;
    }

    ArrayList<Measure> getMeasures()
    {
        return measures;
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

    public abstract boolean equals(Object o);

    public abstract int hashCode();

    public String getHtmlBlockId() { return "C"; }

    public enum EditLocation
    {
        insert,
        replace,
        append,
        delete;
    }

    protected transient int parseLength;
    protected transient ArrayList<Measure> measures;
    protected static final int measuresPerLine = 4;
}
