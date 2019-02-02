package com.bsteele.bsteeleMusicApp.client.songs;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ScaleNoteTest extends TestCase {

    @Test
    public void testParseMarkupString() {
        assertEquals(ScaleNote.Fs, ScaleNote.parse("F#"));
        assertEquals(ScaleNote.F, ScaleNote.parse("F"));
        assertEquals(ScaleNote.F, ScaleNote.parse("F7"));
        assertEquals(ScaleNote.Fs, ScaleNote.parse("F#sus7"));
        assertEquals(ScaleNote.As, ScaleNote.parse("A#"));
        assertFalse(ScaleNote.A.equals(ScaleNote.parse("a")));
        assertEquals(ScaleNote.A, ScaleNote.parse("A"));
        assertEquals(ScaleNote.A, ScaleNote.parse("A7"));
        assertEquals(ScaleNote.Bb, ScaleNote.parse("Bb"));
        assertEquals(ScaleNote.Es, ScaleNote.parse("E#sus7"));
        assertEquals(ScaleNote.Ab, ScaleNote.parse("Ab"));
        assertEquals(ScaleNote.Gb, ScaleNote.parse("Gb"));
    }
}