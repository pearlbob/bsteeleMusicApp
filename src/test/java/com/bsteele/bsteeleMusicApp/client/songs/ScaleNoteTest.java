package com.bsteele.bsteeleMusicApp.client.songs;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ScaleNoteTest {

    @Test
    public void parseMarkupString() {
        assertEquals(ScaleNote.Fs, ScaleNote.parseMarkupString("F#sus7"));
        assertEquals(ScaleNote.As, ScaleNote.parseMarkupString("A#"));
        assertFalse(ScaleNote.A.equals(ScaleNote.parseMarkupString("a")));
        assertEquals(ScaleNote.A, ScaleNote.parseMarkupString("A"));
        assertEquals(ScaleNote.A, ScaleNote.parseMarkupString("A7"));
        assertEquals(ScaleNote.Bb, ScaleNote.parseMarkupString("Bb"));
        assertEquals(ScaleNote.Es, ScaleNote.parseMarkupString("E#sus7"));
        assertEquals(ScaleNote.Ab, ScaleNote.parseMarkupString("Ab"));
        assertEquals(ScaleNote.Gb, ScaleNote.parseMarkupString("Gb"));
    }
}