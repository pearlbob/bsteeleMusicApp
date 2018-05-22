package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.shared.Util;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import java.util.ArrayList;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ChordSection extends MeasureSequenceItem {
    public ChordSection(SectionVersion sectionVersion, ArrayList<MeasureNode> measureNodes) {
        super(sectionVersion, measureNodes);
    }

    public static ChordSection parse(String s, int beatsPerBar) {
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

        ArrayList<MeasureNode> measureSequenceItems = new ArrayList<>();
        ArrayList<MeasureNode> measures = new ArrayList<>();
        ArrayList<MeasureNode> lineMeasures = new ArrayList<>();
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
                    measureSequenceItems.add(new MeasureSequenceItem(sectionVersion, measures));
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
                        measureSequenceItems.add(new MeasureSequenceItem(sectionVersion, measures));
                        measures = new ArrayList<>();
                    }
                    String ns = mr.getGroup(1);
                    n += ns.length();
                    int repeatTotal = Integer.parseInt(ns);
                    measureSequenceItems.add(new MeasureRepeat(sectionVersion, lineMeasures, repeatTotal));
                    lineMeasures = new ArrayList<>();
                }
                util.clear();
                continue;
            }

            if (util.wasNewline() && !repeatMarker) {
                //  add line of measures to output collection
                for (MeasureNode m : lineMeasures)
                    measures.add(m);
                lineMeasures = new ArrayList<>();
                util.clear();
            }

            //  add a measure to the current line measures
            Measure measure = Measure.parse(sectionVersion, ms, beatsPerBar, beatsPerBar, lastMeasure);
            if (measure != null) {
                n += measure.getParseLength();
                lineMeasures.add(measure);
                lastMeasure = measure;
            } else {
                //  look for a comment
                MeasureComment measureComment = MeasureComment.parse(sectionVersion, ms);
                if (measureComment != null) {
                    n += measureComment.getParseLength();
                    lineMeasures.add(measureComment);
                }

                break;      //  fixme: not a measure, we're done
            }
        }

        //  don't assume every line has an eol
        for (MeasureNode mn : lineMeasures)
            measures.add(mn);
        if (!measures.isEmpty()) {
            measureSequenceItems.add(new MeasureSequenceItem(sectionVersion, measures));
        }

        ChordSection ret = new ChordSection(sectionVersion, measureSequenceItems);
        ret.parseLength = n;
        return ret;
    }

    /**
     * Set the sectionVersion beats per minute.
     *
     * @param bpm the defaultBpm to set
     */
    public void setBeatsPerMinute(int bpm) {
        this.bpm = bpm;
    }

    /**
     * Return the sectionVersion beats per minute
     * or null to default to the song BPM.
     *
     * @return the sectionVersion BPM or null
     */
    public Integer getBeatsPerMinute() {
        return bpm;
    }

    /**
     * Set the sections's number of beats per bar
     *
     * @param beatsPerBar the beats per bar to set
     */
    private void setBeatsPerBar(int beatsPerBar) {
        this.beatsPerBar = beatsPerBar;
    }

    /**
     * Return the sections's number of beats per bar or null to default to the song's number of beats per bar
     *
     * @return the number of beats per bar
     */
    public Integer getBeatsPerBar() {
        return beatsPerBar;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getSectionVersion().toString()).append("{");

        if (measureNodes != null)
            for (MeasureNode measureNode : measureNodes) {
                sb.append(measureNode.toString()).append(" ");
            }
        sb.append("}");
        return sb.toString();
    }

    private Integer bpm;
    private Integer beatsPerBar;
}
