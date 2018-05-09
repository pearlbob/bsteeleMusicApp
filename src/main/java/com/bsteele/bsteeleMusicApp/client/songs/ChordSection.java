package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.shared.Util;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import java.util.ArrayList;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ChordSection {
    public ChordSection(SectionVersion section, ArrayList<MeasureSequenceItem> measureSequenceItems) {
        this.section = section;
        this.measureSequenceItems = measureSequenceItems;
    }

    public static ChordSection parse(String s, int beatsPerBar) {
        if (s == null || s.length() <= 0)
            return null;

        Util util = new Util();
        if ((s = util.stripLeadingWhitespace(s)) == null)
            return null;
        int n = util.getLeadingWhitespaceCount();

        SectionVersion section = Section.parse(s);
        if (section == null)
            return null;

        n += section.getParseLength();
        ArrayList<MeasureSequenceItem> measureSequenceItems = new ArrayList<>();
        ArrayList<Measure> measures = new ArrayList<>();
        ArrayList<Measure> lineMeasures = new ArrayList<>();
        boolean repeatMarker = false;
        int sequenceNumber = 0;
        for (int i = 0; i < 2000; i++)          //  arbitrary safety hard limit
        {
            String ms = s.substring(n);
            if ((ms = util.stripLeadingWhitespace(ms)) == null)
                break;
            n += util.getLeadingWhitespaceCount();

            //  look for a repeat marker
            if (ms.charAt(0) == '|') {
                if (!measures.isEmpty()) {
                    //  add measures prior to the repeat to the output
                    measureSequenceItems.add(new MeasureSequenceItem(section, measures, sequenceNumber, 0));
                    sequenceNumber += measures.size();
                    measures = new ArrayList<>();
                }
                repeatMarker = true;
                n++;
                util.clear();
                continue;
            }

            //  look for a repeat end
            if (ms.charAt(0) == 'x' || ms.charAt(0) == 'X') {
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
                        measureSequenceItems.add(new MeasureSequenceItem(section, measures, sequenceNumber, 0));
                        sequenceNumber += measures.size();
                        measures = new ArrayList<>();
                    }
                    String ns = mr.getGroup(1);
                    n += ns.length();
                    int repeatTotal = Integer.parseInt(ns);
                    measureSequenceItems.add(new MeasureSequenceItem(section, lineMeasures, sequenceNumber, repeatTotal));
                    sequenceNumber += lineMeasures.size() * repeatTotal;
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
            Measure measure = Measure.parse(ms, beatsPerBar);
            if (measure == null) {
                //  fixme: look for a comment in parenthesis

                //  fixme: treat the rest of the line as a comment/error

                break;      //  fixme: not a measure, we're done
            }

            n += measure.getParseLength();
            lineMeasures.add(measure);
        }

        //  don't assume every line has an eol
        for (Measure m : lineMeasures)
            measures.add(m);
        if (!measures.isEmpty()) {
            measureSequenceItems.add(new MeasureSequenceItem(section, measures, sequenceNumber, 0));
        }

        ChordSection ret = new ChordSection(section, measureSequenceItems);
        ret.parseLength = n;
        return ret;
    }

    public SectionVersion getSection() {
        return section;
    }

    public void setSection(SectionVersion section) {
        this.section = section;
    }

    /**
     * Set the section beats per minute.
     *
     * @param bpm the defaultBpm to set
     */
    public void setBeatsPerMinute(int bpm) {
        this.bpm = bpm;
    }

    /**
     * Return the section beats per minute
     * or null to default to the song BPM.
     *
     * @return the section BPM or null
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

    public int getParseLength() {
        return parseLength;
    }

    public ArrayList<MeasureSequenceItem> getMeasureSequenceItems() {
        return measureSequenceItems;
    }

    public void setMeasureSequenceItems(ArrayList<MeasureSequenceItem> measureSequenceItems) {
        this.measureSequenceItems = measureSequenceItems;
    }

    public int getTotalMeasures() {
        int ret = 0;

        for (MeasureSequenceItem measureSequenceItem : measureSequenceItems)
            ret += measureSequenceItem.getTotalMeasures();
        return ret;

    }

    private SectionVersion section;
    private Integer bpm;
    private Integer beatsPerBar;
    private ArrayList<MeasureSequenceItem> measureSequenceItems;
    private transient int parseLength;


}
