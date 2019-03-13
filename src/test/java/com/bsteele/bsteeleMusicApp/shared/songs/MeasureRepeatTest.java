package com.bsteele.bsteeleMusicApp.shared.songs;

import org.junit.Test;

import java.util.logging.Logger;

import static org.junit.Assert.*;

public class MeasureRepeatTest {

    @Test
    public void parseMarkup() {
        MeasureRepeat measureRepeat;

        //  bad input means this is not a repeat
        measureRepeat = MeasureRepeat.parse("[A B C D [ x2 E F", 0, 4);
        assertNull(measureRepeat);

        String s = "[A B C D ] x2 ";
        StringBuffer sb = new StringBuffer(s);
        MeasureRepeat refRepeat = MeasureRepeat.parse(sb, 0, 4);
        assertNotNull(refRepeat);
        assertEquals(s, refRepeat.toMarkup());
        assertEquals(0, sb.length());

        s = "[A B C D ] x2 E F";
        measureRepeat = MeasureRepeat.parse(s, 0, 4);
        assertNotNull(measureRepeat);
        assertTrue(s.startsWith(measureRepeat.toMarkup()));
        assertEquals(refRepeat, measureRepeat);

        s = "   [   A B   C D \n]\nx2 Eb Fmaj7";
        measureRepeat = MeasureRepeat.parse(s, 0, 4);
        assertNotNull(measureRepeat);
        assertEquals(refRepeat, measureRepeat);

        //  test without brackets
        measureRepeat = MeasureRepeat.parse("   A B C D  x2 E F", 0, 4);
        assertNotNull(measureRepeat);
        assertEquals(refRepeat, measureRepeat);

        //  test with comment
        refRepeat = MeasureRepeat.parse("   A B(yo)C D  x2 E F", 0, 4);
        assertNotNull(refRepeat);
        assertEquals("[A B (yo) C D ] x2 ", refRepeat.toMarkup());

        measureRepeat = MeasureRepeat.parse("   A B yo\nC D  x2 E F", 0, 4);
        assertNotNull(measureRepeat);
        assertEquals("[A B (yo) C D ] x2 ", measureRepeat.toMarkup());
        assertEquals(refRepeat, measureRepeat);

        measureRepeat = MeasureRepeat.parse(" [   A B yo\nC D]x2 E F", 0, 4);
        assertNotNull(measureRepeat);
        assertEquals("[A B (yo) C D ] x2 ", measureRepeat.toMarkup());
        assertEquals(refRepeat, measureRepeat);
    }

    @Test
    public void testMultilineInput() {
        MeasureRepeat measureRepeat;

        ChordSection chordSection = ChordSection.parse(
                "v3: A B C D | \n E F G G#|x2\n"
                , 4);
        assertNotNull(chordSection);
        Phrase phrase = chordSection.getPhrase(0);
        assertTrue(phrase instanceof MeasureRepeat);
        measureRepeat = (MeasureRepeat) phrase;
        logger.fine(measureRepeat.toMarkup());
        ChordSectionLocation loc = new ChordSectionLocation(chordSection, 0);
        logger.fine(loc.toString());
        assertEquals( "V3:0", loc.toString());
    }

    private static Logger logger = Logger.getLogger(MeasureRepeatTest.class.getName());
}