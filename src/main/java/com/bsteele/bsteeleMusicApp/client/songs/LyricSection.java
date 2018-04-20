package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.legacy.LegacyDrumSection;

import java.util.ArrayList;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

/**
 * A sectionVersion of a song that carries the lyrics, any special drum sectionVersion,
 * and the chord changes on a measure basis
 * with ultimately beat resolution.
 */
public class LyricSection {

    /**
     * Get the lyric sectionVersion's identifier
     *
     * @return the identifier
     */
    public final SectionVersion getSectionVersion() {
        return sectionVersion;
    }

    /**
     * Get the lyric sectionVersion's identifier
     *
     * @param sectionVersion the identifier
     */
    public final void setSectionVersion(SectionVersion sectionVersion) {
        this.sectionVersion = sectionVersion;
    }


    /**
     * The sectionVersion's measures.
     *
     * @return the sectionVersion's measures
     */
    public final ArrayList<LyricsLine> getLyricsLines() {
        return lyricsLines;
    }

    public final void setLyricsLines(ArrayList<LyricsLine> lyricsLines) {
        this.lyricsLines = lyricsLines;
    }

    public final void add(LyricsLine lyricsLine) {
        lyricsLines.add(lyricsLine);
    }


    /**
     * Get the song's default drum sectionVersion.
     * The sectionVersion will be played through all of its measures
     * and then repeated as required for the sectionVersion's duration.
     * When done, the drums will default back to the song's default drum sectionVersion.
     * @return the drum sectionVersion
     */
    public final LegacyDrumSection getDrumSection() {
        return drumSection;
    }

    /**
     * Set the song's default drum sectionVersion
     * @param drumSection the drum sectionVersion
     */
    public final void setDrumSection(LegacyDrumSection drumSection) {
        this.drumSection = drumSection;
    }

    private SectionVersion sectionVersion;
    private LegacyDrumSection drumSection = new LegacyDrumSection();
    private ArrayList<LyricsLine> lyricsLines = new ArrayList<>();
}
