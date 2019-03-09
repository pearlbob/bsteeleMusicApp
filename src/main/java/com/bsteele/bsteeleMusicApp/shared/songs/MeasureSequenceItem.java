package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.Grid;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureSequenceItem extends MeasureNode {
    public MeasureSequenceItem(@Nonnull ArrayList<Measure> measures) {
        this.measures = measures;
    }

    public ArrayList<Measure> getMeasures() {
        return measures;
    }

    public int getTotalMoments() {
        return measures.size();
    }

    @Override
    public ArrayList<String> generateInnerHtml(Key key, int tran, boolean expandRepeats) {
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
    public void addToGrid(Grid<MeasureNode> grid, @Nonnull ChordSection chordSection) {
        logger.finest("MeasureSequenceItem.addToGrid()");

        for (Measure measure : measures) {
            if (grid.lastRowSize() >= MusicConstant.measuresPerDisplayRow + 1)
                grid.addTo(0, grid.getRowCount(), chordSection);
            measure.addToGrid(grid, chordSection);
        }
    }

    @Override
    public String transpose(@Nonnull Key key, int halfSteps) {
        return "MeasureSequenceItem";   //  error
    }


    public MeasureNode findMeasureNode(MeasureNode measureNode) {
        for (Measure m : measures) {
            if (m == measureNode)
                return m;
        }
        return null;
    }

    public int findMeasureNodeIndex(MeasureNode measureNode) {
        int i = 0;
        for (Measure m : measures) {
            if (m == measureNode)
                return i;
            i++;
        }
        return -1;
    }

    public final Measure findMeasure(int n) {
        try {
            return measures.get(n);
        } catch (Exception e) {
            return null;
        }
    }

    boolean insert(MeasureNode measureNode, Measure newMeasure) {
        if (measures == null)
            measures = new ArrayList<>();
        if (measureNode == null || !(measureNode instanceof Measure) || measures.isEmpty()) {
            measures.add(newMeasure);
            return true;
        }

        Measure oldMeasure = (Measure) measureNode;

        for (int i = 0; i < measures.size(); i++) {

            if (oldMeasure == measures.get(i)) {
                measures.add(i, newMeasure);
                return true;
            }
        }
        measures.add(newMeasure);
        return true;
    }

    boolean replace(MeasureNode measureNode, Measure newMeasure) {
        if (measureNode == null || measures == null || measures.isEmpty())
            return false;

        if (!(measureNode instanceof Measure))
            return false;

        Measure oldMeasure = (Measure) measureNode;
        for (int i = 0; i < measures.size(); i++) {
            if (oldMeasure == measures.get(i)) {
                ArrayList<Measure> replacementList = new ArrayList<>();
                if (i > 0)
                    replacementList.addAll(measures.subList(0, i));
                replacementList.add(newMeasure);
                if (i < measures.size() - 1)
                    replacementList.addAll(measures.subList(i + 1, measures.size()));
                measures = replacementList;
                return true;
            }
        }
        return false;
    }

    boolean append(MeasureNode measureNode, Measure newMeasure) {
        if (measures == null)
            measures = new ArrayList<>();
        if (measureNode == null || !(measureNode instanceof Measure) || measures.isEmpty()) {
            measures.add(newMeasure);
            return true;
        }

        Measure oldMeasure = (Measure) measureNode;

        for (int i = 0; i < measures.size(); i++) {
            if (oldMeasure == measures.get(i)) {
                measures.add(i + 1, newMeasure);
                return true;
            }
        }
        measures.add(newMeasure);
        return true;
    }

    /**
     * Delete the given measure if it belongs in the sequence item.
     *
     * @param measure the measure to be deleted
     * @return true if the measure was found and deleted.
     */
    public boolean delete(Measure measure) {
        if (measures == null)
            return false;
        return measures.remove(measure);
    }

    @Override
    public boolean isSingleItem() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof MeasureSequenceItem)) return false;
        MeasureSequenceItem that = (MeasureSequenceItem) o;
        return Objects.equals(measures, that.measures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(measures);
    }

    @Override
    public String getId() {
        return null;
    }


    @Override
    public String toMarkup() {
        StringBuilder sb = new StringBuilder();
        if (measures != null)
            for (Measure measure : measures) {
                sb.append(measure.toString()).append(" ");
            }
        return sb.toString();
    }

    @Override
    public String toString() {
       return toMarkup();
    }

    public final int size() {
        return measures.size();
    }

    protected transient ArrayList<Measure> measures;

    private static final Logger logger = Logger.getLogger(MeasureSequenceItem.class.getName());
}
