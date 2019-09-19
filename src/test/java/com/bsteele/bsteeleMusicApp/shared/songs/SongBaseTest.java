package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.Grid;
import com.bsteele.bsteeleMusicApp.shared.GridCoordinate;
import junit.framework.TestCase;
import org.junit.Test;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class SongBaseTest
        extends TestCase {

    @Test
    public void testEquals() {

        SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        SongBase b = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");

        assertTrue(a.equals(a));
        assertTrue(a.hashCode() == a.hashCode());
        assertTrue(a.equals(b));
        assertTrue(a.hashCode() == b.hashCode());
        b = createSongBase("B", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        assertTrue(!a.equals(b));
        assertTrue(a.hashCode() != b.hashCode());
        b = createSongBase("A", "bobby", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        assertTrue(!a.equals(b));
        assertTrue(a.hashCode() != b.hashCode());
        b = createSongBase("A", "bob", "photos.bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        assertTrue(a.getSongId().equals(b.getSongId()));
        assertTrue(a.hashCode() != b.hashCode());

        b = createSongBase("A", "bob", "bsteele.com", Key.Ab,
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        assertTrue(!a.equals(b));
        assertTrue(a.hashCode() != b.hashCode());

        b = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                102, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        assertTrue(!a.equals(b));
        assertTrue(a.hashCode() != b.hashCode());

        b = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 3, 8, "v: A B C D", "v: bob, bob, bob berand");
        assertTrue(!a.equals(b));
        //top
        assertTrue(a.hashCode() != b.hashCode());

        b = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 8, "v: A B C D", "v: bob, bob, bob berand");
        assertTrue(!a.equals(b));
        assertTrue(a.hashCode() != b.hashCode());

        b = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A A C D", "v: bob, bob, bob berand");
        assertTrue(!a.equals(b));
        assertTrue(a.hashCode() != b.hashCode());

        b = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand.");
        assertTrue(!a.equals(b));
        assertTrue(a.hashCode() != b.hashCode());


    }

    @Test
    public void testCurrentLocation() {
        try {
            SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 8, "I:v: A BCm7/ADE C D", "I:v: bob, bob, bob berand");
            assertEquals(MeasureEditType.append, a.getCurrentMeasureEditType());
            logger.fine(a.getCurrentChordSectionLocation().toString());

            assertEquals(Measure.parse("D", a.getBeatsPerBar()), a.getCurrentMeasureNode());

            a.setCurrentChordSectionLocation(ChordSectionLocation.parse("i:0:0"));
            assertEquals(Measure.parse("A", a.getBeatsPerBar()), a.getCurrentMeasureNode());
            a.setCurrentChordSectionLocation(ChordSectionLocation.parse("i:0:1"));
            assertEquals(Measure.parse("BCm7/ADE", a.getBeatsPerBar()), a.getCurrentMeasureNode());
            a.setCurrentChordSectionLocation(ChordSectionLocation.parse("i:0:3"));    //  move to end
            assertEquals(Measure.parse("D", a.getBeatsPerBar()), a.getCurrentMeasureNode());
            a.setCurrentChordSectionLocation(ChordSectionLocation.parse("v:0:0"));
            assertEquals(Measure.parse("A", a.getBeatsPerBar()), a.getCurrentMeasureNode());
            a.setCurrentChordSectionLocation(ChordSectionLocation.parse("v:0:5"));    //  refuse to move past end
            assertEquals(Measure.parse("D", a.getBeatsPerBar()), a.getCurrentMeasureNode());
            a.setCurrentChordSectionLocation(ChordSectionLocation.parse("v:0:3"));    //  move to end
            assertEquals(Measure.parse("D", a.getBeatsPerBar()), a.getCurrentMeasureNode());

            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 8, "I:v: A B C D ch3: [ E F G A ] x4 A# C D# F", "I:v: bob, bob, bob berand");
            assertEquals(MeasureEditType.append, a.getCurrentMeasureEditType());
            assertEquals(Measure.parse("F", a.getBeatsPerBar()), a.getCurrentMeasureNode());
            a.setCurrentChordSectionLocation(ChordSectionLocation.parse("i:0:0"));
            assertEquals(Measure.parse("A", a.getBeatsPerBar()), a.getCurrentMeasureNode());
            a.setCurrentChordSectionLocation(ChordSectionLocation.parse("i:0:3234234"));    //  move to end
            assertEquals(Measure.parse("D", a.getBeatsPerBar()), a.getCurrentMeasureNode());
            a.setCurrentChordSectionLocation(ChordSectionLocation.parse("v:0:0"));
            assertEquals(Measure.parse("A", a.getBeatsPerBar()), a.getCurrentMeasureNode());
            a.setCurrentChordSectionLocation(ChordSectionLocation.parse("c3:1:0"));
            assertEquals(Measure.parse("A#", a.getBeatsPerBar()), a.getCurrentMeasureNode());
            a.setCurrentChordSectionLocation(ChordSectionLocation.parse("c3:1:3"));    //  move to end
            assertEquals(Measure.parse("F", a.getBeatsPerBar()), a.getCurrentMeasureNode());
            ChordSection cs = ChordSection.parse("c3:", a.getBeatsPerBar());
            ChordSection chordSection = a.findChordSection(cs.getSectionVersion());
            assertNotNull(chordSection);
            assertEquals(cs.getSectionVersion(), chordSection.getSectionVersion());
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testEdits() {
        try {
            SongBase a;
            Measure newMeasure;
            String text;


            {
                a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                        100, 4, 8, "I:v: A B C D", "I:v: bob, bob, bob berand");

                TreeSet<ChordSection> chordSections = new TreeSet<ChordSection>(a.getChordSections());
                ChordSection chordSection = chordSections.higher(chordSections.first());
                Phrase phrase = chordSection.getPhrases().get(0);

                Measure measure = phrase.getMeasures().get(1);
                assertEquals(4, phrase.getMeasures().size());
                assertEquals(ScaleNote.B, measure.getChords().get(0).getScaleChord().getScaleNote());
                newMeasure = Measure.parse("G", a.getBeatsPerBar());
            }

            {
                String chords = "I: A A♯ B C  V: C♯ D D♯ E";
                a.parseChords(chords);
                text = a.toMarkup().trim();
                //logger.info("\"" + text + "\"");
                assertEquals(chords, text);
                newMeasure = Measure.parse("F", a.getBeatsPerBar());
                for (int i = 0; i < 8; i++) {
                    //logger.fine();

                    String s = a.getKey().getScaleNoteByHalfStep(i).toString().substring(0, 1) + "♯?";
                    chords = chords.replaceFirst(s, "F");
                    //logger.fine("chords: " + chords);
                }
            }

            {
                //  backwards
                String chords = "I: A A A A  V: B♯ C D♯ E";
                String chordSequence = "AAAABCDE";
                a.parseChords(chords);
                newMeasure = Measure.parse("F", 4);
                for (int i = 7; i >= 4; i--) {
                    String s = chordSequence.substring(i, i + 1) + "♯?";
                    logger.fine("s: " + s);
                    chords = chords.replaceFirst(s, "F");

                    logger.fine("chords: " + chords);

                    SongMoment songMoment = a.getSongMoment(i);
                    logger.fine("songMoment: " + songMoment.toString());
                    logger.finest("ChordSectionLocation: " + songMoment.getChordSectionLocation().toString());
                }
            }

            for (int i = 50; i < 401; i++) {
                a.setBeatsPerMinute(i);
                assertEquals(i, a.getBeatsPerMinute());
            }
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    public void testChordSectionEntry() {
        SongBase a;
        Measure newMeasure;
        String text;

        //  empty sections
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "i: v: t:",
                "i: dude v: bob, bob, bob berand");

        assertEquals("I: [] V: [] T: []", a.toMarkup().trim());
        assertTrue(a.edit(a.parseChordEntry("t: G G C G")));
        assertEquals("I: [] V: [] T: G G C G", a.toMarkup().trim());
        assertTrue(a.edit(a.parseChordEntry("I: V:  A B C D")));
        assertEquals("I: V: A B C D  T: G G C G", a.toMarkup().trim());
        try {
            assertEquals("I: A B C D", a.findChordSection("I:").toMarkup().trim());
            assertEquals("V: A B C D", a.findChordSection("V:").toMarkup().trim());
            assertEquals("T: G G C G", a.findChordSection("T:").toMarkup().trim());
        } catch (ParseException e) {
            fail();
        }

        //  auto rows of 4 when 8 or more measures entered at once
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "i: v: t:",
                "i: dude v: bob, bob, bob berand");

        assertTrue(a.edit(a.parseChordEntry("I: A B C D A B C D")));
        try {
            assertEquals("I: A B C D, A B C D,", a.findChordSection("I:").toMarkup().trim());
        } catch (ParseException e) {
            fail();
        }


    }

    @Test
    public void testFind() {
        try {
            {
                SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                        100, 4, 4, "i: A B C D v: E F G A# t: Gm Gm",
                        "i: dude v: bob, bob, bob berand");


                assertNull(a.findChordSection("ch:"));
                ChordSection chordSection = a.findChordSection("i:");
                logger.fine(chordSection.toMarkup());
                assertEquals("I: A B C D ", chordSection.toMarkup());

                chordSection = a.findChordSection("v:");
                logger.fine(chordSection.toMarkup());
                logger.fine(a.findChordSection("v:").toMarkup());
                assertEquals("V: E F G A♯ ", chordSection.toMarkup());

                chordSection = a.findChordSection("t:");
                logger.fine(chordSection.toMarkup());
                logger.fine(a.findChordSection("t:").toMarkup());
                assertEquals("T: Gm Gm ", chordSection.toMarkup());


                StringBuffer sb = new StringBuffer("abcdefg");
                sb.delete(0, 1);
                sb.delete(0, sb.length());
                logger.fine("<" + sb.toString() + ">");

//            logger.fine(a.findMeasureNode("i:1"));
//            logger.fine(a.findMeasureNode("i:3"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testSetRepeats() {
        {
            SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4, "i: A B C D v: E F G A#",
                    "i: v: bob, bob, bob berand");

            MeasureNode m = a.findMeasureNode(new GridCoordinate(0, 4));
            ChordSectionLocation chordSectionLocation = a.findChordSectionLocation(m);
            logger.fine(chordSectionLocation.toString());
            a.setRepeat(chordSectionLocation, 2);
            logger.finer(a.toMarkup());
            assertEquals("I: [A B C D ] x2  V: E F G A♯", a.toMarkup().trim());

            //  remove the repeat
            chordSectionLocation = a.findChordSectionLocation(m);
            a.setRepeat(chordSectionLocation, 1);
            assertEquals("I: A B C D  V: E F G A♯", a.toMarkup().trim());
        }

        {
            SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4, "i: A B C D v: E F G A#",
                    "i: v: bob, bob, bob berand");

            logger.fine(a.logGrid());
            Grid<ChordSectionLocation> grid;

            grid = a.getChordSectionLocationGrid();
            for (int row = 0; row < grid.getRowCount(); row++) {
                a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                        100, 4, 4, "i: A B C D v: E F G A#",
                        "i: v: bob, bob, bob berand");
                grid = a.getChordSectionLocationGrid();
                ArrayList<ChordSectionLocation> cols = grid.getRow(row);
                for (int col = 1; col < cols.size(); col++)
                    for (int r = 6; r > 1; r--) {
                        MeasureNode m = a.findMeasureNode(new GridCoordinate(row, col));
                        ChordSectionLocation chordSectionLocation = a.findChordSectionLocation(m);
                        a.setRepeat(chordSectionLocation, r);
                        String s = a.toMarkup().trim();
                        logger.fine(s);
                        if (row == 0)
                            assertEquals("I: [A B C D ] x" + r + "  V: E F G A♯", s);
                        else
                            assertEquals("I: A B C D" + "  V: [E F G A♯ ] x" + r, s);
                    }
            }
        }
    }

    @Test
    public void testComments() {
        SongBase a;
        TreeSet<ChordSection> chordSections;
        ChordSection chordSection;
        MeasureNode measureNode;
        ArrayList<Measure> measures;

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        chordSections = new TreeSet<ChordSection>(a.getChordSections());
        assertEquals(1, chordSections.size());
        chordSection = chordSections.first();
        measures = chordSection.getPhrases().get(0).getMeasures();
        assertEquals(4, measures.size());

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D (yo)", "v: bob, bob, bob berand");
        chordSections = new TreeSet<ChordSection>(a.getChordSections());
        assertEquals(1, chordSections.size());
        chordSection = chordSections.first();
        measures = chordSection.getPhrases().get(0).getMeasures();
        assertEquals(5, measures.size());
        assertEquals("(yo)", measures.get(4).toMarkup());
    }


    @Test
    public void testGetGrid() {
        SongBase a;
        Measure measure;

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D, E F G A C: D D GD E\n"
                        + "A B C D x3\n"
                        + "Ab G Gb F", "v: bob, bob, bob berand");
        logger.fine(a.logGrid());
        Grid<ChordSectionLocation> grid = a.getChordSectionLocationGrid();

        assertEquals(5, grid.getRowCount());
        for (int r = 2; r < grid.getRowCount(); r++) {
            ArrayList<ChordSectionLocation> row = grid.getRow(r);
            for (int c = 1; c < row.size(); c++) {
                measure = null;

                MeasureNode node = a.findMeasureNode(row.get(c));
                switch (r) {
                    case 0:
                        switch (c) {
                            case 0:
                                assertEquals(ChordSection.class, node.getClass());
                                assertEquals(Section.verse, ((ChordSection) node).getSectionVersion().getSection());
                                break;
                            case 1:
                                measure = (Measure) node;
                                assertEquals(ScaleNote.A, measure.getChords().get(0).getScaleChord().getScaleNote());
                                break;
                            case 4:
                                measure = (Measure) node;
                                assertEquals(ScaleNote.D, measure.getChords().get(0).getScaleChord().getScaleNote());
                                break;
                        }
                        break;
                    case 2:
                        switch (c) {
                            case 0:
                                assertEquals(ChordSection.class, node.getClass());
                                assertEquals(Section.chorus, ((ChordSection) node).getSectionVersion().getSection());
                                break;
                            case 1:
                            case 2:
                                measure = (Measure) node;
                                assertEquals(ScaleNote.D, measure.getChords().get(0).getScaleChord().getScaleNote());
                                break;
                            case 3:
                                measure = (Measure) node;
                                assertEquals(ScaleNote.G, measure.getChords().get(0).getScaleChord().getScaleNote());
                                break;
                            case 4:
                                measure = (Measure) node;
                                assertEquals(ScaleNote.E, measure.getChords().get(0).getScaleChord().getScaleNote());
                                break;
                        }
                        break;
                    case 3:
                        switch (c) {
                            case 0:
                                assertEquals(ChordSection.class, node.getClass());
                                assertEquals(Section.chorus, ((ChordSection) node).getSectionVersion().getSection());
                                break;
                            case 1:
                                measure = (Measure) node;
                                assertEquals(ScaleNote.A, measure.getChords().get(0).getScaleChord().getScaleNote());
                                break;
                            case 4:
                                measure = (Measure) node;
                                assertEquals(ScaleNote.D, measure.getChords().get(0).getScaleChord().getScaleNote());
                                break;
                        }
                        break;
                }

                if (measure != null) {
                    assertEquals(measure, a.findMeasureNode(a.getChordSectionLocationGrid().get(c, r)));
                    logger.fine("measure(" + c + "," + r + "): " + measure.toMarkup());
                    ChordSectionLocation loc = a.findChordSectionLocation(measure);
                    logger.fine("loc: " + loc.toString());
                    a.setCurrentChordSectionLocation(loc);

                    logger.fine("current: " + a.getCurrentMeasureNode().toMarkup());
                    assertEquals(measure, a.getCurrentMeasureNode());
                }
                logger.finest("grid[" + r + "," + c + "]: " + node.toString());
            }
        }

    }


    @Test
    public void testFindChordSectionLocation() {
        int beatsPerBar = 4;
        SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "i: A B C D V: D E F F# " +
                        "v1:    Em7 E E G \n" +
                        "       C D E Eb7 x2\n" +
                        "v2:    A B C D |\n" +
                        "       E F G7 G#m | x2\n" +
                        "       D C GB GbB \n" +
                        "C: F F# G G# Ab A O: C C C C B",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no:");
        logger.fine(a.getSongId().toString());
        logger.fine("\t" + a.toMarkup());
        logger.fine(a.getRawLyrics());

        ChordSectionLocation chordSectionLocation = null;
        try {
            chordSectionLocation = ChordSectionLocation.parse("v:0:0");

            MeasureNode m = a.findMeasureNode(chordSectionLocation);
            assertEquals(Measure.parse("D", a.getBeatsPerBar()), m);
            assertEquals(m, a.findMeasureNode(chordSectionLocation));

            m = a.findMeasureNode(ChordSectionLocation.parse("v:0:3"));
            assertEquals(Measure.parse("F#", a.getBeatsPerBar()), m);
            m = a.findMeasureNode(ChordSectionLocation.parse("v:0:4"));
            assertNull(m);
            m = a.findMeasureNode(ChordSectionLocation.parse("v1:2:0"));
            assertNull(m);
            m = a.findMeasureNode(ChordSectionLocation.parse("v1:1:1"));
            assertEquals(Measure.parse("D", a.getBeatsPerBar()), m);
            m = a.findMeasureNode(ChordSectionLocation.parse("v1:0:0"));
            assertEquals(Measure.parse("Em7", a.getBeatsPerBar()), m);
            m = a.findMeasureNode(ChordSectionLocation.parse("v1:0:4"));
            assertNull(m);
            m = a.findMeasureNode(ChordSectionLocation.parse("v1:0:3"));
            assertEquals(Measure.parse("G", a.getBeatsPerBar()), m);
            m = a.findMeasureNode(ChordSectionLocation.parse("v1:1:0"));
            assertEquals(Measure.parse("C", a.getBeatsPerBar()), m);
            m = a.findMeasureNode(ChordSectionLocation.parse("v1:1:1"));
            assertEquals(Measure.parse("D", a.getBeatsPerBar()), m);
            m = a.findMeasureNode(ChordSectionLocation.parse("v1:0:9"));//    repeats don't count here
            assertNull(m);
            m = a.findMeasureNode(ChordSectionLocation.parse("v2:0:0"));
            assertEquals(Measure.parse("A", a.getBeatsPerBar()), m);
            m = a.findMeasureNode(ChordSectionLocation.parse("v2:0:3"));
            assertEquals(Measure.parse("D", a.getBeatsPerBar()), m);
            m = a.findMeasureNode(ChordSectionLocation.parse("v2:0:4"));
            assertEquals(Measure.parse("E", a.getBeatsPerBar()), m);
            m = a.findMeasureNode(ChordSectionLocation.parse("v2:1:3"));
            assertEquals(Measure.parse("GbB", a.getBeatsPerBar()), m);
            m = a.findMeasureNode(ChordSectionLocation.parse("v2:1:4"));
            assertNull(m);
            m = a.findMeasureNode(ChordSectionLocation.parse("o:0:4"));
            assertEquals(Measure.parse("B", a.getBeatsPerBar()), m);
            m = a.findMeasureNode(ChordSectionLocation.parse("o:0:5"));
            assertNull(m);
            m = a.findMeasureNode(ChordSectionLocation.parse("c:0:5"));
            assertEquals(Measure.parse("A", a.getBeatsPerBar()), m);
            m = a.findMeasureNode(ChordSectionLocation.parse("c:0:6"));
            assertNull(m);
            m = a.findMeasureNode(ChordSectionLocation.parse("i:0:0"));
            assertEquals(Measure.parse("A", a.getBeatsPerBar()), m);

            chordSectionLocation = ChordSectionLocation.parse("v:0");
            MeasureNode mn = a.findMeasureNode(chordSectionLocation);
            logger.fine(mn.toMarkup());
            assertEquals("D E F F♯ ", mn.toMarkup());

            chordSectionLocation = ChordSectionLocation.parse("out:");
            mn = a.findMeasureNode(chordSectionLocation);
            logger.fine(mn.toMarkup());
            assertEquals("O: C C C C B ", mn.toMarkup());
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testMeasureDelete() {
        int beatsPerBar = 4;
        SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "i: A B C D V: D E F F# ",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");

        ChordSectionLocation loc = null;
        try {
            loc = ChordSectionLocation.parse("i:0:2");
            a.setCurrentChordSectionLocation(loc);
            logger.fine(a.getCurrentChordSectionLocation().toString());
            logger.fine(a.findMeasureNode(a.getCurrentChordSectionLocation()).toMarkup());
            a.chordSectionLocationDelete(loc);
            logger.fine(a.findChordSection(SectionVersion.parse("i:")).toMarkup());
            logger.fine(a.findMeasureNode(loc).toMarkup());
            logger.fine("loc: " + a.getCurrentChordSectionLocation().toString());
            logger.fine(a.findMeasureNode(a.getCurrentChordSectionLocation()).toMarkup());
            assertEquals(a.findMeasureNode(loc),
                    a.findMeasureNode(a.getCurrentChordSectionLocation()));

            assertEquals("I: A B D ", a.getChordSection(SectionVersion.parse("i:")).toMarkup());
            assertEquals(Measure.parse("D", beatsPerBar), a.getCurrentChordSectionLocationMeasureNode());
            logger.fine("cur: " + a.getCurrentChordSectionLocationMeasureNode().toMarkup());

            a.chordSectionLocationDelete(loc);
            assertEquals("I: A B ", a.getChordSection(SectionVersion.parse("i:")).toMarkup());
            logger.fine(a.getCurrentChordSectionLocationMeasureNode().toMarkup());
            assertEquals(Measure.parse("B", beatsPerBar), a.getCurrentChordSectionLocationMeasureNode());

            a.chordSectionLocationDelete(ChordSectionLocation.parse("i:0:0"));
            assertEquals("I: B ", a.getChordSection(SectionVersion.parse("i:")).toMarkup());
            logger.fine(a.getCurrentChordSectionLocationMeasureNode().toMarkup());
            assertEquals(Measure.parse("B", beatsPerBar), a.getCurrentChordSectionLocationMeasureNode());

            a.chordSectionLocationDelete(ChordSectionLocation.parse("i:0:0"));
            assertEquals("I: []", a.getChordSection(SectionVersion.parse("i:")).toMarkup());
            assertNull(a.getCurrentChordSectionLocationMeasureNode());
            //assertEquals(ChordSection.parse("I:", beatsPerBar ),a.getCurrentChordSectionLocationMeasureNode());

            assertEquals("V: D E F F♯ ", a.getChordSection(SectionVersion.parse("v:")).toMarkup());
            a.setCurrentChordSectionLocation(ChordSectionLocation.parse("v:0:3"));
            assertEquals(Measure.parse("F#", beatsPerBar), a.getCurrentChordSectionLocationMeasureNode());
            a.chordSectionLocationDelete(ChordSectionLocation.parse("v:0:3"));
            assertEquals(Measure.parse("F", beatsPerBar), a.getCurrentChordSectionLocationMeasureNode());
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testLastLineWithoutNewline() {
        SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D V: D E F F# ",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro\n");
        String lyrics = "i:\n" +
                "v: bob, bob, bob berand\n" +
                "c: sing chorus here \n" +
                "o: last line of outro";
        //logger.fine(    a.getRawLyrics());
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D V: D E F F# ",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        assertEquals(lyrics, a.getRawLyrics());
    }

    @Test
    public void testSongWithoutStartingSection() {  //  fixme: doesn't test much, not very well

        SongBase a = createSongBase("Rio", "Duran Duran", "Sony/ATV Music Publishing LLC", Key.getDefault(),
                100, 4, 4,
                //  not much of this chord chart is correct!
                "Verse\n" +
                        "C#m A♭ FE A♭  x4\n" +
                        "Prechorus\n" +
                        "C C/\n" +
                        "chorus\n" +
                        "C G B♭ F  x4\n" +
                        "Tag Chorus\n",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro\n");

        logger.fine(a.logGrid());
        Grid<ChordSectionLocation> grid = a.getChordSectionLocationGrid();
        assertEquals(10, grid.getRowCount());   //  comments on their own line add a bunch
        ArrayList<ChordSectionLocation> row = grid.getRow(0);
        assertEquals(2, row.size());
        row = grid.getRow(1);
        logger.fine("row: " + row.toString());
        assertEquals(6, row.size());
        row = grid.getRow(2);
        assertEquals(2, row.size());
        assertNull(grid.get(0, 5));
    }

    @Test
    public void testOddSongs() {
        SongBase a;
        int beatsPerBar = 4;
        ChordSectionLocation location;

        try {
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, beatsPerBar, 4,
                    "O:" +
                            "D..Dm7 Dm7 C..B♭maj7 B♭maj7\n" +
                            " x12",
                    "o: nothing");
            logger.fine(a.logGrid());
            location = new ChordSectionLocation(new SectionVersion(Section.outro), 0, 3);
            MeasureNode measureNode = a.findMeasureNode(location);
            logger.fine(measureNode.toMarkup());
            assertEquals(Measure.parse("B♭maj7", beatsPerBar), measureNode);
            assertEquals(Measure.parse("B♭maj7", beatsPerBar), a.findMeasureNode(new GridCoordinate(0, 4)));

            final int row = 0;
            final int lastCol = 3;
            location = new ChordSectionLocation(new SectionVersion(Section.outro), row, lastCol);
            measureNode = a.findMeasureNode(location);
            logger.fine(measureNode.toMarkup());
            assertEquals(measureNode, a.findMeasureNode(new GridCoordinate(row, lastCol + 1)));

            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "I1:\n" +
                            "CD CD CD A\n" +    //  toss the dot as a comment
                            "D X\n" +
                            "\n" +
                            "V:\n" +
                            "D7 D7 CD CD\n" +
                            "D7 D7 CD CD\n" +
                            "D7 D7 CD CD\n" +
                            "D7 D7 CD D.\n" +
                            "\n" +
                            "I2:\n" +
                            "CD CD CD D.\n" +
                            "D\n" +
                            "\n" +
                            "C1:\n" +
                            "DB♭ B♭ DC C\n" +
                            "DB♭ B♭ DC C\n" +
                            "DB♭ B♭ DC DC\n" +
                            "DC\n" +
                            "\n" +
                            "I3:\n" +
                            "D\n" +
                            "\n" +
                            "C2:\n" +
                            "DB♭ B♭ DC C\n" +
                            "DB♭ B♭ DC C\n" +
                            "DB♭ B♭ DC C\n" +
                            "DB♭ B♭ DC DC\n" +
                            "DC DC\n" +
                            "\n" +
                            "I4:\n" +
                            "C5D5 C5D5 C5D5 C5D5 x7\n" +
                            "\n" +
                            "O:\n" +
                            "C5D5 C5D5 C5D5 C5D5\n" +
                            "C5D5 C5D5 C5D5 C5D#",
                    "i1:\nv: bob, bob, bob berand\ni2: nope\nc1: sing \ni3: chorus here \ni4: mo chorus here\no: last line of outro");
            logger.fine(a.logGrid());
            assertEquals(Measure.parse("X", a.getBeatsPerBar()), a.findMeasureNode(new GridCoordinate(1, 2)));
            assertEquals(Measure.parse("C5D5", a.getBeatsPerBar()), a.findMeasureNode(new GridCoordinate(5, 4)));
            assertEquals(Measure.parse("DC", a.getBeatsPerBar()), a.findMeasureNode(new GridCoordinate(18, 2)));
            assertEquals(Measure.parse("C5D#", a.getBeatsPerBar()), a.findMeasureNode(new GridCoordinate(20, 4)));


            //  not what's intended, but what's declared
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "V:I:O:\n" +
                            "Ebsus2 Bb Gm7 C\n" +
                            "\n" +
                            "C:\n" +
                            "Cm F Bb Eb x3\n" +
                            "Cm F\n" +
                            "\n"
                            + "O:V:\n"
                    ,
                    "i1:\nv: bob, bob, bob berand\ni2: nope\nc1: sing \ni3: chorus here \ni4: mo chorus here\no: last line of outro");
            logger.fine(a.logGrid());
            assertEquals(Measure.parse("Gm7", a.getBeatsPerBar()), a.findMeasureNode(new GridCoordinate(0, 3)));
            assertEquals(Measure.parse("Cm", a.getBeatsPerBar()), a.findMeasureNode(new GridCoordinate(2, 1)));
            assertEquals(Measure.parse("F", a.getBeatsPerBar()), a.findMeasureNode(new GridCoordinate(2, 2)));
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGridStuff() {

        int beatsPerBar = 4;
        SongBase a;
        Grid<ChordSectionLocation> grid;
        MeasureNode measureNode;
        ChordSectionLocation location;
        GridCoordinate gridCoordinate;

        try {

            //  see that section identifiers are on first phrase row
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, beatsPerBar, 4,
                    "I: [Am Am/G Am/F♯ FE ] x4  v: [Am Am/G Am/F♯ FE ] x2  C: F F C C G G F F  O: Dm C B B♭ A  ",
                    "i:\nv: bob, bob, bob berand\nv: nope\nc: sing chorus here o: end here");

            logger.finer(a.toMarkup());
            assertEquals(new GridCoordinate(0, 1), a.getGridCoordinate(ChordSectionLocation.parse("I:0:0")));

            assertEquals(new GridCoordinate(0, 0), a.getGridCoordinate(ChordSectionLocation.parse("I:")));
            assertEquals(new GridCoordinate(1, 0), a.getGridCoordinate(ChordSectionLocation.parse("V:")));
            assertEquals(new GridCoordinate(2, 0), a.getGridCoordinate(ChordSectionLocation.parse("C:")));

            //  see that section identifiers are on first phrase row
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, beatsPerBar, 4,
                    "I: Am Am/G Am/F♯ FE, A B C D, v: [Am Am/G Am/F♯ FE ] x2  C: F F C C G G F F  O: Dm C B B♭ A  ",
                    "i:\nv: bob, bob, bob berand\nv: nope\nc: sing chorus here o: end here");

            logger.finer(a.logGrid());
            assertEquals(new GridCoordinate(0, 1), a.getGridCoordinate(ChordSectionLocation.parse("I:0:0")));

            assertEquals(new GridCoordinate(0, 0), a.getGridCoordinate(ChordSectionLocation.parse("I:")));
            assertEquals(new GridCoordinate(2, 0), a.getGridCoordinate(ChordSectionLocation.parse("V:")));
            assertEquals(new GridCoordinate(3, 0), a.getGridCoordinate(ChordSectionLocation.parse("C:")));


            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, beatsPerBar, 4,
                    "I: V: [Am Am/G Am/F♯ FE ] x4  I2: [Am Am/G Am/F♯ FE ] x2  C: F F C C G G F F  O: Dm C B B♭ A  ",
                    "i:\nv: bob, bob, bob berand\nv: nope\nc: sing chorus here");

            logger.finer(a.toMarkup());
            assertEquals(new GridCoordinate(0, 1), a.getGridCoordinate(ChordSectionLocation.parse("V:0:0")));

            assertEquals(new GridCoordinate(0, 0), a.getGridCoordinate(ChordSectionLocation.parse("I:")));
            assertEquals(new GridCoordinate(0, 0), a.getGridCoordinate(ChordSectionLocation.parse("V:")));
            assertEquals(new GridCoordinate(2, 0), a.getGridCoordinate(ChordSectionLocation.parse("C:")));

            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, beatsPerBar, 4,
                    "I: V: c: G D G D ",
                    "i:\nv: bob, bob, bob berand\nv: nope\nc: sing chorus here");

            logger.finer(a.toMarkup());

            assertEquals(new GridCoordinate(0, 0), a.getGridCoordinate(ChordSectionLocation.parse("I:")));
            assertEquals(new GridCoordinate(0, 0), a.getGridCoordinate(ChordSectionLocation.parse("V:")));
            assertEquals(new GridCoordinate(0, 0), a.getGridCoordinate(ChordSectionLocation.parse("C:")));
            location = ChordSectionLocation.parse("I:0:0");
            assertEquals(new GridCoordinate(0, 1), a.getGridCoordinate(ChordSectionLocation.parse("I:0:0")));
            assertEquals(Measure.parse("G", beatsPerBar), a.findMeasureNode(location));
            assertEquals(new GridCoordinate(0, 1), a.getGridCoordinate(ChordSectionLocation.parse("v:0:0")));
            assertEquals(Measure.parse("G", beatsPerBar), a.findMeasureNode(location));
            assertEquals(new GridCoordinate(0, 1), a.getGridCoordinate(ChordSectionLocation.parse("c:0:0")));
            assertEquals(Measure.parse("G", beatsPerBar), a.findMeasureNode(location));
            logger.fine(a.logGrid());
            gridCoordinate = new GridCoordinate(0, 0);
            location = a.getChordSectionLocation(gridCoordinate);
            logger.fine(location.toString());
            assertEquals(gridCoordinate, a.getGridCoordinate(location));
            location = ChordSectionLocation.parse("V:0:0");
            assertEquals(new GridCoordinate(0, 1), a.getGridCoordinate(ChordSectionLocation.parse("i:0:0")));
            assertEquals(Measure.parse("G", beatsPerBar), a.findMeasureNode(location));
            gridCoordinate = new GridCoordinate(0, 0);
            location = a.getChordSectionLocation(gridCoordinate);
            logger.fine(location.toString());
            assertEquals(gridCoordinate, a.getGridCoordinate(location));
            assertEquals("I: V: C: ", location.toString());


            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, beatsPerBar, 4,
                    "verse: A B C D prechorus: D E F F# chorus: G D C G x3",
                    "i:\nv: bob, bob, bob berand\npc: nope\nc: sing chorus here \no: last line of outro");

            logger.finer(a.toMarkup());

            grid = a.getChordSectionLocationGrid();
            for (int r = 0; r < grid.getRowCount(); r++) {
                ArrayList<ChordSectionLocation> row = grid.getRow(r);
                for (int c = 0; c < row.size(); c++) {
                    GridCoordinate coordinate = new GridCoordinate(r, c);
                    assertNotNull(coordinate);
                    assertNotNull(coordinate.toString());
                    assertNotNull(a.getChordSectionLocationGrid());
                    assertNotNull(a.getChordSectionLocationGrid().getRow(r));
                    ChordSectionLocation chordSectionLocation = a.getChordSectionLocationGrid().getRow(r).get(c);
                    if (chordSectionLocation == null)
                        continue;
                    assertNotNull(chordSectionLocation.toString());
                    logger.fine(coordinate.toString() + "  " + chordSectionLocation.toString());
                    ChordSectionLocation loc = a.getChordSectionLocation(coordinate);
                    logger.fine(loc.toString());
                    assertEquals(a.getChordSectionLocationGrid().getRow(r).get(c), a.getChordSectionLocation(coordinate));
                    assertEquals(coordinate, a.getGridCoordinate(loc));
                    assertEquals(loc, a.getChordSectionLocation(coordinate));    //  well, yeah
                }
            }
        } catch (ParseException pe) {
            fail(pe.getMessage());
        }
    }

    @Test
    public void testComputeMarkup() {
        int beatsPerBar = 4;
        SongBase a;
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "verse: C2: V2: A B C D prechorus: D E F F# chorus: G D C G x3",
                "i:\nv: bob, bob, bob berand\npc: nope\nc: sing chorus here \no: last line of outro");
        assertEquals("V: V2: C2: A B C D  PC: D E F F♯  C: [G D C G ] x3", a.toMarkup().trim());
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "verse: A B C D prechorus: D E F F# chorus: G D C G x3",
                "i:\nv: bob, bob, bob berand\npc: nope\nc: sing chorus here \no: last line of outro");
        assertEquals("V: A B C D  PC: D E F F♯  C: [G D C G ] x3", a.toMarkup().trim());


    }

    @Test
    public void testDebugSongMoments() {
        int beatsPerBar = 4;
        SongBase a;

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "verse: C2: V2: A B C D Ab Bb Eb Db prechorus: D E F F# o:chorus: G D C G x3 T: A",
                "i:\nv: bob, bob, bob berand\npc: nope\nc: sing chorus here \nv: nope\nc: yes\nv: nope\nt:\no: last line of outro\n");
        a.debugSongMoments();

        int count = 0;
        for (int momentNumber = 0; momentNumber < a.getSongMomentsSize(); momentNumber++) {
            SongMoment songMoment = a.getSongMoment(momentNumber);
            if (songMoment == null)
                break;
            logger.fine(songMoment.toString());
            assertEquals(count, songMoment.getMomentNumber());
            GridCoordinate momentGridCoordinate = a.getMomentGridCoordinate(songMoment);
            assertNotNull(momentGridCoordinate);

            count++;
        }
    }

    @Test
    public void testChordSectionBeats() {
        int beatsPerBar = 4;
        SongBase a;

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "verse: C2: V2: A B C D Ab Bb Eb Db prechorus: D E F F# o:chorus: G D C G x3 T: A",
                "i:\nv: bob, bob, bob berand\npc: nope\nc: sing chorus here \nv: nope\nc: yes\nv: nope\nt:\no: last line of outro\n");
        try {
            assertEquals(8 * 4, a.getChordSectionBeats(SectionVersion.parse("v:")));
            assertEquals(8 * 4, a.getChordSectionBeats(SectionVersion.parse("c2:")));
            assertEquals(8 * 4, a.getChordSectionBeats(SectionVersion.parse("v2:")));
            assertEquals(4 * 3 * 4, a.getChordSectionBeats(SectionVersion.parse("o:")));
            assertEquals(4, a.getChordSectionBeats(SectionVersion.parse("t:")));
        } catch (ParseException pe) {
            fail(pe.getMessage());
        }
    }

    @Test
    public void testChordSectionRows() {
        int beatsPerBar = 4;
        SongBase a;

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "verse: C2: V2: A B C D, Ab Bb Eb Db prechorus: D E F F# o:chorus: G D C G x3 T: A",
                "i:\nv: bob, bob\npc: nope\nc: sing \nC2: d\nV2:df\nv: nope\nc: yes\nv: nope\nt:\no: last line of outro\n");
        try {
            assertEquals(2, a.getChordSectionRows(SectionVersion.parse("v:")));
            assertEquals(2, a.getChordSectionRows(SectionVersion.parse("c2:")));
            assertEquals(2, a.getChordSectionRows(SectionVersion.parse("v2:")));
            assertEquals(1, a.getChordSectionRows(SectionVersion.parse("o:")));
            assertEquals(1, a.getChordSectionRows(SectionVersion.parse("t:")));
            assertEquals(0, a.getChordSectionRows(SectionVersion.parse("v3:")));
        } catch (ParseException pe) {
            fail(pe.getMessage());
        }
    }

    @Test
    public void testSongMomentGridding() {
        int beatsPerBar = 4;
        SongBase a;

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "I: [A B C D, E F]  x2  ",
                "i:\n");
        a.debugSongMoments();
        {
            //  verify repeats stay on correct row
            SongMoment songMoment;

            for (int momentNumber = 0; momentNumber < 4; momentNumber++) {
                songMoment = a.getSongMoment(momentNumber);
                assertEquals(0, a.getMomentGridCoordinate(songMoment.getMomentNumber()).getRow());
            }
            for (int momentNumber = 4; momentNumber < 6; momentNumber++) {
                songMoment = a.getSongMoment(momentNumber);
                assertEquals(1, a.getMomentGridCoordinate(songMoment.getMomentNumber()).getRow());
            }
            for (int momentNumber = 6; momentNumber < 10; momentNumber++) {
                songMoment = a.getSongMoment(momentNumber);
                assertEquals(0, a.getMomentGridCoordinate(songMoment.getMomentNumber()).getRow());
            }
            for (int momentNumber = 10; momentNumber < 12; momentNumber++) {
                songMoment = a.getSongMoment(momentNumber);
                assertEquals(1, a.getMomentGridCoordinate(songMoment.getMomentNumber()).getRow());
            }
        }

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "verse: C2: V2: A B C D  x2  prechorus: D E F F#, G# A# B C o:chorus: G D C G x3 T: A",
                "i:\nv: bob, bob, bob berand\npc: nope\nc: sing chorus here \nv: nope\nc: yes\nv: nope\nt:\no: last line of outro\n");
        a.debugSongMoments();

        {
            //  verify beats total as expected
            int beats = 0;
            for (int momentNumber = 0; momentNumber < a.getSongMomentsSize(); momentNumber++) {
                SongMoment songMoment = a.getSongMoment(momentNumber);
                if (songMoment == null)
                    break;
                assertEquals(beats, songMoment.getBeatNumber());
                beats += songMoment.getMeasure().getBeatCount();
            }
        }
        {
            int count = 0;
            for (int momentNumber = 0; momentNumber < a.getSongMomentsSize(); momentNumber++) {
                SongMoment songMoment = a.getSongMoment(momentNumber);
                if (songMoment == null)
                    break;
                logger.fine(" ");
                logger.fine(songMoment.toString());
                assertEquals(count, songMoment.getMomentNumber());
                GridCoordinate momentGridCoordinate = a.getMomentGridCoordinate(songMoment);
                assertNotNull(momentGridCoordinate);
                logger.finer(momentGridCoordinate.toString());

                count++;
            }
        }
        {
            //  verify repeats stay on correct row
            SongMoment songMoment;

            for (int momentNumber = 0; momentNumber < 8; momentNumber++) {
                songMoment = a.getSongMoment(momentNumber);
                assertEquals(0, a.getMomentGridCoordinate(songMoment.getMomentNumber()).getRow());
            }
            for (int momentNumber = 8; momentNumber < 12; momentNumber++) {
                songMoment = a.getSongMoment(momentNumber);
                assertEquals(1, a.getMomentGridCoordinate(songMoment.getMomentNumber()).getRow());
            }
            for (int momentNumber = 12; momentNumber < 16; momentNumber++) {
                songMoment = a.getSongMoment(momentNumber);
                assertEquals(2, a.getMomentGridCoordinate(songMoment.getMomentNumber()).getRow());
            }
            for (int momentNumber = 16; momentNumber < 28; momentNumber++) {
                songMoment = a.getSongMoment(momentNumber);
                assertEquals(3, a.getMomentGridCoordinate(songMoment.getMomentNumber()).getRow());
            }
            for (int momentNumber = 28; momentNumber < 36; momentNumber++) {
                songMoment = a.getSongMoment(momentNumber);
                assertEquals(4, a.getMomentGridCoordinate(songMoment.getMomentNumber()).getRow());
            }
            for (int momentNumber = 48; momentNumber < 56; momentNumber++) {
                songMoment = a.getSongMoment(momentNumber);
                assertEquals(6, a.getMomentGridCoordinate(songMoment.getMomentNumber()).getRow());
            }
        }


    }

    @Test
    public void testGetBeatNumberAtTime() {
        final int dtDiv = 2;
        int beatsPerBar = 4;
        SongBase a;

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "I: A B C D E F  x2  ",
                "i:\n");

        for (int bpm = 60; bpm < 132; bpm++) {
            double dt = 60.0 / (dtDiv * bpm);

            int expected = -12;
            int count = 0;
            for (double t = (-8 * 3) * dt; t < (8 * 3) * dt; t += dt) {
                logger.fine(bpm + " " + t + ": " + expected + "  " + a.getBeatNumberAtTime(bpm, t));
                int result = a.getBeatNumberAtTime(bpm, t);
                if (result != expected) {
                    //  deal with test rounding issues
                    logger.fine("t/dt - e: " + (t / (2 * dt) - expected));
                    assertTrue((t / (dtDiv * dt) - expected) < 1e-14);
                }
                count++;
                if (count > 1) {
                    count = 0;
                    expected++;
                }
            }
        }
    }

    @Test
    public void testGetSongMomentNumberAtTime() {
        final int dtDiv = 2;
        int beatsPerBar = 4;
        SongBase a;

        for (int bpm = 60; bpm < 132; bpm++) {
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    bpm, beatsPerBar, 4,
                    "I: A B C D E F  x2  ",
                    "i:\n");

            double dt = 60.0 / (dtDiv * bpm);

            int expected = -3;
            int count = 0;
            for (double t = -8 * 3 * dt; t < 8 * 3 * dt; t += dt) {
                int result = a.getSongMomentNumberAtTime(t);
                logger.fine(t
                        + " = " + (t / dt) + " x dt "
                        + "  " + expected
                        + "  @" + count
                        + "  b:" + a.getBeatNumberAtTime(bpm, t)
                        + ": " + a.getSongMomentNumberAtTime(t)
                        + ", bpm: " + bpm
                );
                if (expected != result) {
                    //  deal with test rounding issues
                    double e = t / (dtDiv * dt) / beatsPerBar - expected;
                    logger.fine("error: " + e);
                    assertTrue(e < 1e-14);
                }
                // assertEquals(expected, );
                count++;
                if (count >= 8) {
                    count = 0;
                    expected++;
                }
            }
        }
    }

    /**
     * A convenience constructor used to enforce the minimum requirements for a song.
     *
     * @param title
     * @param artist
     * @param copyright
     * @param key
     * @param bpm
     * @param beatsPerBar
     * @param unitsPerMeasure
     * @param chords
     * @param lyrics
     * @return
     */
    static final SongBase createSongBase(@NotNull String title, @NotNull String artist,
                                         @NotNull String copyright,
                                         @NotNull Key key, int bpm, int beatsPerBar, int unitsPerMeasure,
                                         @NotNull String chords, @NotNull String lyrics) {
        SongBase song = new SongBase();
        song.setTitle(title);
        song.setArtist(artist);
        song.setCopyright(copyright);
        song.setKey(key);
        song.setUnitsPerMeasure(unitsPerMeasure);
        try {
            song.parseChords(chords);
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
        song.setRawLyrics(lyrics);

        song.setBeatsPerMinute(bpm);
        song.setBeatsPerBar(beatsPerBar);

        return song;
    }

    private static Logger logger = Logger.getLogger(SongBaseTest.class.getName());
}