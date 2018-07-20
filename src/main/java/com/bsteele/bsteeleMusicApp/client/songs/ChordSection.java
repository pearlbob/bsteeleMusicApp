package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.Grid;
import com.bsteele.bsteeleMusicApp.client.util.CssConstants;
import com.bsteele.bsteeleMusicApp.shared.Util;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ChordSection extends MeasureNode
{
    public ChordSection(SectionVersion sectionVersion, ArrayList<MeasureSequenceItem> measureSequenceItems)
    {
        this.sectionVersion = sectionVersion;
        this.measureSequenceItems = measureSequenceItems;
    }

    public final static ChordSection parse(String s, int beatsPerBar)
    {
        if (s == null || s.length() <= 0)
            return null;

        Util util = new Util();
        String ms;
        if ((ms = util.stripLeadingWhitespace(s)) == null)
            return null;
        int n = util.getLeadingWhitespaceCount();

        SectionVersion sectionVersion = Section.parse(ms);
        if (sectionVersion == null) {
            //  cope with badly formatted songs
            sectionVersion = new SectionVersion(Section.verse);
        }
        n += sectionVersion.getParseLength();

        ArrayList<MeasureSequenceItem> measureSequenceItems = new ArrayList<>();
        ArrayList<Measure> measures = new ArrayList<>();
        ArrayList<Measure> lineMeasures = new ArrayList<>();
        boolean repeatMarker = false;
        Measure lastMeasure = null;
        for (int i = 0; i < 2000; i++)          //  arbitrary safety hard limit
        {
            ms = s.substring(n);
            if ((ms = util.stripLeadingWhitespace(ms)) == null)
                break;
            n += util.getLeadingWhitespaceCount();

            //  quit if next section found
            if (Section.parse(ms) != null)
                break;

            //  look for a repeat marker
            if (ms.charAt(0) == '|') {
                if (!measures.isEmpty()) {
                    //  add measures prior to the repeat to the output
                    measureSequenceItems.add(new MeasureSequenceItem(measures));
                    measures = new ArrayList<>();
                }
                repeatMarker = true;
                n++;
                util.clear();
                continue;
            }

            //  look for a repeat end
            if (ms.charAt(0) == 'x') {
                repeatMarker = false;
                n++;
                ms = s.substring(n);
                //  look for repeat count
                if ((ms = util.stripLeadingWhitespace(ms)) == null)
                    break;
                n += util.getLeadingWhitespaceCount();

                final RegExp oneOrMoreDigitsRegexp = RegExp.compile("^(\\d+)");
                MatchResult mr = oneOrMoreDigitsRegexp.exec(ms);
                if (mr != null) {
                    if (!measures.isEmpty()) {
                        //  add measures prior to the single line repeat to the output
                        measureSequenceItems.add(new MeasureSequenceItem(measures));
                        measures = new ArrayList<>();
                    }
                    String ns = mr.getGroup(1);
                    n += ns.length();
                    int repeatTotal = Integer.parseInt(ns);
                    measureSequenceItems.add(new MeasureRepeat(lineMeasures, repeatTotal));
                    lineMeasures = new ArrayList<>();
                }
                util.clear();
                continue;
            }

            if (util.wasNewline() && !repeatMarker) {
                //  add line of measures to output collection
                for (Measure m : lineMeasures)
                    measures.add(m);
                lineMeasures = new ArrayList<>();
                util.clear();
            }

            //  add a measure to the current line measures
            Measure measure = Measure.parse(ms, beatsPerBar, lastMeasure);
            if (measure != null) {
                n += measure.getParseLength();
                lineMeasures.add(measure);
                lastMeasure = measure;
            } else {
                //  look for a comment
                MeasureComment measureComment = MeasureComment.parse(ms);
                if (measureComment != null) {
                    n += measureComment.getParseLength();
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
        ret.parseLength = n;
        return ret;
    }


    @Override
    public void addToGrid(@Nonnull Grid<MeasureNode> grid, @Nonnull ChordSection chordSection)
    {
        logger.finest("ChordSection.addToGrid()");

        for (MeasureSequenceItem measureSequenceItem : measureSequenceItems) {
            grid.addTo(0, grid.getRowCount(), this);
            measureSequenceItem.addToGrid(grid, this);
        }
        if (grid.isEmpty())
            //  initial editing
            grid.addTo(0, grid.getRowCount(), this);
    }

    @Override
    public ArrayList<String> generateInnerHtml(Key key, int tran, boolean expandRepeats)
    {
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
        }

        return ret;
    }

    public int getTotalMoments()
    {
        int total = 0;
        for (MeasureSequenceItem measureSequenceItem : measureSequenceItems) {
            total += measureSequenceItem.getTotalMoments();
        }
        return total;
    }

    /**
     * Set the sectionVersion beats per minute.
     *
     * @param bpm the defaultBpm to set
     */
    public final void setBeatsPerMinute(int bpm)
    {
        this.bpm = bpm;
    }

    /**
     * Return the sectionVersion beats per minute
     * or null to default to the song BPM.
     *
     * @return the sectionVersion BPM or null
     */
    public final Integer getBeatsPerMinute()
    {
        return bpm;
    }

    /**
     * Set the sections's number of beats per bar
     *
     * @param beatsPerBar the beats per bar to set
     */
    private final void setBeatsPerBar(int beatsPerBar)
    {
        this.beatsPerBar = beatsPerBar;
    }

    /**
     * Return the sections's number of beats per bar or null to default to the song's number of beats per bar
     *
     * @return the number of beats per bar
     */
    public final Integer getBeatsPerBar()
    {
        return beatsPerBar;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChordSection that = (ChordSection) o;
        return Objects.equals(sectionVersion, that.sectionVersion)
                && Objects.equals(measureSequenceItems, that.measureSequenceItems)
                && Objects.equals(bpm, that.bpm)
                && Objects.equals(beatsPerBar, that.beatsPerBar);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(sectionVersion.hashCode(), measureSequenceItems.hashCode(), bpm, beatsPerBar);
    }

    @Override
    public String toText(){
       return getSectionVersion().toString();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getSectionVersion().toString()).append(super.toString());
        return sb.toString();
    }

    public final SectionVersion getSectionVersion()
    {
        return sectionVersion;
    }

    public ArrayList<MeasureSequenceItem> getMeasureSequenceItems()
    {
        return measureSequenceItems;
    }

    private SectionVersion sectionVersion;
    private ArrayList<MeasureSequenceItem> measureSequenceItems;
    private Integer bpm;
    private Integer beatsPerBar;
    private static final Logger logger = Logger.getLogger(SectionVersion.class.getName());

}
