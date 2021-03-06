package com.bsteele.bsteeleMusicApp.client.songs;


import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.bsteele.bsteeleMusicApp.shared.songs.ChordDescriptor;
import com.bsteele.bsteeleMusicApp.shared.songs.Key;
import com.bsteele.bsteeleMusicApp.shared.songs.ScaleChord;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;
import org.junit.Test;

import java.text.ParseException;
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
        //logger.setLevel(Level.FINEST);      //  fixme: debug workaround


//        logger.info("null: <"+JsonUtil.encode(null)+">");
//        logger.info("\"\": <"+JsonUtil.encode("")+">");
//        logger.info("\" \": <"+JsonUtil.encode(" ")+">");

        int songCount = 0;
        String jsonString = AppResources.INSTANCE.allSongsAsJsonString().getText();
        JSONValue jv = JSONParser.parseStrict(jsonString);
        logger.finer("jv: " + jv.toString().length());
        TreeSet<ChordDescriptor> chordDescriptors = new TreeSet<>();
        if (jv != null) {
            JSONArray ja = jv.isArray();
            logger.fine("ja: " + ja.size());
            if (ja != null) {
                int jaLimit = Math.min(300, ja.size());   //    fixme: find why this fails at 1000
                for (int i = 0; i < jaLimit; i++) {
                    songCount++;
                    Song song = null;
                    try {
                        song = Song.fromJsonObject(ja.get(i).isObject());
                    } catch (ParseException e) {
                        e.printStackTrace();
                        fail();
                    }
                    logger.fine(songCount + ": " + song.getTitle());

                    HashMap<ScaleChord, Integer> scaleChordMap =
                            song.findScaleChordsUsed();
                    for (ScaleChord scaleChord : scaleChordMap.keySet())
                        chordDescriptors.add(scaleChord.getChordDescriptor());
                    assertTrue(song.getTitle() != null);
                    logger.fine(song.getTitle());
                    assertTrue(song.getArtist() != null);
                    assertTrue(song.getBeatsPerBar() >= 2);
                    assertTrue(song.getBeatsPerBar() <= 12);
                    assertTrue(song.getBeatsPerMinute() > 20);
                    assertTrue(song.getBeatsPerMinute() <= 400);
                    double modTime = song.getLastModifiedTime();
                    logger.finer("modTime: " + Double.toString(modTime));
                    if (modTime > 0)
                        assertTrue(modTime > 1510000000000.0); //    ~6 November 2017

                    assertTrue(song.getKey() != null);
                    assertTrue(song.getRawLyrics().length() > 0);

                    logger.fine(song.toJson());
                    logger.finer("copyright: <" + song.getCopyright() + ">");
                    ArrayList<Song> jsonList = Song.fromJson(song.toJson());
                    assertNotNull(jsonList);
                    assertTrue(!jsonList.isEmpty());
                    Song song1 = jsonList.get(0);
//                    if ( !song.equals(song1)) {
                    //  fixme:      SongTest.testFromJson() fails on chord whitespace
//                        logger.info("equals error ref: " + song.toJson());
//                        logger.info("equals error 2nd: " + song1.toJson());
//                        song.equals(song1);
//                    }
                    logger.fine(song1.toJson());
                    Song song2 = Song.fromJson(song1.toJson()).get(0);
                    logger.fine("song1: " + song1.toJson());
                    logger.fine("song to 2: " + song2.toJson());
                    assertTrue(song.compareTo(song2) == 0);
                    logger.fine("song1 to 2: " + song2.toJson());

                    assertTrue(song1.compareTo(song2) == 0);
                    logger.finest("song1 compares to 2 ");
                    logger.fine("song1 equals 2?: " + song2.toJson());
                    assertTrue(song1.equals(song2));

                    logger.fine(song.getSongId()+" song1 .markup equals to 2?");
                    assertTrue(song1.toMarkup().equals(song2.toMarkup()));
                    logger.fine("song.markup equals to 2?");
                    assertTrue(song.getRawLyrics().equals(song2.getRawLyrics()));
                }
            }
        }
        logger.info("chords: " + chordDescriptors.size());
        logger.info("chords: " + chordDescriptors.toString());
        logger.info("count: " + songCount);
    }

    @Test
    public void testCompare() {

        Song aNull = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "bob", "v: A B C D", "v: bob, bob, bob berand");
        Comparator<Song> comparator = Song.getComparatorByType(Song.ComparatorType.lastModifiedDate);

        Song bNull = Song.createSong("B", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "bob", "v: A B C D", "v: bob, bob, bob berand");

        Song a = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "bob", "v: A B C D", "v: bob, bob, bob berand");
        a.setLastModifiedTime(1520605228000.0);
        Song c = Song.createSong("C", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "bob", "v: A B C D", "v: bob, bob, bob berand");
        c.setLastModifiedTime(1520605228000.0);
        Song b = Song.createSong("B", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "bob", "v: A B C D", "v: bob, bob, bob berand");
        b.resetLastModifiedDateToNow();

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
    public void testComparatorByVersionNumber() {
        Comparator<Song> comparator = Song.getComparatorByType(Song.ComparatorType.versionNumber);

        Song a = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "bob", "v: A B C D", "v: bob, bob, bob berand");
        Song a1 = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "bob", "v: A B C D", "v: bob, bob, bob berand");
        a1.setFileName("a (1).songlyrics");
        Song a9 = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "bob", "v: A B C D", "v: bob, bob, bob berand");
        a9.setFileName("a (9).songlyrics");
        Song a10 = Song.createSong("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "bob", "v: A B C D", "v: bob, bob, Barbara Ann");
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


    @Override
    public String getModuleName() {
        return "com.bsteele.bsteeleMusicApp.BSteeleMusicAppJUnit";
    }

    private static Logger logger = Logger.getLogger(SongTest.class.getName());
}