package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.util.MarkedString;
import com.bsteele.bsteeleMusicApp.shared.util.Util;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class Phrase extends MeasureNode {
    public Phrase(@Nonnull List<Measure> measures, int phraseIndex) {
        this.measures = (measures == null ? new ArrayList<>() : new ArrayList<>(measures));
        this.phraseIndex = phraseIndex;
    }

    public ArrayList<Measure> getMeasures() {
        return measures;
    }

    public int getTotalMoments() {
        return measures.size();
    }   //  fixme

    static Phrase parse(String string, int phraseIndex, int beatsPerBar, Measure priorMeasure)
            throws ParseException {
        return parse(new MarkedString(string), phraseIndex, beatsPerBar, priorMeasure);
    }


    static Phrase parse(MarkedString markedString, int phraseIndex, int beatsPerBar, Measure priorMeasure)
            throws ParseException {
        if (markedString == null || markedString.isEmpty())
            throw new ParseException("no data to parse", 0);

        ArrayList<Measure> measures = new ArrayList<>();

        Util.stripLeadingSpaces(markedString);

        //  look for a set of measures and comments
        int initialMark = markedString.mark();

        boolean hasBracket = markedString.charAt(0) == '[';
        if (hasBracket)
            markedString.consume(1);

        for (int i = 0; i < 1e3; i++) {   //  safety
            Util.stripLeadingSpaces(markedString);
            if (markedString.isEmpty())
                break;

            //  assure this is not a section
            if (Section.lookahead(markedString))
                break;

            try {
                priorMeasure = Measure.parse(markedString, beatsPerBar, priorMeasure);
                measures.add(priorMeasure);
                continue;
            } catch (ParseException pex) {
            }

            //  force junk into a comment
            try {
                measures.add(MeasureComment.parse(markedString));
                priorMeasure = null;
                continue;
            } catch (ParseException pex) {
            }

            //  end of bracketed phrase
            if (hasBracket && markedString.charAt(0) == ']') {
                markedString.consume(1);
            }

            break;
        }

        //  note: bracketed phrases can be empty
        if (!hasBracket && measures.isEmpty())
            throw new ParseException("no measures found in parse", 0);

        return new Phrase(measures, phraseIndex);
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
    public String transpose(@Nonnull Key key, int halfSteps) {
        return "Phrase";   //  error
    }


     MeasureNode findMeasureNode(MeasureNode measureNode) {
        for (Measure m : measures) {
            if (m == measureNode)
                return m;
        }
        return null;
    }

    final int findMeasureNodeIndex(MeasureNode measureNode) throws IndexOutOfBoundsException {
        if (measureNode == null)
            throw new IndexOutOfBoundsException("measureNode null");

        int ret = measures.indexOf(measureNode);
        if (ret < 0)
            throw new IndexOutOfBoundsException("measureNode not found: " + measureNode.toMarkup());

        return ret;
    }

    final boolean insert(int index, MeasureNode newMeasureNode) {
        if (newMeasureNode == null)
            return false;

        switch (newMeasureNode.getMeasureNodeType()) {
            case measure:
                break;
            default:
                return false;
        }

        Measure newMeasure = (Measure) newMeasureNode;

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

    final boolean replace(int index, MeasureNode newMeasureNode) {
        if (measures == null || measures.isEmpty())
            return false;

        if (newMeasureNode == null)
            return false;

        switch (newMeasureNode.getMeasureNodeType()) {
            case measure:
                break;
            default:
                return false;
        }

        Measure newMeasure = (Measure) newMeasureNode;

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

    final boolean add(ArrayList<Measure> newMeasures) {
        if (newMeasures == null || newMeasures.isEmpty())
            return false;
        if (measures == null)
            measures = new ArrayList<Measure>();
        measures.addAll(newMeasures);
        return true;
    }

    final boolean add(int index, ArrayList<Measure> newMeasures) {
        if (newMeasures == null || newMeasures.isEmpty())
            return false;
        if (measures == null)
            measures = new ArrayList<Measure>();
        index = Math.min(index, measures.size() - 1);
        measures.addAll(index, newMeasures);
        return true;
    }

    final boolean append(int index, MeasureNode newMeasureNode) {
        if (newMeasureNode == null)
            return false;

        switch (newMeasureNode.getMeasureNodeType()) {
            case measure:
                break;
            default:
                return false;
        }

        Measure newMeasure = (Measure) newMeasureNode;

        if (measures == null)
            measures = new ArrayList<>();
        if (measures.isEmpty()) {
            measures.add(newMeasure);
            return true;
        }

        try {
            measures.add(index + 1, newMeasure);
        } catch (IndexOutOfBoundsException ex) {
            measures.add(newMeasure);   //  default to the end!
        }

        return true;
    }

    final boolean edit(MeasureEditType type, int index, MeasureNode newMeasureNode) {
        if (newMeasureNode == null) {
            switch (type) {
                case delete:
                    break;
                default:
                    return false;
            }
        } else {
            //  reject wrong node type
            switch (newMeasureNode.getMeasureNodeType()) {
                case phrase:    //  multiple measures
                case comment:
                case measure:
                    break;
                //  repeats not allowed!
                default:
                    return false;
            }
        }

        //  assure measures are ready
        switch (type) {
            case replace:
            case delete:
                if (measures == null || measures.isEmpty())
                    return false;
                break;
            case insert:
            case append:
                if (measures == null)
                    measures = new ArrayList<>();

                //  index doesn't matter
                if (measures.isEmpty()) {
                    if (newMeasureNode.isSingleItem())
                        measures.add((Measure) newMeasureNode);
                    else
                        measures.addAll(((Phrase) newMeasureNode).getMeasures());
                    return true;
                }
                break;
            default:
                return false;
        }

        //  edit by type
        switch (type) {
            case delete:
                try {
                    measures.remove(index);
                    //  note: newMeasureNode is ignored
                } catch (IndexOutOfBoundsException ex) {
                    return false;
                }
                break;
            case insert:
                try {
                    if (newMeasureNode.isSingleItem())
                        measures.add(index, (Measure) newMeasureNode);
                    else
                        measures.addAll(index, ((Phrase) newMeasureNode).getMeasures());
                } catch (IndexOutOfBoundsException ex) {
                    //  default to the end!
                    if (newMeasureNode.isSingleItem())
                        measures.add((Measure) newMeasureNode);
                    else
                        measures.addAll(((Phrase) newMeasureNode).getMeasures());
                }
                break;
            case append:
                try {
                    if (newMeasureNode.isSingleItem())
                        measures.add(index + 1, (Measure) newMeasureNode);
                    else
                        measures.addAll(index + 1, ((Phrase) newMeasureNode).getMeasures());
                } catch (IndexOutOfBoundsException ex) {
                    //  default to the end!
                    if (newMeasureNode.isSingleItem())
                        measures.add((Measure) newMeasureNode);
                    else
                        measures.addAll(((Phrase) newMeasureNode).getMeasures());
                }
                break;
            case replace:
                try {
                    measures.remove(index);
                    if (newMeasureNode.isSingleItem())
                        measures.add(index, (Measure) newMeasureNode);
                    else
                        measures.addAll(index, ((Phrase) newMeasureNode).getMeasures());
                } catch (IndexOutOfBoundsException ex) {
                    //  default to the end!
                    if (newMeasureNode.isSingleItem())
                        measures.add((Measure) newMeasureNode);
                    else
                        measures.addAll(((Phrase) newMeasureNode).getMeasures());
                }
                break;
            default:
                return false;
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

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    public int compareTo(Phrase o) {
        int limit = Math.min(measures.size(), o.measures.size());
        for (int i = 0; i < limit; i++) {
            int ret = measures.get(i).compareTo(o.measures.get(i));
            if (ret != 0)
                return ret;
        }
        if (measures.size() != o.measures.size())
            return measures.size() < o.measures.size() ? -1 : 1;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phrase that = (Phrase) o;

        if ((measures == null || measures.isEmpty())
                && (that.measures == null || that.measures.isEmpty()))
            return true;
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
    public MeasureNodeType getMeasureNodeType() {
        return MeasureNodeType.phrase;
    }

    @Override
    boolean isEmpty() {
        return measures == null || measures.isEmpty();
    }

    @Override
    public String toMarkup() {
        if (measures == null || measures.isEmpty())
            return "[]";

        StringBuilder sb = new StringBuilder();
        for (Measure measure : measures) {
            sb.append(measure.toString()).append(" ");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return toMarkup() + "\n";
    }

    final int size() {
        return measures.size();
    }

    final int getPhraseIndex() {
        return phraseIndex;
    }

    final void setPhraseIndex(int phraseIndex) {
        this.phraseIndex = phraseIndex;
    }

    protected transient ArrayList<Measure> measures;
    private int phraseIndex;

    private static final Logger logger = Logger.getLogger(Phrase.class.getName());

}
