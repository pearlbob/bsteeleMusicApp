package com.bsteele.bsteeleMusicApp.client.songs;

import java.util.ArrayList;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

/**
 * A line of lyrics in a section of a song.
 * Holds the lyrics.
 */
public class LyricsLine {

    /**
     * A convenience constructor to build a typical lyrics line.
     * @param lyrics the lyrics to be sung over this lyrics line
     */
    public LyricsLine(String lyrics){
        setLyrics(lyrics);
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

    private String lyrics;
}
