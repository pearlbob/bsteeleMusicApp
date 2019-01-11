package com.bsteele.bsteeleMusicApp.client.songs;

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
    }

    public final SectionVersion getSectionVersion()
    {
        return sectionVersion;
    }

    public final void setSectionVersion(SectionVersion sectionVersion)
    {
        this.sectionVersion = sectionVersion;
    }

    public int getParseLength()
    {
        return parseLength;
    }

    public int getTotalMeasures()
    {
        return (measures != null ? measures.size() : 0);
    }

    public ArrayList<MeasureNode> getMeasureNodes()
    {
        return null;
    }

    public ArrayList<Measure> getMeasures()
    {
        return measures;
    }

    public boolean isSingleItem()
    {
        return true;
    }

    public boolean isRepeat()
    {
        return false;
    }

    public abstract String generateHtml(@Nonnull SongMoment songMoment, @Nonnull Key key, int tran);

    public abstract ArrayList<String> generateInnerHtml(@Nonnull Key key, int tran);

    public abstract boolean equals(Object o);

    public abstract int hashCode();

    private SectionVersion sectionVersion;
    protected transient int parseLength;
    protected transient ArrayList<Measure> measures;
    protected static final int measuresPerLine = 4;
}
