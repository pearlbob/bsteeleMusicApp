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
        Section section = Section.verse;
        SectionVersion sectionVersion = new SectionVersion(section);

        for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++) {
            Measure m = Measure.parse(sectionVersion, "A", beatsPerBar);
            assertEquals(beatsPerBar, m.getBeatCount());
            assertEquals(1, m.getChords().size());
            Chord chord = m.getChords().get(0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.A), beatsPerBar, beatsPerBar), chord);
        }

        for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++) {
            Measure m = Measure.parse(sectionVersion, "BC", beatsPerBar);
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
            Measure m = Measure.parse(sectionVersion, "E#m7. ", beatsPerBar);
            assertEquals(beatsPerBar, m.getBeatCount());
            assertEquals(1, m.getChords().size());
            Chord chord0 = m.getChords().get(0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.Es, ChordDescriptor.minor7), beatsPerBar, beatsPerBar), chord0);
        }
        for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++) {
            Measure m = Measure.parse(sectionVersion, "E#m7Gb7", beatsPerBar);
            assertEquals(beatsPerBar, m.getBeatCount());
            assertEquals(2, m.getChords().size());
            Chord chord0 = m.getChords().get(0);
            Chord chord1 = m.getChords().get(1);
            int beat0 = beatsPerBar / 2;
            int beat1 = beatsPerBar - beat0;
            assertEquals(new Chord(new ScaleChord(ScaleNote.Es, ChordDescriptor.minor7), beat0, beatsPerBar), chord0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.Gb, ChordDescriptor.dominant7), beat1, beatsPerBar), chord1);
        }
        for (int beatsPerBar = 3; beatsPerBar <= 4; beatsPerBar++) {
            Measure m = Measure.parse(sectionVersion, "F#m7.Asus4", beatsPerBar);
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
            Measure m = Measure.parse(sectionVersion, "A/G#", beatsPerBar);
            assertEquals(beatsPerBar, m.getBeatCount());
            assertEquals(1, m.getChords().size());
            Chord chord = m.getChords().get(0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.A), beatsPerBar, beatsPerBar,
                    new ScaleChord(ScaleNote.Gs), AnticipationOrDelay.none), chord);
        }
        for (int beatsPerBar = 3; beatsPerBar <= 4; beatsPerBar++) {
            Measure m = Measure.parse(sectionVersion, "C/F#.G", beatsPerBar);
            assertEquals(beatsPerBar, m.getBeatCount());
            assertEquals(2, m.getChords().size());
            Chord chord0 = m.getChords().get(0);
            Chord chord1 = m.getChords().get(1);
            int beat0 = 2;
            int beat1 = beatsPerBar - beat0;
            assertEquals(new Chord(new ScaleChord(ScaleNote.C), beat0, beatsPerBar,
                    new ScaleChord(ScaleNote.Fs), AnticipationOrDelay.none), chord0);
            assertEquals(new Chord(new ScaleChord(ScaleNote.G), beat1, beatsPerBar), chord1);
        }
        {
            for (int beatsPerBar = 3; beatsPerBar <= 4; beatsPerBar++) {
                Measure m0 = Measure.parse(sectionVersion, "C", beatsPerBar);
                Measure m = Measure.parse(sectionVersion, "-", beatsPerBar, m0);
                assertEquals(beatsPerBar, m.getBeatCount());
                assertEquals(1, m.getChords().size());
                assertEquals(m0.getChords(), m.getChords());
            }
        }
        {
            for (int beatsPerBar = 3; beatsPerBar <= 4; beatsPerBar++) {
                Measure m = Measure.parse(sectionVersion, "X", beatsPerBar);
                assertEquals(beatsPerBar, m.getBeatCount());
                assertEquals(0, m.getChords().size());
            }
        }
    }
}