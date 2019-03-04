package com.bsteele.bsteeleMusicApp.shared.songs;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureTest extends TestCase {

    @Test
    public void testParse() {

        {
            String s = "A.B.";
            Measure m = Measure.parse(s, 5);
            assertNotNull(m);
            //  explicit measure short of beats is left as specified
            assertEquals("A.B.", m.toMarkup());
        }
        {
            String s = "AB";
            Measure m = Measure.parse(s, 5);
            assertNotNull(m);
            //  5/4 split across two chords, first one gets the extra beat
            assertEquals("A..B.", m.toMarkup());
        }
        {
            String s = "A.B.";
            Measure m = Measure.parse(s, 4);
            assertNotNull(m);
            //  default to simplest expression
            assertEquals("AB", m.toMarkup());
        }
        {
            String s = "A/GA/F♯";
            Measure m = Measure.parse(s, 4);
            assertNotNull(m);
            assertEquals(s, m.toMarkup());
        }
        {
            String s = "A/G";
            Measure m = Measure.parse(s, 4);
            assertNotNull(m);
            assertEquals(s, m.toMarkup());
        }
        {
            String s = "F.";
            Measure m = Measure.parse(s, 4);
            assertNotNull(m);
            assertEquals(s, m.toMarkup());
        }
        {
            String s = "F.";
            Measure m = Measure.parse(s, 2);
            assertNotNull(m);
            assertEquals("F", m.toMarkup());
        }
        {
            String s = "F..";
            Measure m = Measure.parse(s, 4);
            assertNotNull(m);
            assertEquals(s, m.toMarkup());
        }
        {
            String s = "F...";
            Measure m = Measure.parse(s, 4);
            assertNotNull(m);
            assertEquals("F", m.toMarkup());
        }
        {
            String s = "A..B";
            Measure m = Measure.parse(s, 4);
            assertNotNull(m);
            assertEquals(s, m.toMarkup());
        }
        {
            String s = "AB..";
            Measure m = Measure.parse(s, 4);
            assertNotNull(m);
            assertEquals(s, m.toMarkup());
        }
        {
            String s = "ABC.";
            Measure m = Measure.parse(s, 4);
            assertNotNull(m);
            assertEquals(s, m.toMarkup());
        }
        {
            String s = "A.BC";
            Measure m = Measure.parse(s, 4);
            assertNotNull(m);
            assertEquals(s, m.toMarkup());
        }

        {
            String s = "E♭F";
            Measure m = Measure.parse(s, 4);
            assertNotNull(m);
            assertEquals(s, m.toMarkup());
        }
        {
            String s = "E♭F";
            Measure m = Measure.parse(s, 3);
            assertNotNull(m);
            assertEquals("E♭.F", m.toMarkup());
        }
        {
            Measure m = Measure.parse("E#m7.. ", 2);
            //  too many beats or over specified, doesn't cover the beats per bar
            assertNotNull(m);  //  fixme: Measure.parse() on errors
        }
        {
            int beatsPerBar = 4;
            Measure m = Measure.parse(" .G ", beatsPerBar);
            assertNull(m);
        }
        {
            int beatsPerBar = 3;
            //System.out.println("beatsPerBar: " + beatsPerBar);
            Measure m;
            m = Measure.parse(" .G ", beatsPerBar);
            assertNull(m);
            m = Measure.parse("E#m7... ", beatsPerBar);
            assertNotNull(m);   //  fixme: Measure.parse() on errors
            m = Measure.parse("E#m7. ", beatsPerBar);
            assertNotNull(m);   //  fixme: Measure.parse() on errors
        }

        {
            //  test beat allocation
            Measure m = Measure.parse("EAB", 4);
            assertEquals(3, m.getChords().size());
            assertEquals(2, m.getChords().get(0).getBeats());
            assertEquals(1, m.getChords().get(1).getBeats());
            assertEquals(1, m.getChords().get(2).getBeats());

            m = Measure.parse("EA", 4);
            assertEquals(2, m.getChords().size());
            assertEquals(2, m.getChords().get(0).getBeats());
            assertEquals(2, m.getChords().get(1).getBeats());
            m = Measure.parse("E..A", 4);
            assertEquals(2, m.getChords().size());
            assertEquals(3, m.getChords().get(0).getBeats());
            assertEquals(1, m.getChords().get(1).getBeats());

            m = Measure.parse("E.A", 4);
            assertEquals(2, m.getChords().size());
            assertEquals(2, m.getChords().get(0).getBeats());
            assertEquals(2, m.getChords().get(1).getBeats());
            m = Measure.parse("E.A.", 4);
            assertEquals(2, m.getChords().size());
            assertEquals(2, m.getChords().get(0).getBeats());
            assertEquals(2, m.getChords().get(1).getBeats());
            m = Measure.parse("EA.", 4);
            assertEquals(2, m.getChords().size());
            assertEquals(2, m.getChords().get(0).getBeats());
            assertEquals(2, m.getChords().get(1).getBeats());
            m = Measure.parse("EA.", 6);
            assertEquals(2, m.getChords().size());
            assertEquals(4, m.getChords().get(0).getBeats());
            assertEquals(2, m.getChords().get(1).getBeats());


            //  too many specific beats
            m = Measure.parse("E..A.", 4);
            assertNotNull(m);  //  fixme: Measure.parse() on errors

            //  too few specific beats
            m = Measure.parse("E..A.", 6);
            assertNotNull(m);  //  fixme: Measure.parse() on errors
        }

        for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++) {
            Measure m = Measure.parse("A", beatsPerBar);
            assertEquals(beatsPerBar, m.getBeatCount());
            assertEquals(1, m.getChords().size());
            Chord chord = m.getChords().get(0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.A), beatsPerBar, beatsPerBar), chord);
        }

        for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++) {
            Measure m = Measure.parse("BC", beatsPerBar);
            assertEquals(beatsPerBar, m.getBeatCount());
            assertEquals(2, m.getChords().size());
            Chord chord0 = m.getChords().get(0);
            Chord chord1 = m.getChords().get(1);
            int beat1 = beatsPerBar / 2;    //  smaller beat on 3 in 3 beats
            int beat0 = beatsPerBar - beat1;
            Chord refChord = new Chord(new ScaleChord(ScaleNote.B), beat0, beatsPerBar);
            assertEquals(refChord, chord0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.C), beat1, beatsPerBar), chord1);
        }
        for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++) {
            // System.out.println("beatsPerBar: " + beatsPerBar);
            Measure m = Measure.parse("E#m7. ", beatsPerBar);
            if (beatsPerBar > 2)
                //  over specified, doesn't cover the beats per bar
                assertNotNull(m);  //  fixme: Measure.parse() on errors
            else {
                assertEquals(beatsPerBar, m.getBeatCount());
                assertEquals(1, m.getChords().size());
                Chord chord0 = m.getChords().get(0);
                assertEquals(new Chord(new ScaleChord(ScaleNote.Es, ChordDescriptor.minor7), beatsPerBar, beatsPerBar), chord0);
            }
        }
        for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++) {
            //System.out.println("beatsPerBar: " + beatsPerBar);
            Measure m = Measure.parse("E#m7.. ", beatsPerBar);
            if (beatsPerBar != 3)
                //  too many beats or over specified, doesn't cover the beats per bar
                assertNotNull(m);  //  fixme: Measure.parse() on errors
            else {
                assertEquals(beatsPerBar, m.getBeatCount());
                assertEquals(1, m.getChords().size());
                Chord chord0 = m.getChords().get(0);
                assertEquals(new Chord(new ScaleChord(ScaleNote.Es, ChordDescriptor.minor7), beatsPerBar, beatsPerBar), chord0);
            }
        }
        for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++) {
            Measure m = Measure.parse("E#m7Gb7", beatsPerBar);
            assertEquals(beatsPerBar, m.getBeatCount());
            assertEquals(2, m.getChords().size());
            Chord chord0 = m.getChords().get(0);
            Chord chord1 = m.getChords().get(1);
            int beat1 = beatsPerBar / 2;
            int beat0 = beatsPerBar - beat1;
            assertEquals(new Chord(new ScaleChord(ScaleNote.Es, ChordDescriptor.minor7), beat0, beatsPerBar), chord0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.Gb, ChordDescriptor.dominant7), beat1, beatsPerBar), chord1);
        }
        for (int beatsPerBar = 3; beatsPerBar <= 4; beatsPerBar++) {
            Measure m = Measure.parse("F#m7.Asus4", beatsPerBar);
            assertEquals(beatsPerBar, m.getBeatCount());
            assertEquals(2, m.getChords().size());
            Chord chord0 = m.getChords().get(0);
            Chord chord1 = m.getChords().get(1);
            int beat0 = 2;
            int beat1 = beatsPerBar - beat0;
            assertEquals(new Chord(new ScaleChord(ScaleNote.Fs, ChordDescriptor.minor7), beat0, beatsPerBar), chord0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.A, ChordDescriptor.suspended4), beat1, beatsPerBar), chord1);
        }
        for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++) {
            Measure m = Measure.parse("A/G#", beatsPerBar);
            assertEquals(beatsPerBar, m.getBeatCount());
            assertEquals(1, m.getChords().size());
            Chord chord = m.getChords().get(0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.A), beatsPerBar, beatsPerBar,
                    new ScaleChord(ScaleNote.Gs), ChordAnticipationOrDelay.none), chord);
        }
        for (int beatsPerBar = 3; beatsPerBar <= 4; beatsPerBar++) {
            Measure m = Measure.parse("C/F#.G", beatsPerBar);
            assertEquals(beatsPerBar, m.getBeatCount());
            assertEquals(2, m.getChords().size());
            Chord chord0 = m.getChords().get(0);
            Chord chord1 = m.getChords().get(1);
            int beat0 = 2;
            int beat1 = beatsPerBar - beat0;
            assertEquals(new Chord(new ScaleChord(ScaleNote.C), beat0, beatsPerBar,
                    new ScaleChord(ScaleNote.Fs), ChordAnticipationOrDelay.none), chord0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.G), beat1, beatsPerBar), chord1);
        }
        {
            for (int beatsPerBar = 3; beatsPerBar <= 4; beatsPerBar++) {
                Measure m0 = Measure.parse("C", beatsPerBar);
                Measure m = Measure.parse(new StringBuffer("-"), beatsPerBar, m0);
                assertEquals(beatsPerBar, m.getBeatCount());
                assertEquals(1, m.getChords().size());
                assertEquals(m0.getChords(), m.getChords());
            }
        }
        {
            for (int beatsPerBar = 3; beatsPerBar <= 4; beatsPerBar++) {
                Measure m = Measure.parse("X", beatsPerBar);
                assertEquals(beatsPerBar, m.getBeatCount());
                assertEquals(0, m.getChords().size());
            }
        }
    }
}