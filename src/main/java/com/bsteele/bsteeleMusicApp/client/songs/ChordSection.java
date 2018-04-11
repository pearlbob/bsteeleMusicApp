package com.bsteele.bsteeleMusicApp.client.songs;

import java.util.ArrayList;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ChordSection {
    public ChordSection(Section.Version section, ArrayList<Measure> measures) {
        this.section = section;
        this.measures = measures;
    }

    public static ChordSection parse(String s, int beatsPerBar) {
        if (s == null || s.length() <= 0)
            return null;

        Section.Version section = Section.parse( s );
        int n = section.getParseLength();
        ArrayList<Measure> measures = new ArrayList<>();
        Measure measure = Measure.parse( s, beatsPerBar );
        measures.add(measure);
        ChordSection ret = new ChordSection(section,measures);
        ret.parseLength = n;
        return ret;
    }

    public Section.Version getSection() {
        return section;
    }

    public void setSection(Section.Version section) {
        this.section = section;
    }

    public ArrayList<Measure> getMeasures() {
        return measures;
    }

    public void setMeasures(ArrayList<Measure> measures) {
        this.measures = measures;
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

    private Section.Version section;
    private Integer bpm;
    private Integer beatsPerBar;
    private ArrayList<Measure> measures;
    private transient int parseLength;


}
