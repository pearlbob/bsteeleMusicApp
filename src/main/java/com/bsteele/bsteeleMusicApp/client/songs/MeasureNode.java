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
    public MeasureNode(@Nonnull SectionVersion sectionVersion)
    {
        this.sectionVersion = sectionVersion;
        sequenceItem = null;
    }

    public MeasureNode(@Nonnull SectionVersion sectionVersion, MeasureSequenceItem sequenceItem)
    {
        this.sectionVersion = sectionVersion;
        this.sequenceItem = sequenceItem;
    }

    public final SectionVersion getSectionVersion()
    {
        return sectionVersion;
    }

    MeasureSequenceItem getSequenceItem()
    {
        return sequenceItem;
    }

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

    abstract String generateHtml(@Nonnull SongMoment songMoment, @Nonnull Key key, int tran);

    public abstract ArrayList<String> generateInnerHtml(@Nonnull Key key, int tran, boolean expandRepeats);

    public abstract void addToGrid(@Nonnull Grid<MeasureNode> grid, @Nonnull ChordSection chordSection);

    public abstract boolean equals(Object o);

    public abstract int hashCode();

    private SectionVersion sectionVersion;
    protected transient int parseLength;
    private transient MeasureSequenceItem sequenceItem;
    protected transient ArrayList<Measure> measures;
    protected static final int measuresPerLine = 4;
}
