package com.bsteele.bsteeleMusicApp.client.songs;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureTest extends TestCase {

    @Test
    public void testParse() {
        for (int beatCount = 2; beatCount <= 4; beatCount++) {
            Measure m = Measure.parse("A", beatCount);
            assertEquals(beatCount, m.getBeatCount());
            assertEquals(1, m.getChords().size());
            Chord chord = m.getChords().get(0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.A), beatCount), chord);
        }

        for (int beatCount = 2; beatCount <= 4; beatCount++) {
            Measure m = Measure.parse("BC", beatCount);
            assertEquals(beatCount, m.getBeatCount());
            assertEquals(2, m.getChords().size());
            Chord chord0 = m.getChords().get(0);
            Chord chord1 = m.getChords().get(1);
            int beat0 = beatCount / 2;
            int beat1 = beatCount - beat0;
            assertEquals(new Chord(new ScaleChord(ScaleNote.B), beat0), chord0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.C), beat1), chord1);
        }
        for (int beatCount = 2; beatCount <= 4; beatCount++) {
            Measure m = Measure.parse("E#m7. ", beatCount);
            assertEquals(beatCount, m.getBeatCount());
            assertEquals(1, m.getChords().size());
            Chord chord0 = m.getChords().get(0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.Es,ChordDescriptor.minor7), beatCount), chord0);
        }
        for (int beatCount = 2; beatCount <= 4; beatCount++) {
            Measure m = Measure.parse("E#m7Gb7", beatCount);
            assertEquals(beatCount, m.getBeatCount());
            assertEquals(2, m.getChords().size());
            Chord chord0 = m.getChords().get(0);
            Chord chord1 = m.getChords().get(1);
            int beat0 = beatCount / 2;
            int beat1 = beatCount - beat0;
            assertEquals(new Chord(new ScaleChord(ScaleNote.Es,ChordDescriptor.minor7), beat0), chord0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.Gb,ChordDescriptor.dominant7), beat1), chord1);
        }
        for (int beatCount = 3; beatCount <= 4; beatCount++) {
            Measure m = Measure.parse("F#m7.Asus4", beatCount);
            assertEquals(beatCount, m.getBeatCount());
            assertEquals(2, m.getChords().size());
            Chord chord0 = m.getChords().get(0);
            Chord chord1 = m.getChords().get(1);
            int beat0 = 2;
            int beat1 = beatCount - beat0;
            assertEquals(new Chord(new ScaleChord(ScaleNote.Fs,ChordDescriptor.minor7), beat0), chord0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.A,ChordDescriptor.suspended4), beat1), chord1);
        }
        for (int beatCount = 2; beatCount <= 4; beatCount++) {
            Measure m = Measure.parse("A/G#", beatCount);
            assertEquals(beatCount, m.getBeatCount());
            assertEquals(1, m.getChords().size());
            Chord chord = m.getChords().get(0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.A), beatCount,
                    new ScaleChord(ScaleNote.Gs), AnticipationOrDelay.none), chord);
        }
        for (int beatCount = 3; beatCount <= 4; beatCount++) {
            Measure m = Measure.parse("C/F#.G", beatCount);
            assertEquals(beatCount, m.getBeatCount());
            assertEquals(2, m.getChords().size());
            Chord chord0 = m.getChords().get(0);
            Chord chord1 = m.getChords().get(1);
            int beat0 = 2;
            int beat1 = beatCount - beat0;
            assertEquals(new Chord(new ScaleChord(ScaleNote.C), beat0,
                    new ScaleChord(ScaleNote.Fs), AnticipationOrDelay.none), chord0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.G), beat1), chord1);
        }
    }
}