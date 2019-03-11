package com.bsteele.bsteeleMusicApp.shared.songs;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class ChordSectionLocationTest {

    @Test
    public void parse() {
        for (Section section : Section.values()) {
            for (int v = 1; v <= 4; v++) {
                for (int phraseIndex = 1; phraseIndex <= 3; phraseIndex++) {
                    SectionVersion sectionVersion = new SectionVersion(section, v);
                    ChordSection chordSection = new ChordSection(sectionVersion);
                    for (int index = 1; index <= 40; index++) {
                        ChordSectionLocation chordSectionLocationExpected = new ChordSectionLocation(chordSection, phraseIndex, index);
                        StringBuffer sb = new StringBuffer(sectionVersion.getId() + ":" + phraseIndex + ":" + index);
                        ChordSectionLocation chordSectionLocation = ChordSectionLocation.parse(sb);
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
                    ChordSection chordSection = new ChordSection(sectionVersion);
                    for (int index = 1; index <= 40; index++) {
                        ChordSectionLocation chordSectionLocationExpected = new ChordSectionLocation(chordSection, phraseIndex, index);
                        StringBuffer sb = new StringBuffer(chordSectionLocationExpected.toString());
                        ChordSectionLocation chordSectionLocation = ChordSectionLocation.parse(sb);
                        assertEquals(chordSectionLocationExpected, chordSectionLocation);
                    }
                }
            }
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
        ChordSection chordSection = new ChordSection(sectionVersion);
        assertEquals(Measure.parse("Em7", beatsPerBar),
                a.findMeasure(new ChordSectionLocation(chordSection, 0, 0)));
        assertEquals(Measure.parse("G", beatsPerBar),
                a.findMeasure(new ChordSectionLocation(chordSection, 0, 3)));
        assertEquals(Measure.parse("Eb7", beatsPerBar),
                a.findMeasure(new ChordSectionLocation(chordSection, 1, 3)));
        assertNull(a.findMeasure(new ChordSectionLocation(chordSection, 0, 9)));

        chordSection = ChordSection.parse("v2:", beatsPerBar);
        assertEquals(Measure.parse("A", beatsPerBar),
                a.findMeasure(new ChordSectionLocation(chordSection, 0, 0)));
        assertEquals(Measure.parse("D", beatsPerBar),
                a.findMeasure(new ChordSectionLocation(chordSection, 0, 3)));
        assertEquals(Measure.parse("G#m", beatsPerBar),
                a.findMeasure(new ChordSectionLocation(chordSection, 0, 7)));
        assertEquals(Measure.parse("GbB", beatsPerBar),
                a.findMeasure(new ChordSectionLocation(chordSection, 1, 3)));
        assertNull(a.findMeasure(new ChordSectionLocation(chordSection, 1, 4234)));

        //  no Ch2:
        section = Section.chorus;
        chordSection = new ChordSection(new SectionVersion(section, v));
        assertNull(a.findMeasure(new ChordSectionLocation(chordSection, 0, 0)));

        chordSection = new ChordSection(new SectionVersion(section, 0));
        assertEquals(Measure.parse("F", beatsPerBar),
                a.findMeasure(new ChordSectionLocation(chordSection, 0, 0)));

        chordSection = new ChordSection(new SectionVersion(Section.outro));
        assertEquals(Measure.parse("B", beatsPerBar),
                a.findMeasure(new ChordSectionLocation(chordSection, 0, 4)));
        assertNull(a.findMeasure(new ChordSectionLocation(chordSection, 0, 5)));
    }

}