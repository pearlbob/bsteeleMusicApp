package com.bsteele.bsteeleMusicApp.client.songs;

import java.util.ArrayList;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

/**
 * A measure in a section of a song.
 * Holds the lyrics, the chord changes and their beats.
 * <p>
 *     When added, chord beat durations exceeding the measure beat count will be ignored on playback.
 * </p>
 */
public class Measure {

    /**
     * A convenience constructor to build a typical measure.
     * @param lyrics the lyrics to be sung over this measure
     * @param beatCount the beat count for the measure
     * @param chords the chords to be played over this measure
     */
    public Measure( String lyrics, int beatCount, ArrayList<Chord> chords){
        setLyrics(lyrics);
        setBeatCount(beatCount);
        setChords(chords);
    }

    /**
     * The lyrics to be sung over this measure.
     * @return the lyrics
     */
    public String getLyrics() {
        return lyrics;
    }

    /**
     * The lyrics to be sung over this measure.
     * @param lyrics the lyrics
     */
    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    /**
     * The beat count for the measure should be set prior to chord additions
     * to avoid awkward behavior when chords are added without a count.
     * Defaults to 4.
     * @return  the beat count for the measure
     */
    public int getBeatCount() {
        return beatCount;
    }

    /**
     * The beat count for this measure.
     * @param beatCount
     */
    public void setBeatCount(int beatCount) {
        this.beatCount = beatCount;
    }

    /**
     * The chords to be played over this measure.
     * @return the chords
     */
    public ArrayList<Chord> getChords() {
        return chords;
    }

    /**
     * The chords to be played over this measure
     * @param chords the chords
     */
    public void setChords(ArrayList<Chord> chords) {
        this.chords = chords;
    }

    private String lyrics;
    private int beatCount = 4;  //  default only
    private ArrayList< Chord> chords = new ArrayList<>();
}
