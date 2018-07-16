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

    public String transpose(@Nonnull Key key, int halfSteps) { return toString(); }     //  default only

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

    void replace(MeasureNode measureNode, Measure newMeasure)
    {
        if (measureNode == null || measures == null || measures.isEmpty())
            return;

        if (!(measureNode instanceof Measure))
            return;

        Measure oldMeasure = (Measure) measureNode;
        for (int i = 0; i < measures.size(); i++) {
            if (oldMeasure.equals(measures.get(i))) {
                ArrayList<Measure> replacementList = new ArrayList<>();
                if (i > 0)
                    replacementList.addAll(measures.subList(0, i));
                replacementList.add(newMeasure);
                if (i < measures.size() - 1)
                    replacementList.addAll(measures.subList(i + 1, measures.size()));
                measures = replacementList;
            }
        }
        //   int i = measures.indexOf(oldMeasure);

    }

    protected transient int parseLength;
    protected transient ArrayList<Measure> measures;
    protected static final int measuresPerLine = 4;
}
