package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.Grid;
import com.bsteele.bsteeleMusicApp.shared.GridCoordinate;
import junit.framework.TestCase;
import org.junit.Test;

import javax.validation.constraints.NotNull;
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
        SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 8, "I:v: A BCm7/ADE C D", "I:v: bob, bob, bob berand");
        assertEquals(MeasureEditType.append, a.getCurrentMeasureEditType());
        logger.fine(a.getCurrentChordSectionLocation().toString());
        assertEquals(Measure.parse("D", a.getBeatsPerBar()), a.getCurrentMeasure());
        a.setCurrentChordSectionLocation(ChordSectionLocation.parse("i:0:0"));
        assertEquals(Measure.parse("A", a.getBeatsPerBar()), a.getCurrentMeasure());
        a.setCurrentChordSectionLocation(ChordSectionLocation.parse("i:0:1"));
        assertEquals(Measure.parse("BCm7/ADE", a.getBeatsPerBar()), a.getCurrentMeasure());
        a.setCurrentChordSectionLocation(ChordSectionLocation.parse("i:0:3"));    //  move to end
        assertEquals(Measure.parse("D", a.getBeatsPerBar()), a.getCurrentMeasure());
        a.setCurrentChordSectionLocation(ChordSectionLocation.parse("v:0:0"));
        assertEquals(Measure.parse("A", a.getBeatsPerBar()), a.getCurrentMeasure());
        a.setCurrentChordSectionLocation(ChordSectionLocation.parse("v:0:5"));    //  refuse to move past end
        assertEquals(Measure.parse("D", a.getBeatsPerBar()), a.getCurrentMeasure());
        a.setCurrentChordSectionLocation(ChordSectionLocation.parse("v:0:3"));    //  move to end
        assertEquals(Measure.parse("D", a.getBeatsPerBar()), a.getCurrentMeasure());

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 8, "I:v: A B C D ch3: [ E F G A ] x4 A# C D# F", "I:v: bob, bob, bob berand");
        assertEquals(MeasureEditType.append, a.getCurrentMeasureEditType());
        assertEquals(Measure.parse("F", a.getBeatsPerBar()), a.getCurrentMeasure());
        a.setCurrentChordSectionLocation(ChordSectionLocation.parse("i:0:0"));
        assertEquals(Measure.parse("A", a.getBeatsPerBar()), a.getCurrentMeasure());
        a.setCurrentChordSectionLocation(ChordSectionLocation.parse("i:0:3234234"));    //  move to end
        assertEquals(Measure.parse("D", a.getBeatsPerBar()), a.getCurrentMeasure());
        a.setCurrentChordSectionLocation(ChordSectionLocation.parse("v:0:0"));
        assertEquals(Measure.parse("A", a.getBeatsPerBar()), a.getCurrentMeasure());
        a.setCurrentChordSectionLocation(ChordSectionLocation.parse("c3:1:0"));
        assertEquals(Measure.parse("A#", a.getBeatsPerBar()), a.getCurrentMeasure());
        a.setCurrentChordSectionLocation(ChordSectionLocation.parse("c3:1:3"));    //  move to end
        assertEquals(Measure.parse("F", a.getBeatsPerBar()), a.getCurrentMeasure());
        ChordSection cs = ChordSection.parse("c3:", a.getBeatsPerBar());
        ChordSection chordSection = a.findChordSection(cs.getSectionVersion());
        assertNotNull(chordSection);
        assertEquals(cs.getSectionVersion(), chordSection.getSectionVersion());
    }

    @Test
    public void testEdits() {
        SongBase a;
        Measure newMeasure;
        String text;

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 8, "", "I:v: bob, bob, bob berand");
        assertTrue(a.addSectionVersion(new SectionVersion(Section.intro)));
        newMeasure = Measure.parse("B", a.getBeatsPerBar());
        assertTrue(a.measureEdit(ChordSectionLocation.parse("I:0"), MeasureEditType.append,
                newMeasure));
        text = a.toMarkup();
        logger.fine(a.toMarkup());
        assertEquals("I: B \n", text);
        newMeasure = Measure.parse("C", a.getBeatsPerBar());
        assertTrue(a.measureEdit(ChordSectionLocation.parse("I:0"), MeasureEditType.append,
                newMeasure));
        text = a.toMarkup();
        assertEquals("I: B C \n", text);
        logger.fine(text);
        newMeasure = Measure.parse("A", a.getBeatsPerBar());
        assertTrue(a.measureEdit(ChordSectionLocation.parse("I:1"), MeasureEditType.insert,
                newMeasure));
        text = a.toMarkup();
        assertEquals("I: A B C \n", text);
        logger.fine(text);

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
            assertTrue(a.measureEdit(new ChordSectionLocation(chordSection.getSectionVersion(), 0, 1), MeasureEditType.replace, newMeasure));
            assertEquals(4, phrase.getMeasures().size());
            phrase = chordSections.higher(chordSections.first()).getPhrases().get(0);
            measure = phrase.getMeasures().get(1);
            assertEquals(ScaleNote.G, measure.getChords().get(0).getScaleChord().getScaleNote());


            measure = phrase.getMeasures().get(0);
            assertEquals(ScaleNote.A, measure.getChords().get(0).getScaleChord().getScaleNote());
            newMeasure = Measure.parse("Gb", a.getBeatsPerBar());
            a.measureEdit(new ChordSectionLocation(chordSection.getSectionVersion(), 0, 0), MeasureEditType.replace, newMeasure);
            assertEquals(4, phrase.getMeasures().size());
            measure = phrase.getMeasures().get(0);
            assertEquals(ScaleNote.Gb, measure.getChords().get(0).getScaleChord().getScaleNote());

            measure = phrase.getMeasures().get(3);
            assertEquals(ScaleNote.D, measure.getChords().get(0).getScaleChord().getScaleNote());
            newMeasure = Measure.parse("F", a.getBeatsPerBar());
            a.measureEdit(new ChordSectionLocation(chordSection.getSectionVersion(), 0, 3), MeasureEditType.replace, newMeasure);
            assertEquals(4, phrase.getMeasures().size());
            measure = phrase.getMeasures().get(3);
            assertEquals(ScaleNote.F, measure.getChords().get(0).getScaleChord().getScaleNote());
        }

        {
            String chords = "I: A A♯ B C \nV: C♯ D D♯ E";
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

                assertTrue(a.measureEdit(a.getSongMoments().get(i).getChordSectionLocation(), MeasureEditType.replace, newMeasure));
                text = a.toMarkup().trim();
                //logger.fine("text  : " + text);
                assertEquals(chords, text);
            }
        }

        {
            //  backwards
            String chords = "I: A A A A \nV: B♯ C D♯ E";
            String chordSequence = "AAAABCDE";
            a.parseChords(chords);
            newMeasure = Measure.parse("F", 4);
            for (int i = 7; i >= 4; i--) {
                String s = chordSequence.substring(i, i + 1) + "♯?";
                logger.fine("s: " + s);
                chords = chords.replaceFirst(s, "F");

                logger.fine("chords: " + chords);

                SongMoment songMoment = a.getSongMoments().get(i);
                logger.fine("songMoment: " + songMoment.toString());
                logger.finest("ChordSectionLocation: " + songMoment.getChordSectionLocation().toString());
                assertTrue(a.measureEdit(songMoment.getChordSectionLocation(), MeasureEditType.replace, newMeasure));
                text = a.toMarkup().trim();
                assertEquals(chords, text);
            }
        }


        for (int i = 50; i < 401; i++) {
            a.setBeatsPerMinute(i);
            assertEquals(i, a.getBeatsPerMinute());
        }

    }

    @Test
    public void testFind() {
        {
            SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4, "i: A B C D v: E F G A# t: Gm Gm",
                    "i: dude v: bob, bob, bob berand");

            assertNull(a.findChordSection(new StringBuffer("ch:")));
            ChordSection chordSection = a.findChordSection(new StringBuffer("i:"));
            logger.fine(chordSection.toMarkup());
            assertEquals("I: A B C D ", chordSection.toMarkup());

            chordSection = a.findChordSection(new StringBuffer("v:"));
            logger.fine(chordSection.toMarkup());
            logger.fine(a.findChordSection(new StringBuffer("v:")).toMarkup());
            assertEquals("V: E F G A♯ ", chordSection.toMarkup());

            chordSection = a.findChordSection(new StringBuffer("t:"));
            logger.fine(chordSection.toMarkup());
            logger.fine(a.findChordSection(new StringBuffer("t:")).toMarkup());
            assertEquals("T: Gm Gm ", chordSection.toMarkup());


            StringBuffer sb = new StringBuffer("abcdefg");
            sb.delete(0, 1);
            sb.delete(0, sb.length());
            logger.fine("<" + sb.toString() + ">");

//            logger.fine(a.findMeasure("i:1"));
//            logger.fine(a.findMeasure("i:3"));
        }
    }

    @Test
    public void testSetRepeats() {
        {
            SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4, "i: A B C D v: E F G A#",
                    "i: v: bob, bob, bob berand");

            Measure m = a.findMeasure(new GridCoordinate(0, 4));
            ChordSectionLocation chordSectionLocation = a.findChordSectionLocation(m);
            a.setRepeat(chordSectionLocation, 2);
            assertEquals("I: [A B C D ] x2 \nV: E F G A♯", a.toMarkup().trim());

            //  remove the repeat
            chordSectionLocation = a.findChordSectionLocation(m);
            a.setRepeat(chordSectionLocation, 1);
            assertEquals("I: A B C D \nV: E F G A♯", a.toMarkup().trim());
        }

        {
            SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4, "i: A B C D v: E F G A#",
                    "i: v: bob, bob, bob berand");

            logger.fine(a.logGrid());
            Grid<ChordSectionLocation> grid = a.getChordSectionLocationGrid();

            for (int row = 0; row < grid.getRowCount(); row++) {
                ArrayList<ChordSectionLocation> cols = grid.getRow(row);
                for (int col = 1; col < cols.size(); col++)
                    for (int r = 6; r > 1; r--) {
                        Measure m = a.findMeasure(new GridCoordinate(row, col));
                        ChordSectionLocation chordSectionLocation = a.findChordSectionLocation(m);
                        a.setRepeat(chordSectionLocation, r);
                        String s = a.toMarkup().trim();
                        logger.fine(s);
                        assertEquals("I: [A B C D ] x" + (row > 0 ? 2 : r)
                                        + (row > 0 ? " \nV: [E F G A♯ ] x" + r : " \nV: E F G A♯"),
                                s);
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
    }


    @Test
    public void testGetGrid() {
        SongBase a;
        Measure measure;

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D E F G A C: D D GD E\n"
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

                    logger.fine("current: " + a.getCurrentMeasure().toMarkup());
                    assertEquals(measure, a.getCurrentMeasure());
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

        ChordSectionLocation chordSectionLocation = ChordSectionLocation.parse(new StringBuffer("v:0:0"));
        Measure m = a.findMeasure(chordSectionLocation);
        assertEquals(Measure.parse("D", a.getBeatsPerBar()), m);
        assertEquals(m, a.findMeasureNode(chordSectionLocation));

        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("v:0:3")));
        assertEquals(Measure.parse("F#", a.getBeatsPerBar()), m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("v:0:4")));
        assertNull(m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("v1:2:0")));
        assertNull(m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("v1:1:1")));
        assertEquals(Measure.parse("D", a.getBeatsPerBar()), m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("v1:0:0")));
        assertEquals(Measure.parse("Em7", a.getBeatsPerBar()), m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("v1:0:4")));
        assertNull(m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("v1:0:3")));
        assertEquals(Measure.parse("G", a.getBeatsPerBar()), m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("v1:1:0")));
        assertEquals(Measure.parse("C", a.getBeatsPerBar()), m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("v1:1:1")));
        assertEquals(Measure.parse("D", a.getBeatsPerBar()), m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("v1:0:9")));//    repeats don't count here
        assertNull(m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("v2:0:0")));
        assertEquals(Measure.parse("A", a.getBeatsPerBar()), m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("v2:0:3")));
        assertEquals(Measure.parse("D", a.getBeatsPerBar()), m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("v2:0:4")));
        assertEquals(Measure.parse("E", a.getBeatsPerBar()), m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("v2:1:3")));
        assertEquals(Measure.parse("GbB", a.getBeatsPerBar()), m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("v2:1:4")));
        assertNull(m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("o:0:4")));
        assertEquals(Measure.parse("B", a.getBeatsPerBar()), m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("o:0:5")));
        assertNull(m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("c:0:5")));
        assertEquals(Measure.parse("A", a.getBeatsPerBar()), m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("c:0:6")));
        assertNull(m);
        m = a.findMeasure(ChordSectionLocation.parse(new StringBuffer("i:0:0")));
        assertEquals(Measure.parse("A", a.getBeatsPerBar()), m);

        chordSectionLocation = ChordSectionLocation.parse(new StringBuffer("v:0"));
        MeasureNode mn = a.findMeasureNode(chordSectionLocation);
        logger.fine(mn.toMarkup());
        assertEquals("D E F F♯ ", mn.toMarkup());

        chordSectionLocation = ChordSectionLocation.parse(new StringBuffer("out:"));
        mn = a.findMeasureNode(chordSectionLocation);
        logger.fine(mn.toMarkup());
        assertEquals("O: C C C C B ", mn.toMarkup());
    }

    @Test
    public void testMeasureDelete() {
        int beatsPerBar = 4;
        SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "i: A B C D V: D E F F# ",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");

        ChordSectionLocation loc = ChordSectionLocation.parse("i:0:2");
        a.setCurrentChordSectionLocation(loc);
        logger.fine(a.getCurrentChordSectionLocation().toString());
        logger.fine(a.findMeasure(a.getCurrentChordSectionLocation()).toMarkup());
        a.chordSectionLocationDelete(loc);
        logger.fine(a.findChordSection(SectionVersion.parse("i:")).toMarkup());
        logger.fine(a.findMeasure(loc).toMarkup());
        logger.fine("loc: " + a.getCurrentChordSectionLocation().toString());
        logger.fine(a.findMeasure(a.getCurrentChordSectionLocation()).toMarkup());
        assertEquals(a.findMeasure(loc),
                a.findMeasure(a.getCurrentChordSectionLocation()));

        assertEquals("I: A B D ", a.getChordSection(SectionVersion.parse("i:")).toString());
        assertEquals(Measure.parse("D", beatsPerBar), a.getCurrentChordSectionLocationMeasure());
        logger.fine("cur: " + a.getCurrentChordSectionLocationMeasure().toMarkup());

        a.chordSectionLocationDelete(loc);
        assertEquals("I: A B ", a.getChordSection(SectionVersion.parse("i:")).toString());
        logger.fine(a.getCurrentChordSectionLocationMeasure().toMarkup());
        assertEquals(Measure.parse("B", beatsPerBar), a.getCurrentChordSectionLocationMeasure());

        a.chordSectionLocationDelete(ChordSectionLocation.parse("i:0:0"));
        assertEquals("I: B ", a.getChordSection(SectionVersion.parse("i:")).toString());
        logger.fine(a.getCurrentChordSectionLocationMeasure().toMarkup());
        assertEquals(Measure.parse("B", beatsPerBar), a.getCurrentChordSectionLocationMeasure());

        a.chordSectionLocationDelete(ChordSectionLocation.parse("i:0:0"));
        assertEquals("I: ", a.getChordSection(SectionVersion.parse("i:")).toString());
        assertNull(a.getCurrentChordSectionLocationMeasure());
        //assertEquals(ChordSection.parse("I:", beatsPerBar ),a.getCurrentChordSectionLocationMeasure());

        assertEquals("V: D E F F♯ ", a.getChordSection(SectionVersion.parse("v:")).toString());
        a.setCurrentChordSectionLocation(ChordSectionLocation.parse("v:0:3"));
        assertEquals(Measure.parse("F#", beatsPerBar), a.getCurrentChordSectionLocationMeasure());
        a.chordSectionLocationDelete(ChordSectionLocation.parse("v:0:3"));
        assertEquals(Measure.parse("F", beatsPerBar), a.getCurrentChordSectionLocationMeasure());

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

        logger.fine( a.logGrid());
        Grid<ChordSectionLocation> grid = a.getChordSectionLocationGrid();
        assertEquals(5, grid.getRowCount());
        ArrayList<ChordSectionLocation> row = grid.getRow(0);
        assertEquals(2, row.size());
        row = grid.getRow(1);
        logger.fine("row: "+row.toString());
        assertEquals(6, row.size());
        row = grid.getRow(2);
        assertEquals(5, row.size());
        row = grid.getRow(3);
        assertEquals(6, row.size());
        row = grid.getRow(4);
        assertEquals(2, row.size());
    }

    @Test
    public void testOddSongs() {
        SongBase a = createSongBase("Rio", "Duran Duran", "Sony/ATV Music Publishing LLC", Key.getDefault(),
                100, 4, 4,
        "V:\n"
                +"Cm7--- F7--- Bbmaj7--- Ebmaj7---\n"
                +"Am7b5--- D7--- Gm7--- G7---\n"
                +"Cm7--- F7--- Bbmaj7--- Ebmaj7--- \n"
                +"Am7b5--- D7--- Gm7--- Gm7---\n"
                +"Am7b5--- D7--- Gm7--- Gm7---\n"
                + "Cm7--- F7--- Bbmaj7--- Bbmaj7---\n"
                + "Am7b5--- D7--- Gm7-C7- Fm7-Bb7-\n"
                + "Am7b5--- D7--- Gm7--- Gm7---\n",
                "V: not important now");
        logger.fine( a.logGrid());
        assertEquals(Measure.parse("Am7b5", a.getBeatsPerBar()), a.findMeasure(new GridCoordinate(16, 1)));
        assertEquals(Measure.parse("Gm7", a.getBeatsPerBar()), a.findMeasure(new GridCoordinate(31, 4)));


        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "I1:\n"+
                        "CD CD CD X.\n"+    //  toss the dot as a comment
                        "D X\n"+
                        "\n"+
                        "V:\n"+
                        "D7 D7 CD CD\n"+
                        "D7 D7 CD CD\n"+
                        "D7 D7 CD CD\n"+
                        "D7 D7 CD D.\n"+
                        "\n"+
                        "I2:\n"+
                        "CD CD CD D.\n"+
                        "D\n"+
                        "\n"+
                        "C1:\n"+
                        "DB♭ B♭ DC C\n"+
                        "DB♭ B♭ DC C\n"+
                        "DB♭ B♭ DC DC\n"+
                        "DC\n"+
                        "\n"+
                        "I3:\n"+
                        "D\n"+
                        "\n"+
                        "C2:\n"+
                        "DB♭ B♭ DC C\n"+
                        "DB♭ B♭ DC C\n"+
                        "DB♭ B♭ DC C\n"+
                        "DB♭ B♭ DC DC\n"+
                        "DC DC\n"+
                        "\n"+
                        "I4:\n"+
                        "C5D5 C5D5 C5D5 C5D5 x7\n"+
                        "\n"+
                        "O:\n"+
                        "C5D5 C5D5 C5D5 C5D5\n"+
                        "C5D5 C5D5 C5D5 C5D5",
                "i1:\nv: bob, bob, bob berand\ni2: nope\nc1: sing \ni3: chorus here \ni4: mo chorus here\no: last line of outro");
        logger.fine( a.logGrid());
        assertEquals(Measure.parse("X", a.getBeatsPerBar()), a.findMeasure(new GridCoordinate(1, 3)));
        assertEquals(Measure.parse("C5D5", a.getBeatsPerBar()), a.findMeasure(new GridCoordinate(5, 4)));
        assertEquals(Measure.parse("DC", a.getBeatsPerBar()), a.findMeasure(new GridCoordinate(18, 2)));
    }

    @Test
    public void testGridStuff() {

        int beatsPerBar = 4;
        SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "verse: A B C D prechorus: D E F F# chorus: G D C G x3",
                "i:\nv: bob, bob, bob berand\npc: nope\nc: sing chorus here \no: last line of outro");

        logger.finer(a.toMarkup());

        Grid<ChordSectionLocation> grid = a.getChordSectionLocationGrid();
        for (int r = 0; r < grid.getRowCount(); r++) {
            ArrayList<ChordSectionLocation> row = grid.getRow(r);
            for (int c = 0; c < row.size(); c++) {
                GridCoordinate coordinate = new GridCoordinate(r, c);
                logger.fine(coordinate.toString() + "  " + a.getChordSectionLocationGrid().getRow(r).get(c).toString());
                ChordSectionLocation loc = a.getChordSectionLocation(coordinate);
                logger.fine( loc.toString());
                assertEquals(a.getChordSectionLocationGrid().getRow(r).get(c),a.getChordSectionLocation(coordinate));
                assertEquals(coordinate,a.getGridCoordinate(loc));
                assertEquals(loc,a.getChordSectionLocation(coordinate));    //  well, yeah
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
    public static final SongBase createSongBase(@NotNull String title, @NotNull String artist,
                                                @NotNull String copyright,
                                                @NotNull Key key, int bpm, int beatsPerBar, int unitsPerMeasure,
                                                @NotNull String chords, @NotNull String lyrics) {
        SongBase song = new SongBase();
        song.setTitle(title);
        song.setArtist(artist);
        song.setCopyright(copyright);
        song.setKey(key);
        song.setUnitsPerMeasure(unitsPerMeasure);
        song.parseChords(chords);
        song.setRawLyrics(lyrics);

        song.setBeatsPerMinute(bpm);
        song.setBeatsPerBar(beatsPerBar);

        return song;
    }

//    @Override
//    public String getModuleName()
//    {
//        return "com.bsteele.bsteeleMusicApp.BSteeleMusicAppJUnit";
//    }

    private static Logger logger = Logger.getLogger(SongBaseTest.class.getName());
}