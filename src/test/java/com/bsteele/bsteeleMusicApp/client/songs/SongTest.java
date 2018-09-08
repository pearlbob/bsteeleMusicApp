package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.Grid;
import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.google.gwt.core.client.JsDate;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class SongTest
        extends GWTTestCase
{

    @Test
    public void testFromJson()
    {
        int songCount = 0;
        String jsonString = AppResources.INSTANCE.allSongsAsJsonString().getText();
        JSONValue jv = JSONParser.parseStrict(jsonString);
        TreeSet<ChordDescriptor> chordDescriptors = new TreeSet<>();
        if (jv != null) {
            JSONArray ja = jv.isArray();
            if (ja != null) {
                int jaLimit = ja.size();
                for (int i = 0; i < jaLimit; i++) {
                    songCount++;
                    Song song = Song.fromJsonObject(ja.get(i).isObject());

                    HashMap<ScaleChord, Integer> scaleChordMap =
                            ScaleChord.findScaleChordsUsed(song.getChordsAsString());
                    for (ScaleChord scaleChord : scaleChordMap.keySet())
                        chordDescriptors.add(scaleChord.getChordDescriptor());
                    assertTrue(song.getTitle() != null);
                    //logger.info(song.getTitle());
                    assertTrue(song.getArtist() != null);
                    assertTrue(song.getBeatsPerBar() >= 2);
                    assertTrue(song.getBeatsPerBar() <= 12);
                    assertTrue(song.getBeatsPerMinute() > 20);
                    assertTrue(song.getBeatsPerMinute() <= 400);
                    JsDate date = song.getLastModifiedDate();
                    if (date != null) {
                        //logger.info(Double.toString(date.getTime()));
                        assertTrue(date.getTime() > 1510000000000.0); //    ~6 November 2017
                    }
                    assertTrue(song.getKey() != null);
                    //logger.info("song.getChordSectionInnerHtmlMap().size() = "+song.getChordSectionInnerHtmlMap()
                    // .size());
                    assertTrue(song.getLyricsAsString().length() > 0);

                    Song song1 = Song.fromJson(song.toJson()).get(0);
//                    if ( !song.equals(song1)) {
                    //  fixme:      SongTest.testFromJson() fails on chord whitespace
//                        logger.info("equals error ref: " + song.toJson());
//                        logger.info("equals error 2nd: " + song1.toJson());
//                        song.equals(song1);
//                    }
                    Song song2 = Song.fromJson(song1.toJson()).get(0);
                    assertTrue(song.compareTo(song2) == 0);
                    assertTrue(song1.compareTo(song2) == 0);
                    assertTrue(song1.equals(song2));
                }
            }
        }
        logger.info("chords: " + chordDescriptors.size());
        logger.info("chords: " + chordDescriptors.toString());
        logger.info("count: " + songCount);
    }

    @Test
    public void testCompare()
    {

        Song aNull = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        Comparator<Song> comparator = Song.getComparatorByType(Song.ComparatorType.lastModifiedDate);

        Song bNull = Song.createSong("B", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");

        Song a = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        a.setLastModifiedDate(JsDate.create(1520605228000.0));
        Song c = Song.createSong("C", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        c.setLastModifiedDate(JsDate.create(1520605228000.0));
        Song b = Song.createSong("B", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        b.setLastModifiedDate(JsDate.create());

        logger.fine("aNull == aNull");
        assertTrue(comparator.compare(aNull, aNull) == 0);
        assertTrue(comparator.compare(aNull, bNull) < 0);   //  alphabetical
        assertTrue(comparator.compare(bNull, aNull) > 0);   //  alphabetical

        logger.fine("a == a");
        assertTrue(comparator.compare(a, a) == 0);
        assertTrue(comparator.compare(b, b) == 0);
        assertTrue(comparator.compare(a, c) < 0);   //  alphabetical
        assertTrue(comparator.compare(c, a) > 0);   //  alphabetical
        assertTrue(comparator.compare(a, b) > 0);  //  newest first
        logger.fine("b < a");
        assertTrue(comparator.compare(b, a) < 0);   //  newest first
        assertTrue(comparator.compare(c, b) > 0);  //  newest first
        assertTrue(comparator.compare(b, c) < 0);  //  newest first

        logger.fine("a < aNull");
        assertTrue(comparator.compare(a, aNull) < 0);  //  null mods last
        logger.fine("a < bNull");
        assertTrue(comparator.compare(a, bNull) < 0);  //  null mods last
        logger.fine("c < aNull");
        assertTrue(comparator.compare(c, aNull) < 0);  //  null mods last
        logger.fine("c < bNull");
        assertTrue(comparator.compare(c, bNull) < 0);  //  null mods last

        logger.fine("b < bNull");
        assertTrue(comparator.compare(b, bNull) < 0);  //  null mods last
        assertTrue(comparator.compare(c, aNull) < 0);  //  null mods last
        assertTrue(comparator.compare(c, bNull) < 0);  //  null mods last

        assertTrue(comparator.compare(aNull, a) > 0);  //  null mods last
        assertTrue(comparator.compare(aNull, b) > 0);  //  null mods last
        assertTrue(comparator.compare(aNull, c) > 0);  //  null mods last

        assertTrue(comparator.compare(bNull, a) > 0);  //  null mods last
        assertTrue(comparator.compare(bNull, b) > 0);  //  null mods last
        assertTrue(comparator.compare(bNull, c) > 0);  //  null mods last
    }

    @Test
    public void testComparatorByVersionNumber()
    {
        Comparator<Song> comparator = Song.getComparatorByType(Song.ComparatorType.versionNumber);

        Song a = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        Song a1 = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        a1.setFileName("a (1).songlyrics");
        Song a9 = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        a9.setFileName("a (9).songlyrics");
        Song a10 = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, Barbara Ann");
        a10.setFileName("a (10).songlyrics");

        assertTrue(comparator.compare(a, a) == 0);
        assertTrue(comparator.compare(a1, a1) == 0);
        assertTrue(comparator.compare(a9, a9) == 0);
        assertTrue(comparator.compare(a10, a10) == 0);

        assertTrue(comparator.compare(a, a1) < 0);
        assertTrue(comparator.compare(a1, a9) < 0);
        assertTrue(comparator.compare(a, a9) < 0);
        assertTrue(comparator.compare(a9, a10) < 0);

        assertTrue(comparator.compare(a1, a) > 0);
        assertTrue(comparator.compare(a9, a1) > 0);
        assertTrue(comparator.compare(a9, a) > 0);
        assertTrue(comparator.compare(a10, a9) > 0);
    }

    @Test
    public void testEquals()
    {

        Song a = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        a.setLastModifiedDate(JsDate.create(1520605228000.0));
        Song b = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        b.setLastModifiedDate(JsDate.create());

        assertTrue(a.equals(a));
        assertTrue(a.hashCode() == a.hashCode());
        assertTrue(a.equals(b));
        assertTrue(a.hashCode() == b.hashCode());
        b = Song.createSong("B", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        assertTrue(!a.equals(b));
        assertTrue(a.hashCode() != b.hashCode());
        b = Song.createSong("A", "bobby", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        assertTrue(!a.equals(b));
        assertTrue(a.hashCode() != b.hashCode());

        //  test differ by date only
//        b = Song.createSong("A", "bob", "photos.bsteele.com", Key.getDefault(),
//                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
//        assertTrue(!a.equals(b));
     //   assertTrue(a.hashCode() != b.hashCode());
     
        b = Song.createSong("A", "bob", "bsteele.com", Key.Ab,
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        assertTrue(!a.equals(b));
        assertTrue(a.hashCode() != b.hashCode());

        b = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                102, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        assertTrue(!a.equals(b));
        assertTrue(a.hashCode() != b.hashCode());

        b = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 3, 8, "v: A B C D", "v: bob, bob, bob berand");
        assertTrue(!a.equals(b));
        //top
        assertTrue(a.hashCode() != b.hashCode());


        b = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 8, "v: A B C D", "v: bob, bob, bob berand");
        assertTrue(!a.equals(b));
        assertTrue(a.hashCode() != b.hashCode());

        b = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A A C D", "v: bob, bob, bob berand");
        assertTrue(!a.equals(b));
        assertTrue(a.hashCode() != b.hashCode());

        b = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand.");
        assertTrue(!a.equals(b));
        assertTrue(a.hashCode() != b.hashCode());

    }

    @Test
    public void testEdits()
    {
        Song a = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
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
        Song a;
        TreeSet<ChordSection> chordSections;
        ChordSection chordSection;
        MeasureNode measureNode;
        ArrayList<Measure> measures;

        a = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        chordSections = a.getChordSections();
        assertEquals(1, chordSections.size());
        chordSection = chordSections.first();
        measures = chordSection.getMeasureSequenceItems().get(0).getMeasures();
        assertEquals(4, measures.size());

        a = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
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
        Song a;
        Measure measure;

        a = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
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

    @Override
    public String getModuleName()
    {
        return "com.bsteele.bsteeleMusicApp.BSteeleMusicAppJUnit";
    }

    private static Logger logger = Logger.getLogger("");
}