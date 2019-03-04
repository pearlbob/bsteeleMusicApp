package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.Grid;
import com.bsteele.bsteeleMusicApp.client.songs.SongChordGridSelection;
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
        assertEquals(MeasureEditLocation.append, a.getCurrentMeasureEditLocation());
        assertEquals(Measure.parse("D", a.getBeatsPerBar()), a.getCurrentMeasureNode());
        a.setCurrentMeasureNode(ChordSectionLocation.parse("i:1"));
        assertEquals(Measure.parse("A", a.getBeatsPerBar()), a.getCurrentMeasureNode());
        a.setCurrentMeasureNode(ChordSectionLocation.parse("i:2"));
        assertEquals(Measure.parse("BCm7/ADE", a.getBeatsPerBar()), a.getCurrentMeasureNode());
        a.setCurrentMeasureNode(ChordSectionLocation.parse("i:4"));    //  move to end
        assertEquals(Measure.parse("D", a.getBeatsPerBar()), a.getCurrentMeasureNode());
        a.setCurrentMeasureNode(ChordSectionLocation.parse("v:1"));
        assertEquals(Measure.parse("A", a.getBeatsPerBar()), a.getCurrentMeasureNode());
        a.setCurrentMeasureNode(ChordSectionLocation.parse("v:0"));    //  refuse to move before start
        assertEquals(Measure.parse("A", a.getBeatsPerBar()), a.getCurrentMeasureNode());
        a.setCurrentMeasureNode(ChordSectionLocation.parse("v:5"));    //  refuse to move past end
        assertEquals(Measure.parse("A", a.getBeatsPerBar()), a.getCurrentMeasureNode());
        a.setCurrentMeasureNode(ChordSectionLocation.parse("v:4"));    //  move to end
        assertEquals(Measure.parse("D", a.getBeatsPerBar()), a.getCurrentMeasureNode());

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 8, "I:v: A B C D ch3: [ E F G A ] x4 A# C D# F", "I:v: bob, bob, bob berand");
        assertEquals(MeasureEditLocation.append, a.getCurrentMeasureEditLocation());
        assertEquals(Measure.parse("F", a.getBeatsPerBar()), a.getCurrentMeasureNode());
        a.setCurrentMeasureNode(ChordSectionLocation.parse("i:1"));
        assertEquals(Measure.parse("A", a.getBeatsPerBar()), a.getCurrentMeasureNode());
        a.setCurrentMeasureNode(ChordSectionLocation.parse("i:4"));    //  move to end
        assertEquals(Measure.parse("D", a.getBeatsPerBar()), a.getCurrentMeasureNode());
        a.setCurrentMeasureNode(ChordSectionLocation.parse("v:1"));
        assertEquals(Measure.parse("A", a.getBeatsPerBar()), a.getCurrentMeasureNode());
        a.setCurrentMeasureNode(ChordSectionLocation.parse("v:0"));    //  refuse to move before start
        assertEquals(Measure.parse("A", a.getBeatsPerBar()), a.getCurrentMeasureNode());
        a.setCurrentMeasureNode(ChordSectionLocation.parse("c3:5"));
        assertEquals(Measure.parse("A#", a.getBeatsPerBar()), a.getCurrentMeasureNode());
        a.setCurrentMeasureNode(ChordSectionLocation.parse("c3:7"));    //  move to end
        assertEquals(Measure.parse("D#", a.getBeatsPerBar()), a.getCurrentMeasureNode());
        ChordSection cs = ChordSection.parse("c3:", a.getBeatsPerBar());
        ChordSection chordSection = a.findChordSection(cs);
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
        assertTrue(a.measureEdit(a.getStructuralMeasureNode(0, 0), MeasureEditLocation.append,
                newMeasure));
        text = a.toMarkup();
        logger.info(a.getChords());
        assertEquals("I: B ", text);
        newMeasure = Measure.parse("C", a.getBeatsPerBar());
        assertTrue(a.measureEdit(a.getStructuralMeasureNode(0, 0), MeasureEditLocation.append,
                newMeasure));
        text = a.toMarkup();
        assertEquals("I: B C ", text);
        logger.fine(text);
        newMeasure = Measure.parse("A", a.getBeatsPerBar());
        assertTrue(a.measureEdit(a.getStructuralMeasureNode(0, 1), MeasureEditLocation.insert,
                newMeasure));
        text = a.toMarkup();
        assertEquals("I: A B C ", text);
        logger.fine(text);


        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 8, "I:v: A B C D", "I:v: bob, bob, bob berand");

        TreeSet<ChordSection> chordSections = new TreeSet<ChordSection>(a.getChordSections());
        MeasureSequenceItem vc2 =
                chordSections.higher(chordSections.first()).getMeasureSequenceItems().get(0);

        Measure measure = vc2.getMeasures().get(1);
        assertEquals(4, vc2.getMeasures().size());
        assertEquals(ScaleNote.B, measure.getChords().get(0).getScaleChord().getScaleNote());
        newMeasure = Measure.parse("G", a.getBeatsPerBar());
        assertTrue(a.measureEdit(measure, MeasureEditLocation.replace, newMeasure));
        assertEquals(4, vc2.getMeasures().size());
        vc2 = chordSections.higher(chordSections.first()).getMeasureSequenceItems().get(0);
        measure = vc2.getMeasures().get(1);
        assertEquals(ScaleNote.G, measure.getChords().get(0).getScaleChord().getScaleNote());


        measure = vc2.getMeasures().get(0);
        assertEquals(ScaleNote.A, measure.getChords().get(0).getScaleChord().getScaleNote());
        newMeasure = Measure.parse("Gb", a.getBeatsPerBar());
        a.measureEdit(measure, MeasureEditLocation.replace, newMeasure);
        assertEquals(4, vc2.getMeasures().size());
        measure = vc2.getMeasures().get(0);
        assertEquals(ScaleNote.Gb, measure.getChords().get(0).getScaleChord().getScaleNote());

        measure = vc2.getMeasures().get(3);
        assertEquals(ScaleNote.D, measure.getChords().get(0).getScaleChord().getScaleNote());
        newMeasure = Measure.parse("F", a.getBeatsPerBar());
        a.measureEdit(measure, MeasureEditLocation.replace, newMeasure);
        assertEquals(4, vc2.getMeasures().size());
        measure = vc2.getMeasures().get(3);
        assertEquals(ScaleNote.F, measure.getChords().get(0).getScaleChord().getScaleNote());


        String chords = "I: A A♯ B C V: C♯ D D♯ E";
        a.setChords(chords);
        text = a.toMarkup().trim();
        //logger.info("\"" + text + "\"");
        assertEquals(chords, text);
        newMeasure = Measure.parse("F", a.getBeatsPerBar());
        for (int i = 0; i < 8; i++) {
            //System.out.println();

            String s = a.getKey().getScaleNoteByHalfStep(i).toString().substring(0, 1) + "♯?";
            chords = chords.replaceFirst(s, "F");
            //System.out.println("chords: " + chords);

            Measure m = a.getSongMoments().get(i).getMeasure();
            assertTrue(a.measureEdit(m, MeasureEditLocation.replace, newMeasure));
            text = a.toMarkup().trim();
            //System.out.println("text  : " + text);
            assertEquals(chords, text);
        }

        {
            //  backwards
            chords = "I: A A A A V: B♯ C D♯ E";
            String chordSequence = "AAAABCDE";
            a.setChords(chords);
            for (int i = 7; i >= 4; i--) {
                String s = chordSequence.substring(i, i + 1) + "♯?";
                chords = chords.replaceFirst(s, "F");

                Measure m = a.getSongMoments().get(i).getMeasure();
                assertTrue(a.measureEdit(m, MeasureEditLocation.replace, newMeasure));
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
            System.out.println(a.findChordSection(new StringBuffer("i:")));
            System.out.println(a.findChordSection(new StringBuffer("v:")));
            System.out.println(a.findChordSection(new StringBuffer("t:")));

            StringBuffer sb = new StringBuffer("abcdefg");
            sb.delete(0, 1);
            sb.delete(0, sb.length());
            System.out.println("<" + sb.toString() + ">");

//            System.out.println(a.findMeasure("i:1"));
//            System.out.println(a.findMeasure("i:3"));
        }
    }

    @Test
    public void testSetRepeats() {
        {
            SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4, "i: A B C D v: E F G A#",
                    "i: v: bob, bob, bob berand");

            Measure m = a.findMeasure(new SongChordGridSelection(0, 4));
            SongChordGridSelection songChordGridSelection = a.findChordGridLocationForMeasureNode(m);
            a.setRepeat(songChordGridSelection, 2);
            assertEquals("I: [A B C D ] x2 V: E F G A♯",
                    a.toMarkup().trim());

            //  remove the repeat
            songChordGridSelection = a.findChordGridLocationForMeasureNode(m);
            a.setRepeat(songChordGridSelection, 1);
            assertEquals("I: A B C D V: E F G A♯",
                    a.toMarkup().trim());
        }

        {
            SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4, "i: A B C D v: E F G A#",
                    "i: v: bob, bob, bob berand");

            //System.out.println(a.getStructuralGridAsOneTextLine());
            Grid<MeasureNode> grid = a.getStructuralGrid();

            for (int row = 0; row < grid.getRowCount(); row++) {
                ArrayList<MeasureNode> cols = grid.getRow(row);
                for (int col = 1; col < cols.size(); col++)
                    for (int r = 6; r > 1; r--) {
                        Measure m = a.findMeasure(new SongChordGridSelection(row, col));
                        SongChordGridSelection songChordGridSelection = a.findChordGridLocationForMeasureNode(m);
                        a.setRepeat(songChordGridSelection, r);
                        assertEquals("I: [A B C D ] x" + (row > 0 ? 2 : r)
                                        + (row > 0 ? " V: [E F G A♯ ] x" + r : " V: E F G A♯"),
                                a.toMarkup().trim());
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
        measures = chordSection.getMeasureSequenceItems().get(0).getMeasures();
        assertEquals(4, measures.size());

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D (yo)", "v: bob, bob, bob berand");
        chordSections = new TreeSet<ChordSection>(a.getChordSections());
        assertEquals(1, chordSections.size());
        chordSection = chordSections.first();
        measures = chordSection.getMeasureSequenceItems().get(0).getMeasures();
        assertEquals(5, measures.size());
    }


    @Test
    public void testGetStructuralGrid() {
        SongBase a;
        Measure measure;

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D E F G A C: D D GD E\n"
                        + "A B C D x3\n"
                        + "Ab G Gb F", "v: bob, bob, bob berand");
        Grid<MeasureNode> grid = a.getStructuralGrid();
        //logger.info(grid.toString());
        assertEquals(5, grid.getRowCount());
        for (int r = 0; r < grid.getRowCount(); r++) {
            ArrayList<MeasureNode> row = grid.getRow(r);
            for (int c = 0; c < row.size(); c++) {
                MeasureNode node = row.get(c);
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
                                assertEquals(ScaleNote.D, measure.getChords().get(0)
                                        .getScaleChord().getScaleNote());
                                break;
                            case 3:
                                measure = (Measure) node;
                                assertEquals(ScaleNote.G, measure.getChords().get(0)
                                        .getScaleChord().getScaleNote());
                                break;
                            case 4:
                                measure = (Measure) node;
                                assertEquals(ScaleNote.E, measure.getChords().get(0)
                                        .getScaleChord().getScaleNote());
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
                                assertEquals(ScaleNote.A, measure.getChords().get(0)
                                        .getScaleChord().getScaleNote());
                                break;
                            case 4:
                                measure = (Measure) node;
                                assertEquals(ScaleNote.D, measure.getChords().get(0)
                                        .getScaleChord().getScaleNote());
                                break;
                        }
                        break;
                }

                logger.finest("grid[" + r + "," + c + "]: " + node.toString());
            }
        }

    }

    @Test
    public void testFindChordSectionLocation() {
        SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D V: D E F F# " +
                        "v1:    Em7 E E G \n" +
                        "       C D E Eb7 x2\n" +
                        "v2:    A B C D |\n" +
                        "       E F G7 G#m | x2\n" +
                        "       D C GB GbB \n" +
                        "C: F F# G G# Ab A O: C C C C B",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no:");
        logger.info(a.getSongId().toString());
        logger.fine("\t" + a.getChords());
        logger.fine(a.getRawLyrics());

        Measure m = a.findChordSectionLocation(ChordSectionLocation.parse(new StringBuffer("v:1")));
        assertEquals(Measure.parse("D", a.getBeatsPerBar()), m);
        m = a.findChordSectionLocation(ChordSectionLocation.parse(new StringBuffer("v:4")));
        assertEquals(Measure.parse("F#", a.getBeatsPerBar()), m);
        m = a.findChordSectionLocation(ChordSectionLocation.parse(new StringBuffer("v:5")));
        assertNull(m);
        m = a.findChordSectionLocation(ChordSectionLocation.parse(new StringBuffer("v1:0")));
        assertNull(m);
        m = a.findChordSectionLocation(ChordSectionLocation.parse(new StringBuffer("v1:1")));
        assertEquals(Measure.parse("Em7", a.getBeatsPerBar()), m);
        m = a.findChordSectionLocation(ChordSectionLocation.parse(new StringBuffer("v1:4")));
        assertEquals(Measure.parse("G", a.getBeatsPerBar()), m);
        m = a.findChordSectionLocation(ChordSectionLocation.parse(new StringBuffer("v1:5")));
        assertEquals(Measure.parse("C", a.getBeatsPerBar()), m);
        m = a.findChordSectionLocation(ChordSectionLocation.parse(new StringBuffer("v1:9")));//    repeats don't count here
        assertNull(m);
        m = a.findChordSectionLocation(ChordSectionLocation.parse(new StringBuffer("v2:1")));
        assertEquals(Measure.parse("A", a.getBeatsPerBar()), m);
        m = a.findChordSectionLocation(ChordSectionLocation.parse(new StringBuffer("v2:4")));
        assertEquals(Measure.parse("D", a.getBeatsPerBar()), m);
        m = a.findChordSectionLocation(ChordSectionLocation.parse(new StringBuffer("v2:5")));
        assertEquals(Measure.parse("E", a.getBeatsPerBar()), m);
        m = a.findChordSectionLocation(ChordSectionLocation.parse(new StringBuffer("v2:12")));
        assertEquals(Measure.parse("GbB", a.getBeatsPerBar()), m);
        m = a.findChordSectionLocation(ChordSectionLocation.parse(new StringBuffer("v2:13")));
        assertNull(m);
        m = a.findChordSectionLocation(ChordSectionLocation.parse(new StringBuffer("o:5")));
        assertEquals(Measure.parse("B", a.getBeatsPerBar()), m);
        m = a.findChordSectionLocation(ChordSectionLocation.parse(new StringBuffer("c:6")));
        assertEquals(Measure.parse("A", a.getBeatsPerBar()), m);
        m = a.findChordSectionLocation(ChordSectionLocation.parse(new StringBuffer("i:1")));
        assertEquals(Measure.parse("A", a.getBeatsPerBar()), m);
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
        //System.out.println(    a.getRawLyrics());
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D V: D E F F# ",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        assertEquals(lyrics, a.getRawLyrics());
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
        song.setChords(chords);
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