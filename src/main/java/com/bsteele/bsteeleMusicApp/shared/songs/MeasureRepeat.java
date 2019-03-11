package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.Grid;
import com.bsteele.bsteeleMusicApp.shared.util.Util;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureRepeat extends Phrase {

    MeasureRepeat(@Nonnull ArrayList<Measure> measures, int phraseIndex, int repeats) {
        super(measures, phraseIndex);
        this.repeatMarker = new MeasureRepeatMarker(repeats);
    }

    static final MeasureRepeat parse(String s, int phraseIndex, int beatsPerBar) {
        return parse(new StringBuffer(s), phraseIndex, beatsPerBar);
    }

    public static final MeasureRepeat parse(StringBuffer sb, int phraseIndex, int beatsPerBar) {
        if (sb == null || sb.length() < 1)
            return null;

        ArrayList<Measure> measures = new ArrayList<>();

        Util.stripLeadingWhitespace(sb);

        StringBuffer lookaheadSb = new StringBuffer(sb);//  fixme: limit size?
        boolean hasBracket = lookaheadSb.charAt(0) == '[';

        if (hasBracket)
            lookaheadSb.delete(0, 1);


        //  look for a set of measures and comments
        for (int i = 0; i < 1e4; i++) {   //  safety
            Util.stripLeadingWhitespace(lookaheadSb);
            if (lookaheadSb.length() == 0)
                return null;

            Measure measure = Measure.parse(lookaheadSb, beatsPerBar);
            if (measure != null) {
                measures.add(measure);
                continue;
            }
            if (lookaheadSb.charAt(0) != ']' && lookaheadSb.charAt(0) != 'x') {
                MeasureComment measureComment = MeasureComment.parse(lookaheadSb);
                if (measureComment != null) {
                    measures.add(measureComment);
                    continue;
                }
            }
            break;
        }

        final RegExp repeatExp = RegExp.compile("^" + (hasBracket ? "\\s*]" : "") + "\\s*x\\s*(\\d+)\\s*");
        MatchResult mr = repeatExp.exec(lookaheadSb.toString());
        if (mr != null) {
            int repeats = Integer.parseInt(mr.getGroup(1));
            MeasureRepeat ret = new MeasureRepeat(measures, phraseIndex, repeats);
            logger.finer("new measure repeat: " + ret.toMarkup());
            sb.delete(0, sb.length() - lookaheadSb.length() + mr.getGroup(0).length());
            return ret;
        }


        return null;
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

    @Override
    public void addToGrid(@Nonnull Grid<MeasureNode> grid, @Nonnull ChordSection chordSection) {
        //  fixme: improve repeats.addToGrid()
        for (MeasureNode measureNode : measures) {
            if (grid.lastRowSize() >= MusicConstant.measuresPerDisplayRow + 1) {
                if (measures.size() > MusicConstant.measuresPerDisplayRow) {
                    grid.add(new MeasureRepeatExtension());
                }
                grid.addTo(0, grid.getRowCount(), chordSection);
            }
            measureNode.addToGrid(grid, chordSection);
        }

        for (int measureCount = measures.size(); measureCount % MusicConstant.measuresPerDisplayRow != 0; measureCount++)
            grid.add(new MeasureComment());
        if (measures.size() > MusicConstant.measuresPerDisplayRow) {
            grid.add(new MeasureRepeatExtension());
        }
        grid.add(getRepeatMarker());
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
        return toMarkup();
    }

    private MeasureRepeatMarker repeatMarker;


    private static final Logger logger = Logger.getLogger(MeasureRepeat.class.getName());
}
