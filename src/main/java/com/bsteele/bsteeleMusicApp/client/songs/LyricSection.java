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
     * The section's measures.
     *
     * @return the section's measures
     */
    public ArrayList<LyricsLine> getLyricsLines() {
        return lyricsLines;
    }

    public void setLyricsLines(ArrayList<LyricsLine> lyricsLines) {
        this.lyricsLines = lyricsLines;
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
    private LegacyDrumSection drumSection = new LegacyDrumSection();
    private ArrayList<LyricsLine> lyricsLines = new ArrayList<>();
}
