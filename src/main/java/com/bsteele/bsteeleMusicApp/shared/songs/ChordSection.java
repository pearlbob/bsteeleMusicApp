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
 * A chord section of a song is typically a collection of measures
 * that constitute a portion of the song that is considered musically a unit.
 * Immutable.
 * <p>
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ChordSection extends MeasureNode implements Comparable<ChordSection> {
    public ChordSection(@Nonnull SectionVersion sectionVersion, ArrayList<MeasureSequenceItem> measureSequenceItems) {
        this.sectionVersion = sectionVersion;
        this.measureSequenceItems = (measureSequenceItems != null ? measureSequenceItems : new ArrayList<>());
    }

    public ChordSection(SectionVersion sectionVersion) {
        this.sectionVersion = sectionVersion;
        this.measureSequenceItems = new ArrayList<>();
    }

    final static ChordSection testParse(String s, int beatsPerBar) {
        return parse(new StringBuffer(s), beatsPerBar);
    }

    public final static ChordSection parse(StringBuffer sb, int beatsPerBar) {
        if (sb == null || sb.length() <= 0)
            return null;

        Util.stripLeadingWhitespace(sb);    //  includes newline
        if (sb.length() <= 0)
            return null;

        SectionVersion sectionVersion = SectionVersion.parse(sb);
        if (sectionVersion == null) {
            //  cope with badly formatted songs
            sectionVersion = new SectionVersion(Section.verse);
        }

        ArrayList<MeasureSequenceItem> measureSequenceItems = new ArrayList<>();
        ArrayList<Measure> measures = new ArrayList<>();
        ArrayList<Measure> lineMeasures = new ArrayList<>();
        boolean repeatMarker = false;
        Measure lastMeasure = null;
        for (int i = 0; i < 2000; i++)          //  arbitrary safety hard limit
        {

            Util.stripLeadingSpaces(sb);
            if (sb.length() <= 0)
                break;

            //  quit if next section found
            if (Section.lookahead(sb))
                break;

            //  look for a repeat marker
            if (sb.charAt(0) == '|') {
                if (!measures.isEmpty()) {
                    //  add measures prior to the repeat to the output
                    measureSequenceItems.add(new MeasureSequenceItem(measures));
                    measures = new ArrayList<>();
                }
                repeatMarker = true;
                sb.delete(0, 1);
                continue;
            }

            //  look for a repeat end
            if (sb.charAt(0) == 'x') {
                repeatMarker = false;
                sb.delete(0, 1);

                //  look for repeat count
                Util.stripLeadingSpaces(sb);
                if (sb == null || sb.length() <= 0)
                    break;

                final RegExp oneOrMoreDigitsRegexp = RegExp.compile("^(\\d+)");
                MatchResult mr = oneOrMoreDigitsRegexp.exec(sb.toString());
                if (mr != null) {
                    if (!measures.isEmpty()) {
                        //  add measures prior to the single line repeat to the output
                        measureSequenceItems.add(new MeasureSequenceItem(measures));
                        measures = new ArrayList<>();
                    }
                    String ns = mr.getGroup(1);
                    sb.delete(0, ns.length());
                    int repeatTotal = Integer.parseInt(ns);
                    measureSequenceItems.add(new MeasureRepeat(lineMeasures, repeatTotal));
                    lineMeasures = new ArrayList<>();
                }
                continue;
            }

            //  use a newline as the default repeat marker
            if (sb.charAt(0) == '\n') {
                if (!repeatMarker) {
                    //  add line of measures to output collection
                    for (Measure m : lineMeasures)
                        measures.add(m);
                    lineMeasures = new ArrayList<>();
                }
                //  consume the newline
                sb.delete(0, 1);
                continue;
            }

            //  add a measure to the current line measures
            Measure measure = Measure.parse(sb, beatsPerBar, lastMeasure);
            if (measure != null) {
                lineMeasures.add(measure);
                lastMeasure = measure;
            } else {
                //  look for a comment
                MeasureComment measureComment = MeasureComment.parse(sb);
                if (measureComment != null) {
                    lineMeasures.add(measureComment);
                } else
                    break;      //  fixme: not a measure, we're done
            }
        }

        //  don't assume every line has an eol
        for (Measure m : lineMeasures)
            measures.add(m);
        if (!measures.isEmpty()) {
            measureSequenceItems.add(new MeasureSequenceItem(measures));
        }

        ChordSection ret = new ChordSection(sectionVersion, measureSequenceItems);
        return ret;
    }


    @Override
    public void addToGrid(@Nonnull Grid<MeasureNode> grid, @Nonnull ChordSection chordSection) {
        logger.finest("ChordSection.addToGrid()");

        if (measureSequenceItems == null || measureSequenceItems.isEmpty())
            //  initial editing
            grid.addTo(0, grid.getRowCount(), this);
        else
            for (MeasureSequenceItem measureSequenceItem : measureSequenceItems) {
                grid.addTo(0, grid.getRowCount(), this);
                measureSequenceItem.addToGrid(grid, this);
            }
    }

    @Override
    public ArrayList<String> generateInnerHtml(Key key, int tran, boolean expandRepeats) {
        ArrayList<String> ret = new ArrayList<>();

        for (MeasureSequenceItem measureSequenceItem : measureSequenceItems) {
            ArrayList<Measure> measures = measureSequenceItem.getMeasures();
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

                        if (measuresOnThisLine % MusicConstant.measuresPerDisplayRow ==
                                MusicConstant.measuresPerDisplayRow - 1) {
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
        }

        return ret;
    }

    public MeasureNode findMeasureNode(MeasureNode measureNode) {
        for (MeasureSequenceItem measureSequenceItem : getMeasureSequenceItems()) {
            if (measureSequenceItem == measureNode)
                return measureSequenceItem;
            MeasureNode mn = measureSequenceItem.findMeasureNode(measureNode);
            if (mn != null)
                return mn;
        }
        return null;
    }

    public MeasureSequenceItem findMeasureSequenceItem(MeasureNode measureNode) {
        for (MeasureSequenceItem msi : getMeasureSequenceItems()) {
            if (measureNode == msi)
                return msi;
            MeasureNode mn = msi.findMeasureNode(measureNode);
            if (mn != null)
                return msi;
        }
        return null;
    }

    public int indexOf(MeasureSequenceItem measureSequenceItem) {
        for (int i = 0; i < getMeasureSequenceItems().size(); i++) {
            MeasureSequenceItem msi = getMeasureSequenceItems().get(i);
            if (measureSequenceItem == msi)
                return i;
        }
        return -1;
    }

    public final Measure findMeasure(StringBuffer sb) {
        if (sb == null)
            return null;
        return findMeasure(Integer.parseInt(sb.toString()));
    }

    public final Measure findMeasure(int n) {
        if (n < 0)
            return null;

        Measure ret;
        for (MeasureSequenceItem msi : measureSequenceItems) {
            if ((ret = msi.findMeasure(n)) != null)
                return ret;
            n -= msi.getMeasures().size();
            if (n < 0)
                return null;
        }
        return null;
    }

    public int getTotalMoments() {
        int total = 0;
        for (MeasureSequenceItem measureSequenceItem : measureSequenceItems) {
            total += measureSequenceItem.getTotalMoments();
        }
        return total;
    }

    /**
     * Return the sectionVersion beats per minute
     * or null to default to the song BPM.
     *
     * @return the sectionVersion BPM or null
     */
    //public final Integer getBeatsPerMinute() {
    //    return bpm;
    //}

    /**
     * Return the sections's number of beats per bar or null to default to the song's number of beats per bar
     *
     * @return the number of beats per bar
     */
//    public final Integer getBeatsPerBar() {
//        return beatsPerBar;
//    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     *
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     *
     * <p>Note: this class has a natural ordering that is
     * inconsistent with equals.
     *
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(ChordSection o) {
        return sectionVersion.compareTo(o.sectionVersion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChordSection that = (ChordSection) o;
        return Objects.equals(sectionVersion, that.sectionVersion)
                && Objects.equals(measureSequenceItems, that.measureSequenceItems)
                //&& Objects.equals(bpm, that.bpm)
                //&& Objects.equals(beatsPerBar, that.beatsPerBar)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionVersion.hashCode(), measureSequenceItems.hashCode()
                //, bpm, beatsPerBar
        );
    }

    @Override
    public String getId() {
        return sectionVersion.getId();
    }

    @Override
    public String toText() {
        return getSectionVersion().toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getSectionVersion().toString())
                .append(measureSequenceItems == null ? "" : measureSequenceItems.toString());
        return sb.toString();
    }

    public final SectionVersion getSectionVersion() {
        return sectionVersion;
    }

    ArrayList<MeasureSequenceItem> getMeasureSequenceItems() {
        return measureSequenceItems;
    }

    private final SectionVersion sectionVersion;
    private ArrayList<MeasureSequenceItem> measureSequenceItems;
    //private Integer bpm;
    //private Integer beatsPerBar;
    private static final Logger logger = Logger.getLogger(SectionVersion.class.getName());

}