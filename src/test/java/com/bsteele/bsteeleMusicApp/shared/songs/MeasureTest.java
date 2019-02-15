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
            Measure m = Measure.testParse("E#m7.. ", 2);
            //  too many beats or over specified, doesn't cover the beats per bar
            assertNotNull(m);  //  fixme: Measure.testParse() on errors
        }
        {
            int beatsPerBar = 4;
            Measure m = Measure.testParse(" .G ", beatsPerBar);
            assertNull(m);
        }
        {
            int beatsPerBar = 3;
            //System.out.println("beatsPerBar: " + beatsPerBar);
            Measure m;
            m = Measure.testParse(" .G ", beatsPerBar);
            assertNull(m);
            m = Measure.testParse("E#m7... ", beatsPerBar);
            assertNotNull(m);   //  fixme: Measure.testParse() on errors
            m = Measure.testParse("E#m7. ", beatsPerBar);
            assertNotNull(m);   //  fixme: Measure.testParse() on errors
        }

        {
            //  test beat allocation
            Measure m = Measure.testParse("EAB", 4);
            assertEquals(3, m.getChords().size());
            assertEquals(2, m.getChords().get(0).getBeats());
            assertEquals(1, m.getChords().get(1).getBeats());
            assertEquals(1, m.getChords().get(2).getBeats());

            m = Measure.testParse("EA", 4);
            assertEquals(2, m.getChords().size());
            assertEquals(2, m.getChords().get(0).getBeats());
            assertEquals(2, m.getChords().get(1).getBeats());
            m = Measure.testParse("E..A", 4);
            assertEquals(2, m.getChords().size());
            assertEquals(3, m.getChords().get(0).getBeats());
            assertEquals(1, m.getChords().get(1).getBeats());

            m = Measure.testParse("E.A", 4);
            assertEquals(2, m.getChords().size());
            assertEquals(2, m.getChords().get(0).getBeats());
            assertEquals(2, m.getChords().get(1).getBeats());
            m = Measure.testParse("E.A.", 4);
            assertEquals(2, m.getChords().size());
            assertEquals(2, m.getChords().get(0).getBeats());
            assertEquals(2, m.getChords().get(1).getBeats());
            m = Measure.testParse("EA.", 4);
            assertEquals(2, m.getChords().size());
            assertEquals(2, m.getChords().get(0).getBeats());
            assertEquals(2, m.getChords().get(1).getBeats());
            m = Measure.testParse("EA.", 6);
            assertEquals(2, m.getChords().size());
            assertEquals(4, m.getChords().get(0).getBeats());
            assertEquals(2, m.getChords().get(1).getBeats());


            //  too many specific beats
            m = Measure.testParse("E..A.", 4);
            assertNotNull(m);  //  fixme: Measure.testParse() on errors

            //  too few specific beats
            m = Measure.testParse("E..A.", 6);
            assertNotNull(m);  //  fixme: Measure.testParse() on errors
        }

        for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++) {
            Measure m = Measure.testParse("A", beatsPerBar);
            assertEquals(beatsPerBar, m.getBeatCount());
            assertEquals(1, m.getChords().size());
            Chord chord = m.getChords().get(0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.A), beatsPerBar, beatsPerBar), chord);
        }

        for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++) {
            Measure m = Measure.testParse("BC", beatsPerBar);
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
            Measure m = Measure.testParse("E#m7. ", beatsPerBar);
            if (beatsPerBar > 2)
                //  over specified, doesn't cover the beats per bar
                assertNotNull(m);  //  fixme: Measure.testParse() on errors
            else {
                assertEquals(beatsPerBar, m.getBeatCount());
                assertEquals(1, m.getChords().size());
                Chord chord0 = m.getChords().get(0);
                assertEquals(new Chord(new ScaleChord(ScaleNote.Es, ChordDescriptor.minor7), beatsPerBar, beatsPerBar), chord0);
            }
        }
        for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++) {
            //System.out.println("beatsPerBar: " + beatsPerBar);
            Measure m = Measure.testParse("E#m7.. ", beatsPerBar);
            if (beatsPerBar != 3)
                //  too many beats or over specified, doesn't cover the beats per bar
                assertNotNull(m);  //  fixme: Measure.testParse() on errors
            else {
                assertEquals(beatsPerBar, m.getBeatCount());
                assertEquals(1, m.getChords().size());
                Chord chord0 = m.getChords().get(0);
                assertEquals(new Chord(new ScaleChord(ScaleNote.Es, ChordDescriptor.minor7), beatsPerBar, beatsPerBar), chord0);
            }
        }
        for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++) {
            Measure m = Measure.testParse("E#m7Gb7", beatsPerBar);
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
            Measure m = Measure.testParse("F#m7.Asus4", beatsPerBar);
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
            Measure m = Measure.testParse("A/G#", beatsPerBar);
            assertEquals(beatsPerBar, m.getBeatCount());
            assertEquals(1, m.getChords().size());
            Chord chord = m.getChords().get(0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.A), beatsPerBar, beatsPerBar,
                    new ScaleChord(ScaleNote.Gs), ChordAnticipationOrDelay.none), chord);
        }
        for (int beatsPerBar = 3; beatsPerBar <= 4; beatsPerBar++) {
            Measure m = Measure.testParse("C/F#.G", beatsPerBar);
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
                Measure m0 = Measure.testParse("C", beatsPerBar);
                Measure m = Measure.parse(new StringBuffer("-"), beatsPerBar, m0);
                assertEquals(beatsPerBar, m.getBeatCount());
                assertEquals(1, m.getChords().size());
                assertEquals(m0.getChords(), m.getChords());
            }
        }
        {
            for (int beatsPerBar = 3; beatsPerBar <= 4; beatsPerBar++) {
                Measure m = Measure.testParse("X", beatsPerBar);
                assertEquals(beatsPerBar, m.getBeatCount());
                assertEquals(0, m.getChords().size());
            }
        }
    }
}