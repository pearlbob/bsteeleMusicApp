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
        assertEquals(ScaleNote.Fs, ScaleNote.testParse("F#"));
        assertEquals(ScaleNote.F, ScaleNote.testParse("F"));
        assertEquals(ScaleNote.F, ScaleNote.testParse("F7"));
        assertEquals(ScaleNote.Fs, ScaleNote.testParse("F#sus7"));
        assertEquals(ScaleNote.As, ScaleNote.testParse("A#"));
        assertFalse(ScaleNote.A.equals(ScaleNote.testParse("a")));
        assertEquals(ScaleNote.A, ScaleNote.testParse("A"));
        assertEquals(ScaleNote.A, ScaleNote.testParse("A7"));
        assertEquals(ScaleNote.Bb, ScaleNote.testParse("Bb"));
        assertEquals(ScaleNote.Es, ScaleNote.testParse("E#sus7"));
        assertEquals(ScaleNote.Ab, ScaleNote.testParse("Ab"));
        assertEquals(ScaleNote.Gb, ScaleNote.testParse("Gb"));
    }
}