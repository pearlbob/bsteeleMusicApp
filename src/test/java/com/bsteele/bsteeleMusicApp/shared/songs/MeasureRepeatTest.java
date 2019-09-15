package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.util.MarkedString;
import org.junit.Test;

import java.text.ParseException;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class MeasureRepeatTest {

    @Test
    public void parseMarkup() {
        MeasureRepeat measureRepeat;

        try {
            try {
                //  bad input means this is not a repeat
                measureRepeat = MeasureRepeat.parse("[A B C D [ x2 E F", 0, 4, null);
                fail();
            } catch (ParseException e) {
                //  expected
            }

            String s = "A B C D |\n E F G Gb x2 ";
            MarkedString markedString = new MarkedString(s);
            MeasureRepeat refRepeat = MeasureRepeat.parse(markedString, 0, 4, null);
            assertNotNull(refRepeat);
            assertEquals("[A B C D, E F G G♭ ] x2 ", refRepeat.toMarkup());
            assertEquals(0, markedString.available());

            s = "[A B C D ] x2 ";
            markedString = new MarkedString(s);
            refRepeat = MeasureRepeat.parse(markedString, 0, 4, null);
            assertNotNull(refRepeat);
            assertEquals(s, refRepeat.toMarkup());
            assertEquals(0, markedString.available());

            s = "[A B C D ] x2 E F";
            measureRepeat = MeasureRepeat.parse(s, 0, 4, null);
            assertNotNull(measureRepeat);
            assertTrue(s.startsWith(measureRepeat.toMarkup()));
            assertEquals(refRepeat, measureRepeat);

            s = "   [   A B   C D ]\nx2 Eb Fmaj7";
            measureRepeat = MeasureRepeat.parse(s, 0, 4, null);
            assertNotNull(measureRepeat);
            assertEquals(refRepeat, measureRepeat);

            s = "A B C D x2 Eb Fmaj7";
            measureRepeat = MeasureRepeat.parse(s, 0, 4, null);
            assertNotNull(measureRepeat);
            assertEquals(refRepeat, measureRepeat);

            //  test without brackets
            measureRepeat = MeasureRepeat.parse("   A B C D  x2 E F", 0, 4, null);
            assertNotNull(measureRepeat);
            assertEquals(refRepeat, measureRepeat);

            //  test with comment
            refRepeat = MeasureRepeat.parse("   A B(yo)C D  x2 E F", 0, 4, null);
            assertNotNull(refRepeat);
            assertEquals("[A B (yo) C D ] x2 ", refRepeat.toMarkup());

            measureRepeat = MeasureRepeat.parse("   A B (yo)|\nC D  x2 E F", 0, 4, null);
            assertNotNull(measureRepeat);
            assertEquals("[A B (yo) C D ] x2 ", measureRepeat.toMarkup());
            assertEquals(refRepeat, measureRepeat);

            measureRepeat = MeasureRepeat.parse(" [   A B (yo)|\nC D]x2 E F", 0, 4, null);
            assertNotNull(measureRepeat);
            assertEquals("[A B (yo) C D ] x2 ", measureRepeat.toMarkup());
            assertEquals(refRepeat, measureRepeat);

            measureRepeat = MeasureRepeat.parse(" [   A B (yo)   C D]x2 E F", 0, 4, null);
            assertNotNull(measureRepeat);
            assertEquals("[A B (yo) C D ] x2 ", measureRepeat.toMarkup());
            assertEquals(refRepeat, measureRepeat);
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testMultilineInput() {
        MeasureRepeat measureRepeat;

        ChordSection chordSection = null;

        try {
            chordSection = ChordSection.parse(
                    "v3: A B C D | \n E F G G# | x2   \n"
                    , 4);
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
        assertNotNull(chordSection);
        Phrase phrase = chordSection.getPhrase(0);
        assertTrue(phrase instanceof MeasureRepeat);
        measureRepeat = (MeasureRepeat) phrase;
        logger.fine(measureRepeat.toMarkup());
        ChordSectionLocation loc = new ChordSectionLocation(chordSection.getSectionVersion(), 0);
        logger.fine(loc.toString());
        assertEquals("V3:0", loc.toString());

        try {
            chordSection = ChordSection.parse(
                    "v3:A B C D|\nE F G G#|x2\n"
                    , 4);
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
        assertNotNull(chordSection);
        phrase = chordSection.getPhrase(0);
        assertTrue(phrase instanceof MeasureRepeat);
        measureRepeat = (MeasureRepeat) phrase;
        logger.fine(measureRepeat.toMarkup());
        loc = new ChordSectionLocation(chordSection.getSectionVersion(), 0);
        logger.fine(loc.toString());
        assertEquals("V3:0", loc.toString());
    }

    private static Logger logger = Logger.getLogger(MeasureRepeatTest.class.getName());
}