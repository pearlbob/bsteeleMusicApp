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
public class Phrase extends MeasureNode {
    public Phrase(@Nonnull ArrayList<Measure> measures, int phraseIndex) {
        this.measures = measures;
        this.phraseIndex = phraseIndex;
    }

    public ArrayList<Measure> getMeasures() {
        return measures;
    }

    public int getTotalMoments() {
        return measures.size();
    }   //  fixme

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
    public String transpose(@Nonnull Key key, int halfSteps) {
        return "Phrase";   //  error
    }


    public MeasureNode findMeasureNode(MeasureNode measureNode) {
        for (Measure m : measures) {
            if (m == measureNode)
                return m;
        }
        return null;
    }

    public int findMeasureNodeIndex(MeasureNode measureNode) throws IndexOutOfBoundsException {
        if (measureNode == null)
            throw new IndexOutOfBoundsException("measureNode null");

        int ret = measures.indexOf(measureNode);
        if (ret < 0)
            throw new IndexOutOfBoundsException("measureNode not found: " + measureNode.toMarkup());

        return ret;
    }

    public final Measure findMeasure(int n) {
        try {
            return measures.get(n);
        } catch (Exception e) {
            return null;
        }
    }

    boolean insert(int index, Measure newMeasure) {
        if (measures == null)
            measures = new ArrayList<>();
        if (measures.isEmpty()) {
            measures.add(newMeasure);
            return true;
        }

        try {
            measures.add(index, newMeasure);
        } catch (IndexOutOfBoundsException ex) {
            measures.add(newMeasure);   //  default to the end!
        }
        return true;
    }

    boolean replace(int index, Measure newMeasure) {
        if (measures == null || measures.isEmpty())
            return false;

        try {
            ArrayList<Measure> replacementList = new ArrayList<>();
            if (index > 0)
                replacementList.addAll(measures.subList(0, index));
            replacementList.add(newMeasure);
            if (index < measures.size() - 1)
                replacementList.addAll(measures.subList(index + 1, measures.size()));
            measures = replacementList;
        } catch (IndexOutOfBoundsException ex) {
            measures.add(newMeasure);   //  default to the end!
        }
        return true;
    }

    boolean append(int index, Measure newMeasure) {
        if (measures == null)
            measures = new ArrayList<>();
        if ( measures.isEmpty()) {
            measures.add(newMeasure);
            return true;
        }

        try {
            measures.add(index+1, newMeasure);
        } catch (IndexOutOfBoundsException ex) {
            measures.add(newMeasure);   //  default to the end!
        }

        return true;
    }

    final boolean contains(MeasureNode measureNode) {
        return measures.contains(measureNode);
    }

    final Measure getMeasure(int measureIndex) throws NullPointerException, IndexOutOfBoundsException {
        return measures.get(measureIndex);
    }

    /**
     * Delete the given measure if it belongs in the sequence item.
     *
     * @param measure the measure to be deleted
     * @return true if the measure was found and deleted.
     */
    @Deprecated
    public boolean delete(Measure measure) {
        if (measures == null)
            return false;
        return measures.remove(measure);
    }

    final Measure delete(int measureIindex) throws IndexOutOfBoundsException {
        return measures.remove(measureIindex);
    }

    @Override
    public boolean isSingleItem() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Phrase)) return false;
        Phrase that = (Phrase) o;
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
    boolean isEmpty() {
        return measures == null || measures.isEmpty();
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

    public final int getPhraseIndex() {
        return phraseIndex;
    }

    final void setPhraseIndex(int phraseIndex) {
        this.phraseIndex = phraseIndex;
    }

    protected transient ArrayList<Measure> measures;
    private int phraseIndex;

    private static final Logger logger = Logger.getLogger(Phrase.class.getName());

}
