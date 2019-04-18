package com.bsteele.bsteeleMusicApp.shared.songs;

import org.junit.Test;

import java.text.ParseException;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ChordSectionLocationTest {

    @Test
    public void parse() {
        int beatsPerBar = 4;

        try {
        {
                assertEquals(ChordSection.parse("i:", beatsPerBar).toString().trim(), ChordSectionLocation.parse("i:").toString());
            ChordSectionLocation chordSectionLocation = ChordSectionLocation.parse("i:");
            assertEquals("I:", chordSectionLocation.toString());
            assertFalse(chordSectionLocation.isPhrase());
            assertFalse(chordSectionLocation.isMeasure());

            chordSectionLocation = ChordSectionLocation.parse("verse2:");
            assertEquals("V2:", chordSectionLocation.toString());
            assertFalse(chordSectionLocation.isPhrase());
            assertFalse(chordSectionLocation.isMeasure());

            chordSectionLocation = ChordSectionLocation.parse("verse2:0");
            assertEquals("V2:0", chordSectionLocation.toString());
            assertTrue(chordSectionLocation.isPhrase());
            assertFalse(chordSectionLocation.isMeasure());

            chordSectionLocation = ChordSectionLocation.parse("verse2:0:12");
            assertEquals("V2:0:12", chordSectionLocation.toString());
            assertFalse(chordSectionLocation.isPhrase());
            assertTrue(chordSectionLocation.isMeasure());
        }

        for (Section section : Section.values()) {
            for (int v = 1; v <= 4; v++) {
                for (int phraseIndex = 1; phraseIndex <= 3; phraseIndex++) {
                    SectionVersion sectionVersion = new SectionVersion(section, v);
                    for (int index = 1; index <= 40; index++) {
                        ChordSectionLocation chordSectionLocationExpected = new ChordSectionLocation(sectionVersion, phraseIndex, index);
                        ChordSectionLocation chordSectionLocation = ChordSectionLocation.parse(
                                sectionVersion.getId() + ":" + phraseIndex + ":" + index);
                        //System.out.println(chordSectionLocationExpected);
                        assertEquals(chordSectionLocationExpected, chordSectionLocation);
                    }
                }
            }
        }

        for (Section section : Section.values()) {
            for (int v = 1; v <= 4; v++) {
                for (int phraseIndex = 1; phraseIndex <= 3; phraseIndex++) {
                    SectionVersion sectionVersion = new SectionVersion(section, v);
                    for (int index = 1; index <= 40; index++) {
                        ChordSectionLocation chordSectionLocationExpected = new ChordSectionLocation(sectionVersion, phraseIndex, index);
                        ChordSectionLocation chordSectionLocation = ChordSectionLocation.parse(
                                chordSectionLocationExpected.toString());
                        assertEquals(chordSectionLocationExpected, chordSectionLocation);
                    }
                }
            }
        }
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * find the chord section in the song
     */
    @Test
    public void find() {
        int beatsPerBar = 4;
        SongBase a = SongBaseTest.createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "i: A B C D V: D E F F# " +
                        "v1:    Em7 E E G \n" +
                        "       C D E Eb7 x2\n" +
                        "v2:    A B C D |\n" +
                        "       E F G7 G#m | x2\n" +
                        "       D C GB GbB \n" +
                        "C: F F# G G# Ab A O: C C C C B",
                "i:\nv: bob, bob, bob berand\nv1: lala \nv2: sosos \nc: sing chorus here \no:");
        Section section = Section.verse;
        int v = 1;
        SectionVersion sectionVersion = new SectionVersion(section, v);

        try {
            assertEquals(Measure.parse("Em7", beatsPerBar),
                    a.findMeasureNode(new ChordSectionLocation(sectionVersion, 0, 0)));
            assertEquals(Measure.parse("G", beatsPerBar),
                    a.findMeasureNode(new ChordSectionLocation(sectionVersion, 0, 3)));
            assertEquals(Measure.parse("Eb7", beatsPerBar),
                    a.findMeasureNode(new ChordSectionLocation(sectionVersion, 1, 3)));
            assertNull(a.findMeasureNode(new ChordSectionLocation(sectionVersion, 0, 9)));

            sectionVersion = SectionVersion.parse("v2:");
            assertEquals(Measure.parse("A", beatsPerBar),
                    a.findMeasureNode(new ChordSectionLocation(sectionVersion, 0, 0)));
            assertEquals(Measure.parse("D", beatsPerBar),
                    a.findMeasureNode(new ChordSectionLocation(sectionVersion, 0, 3)));
            assertEquals(Measure.parse("G#m", beatsPerBar),
                    a.findMeasureNode(new ChordSectionLocation(sectionVersion, 0, 7)));
            assertEquals(Measure.parse("GbB", beatsPerBar),
                    a.findMeasureNode(new ChordSectionLocation(sectionVersion, 1, 3)));
            assertNull(a.findMeasureNode(new ChordSectionLocation(sectionVersion, 1, 4)));
            assertNull(a.findMeasureNode(new ChordSectionLocation(sectionVersion, 1, 4234)));


            //  no Ch2:
            section = Section.chorus;
            sectionVersion = new SectionVersion(section, v);
            assertNull(a.findMeasureNode(new ChordSectionLocation(sectionVersion, 0, 0)));

            sectionVersion = new SectionVersion(section, 0);
            assertEquals(Measure.parse("F", beatsPerBar),
                    a.findMeasureNode(new ChordSectionLocation(sectionVersion, 0, 0)));

            sectionVersion = new SectionVersion(Section.outro);
            assertEquals(Measure.parse("B", beatsPerBar),
                    a.findMeasureNode(new ChordSectionLocation(sectionVersion, 0, 4)));
            assertNull(a.findMeasureNode(new ChordSectionLocation(sectionVersion, 0, 5)));
        }
        catch (ParseException pex){
            logger.info("unexpected parse error: "+pex.getMessage());
        }
    }

    private static Logger logger = Logger.getLogger(ChordSectionLocationTest.class.getName());

}