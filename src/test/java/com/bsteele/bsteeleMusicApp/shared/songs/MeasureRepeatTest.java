package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.Grid;
import com.bsteele.bsteeleMusicApp.shared.GridCoordinate;
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

    @Test
    public void testGridMapping() {
        int beatsPerBar = 4;
        SongBase a;

        a = SongBaseTest.createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "V: [G Bm F♯m G, GBm ] x3",
                "v: bob, bob, bob berand\n");
        a.debugSongMoments();

        try {
            ChordSection cs = ChordSection.parse("v:", a.getBeatsPerBar());
            ChordSection chordSection = a.findChordSection(cs.getSectionVersion());
            assertNotNull(chordSection);
            Phrase phrase = chordSection.getPhrase(0);
            assertTrue(phrase.isRepeat());

            MeasureRepeat measureRepeat = (MeasureRepeat) phrase;
            assertEquals(5, measureRepeat.size());

            Grid<ChordSectionLocation> grid = a.getChordSectionLocationGrid();
            MeasureNode measureNode = a.findMeasureNode(new GridCoordinate(0, 1));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("G", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(0, 2));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("Bm", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(0, 3));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("F♯m", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(0, 4));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("G", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(1, 1));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("GBm", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(1, 2));
            assertNull(measureNode);
            measureNode = a.findMeasureNode(new GridCoordinate(1, 3));
            assertNull(measureNode);
            measureNode = a.findMeasureNode(new GridCoordinate(1, 4));
            assertNull(measureNode);


            ChordSectionLocation chordSectionLocation = a.getChordSectionLocation(new GridCoordinate(1, 4+1));
            assertNotNull(chordSectionLocation);
            assertEquals(ChordSectionLocation.Marker.repeatLowerRight, chordSectionLocation.getMarker());
            chordSectionLocation = a.getChordSectionLocation(new GridCoordinate(1, 4+1+1));
            assertNotNull(chordSectionLocation);
            assertEquals(ChordSectionLocation.Marker.none, chordSectionLocation.getMarker());
            measureNode =  a.findMeasureNode(chordSectionLocation);
            assertNotNull(measureNode);
            assertTrue(measureNode.isRepeat());
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }

                a = SongBaseTest.createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "v: [A B , Ab Bb Eb, D C G G# ] x3 T: A",
                "i:\nv: bob, bob, bob berand\nt: last line \n");
        a.debugSongMoments();

        try {
            ChordSection cs = ChordSection.parse("v:", a.getBeatsPerBar());
            ChordSection chordSection = a.findChordSection(cs.getSectionVersion());
            assertNotNull(chordSection);
            Phrase phrase = chordSection.getPhrase(0);
            assertTrue(phrase.isRepeat());

            MeasureRepeat measureRepeat = (MeasureRepeat) phrase;
            assertEquals(9, measureRepeat.size());

            Grid<ChordSectionLocation> grid = a.getChordSectionLocationGrid();
            MeasureNode measureNode = a.findMeasureNode(new GridCoordinate(0, 1));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("A", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(0, 2));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("B", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(0, 3));
            assertNull(measureNode);
            measureNode = a.findMeasureNode(new GridCoordinate(0, 4));
            assertNull(measureNode);


            measureNode = a.findMeasureNode(new GridCoordinate(1, 3));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("Eb", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(1, 4));
            assertNull(measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(1, 4));
            assertNull(measureNode);


            measureNode = a.findMeasureNode(new GridCoordinate(2, 4));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("G#", a.getBeatsPerBar()), measureNode);

            ChordSectionLocation chordSectionLocation = a.getChordSectionLocation(new GridCoordinate(2, 4+1));
            assertNotNull(chordSectionLocation);
            assertEquals(ChordSectionLocation.Marker.repeatLowerRight, chordSectionLocation.getMarker());
            chordSectionLocation = a.getChordSectionLocation(new GridCoordinate(2, 4+1+1));
            assertNotNull(chordSectionLocation);
            assertEquals(ChordSectionLocation.Marker.none, chordSectionLocation.getMarker());
            measureNode =  a.findMeasureNode(chordSectionLocation);
            assertNotNull(measureNode);
            assertTrue(measureNode.isRepeat());
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }


        a = SongBaseTest.createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "v: E F F# G [A B C D Ab Bb Eb Db D C G Gb D C G# A#] x3 T: A",
                //         1 2 3  4  1 2 3 4 5  6  7  8  1 2 3 4  5 6 7  8
                //                                       9 101112 131415 16
                "i:\nv: bob, bob, bob berand\nt: last line \n");
        a.debugSongMoments();

        try {
            ChordSection cs = ChordSection.parse("v:", a.getBeatsPerBar());
            ChordSection chordSection = a.findChordSection(cs.getSectionVersion());
            assertNotNull(chordSection);
            Phrase phrase = chordSection.getPhrase(1);
            assertTrue(phrase.isRepeat());

            MeasureRepeat measureRepeat = (MeasureRepeat) phrase;
            assertEquals(16, measureRepeat.size());

            Grid<ChordSectionLocation> grid = a.getChordSectionLocationGrid();
            MeasureNode measureNode = a.findMeasureNode(new GridCoordinate(1, 1));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("A", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(2, 1));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("D", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(2, 4));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("Gb", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(2, 7));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("G#", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(2, 8));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("A#", a.getBeatsPerBar()), measureNode);

            ChordSectionLocation chordSectionLocation = a.getChordSectionLocation(new GridCoordinate(2, 8+1));
            assertNotNull(chordSectionLocation);
            assertEquals(ChordSectionLocation.Marker.repeatLowerRight, chordSectionLocation.getMarker());
            chordSectionLocation = a.getChordSectionLocation(new GridCoordinate(2, 8+1+1));
            assertNotNull(chordSectionLocation);
            assertEquals(ChordSectionLocation.Marker.none, chordSectionLocation.getMarker());
            measureNode =  a.findMeasureNode(chordSectionLocation);
            assertNotNull(measureNode);
            assertTrue(measureNode.isRepeat());
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }

        a = SongBaseTest.createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "v: A B C D x3 T: A",
                "i:\nv: bob, bob, bob berand\nt: last line \n");
        a.debugSongMoments();

        try {
            ChordSection cs = ChordSection.parse("v:", a.getBeatsPerBar());
            ChordSection chordSection = a.findChordSection(cs.getSectionVersion());
            assertNotNull(chordSection);
            Phrase phrase = chordSection.getPhrase(0);
            assertTrue(phrase.isRepeat());

            MeasureRepeat measureRepeat = (MeasureRepeat) phrase;
            assertEquals(4, measureRepeat.size());

            Grid<ChordSectionLocation> grid = a.getChordSectionLocationGrid();
            MeasureNode measureNode = a.findMeasureNode(new GridCoordinate(0, 1));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("A", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(0, 4));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("D", a.getBeatsPerBar()), measureNode);

            ChordSectionLocation   chordSectionLocation = a.getChordSectionLocation(new GridCoordinate(0, 4+1));
            assertNotNull(chordSectionLocation);
            assertEquals(ChordSectionLocation.Marker.none, chordSectionLocation.getMarker());
            measureNode =  a.findMeasureNode(chordSectionLocation);
            assertNotNull(measureNode);
            assertTrue(measureNode.isRepeat());
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }

        a = SongBaseTest.createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "v: [A B C D] x3 T: A",
                "i:\nv: bob, bob, bob berand\nt: last line \n");
        a.debugSongMoments();

        try {
            ChordSection cs = ChordSection.parse("v:", a.getBeatsPerBar());
            ChordSection chordSection = a.findChordSection(cs.getSectionVersion());
            assertNotNull(chordSection);
            Phrase phrase = chordSection.getPhrase(0);
            assertTrue(phrase.isRepeat());

            MeasureRepeat measureRepeat = (MeasureRepeat) phrase;
            assertEquals(4, measureRepeat.size());

            Grid<ChordSectionLocation> grid = a.getChordSectionLocationGrid();
            MeasureNode measureNode = a.findMeasureNode(new GridCoordinate(0, 1));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("A", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(0, 4));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("D", a.getBeatsPerBar()), measureNode);

            ChordSectionLocation   chordSectionLocation = a.getChordSectionLocation(new GridCoordinate(0, 4+1));
            assertNotNull(chordSectionLocation);
            assertEquals(ChordSectionLocation.Marker.none, chordSectionLocation.getMarker());
            measureNode =  a.findMeasureNode(chordSectionLocation);
            assertNotNull(measureNode);
            assertTrue(measureNode.isRepeat());
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }

        a = SongBaseTest.createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "v: [A B C D, Ab Bb Eb Db, D C G G# ] x3 T: A",
                "i:\nv: bob, bob, bob berand\nt: last line \n");
        a.debugSongMoments();

        try {
            ChordSection cs = ChordSection.parse("v:", a.getBeatsPerBar());
            ChordSection chordSection = a.findChordSection(cs.getSectionVersion());
            assertNotNull(chordSection);
            Phrase phrase = chordSection.getPhrase(0);
            assertTrue(phrase.isRepeat());

            MeasureRepeat measureRepeat = (MeasureRepeat) phrase;
            assertEquals(12, measureRepeat.size());

            Grid<ChordSectionLocation> grid = a.getChordSectionLocationGrid();
            MeasureNode measureNode = a.findMeasureNode(new GridCoordinate(0, 1));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("A", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(0, 4));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("D", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(1, 4));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("Db", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(2, 4));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("G#", a.getBeatsPerBar()), measureNode);

            ChordSectionLocation chordSectionLocation = a.getChordSectionLocation(new GridCoordinate(2, 4+1));
            assertNotNull(chordSectionLocation);
            assertEquals(ChordSectionLocation.Marker.repeatLowerRight, chordSectionLocation.getMarker());
            chordSectionLocation = a.getChordSectionLocation(new GridCoordinate(2, 4+1+1));
            assertNotNull(chordSectionLocation);
            assertEquals(ChordSectionLocation.Marker.none, chordSectionLocation.getMarker());
            measureNode =  a.findMeasureNode(chordSectionLocation);
            assertNotNull(measureNode);
            assertTrue(measureNode.isRepeat());
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }


        a = SongBaseTest.createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "v: [A B C D, Ab Bb Eb Db, D C G# ] x3 T: A",
                "i:\nv: bob, bob, bob berand\nt: last line \n");
        a.debugSongMoments();

        try {
            ChordSection cs = ChordSection.parse("v:", a.getBeatsPerBar());
            ChordSection chordSection = a.findChordSection(cs.getSectionVersion());
            assertNotNull(chordSection);
            Phrase phrase = chordSection.getPhrase(0);
            assertTrue(phrase.isRepeat());

            MeasureRepeat measureRepeat = (MeasureRepeat) phrase;
            assertEquals(11, measureRepeat.size());

            Grid<ChordSectionLocation> grid = a.getChordSectionLocationGrid();
            MeasureNode measureNode = a.findMeasureNode(new GridCoordinate(0, 1));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("A", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(0, 4));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("D", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(1, 4));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("Db", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(2, 3));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("G#", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(2, 4));
            assertNull(measureNode);

            ChordSectionLocation chordSectionLocation = a.getChordSectionLocation(new GridCoordinate(2, 4+1));
            assertNotNull(chordSectionLocation);
            assertEquals(ChordSectionLocation.Marker.repeatLowerRight, chordSectionLocation.getMarker());
            chordSectionLocation = a.getChordSectionLocation(new GridCoordinate(2, 4+1+1));
            assertNotNull(chordSectionLocation);
            assertEquals(ChordSectionLocation.Marker.none, chordSectionLocation.getMarker());
            measureNode =  a.findMeasureNode(chordSectionLocation);
            assertNotNull(measureNode);
            assertTrue(measureNode.isRepeat());
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }

        a = SongBaseTest.createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "v: [A B C D, Ab Bb Eb Db, D C G G# ] x3 E F F# G T: A",
                "i:\nv: bob, bob, bob berand\nt: last line \n");
        a.debugSongMoments();

        try {
            ChordSection cs = ChordSection.parse("v:", a.getBeatsPerBar());
            ChordSection chordSection = a.findChordSection(cs.getSectionVersion());
            assertNotNull(chordSection);
            Phrase phrase = chordSection.getPhrase(0);
            assertTrue(phrase.isRepeat());

            MeasureRepeat measureRepeat = (MeasureRepeat) phrase;
            assertEquals(12, measureRepeat.size());

            Grid<ChordSectionLocation> grid = a.getChordSectionLocationGrid();
            MeasureNode measureNode = a.findMeasureNode(new GridCoordinate(0, 1));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("A", a.getBeatsPerBar()), measureNode);
            measureNode = a.findMeasureNode(new GridCoordinate(1, 4));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("Db", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(0, 4));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("D", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(1, 4));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("Db", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(2, 4));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("G#", a.getBeatsPerBar()), measureNode);
            ChordSectionLocation chordSectionLocation = a.getChordSectionLocation(new GridCoordinate(2, 4+1));
            assertNotNull(chordSectionLocation);
            assertEquals(ChordSectionLocation.Marker.repeatLowerRight, chordSectionLocation.getMarker());
            chordSectionLocation = a.getChordSectionLocation(new GridCoordinate(2, 4+1+1));
            assertNotNull(chordSectionLocation);
            assertEquals(ChordSectionLocation.Marker.none, chordSectionLocation.getMarker());
            measureNode =  a.findMeasureNode(chordSectionLocation);
            assertNotNull(measureNode);
            assertTrue(measureNode.isRepeat());
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }




        a = SongBaseTest.createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "v: E F F# Gb [A B C D, Ab Bb Eb Db, D C G G# ] x3 T: A",
                "i:\nv: bob, bob, bob berand\nt: last line \n");
        a.debugSongMoments();

        try {
            ChordSection cs = ChordSection.parse("v:", a.getBeatsPerBar());
            ChordSection chordSection = a.findChordSection(cs.getSectionVersion());
            assertNotNull(chordSection);
            Phrase phrase = chordSection.getPhrase(1);
            assertTrue(phrase.isRepeat());

            MeasureRepeat measureRepeat = (MeasureRepeat) phrase;
            assertEquals(12, measureRepeat.size());

            Grid<ChordSectionLocation> grid = a.getChordSectionLocationGrid();
            MeasureNode measureNode = a.findMeasureNode(new GridCoordinate(1, 1));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("A", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(0, 4));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("Gb", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(1, 3));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("C", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(2, 4));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("Db", a.getBeatsPerBar()), measureNode);

            measureNode = a.findMeasureNode(new GridCoordinate(3, 4));
            assertNotNull(measureNode);
            assertEquals(Measure.parse("G#", a.getBeatsPerBar()), measureNode);

            ChordSectionLocation chordSectionLocation = a.getChordSectionLocation(new GridCoordinate(3, 4+1));
            assertNotNull(chordSectionLocation);
            assertEquals(ChordSectionLocation.Marker.repeatLowerRight, chordSectionLocation.getMarker());
            chordSectionLocation = a.getChordSectionLocation(new GridCoordinate(3, 4+1+1));
            assertNotNull(chordSectionLocation);
            assertEquals(ChordSectionLocation.Marker.none, chordSectionLocation.getMarker());
            measureNode =  a.findMeasureNode(chordSectionLocation);
            assertNotNull(measureNode);
            assertTrue(measureNode.isRepeat());
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }



    }

    private static Logger logger = Logger.getLogger(MeasureRepeatTest.class.getName());
}