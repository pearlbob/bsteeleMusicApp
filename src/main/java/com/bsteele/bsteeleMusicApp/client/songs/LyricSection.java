package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.legacy.LegacyDrumSection;

import java.util.ArrayList;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

/**
 * A section of a song that carries the lyrics, any special drum section,
 * and the chord changes on a measure basis
 * with ultimately beat resolution.
 */
public class LyricSection {

    /**
     * Get the lyric section's identifier
     *
     * @return the identifier
     */
    public Section getSection() {
        return section;
    }

    /**
     * Get the lyric section's identifier
     *
     * @param section the identifier
     */
    public void setSection(Section section) {
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

    /**
     * The section's measures.
     *
     * @return the section's measures
     */
    public ArrayList<Measure> getMeasures() {
        return measures;
    }

    public void setMeasures(ArrayList<Measure> measures) {
        this.measures = measures;
    }


    /**
     * Get the song's default drum section.
     * The section will be played through all of its measures
     * and then repeated as required for the section's duration.
     * When done, the drums will default back to the song's default drum section.
     * @return the drum section
     */
    public LegacyDrumSection getDrumSection() {
        return drumSection;
    }

    /**
     * Set the song's default drum section
     * @param drumSection the drum section
     */
    public void setDrumSection(LegacyDrumSection drumSection) {
        this.drumSection = drumSection;
    }

    private Section section;
    private Integer bpm;
    private Integer beatsPerBar;
    private LegacyDrumSection drumSection = new LegacyDrumSection();
    private ArrayList<Measure> measures = new ArrayList<>();
}
