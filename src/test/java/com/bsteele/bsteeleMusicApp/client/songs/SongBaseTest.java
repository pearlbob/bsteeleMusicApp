package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.Grid;
import com.google.gwt.core.client.JsDate;
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
        extends TestCase
{

    @Test
    public void testEquals()
    {

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
        assertTrue(a.equals(b));
        assertTrue(a.hashCode() == b.hashCode());

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
    public void testEdits()
    {
        SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 8, "v: A B C D", "v: bob, bob, bob berand");
        MeasureSequenceItem vc2 = a.getChordSections().first().getMeasureSequenceItems().get(0);

        Measure measure = vc2.getMeasures().get(1);
        assertEquals(4, vc2.getMeasures().size());
        assertEquals(ScaleNote.B, measure.getChords().get(0).getScaleChord().getScaleNote());
        Measure newMeasure = Measure.parse("G", a.getBeatsPerBar());
        a.measureEdit(measure, MeasureSequenceItem.EditLocation.replace, newMeasure);
        assertEquals(4, vc2.getMeasures().size());
        measure = vc2.getMeasures().get(1);
        assertEquals(ScaleNote.G, measure.getChords().get(0).getScaleChord().getScaleNote());


        measure = vc2.getMeasures().get(0);
        assertEquals(ScaleNote.A, measure.getChords().get(0).getScaleChord().getScaleNote());
        newMeasure = Measure.parse("Gb", a.getBeatsPerBar());
        a.measureEdit(measure, MeasureSequenceItem.EditLocation.replace, newMeasure);
        assertEquals(4, vc2.getMeasures().size());
        measure = vc2.getMeasures().get(0);
        assertEquals(ScaleNote.Gb, measure.getChords().get(0).getScaleChord().getScaleNote());

        measure = vc2.getMeasures().get(3);
        assertEquals(ScaleNote.D, measure.getChords().get(0).getScaleChord().getScaleNote());
        newMeasure = Measure.parse("F", a.getBeatsPerBar());
        a.measureEdit(measure, MeasureSequenceItem.EditLocation.replace, newMeasure);
        assertEquals(4, vc2.getMeasures().size());
        measure = vc2.getMeasures().get(3);
        assertEquals(ScaleNote.F, measure.getChords().get(0).getScaleChord().getScaleNote());

        vc2 = a.getChordSections().first().getMeasureSequenceItems().get(0);
        logger.info(a.getChordSections().toString());
        logger.info(vc2.toString());
        logger.info(vc2.getMeasures().toString());
    }

    @Test
    public void testComments()
    {
        SongBase a;
        TreeSet<ChordSection> chordSections;
        ChordSection chordSection;
        MeasureNode measureNode;
        ArrayList<Measure> measures;

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        chordSections = a.getChordSections();
        assertEquals(1, chordSections.size());
        chordSection = chordSections.first();
        measures = chordSection.getMeasureSequenceItems().get(0).getMeasures();
        assertEquals(4, measures.size());

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D (yo)", "v: bob, bob, bob berand");
        chordSections = a.getChordSections();
        assertEquals(1, chordSections.size());
        chordSection = chordSections.first();
        measures = chordSection.getMeasureSequenceItems().get(0).getMeasures();
        assertEquals(5, measures.size());
    }


    @Test
    public void testGetStructuralGrid()
    {
        SongBase a;
        Measure measure;

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D E F G A C: D D GD E\n"
                        + "A B C D x3\n"
                        + "Ab G Gb F", "v: bob, bob, bob berand");
        Grid<MeasureNode> grid = a.getStructuralGrid();
        logger.info(grid.toString());
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
    private static final SongBase createSongBase(@NotNull String title, @NotNull String artist,
                                        @NotNull String copyright,
                                        @NotNull Key key, int bpm, int beatsPerBar, int unitsPerMeasure,
                                        @NotNull String chords, @NotNull String lyrics)
    {
        SongBase song = new SongBase();
        song.setTitle(title);
        song.setArtist(artist);
        song.setCopyright(copyright);
        song.setKey(key);
        song.setUnitsPerMeasure(unitsPerMeasure);
        song.setRawLyrics ( lyrics);
        song.setChords (chords);

        song.parseChordTable(chords);
        song.parseLyricsToSectionSequence(lyrics);
        song.parseLyrics();
        song.setBeatsPerMinute(bpm);
        song.setBeatsPerBar(beatsPerBar);
        song.parse();

        return song;
    }

//    @Override
//    public String getModuleName()
//    {
//        return "com.bsteele.bsteeleMusicApp.BSteeleMusicAppJUnit";
//    }

    private static Logger logger = Logger.getLogger("");
}