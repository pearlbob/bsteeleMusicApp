package com.bsteele.bsteeleMusicApp.shared.songs;

import junit.framework.TestCase;
import org.junit.Test;

import java.text.ParseException;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ScaleNoteTest extends TestCase {

    @Test
    public void testParseMarkupString() {
        try {
            assertEquals(ScaleNote.Fs, ScaleNote.parse("F#"));
            assertEquals(ScaleNote.Fs, ScaleNote.parse("F"+MusicConstant.sharpChar));
            assertEquals(ScaleNote.F, ScaleNote.parse("F"));
            assertEquals(ScaleNote.F, ScaleNote.parse("F7"));
            assertEquals(ScaleNote.Fs, ScaleNote.parse("F#sus7"));
            assertEquals(ScaleNote.As, ScaleNote.parse("A#"));
            assertEquals(ScaleNote.A, ScaleNote.parse("A"));
            assertEquals(ScaleNote.A, ScaleNote.parse("A7"));
            assertEquals(ScaleNote.Bb, ScaleNote.parse("Bb"));
            assertEquals(ScaleNote.Es, ScaleNote.parse("E#sus7"));
            assertEquals(ScaleNote.Ab, ScaleNote.parse("Ab"));
            assertEquals(ScaleNote.Ab, ScaleNote.parse("A"+MusicConstant.flatChar));
            assertEquals(ScaleNote.Gb, ScaleNote.parse("Gb"));
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }

        try {
            assertFalse(ScaleNote.A.equals(ScaleNote.parse("a")));
            fail();
        } catch (ParseException e) {
            //  expected
        }
    }
}