package com.bsteele.bsteeleMusicApp.client.songs;

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
        extends GWTTestCase {

    @Test
    public void testFromJson() {
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

                    HashMap<ScaleChord, Integer> scaleChordMap = ScaleChord.findScaleChordsUsed(song.getChordsAsString());
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
                        assertTrue(date.getTime() > 1518820808000.0);
                    }
                    assertTrue(song.getKey() != null);
                    assertTrue(song.getChordSectionMap().size() > 0);
                    assertTrue(song.getLyricsAsString().length() > 0);

                    Song song1 = Song.fromJson(song.toJson());
//                    if ( !song.equals(song1)) {
                    //  fixme:      SongTest.testFromJson() fails on chord whitespace
//                        logger.info("equals error ref: " + song.toJson());
//                        logger.info("equals error 2nd: " + song1.toJson());
//                        song.equals(song1);
//                    }
                    Song song2 = Song.fromJson(song1.toJson());
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
    public void testChordSectionParse() {
        {
            ChordSection chordSection = ChordSection.parse("I: A B C D\n" +
                    "AbBb/G# Am7 Ebsus4 C7/Bb", 4);
            assertTrue(chordSection != null);
            SectionVersion intro = new SectionVersion(Section.intro);
            assertTrue(chordSection.getSection().equals(intro));
            ArrayList<MeasureSequenceItem> measureSequences = chordSection.getMeasureSequenceItems();
            assertTrue(measureSequences != null);
            assertEquals(1, measureSequences.size());
            ArrayList<Measure> measures = measureSequences.get(0).getMeasures();
            assertEquals(8, measures.size());
            assertEquals(ScaleNote.A, measures.get(0).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.B, measures.get(1).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.C, measures.get(2).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.D, measures.get(3).getChords().get(0).getScaleChord().getScaleNote());
        }
        {
            ChordSection chordSection = ChordSection.parse("I: A B C D\n" +
                    "AbBb/G# Am7 Ebsus4 C7/Bb x4", 4);
            assertTrue(chordSection != null);
            SectionVersion intro = new SectionVersion(Section.intro);
            assertTrue(chordSection.getSection().equals(intro));
            ArrayList<MeasureSequenceItem> measureSequenceItems = chordSection.getMeasureSequenceItems();
            assertTrue(measureSequenceItems != null);
            assertEquals(2, measureSequenceItems.size());
            MeasureSequenceItem measureSequenceItem = measureSequenceItems.get(0);
            assertEquals(0, measureSequenceItem.getSequenceNumber());
            assertEquals(4, measureSequenceItem.getTotalMeasures());
            ArrayList<Measure> measures = measureSequenceItem.getMeasures();
            assertEquals(4, measures.size());
            assertEquals(ScaleNote.A, measures.get(0).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.B, measures.get(1).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.C, measures.get(2).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.D, measures.get(3).getChords().get(0).getScaleChord().getScaleNote());
            measureSequenceItem = measureSequenceItems.get(1);
            assertEquals(4, measureSequenceItem.getSequenceNumber());
            assertEquals(4 * 4, measureSequenceItem.getTotalMeasures());
            measures = measureSequenceItem.getMeasures();
            assertEquals(4, measures.size());
            assertEquals(2, measures.get(0).getChords().size());
            assertEquals(ScaleNote.Ab, measures.get(0).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.Bb, measures.get(0).getChords().get(1).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.A, measures.get(1).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.Eb, measures.get(2).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.C, measures.get(3).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(4 + 4 * 4, chordSection.getTotalMeasures());
        }
        {
            ChordSection chordSection = ChordSection.parse("I:       A B C D\n\n", 4);
            assertEquals(4, chordSection.getTotalMeasures());
            chordSection = ChordSection.parse("\n\tI:\n       A B C D\n\n", 4);
            assertEquals(4, chordSection.getTotalMeasures());
            chordSection = ChordSection.parse("v: A B C D\n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb\n", 4);
            assertEquals(8, chordSection.getTotalMeasures());
            chordSection = ChordSection.parse("v: A B C D\n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb x4\n", 4);
            assertEquals(4 + 4 * 4, chordSection.getTotalMeasures());
            chordSection = ChordSection.parse("v: \n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb x4\n", 4);
            assertEquals(4 * 4, chordSection.getTotalMeasures());
            chordSection = ChordSection.parse("v: A B C D\n\n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb x4\n", 4);
            assertEquals(4 + 4 * 4, chordSection.getTotalMeasures());
            chordSection = ChordSection.parse("v: A B C D\n\n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb x4\n" +
                    "G F F# E", 4);
            assertEquals(4 + 4 * 4 + 4, chordSection.getTotalMeasures());
        }
    }

    @Test
    public void testCompare() {

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
    public void testEquals() {

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
        b = Song.createSong("A", "bob", "photos.bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        assertTrue(!a.equals(b));
        assertTrue(a.hashCode() != b.hashCode());

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

    @Override
    public String getModuleName() {
        return "com.bsteele.bsteeleMusicApp.BSteeleMusicAppJUnit";
    }

    private static Logger logger = Logger.getLogger("");
}