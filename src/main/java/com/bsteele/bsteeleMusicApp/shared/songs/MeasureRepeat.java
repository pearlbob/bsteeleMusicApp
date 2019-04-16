package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.Grid;
import com.bsteele.bsteeleMusicApp.shared.util.MarkedString;
import com.bsteele.bsteeleMusicApp.shared.util.Util;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

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
public class MeasureRepeat extends Phrase {

    MeasureRepeat(@Nonnull List<Measure> measures, int phraseIndex, int repeats) {
        super(measures, phraseIndex);
        this.repeatMarker = new MeasureRepeatMarker(repeats);
    }

    static final MeasureRepeat parse(String s, int phraseIndex, int beatsPerBar)
            throws ParseException {
        return parse(new MarkedString(s), phraseIndex, beatsPerBar);
    }

    static final MeasureRepeat parse(MarkedString markedString, int phraseIndex, int beatsPerBar)
            throws ParseException {
        if (markedString == null || markedString.isEmpty())
            throw new ParseException("no data to parse", 0);

        int initialMark = markedString.mark();

        ArrayList<Measure> measures = new ArrayList<>();

        Util.stripLeadingSpaces(markedString);

        boolean hasBracket = markedString.charAt(0) == '[';
        if (hasBracket)
            markedString.consume(1);


        //  look for a set of measures and comments
        boolean barFound = false;
        for (int i = 0; i < 1e3; i++) {   //  safety
            Util.stripLeadingSpaces(markedString);
            if (markedString.isEmpty()) {
                markedString.resetToMark(initialMark);
                throw new ParseException("no data to parse", 0);
            }

            //  extend the search for a repeat only if the line ends with a |
            if (markedString.charAt(0) == '|') {
                barFound = true;
                markedString.consume(1);
                continue;
            }
            if (markedString.charAt(0) == '\n') {
                markedString.consume(1);
                if (barFound) {
                    barFound = false;
                    continue;
                }
                markedString.resetToMark(initialMark);
                throw new ParseException("repeat not found", 0);
            }

            int mark = markedString.mark();
            try {
                Measure measure = Measure.parse(markedString, beatsPerBar);
                measures.add(measure);
                barFound = false;
                continue;
            } catch (ParseException pex) {
                markedString.resetToMark(mark);
            }

            if (markedString.charAt(0) != ']' && markedString.charAt(0) != 'x') {
                MeasureComment measureComment = MeasureComment.parse(markedString);
                if (measureComment != null) {
                    measures.add(measureComment);
                    continue;
                }
            }
            break;
        }

        final RegExp repeatExp = RegExp.compile("^" + (hasBracket ? "\\s*]" : "") + "\\s*x\\s*(\\d+)\\s*");
        MatchResult mr = repeatExp.exec(markedString.toString());
        if (mr != null) {
            int repeats = Integer.parseInt(mr.getGroup(1));
            MeasureRepeat ret = new MeasureRepeat(measures, phraseIndex, repeats);
            logger.finer("new measure repeat: " + ret.toMarkup());
            markedString.consume(mr.getGroup(0).length());
            return ret;
        }

        markedString.resetToMark(initialMark);
        throw new ParseException("repeat not found", 0);
    }

    @Override
    public int getTotalMoments() {
        return getRepeatMarker().getRepeats() * super.getTotalMoments();
    }


    public final int getRepeats() {
        return getRepeatMarker().getRepeats();
    }


    public final void setRepeats(int repeats) {
        getRepeatMarker().setRepeats(repeats);
    }

    @Override
    public MeasureNodeType getMeasureNodeType() {
        return MeasureNodeType.repeat;
    }

    @Override
    public MeasureNode findMeasureNode(MeasureNode measureNode) {
        MeasureNode ret = super.findMeasureNode(measureNode);
        if (ret != null)
            return ret;
        if (measureNode == repeatMarker)
            return repeatMarker;
        return null;
    }

    @Override
    public boolean delete(Measure measure) {
        if (measure == null)
            return false;
        if (measure == getRepeatMarker()) {
            //  fixme: improve delete repeat marker
            //  fake it
            getRepeatMarker().setRepeats(1);
            return true;
        }
        return super.delete(measure);
    }

    @Override
    public ArrayList<String> generateInnerHtml(Key key, int tran, boolean expandRepeats) {
        ArrayList<String> ret = new ArrayList<>();

        if (measures == null || measures.isEmpty())
            return ret;

        if (expandRepeats) {
            for (int r = 0; r < getRepeatMarker().getRepeats(); r++) {
                MeasureNode lastMeasureNode = null;
                MeasureNode measureNode = null;
                int i = 0;
                for (; i < measures.size(); i++) {
                    if (i > 0 && i % MusicConstant.measuresPerDisplayRow == 0) {
                        ret.add("\n");
                        lastMeasureNode = null;
                    }

                    measureNode = measures.get(i);

                    if (measureNode.isSingleItem()) {
                        if (measureNode.equals(lastMeasureNode))
                            ret.add("-");
                        else
                            ret.addAll(measureNode.generateInnerHtml(key, tran, expandRepeats));
                        lastMeasureNode = measureNode;
                    } else {
                        ret.addAll(measureNode.generateInnerHtml(key, tran, expandRepeats));
                        lastMeasureNode = null;
                    }
                    if (i % MusicConstant.measuresPerDisplayRow == MusicConstant.measuresPerDisplayRow - 1 && i < measures
                            .size() - 1) {
                        // ret.add("|");
                        ret.add("\n");
                    }
                }
                while (i % MusicConstant.measuresPerDisplayRow != 0) {
                    ret.add("");
                    i++;
                }
                ret.add("\n");
            }
        } else {
            MeasureNode lastMeasureNode = null;
            MeasureNode measureNode;
            int i = 0;
            for (; i < measures.size(); i++) {
                if (i > 0 && i % MusicConstant.measuresPerDisplayRow == 0) {
                    ret.add("\n");
                    lastMeasureNode = null;
                }

                measureNode = measures.get(i);

                if (measureNode.isSingleItem()) {
                    if (measureNode.equals(lastMeasureNode))
                        ret.add("-");
                    else
                        ret.addAll(measureNode.generateInnerHtml(key, tran, expandRepeats));
                    lastMeasureNode = measureNode;
                } else {
                    ret.addAll(measureNode.generateInnerHtml(key, tran, expandRepeats));
                    lastMeasureNode = null;
                }
                if (i % MusicConstant.measuresPerDisplayRow == MusicConstant.measuresPerDisplayRow - 1
                        && i < measures.size() - 1) {
                    ret.add("|");
                    ret.add("\n");
                }
            }
            while (i % MusicConstant.measuresPerDisplayRow != 0) {
                ret.add("");
                i++;
            }
            if (measures.size() > MusicConstant.measuresPerDisplayRow)
                ret.add("|");
            ret.add("x" + getRepeatMarker().getRepeats());
            ret.add("\n");
        }

        return ret;
    }

    public final MeasureRepeatMarker getRepeatMarker() {
        return repeatMarker;
    }

    @Override
    public boolean isSingleItem() {
        return false;
    }

    @Override
    public boolean isRepeat() {
        return true;
    }

    @Override
    public String transpose(@Nonnull Key key, int halfSteps) {
        return "x" + getRepeats();
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
    public int compareTo(MeasureRepeat o) {
        int ret = super.compareTo(o);
        if (ret != 0)
            return ret;
        ret = repeatMarker.compareTo(o.repeatMarker);
        if (ret != 0)
            return ret;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeasureRepeat other = (MeasureRepeat) o;
        return getRepeats() == other.getRepeats() && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getRepeats());
    }

    @Override
    public String toMarkup() {
        return "[" + super.toMarkup() + "] x" + getRepeats() + " ";
    }

    @Override
    public String toString() {
        return super.toMarkup()+ " x" + getRepeats() + "\n";
    }

    private MeasureRepeatMarker repeatMarker;


    private static final Logger logger = Logger.getLogger(MeasureRepeat.class.getName());
}
