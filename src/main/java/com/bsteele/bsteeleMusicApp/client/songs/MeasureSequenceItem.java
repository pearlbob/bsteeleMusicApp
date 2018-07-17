package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.Grid;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureSequenceItem extends MeasureNode
{
    public MeasureSequenceItem(@Nonnull ArrayList<Measure> measures)
    {
        this.measures = measures;
    }

    public ArrayList<Measure> getMeasures()
    {
        return measures;
    }

    public int getTotalMoments()
    {
        return measures.size();
    }

    @Override
    public ArrayList<String> generateInnerHtml(Key key, int tran, boolean expandRepeats)
    {
        ArrayList<String> ret = new ArrayList<>();

        if (measures != null && !measures.isEmpty()) {
            MeasureNode lastMeasureNode = null;
            MeasureNode measureNode = null;
            int measuresOnThisLine = 0;
            for (int i = 0; i < measures.size(); i++) {
                measureNode = measures.get(i);

                if (measureNode.isSingleItem()) {
                    if (measureNode.equals(lastMeasureNode))
                        ret.add("-");
                    else
                        ret.addAll(measureNode.generateInnerHtml(key, tran, expandRepeats));
                    lastMeasureNode = measureNode;

                    if (measuresOnThisLine % MusicConstant.measuresPerDisplayRow == MusicConstant
                            .measuresPerDisplayRow - 1) {
                        ret.add("\n");
                        lastMeasureNode = null;
                        measuresOnThisLine = 0;
                    } else
                        measuresOnThisLine++;
                } else {
                    //  a group of measures (typically a repeat)
                    ret.addAll(measureNode.generateInnerHtml(key, tran, expandRepeats));
                    lastMeasureNode = null;
                    measuresOnThisLine = 0;
                }
            }
        }
        ret.add("\n");

        return ret;
    }

    @Override
    public void addToGrid(Grid<MeasureNode> grid, @Nonnull ChordSection chordSection)
    {
        logger.finest("MeasureSequenceItem.addToGrid()");

        for (Measure measure : measures) {
            if (grid.lastRowSize() >= MusicConstant.measuresPerDisplayRow + 1)
                grid.addTo(0, grid.getRowCount(), chordSection);
            measure.addToGrid(grid, chordSection);
        }
    }

    @Override
    public String transpose(@Nonnull Key key, int halfSteps)
    {
        return "MeasureSequenceItem";   //  error
    }

    public enum EditLocation
    {
        insert,
        replace,
        append,
        delete;
    }

    void insert(MeasureNode measureNode, Measure newMeasure)
    {
        if (measures == null)
            measures = new ArrayList<>();
        if (measureNode == null || !(measureNode instanceof Measure) || measures.isEmpty()) {
            measures.add(newMeasure);
            return;
        }

        Measure oldMeasure = (Measure) measureNode;

        for (int i = 0; i < measures.size(); i++) {

            if (oldMeasure.equals(measures.get(i))) {
                measures.add(i, newMeasure);
                return;
            }
        }
        measures.add(newMeasure);
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
    }

    void append(MeasureNode measureNode, Measure newMeasure)
    {
        if (measures == null)
            measures = new ArrayList<>();
        if (measureNode == null || !(measureNode instanceof Measure) || measures.isEmpty()) {
            measures.add(newMeasure);
            return;
        }

        Measure oldMeasure = (Measure) measureNode;

        for (int i = 0; i < measures.size(); i++) {
            if (oldMeasure.equals(measures.get(i)))  {
                measures.add(i+1, newMeasure);
                return;
            }
        }
        measures.add(newMeasure);
    }


    @Override
    public boolean isSingleItem()
    {
        return false;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || !(o instanceof MeasureSequenceItem)) return false;
        MeasureSequenceItem that = (MeasureSequenceItem) o;
        return Objects.equals(measures, that.measures);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(measures);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        if (measures != null)
            for (Measure measure : measures) {
                sb.append(measure.toString()).append(" ");
            }
        sb.append("} ");
        return sb.toString();
    }


    protected transient ArrayList<Measure> measures;

    private static final Logger logger = Logger.getLogger(MeasureSequenceItem.class.getName());
}
