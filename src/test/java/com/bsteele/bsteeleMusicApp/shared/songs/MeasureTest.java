package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.util.MarkedString;
import junit.framework.TestCase;
import org.junit.Test;

import java.text.ParseException;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureTest extends TestCase {

    @Test
    public void testParse() {
        String s;
        Measure m;
        try {
            {
                s = "GD.C";
                m = Measure.parse(s, 4);
                assertNotNull(m);
                //  explicit measure short of beats is left as specified
                assertEquals("GD.C", m.toMarkup());
            }
            {
                s = "AX";

                m = Measure.parse(s, 4);
                assertNotNull(m);
                //  explicit measure short of beats is left as specified
                assertEquals("AX", m.toMarkup());
            }
            {
                s = "AX..";

                m = Measure.parse(s, 4);
                assertNotNull(m);
                //  explicit measure short of beats is left as specified
                assertEquals("AX..", m.toMarkup());
            }
            {
                s = "X";

                m = Measure.parse(s, 4);
                assertNotNull(m);
                //  explicit measure short of beats is left as specified
                assertEquals("X", m.toMarkup());
            }

            {
                s = "A.B.";

                m = Measure.parse(s, 5);
                assertNotNull(m);
                //  explicit measure short of beats is left as specified
                assertEquals("A.B.", m.toMarkup());
            }
            {
                s = "A.B.";

                m = Measure.parse(s, 5);
                assertNotNull(m);
                //  explicit measure short of beats is left as specified
                assertEquals("A.B.", m.toMarkup());
            }
            {
                s = "AB";
                m = Measure.parse(s, 5);
                assertNotNull(m);
                //  5/4 split across two chords, first one gets the extra beat
                assertEquals("A..B.", m.toMarkup());
            }
            {
                s = "A.B.";
                m = Measure.parse(s, 4);
                assertNotNull(m);
                //  default to simplest expression
                assertEquals("AB", m.toMarkup());
            }
            {
                s = "A/GA/F♯";
                m = Measure.parse(s, 4);
                assertNotNull(m);
                assertEquals(s, m.toMarkup());
            }
            {
                s = "A/G";
                m = Measure.parse(s, 4);
                assertNotNull(m);
                assertEquals(s, m.toMarkup());
                assertEquals(4, m.getBeatCount());
            }
            {
                s = "F.";
                m = Measure.parse(s, 4);
                assertNotNull(m);
                assertEquals(s, m.toMarkup());
                assertEquals(2, m.getBeatCount());
            }
            {
                s = "F.";
                m = Measure.parse(s, 2);
                assertNotNull(m);
                assertEquals("F", m.toMarkup());
                assertEquals(2, m.getBeatCount());
            }
            {
                s = "F..";
                m = Measure.parse(s, 4);
                assertNotNull(m);
                assertEquals(s, m.toMarkup());
                assertEquals(3, m.getBeatCount());
            }
            {
                s = "F...";
                m = Measure.parse(s, 4);
                assertNotNull(m);
                assertEquals("F", m.toMarkup());
                assertEquals(4, m.getBeatCount());
            }
            {
                s = "A..B";
                m = Measure.parse(s, 4);
                assertNotNull(m);
                assertEquals(s, m.toMarkup());
                assertEquals(4, m.getBeatCount());
            }
            {
                s = "AB..";
                m = Measure.parse(s, 4);
                assertNotNull(m);
                assertEquals(s, m.toMarkup());
            }
            {
                s = "ABC.";
                m = Measure.parse(s, 4);
                assertNotNull(m);
                assertEquals(s, m.toMarkup());
            }
            {
                s = "A.BC";
                m = Measure.parse(s, 4);
                assertNotNull(m);
                assertEquals(s, m.toMarkup());
            }

            {
                s = "E♭F";
                m = Measure.parse(s, 4);
                assertNotNull(m);
                assertEquals(s, m.toMarkup());
            }
            {
                s = "E♭F";
                m = Measure.parse(s, 3);
                assertNotNull(m);
                assertEquals("E♭.F", m.toMarkup());
            }
            {
                //  test beat allocation
                m = Measure.parse("EAB", 4);
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
                m = Measure.parse("A", beatsPerBar);
                assertEquals(beatsPerBar, m.getBeatCount());
                assertEquals(1, m.getChords().size());
                Chord chord = m.getChords().get(0);
                assertEquals(new Chord(new ScaleChord(ScaleNote.A), beatsPerBar, beatsPerBar), chord);
            }

            for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++) {
                m = Measure.parse("BC", beatsPerBar);
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
                m = Measure.parse("E#m7. ", beatsPerBar);
                assertNotNull(m);
                assertEquals(2, m.getBeatCount());
                assertEquals(1, m.getChords().size());
                Chord chord0 = m.getChords().get(0);
                assertEquals(new Chord(new ScaleChord(ScaleNote.Es, ChordDescriptor.minor7), 2, beatsPerBar), chord0);
            }
            for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++) {
                m = Measure.parse("E#m7Gb7", beatsPerBar);
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
                m = Measure.parse("F#m7.Asus4", beatsPerBar);
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
                m = Measure.parse("A/G#", beatsPerBar);
                assertEquals(beatsPerBar, m.getBeatCount());
                assertEquals(1, m.getChords().size());
                Chord chord = m.getChords().get(0);
                assertEquals(new Chord(new ScaleChord(ScaleNote.A), beatsPerBar, beatsPerBar,
                        ScaleNote.Gs, ChordAnticipationOrDelay.none, true), chord);
            }
            for (int beatsPerBar = 3; beatsPerBar <= 4; beatsPerBar++) {
                m = Measure.parse("C/F#.G", beatsPerBar);
                assertEquals(beatsPerBar, m.getBeatCount());
                assertEquals(2, m.getChords().size());
                Chord chord0 = m.getChords().get(0);
                Chord chord1 = m.getChords().get(1);
                int beat0 = 2;
                int beat1 = beatsPerBar - beat0;
                assertEquals(new Chord(new ScaleChord(ScaleNote.C), beat0, beatsPerBar,
                        ScaleNote.Fs, ChordAnticipationOrDelay.none, true), chord0);
                assertEquals(new Chord(new ScaleChord(ScaleNote.G), beat1, beatsPerBar), chord1);
            }
            {
                for (int beatsPerBar = 3; beatsPerBar <= 4; beatsPerBar++) {
                    Measure m0 = Measure.parse("C", beatsPerBar);
                    m = Measure.parse(new MarkedString("-"), beatsPerBar, m0);
                    assertEquals(beatsPerBar, m.getBeatCount());
                    assertEquals(1, m.getChords().size());
                    assertEquals(m0.getChords(), m.getChords());
                }
            }
            {
                for (int beatsPerBar = 3; beatsPerBar <= 4; beatsPerBar++) {
                    m = Measure.parse("X", beatsPerBar);
                    assertEquals(beatsPerBar, m.getBeatCount());
                    assertEquals(1, m.getChords().size());
                }
            }
            {
                m = Measure.parse("E#m7. ", 3);
                assertNotNull(m);
                assertEquals("E♯m7.", m.toMarkup());
            }
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }

        {
            //  too many beats or over specified, doesn't cover the beats per bar
            try {
                m = Measure.parse("E#m7.. ", 2);
                fail();
            } catch (ParseException e) {
                //  expected
            }
        }
        {
            int beatsPerBar = 4;
            try {
                m = Measure.parse(" .G ", beatsPerBar);
                fail();
            } catch (ParseException e) {
                //  expected
            }
        }
        {
            int beatsPerBar = 3;
            //System.out.println("beatsPerBar: " + beatsPerBar);
            try {
                m = Measure.parse(" .G ", beatsPerBar);
                fail();
            } catch (ParseException e) {
                //  expected
            }
            try {
                m = Measure.parse("E#m7... ", beatsPerBar);
                fail();
            } catch (ParseException e) {
                //  expected
            }
        }
        for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++) {
            try {
                m = Measure.parse("E#m7.. ", beatsPerBar);
                assertEquals(3, m.getBeatCount());
                assertEquals(1, m.getChords().size());
                Chord chord0 = m.getChords().get(0);
                assertEquals(new Chord(new ScaleChord(ScaleNote.Es, ChordDescriptor.minor7), 3, beatsPerBar), chord0);
            } catch (ParseException e) {
                //  too many beats or over specified, doesn't cover the beats per bar
                if (beatsPerBar < 3)
                    continue;
                fail();     //  parse failed
            }
        }
    }
}