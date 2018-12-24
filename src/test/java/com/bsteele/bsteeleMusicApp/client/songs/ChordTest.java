package com.bsteele.bsteeleMusicApp.client.songs;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.TreeSet;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ChordTest extends TestCase {

    @Test
    public void testSetScaleChord() {

        TreeSet<ScaleChord> slashScaleChords = new TreeSet<>();
        int beatsPerBar = 4;
        //       for (ChordAnticipationOrDelay anticipationOrDelay : ChordAnticipationOrDelay.values()) {
        ChordAnticipationOrDelay anticipationOrDelay = ChordAnticipationOrDelay.none;
        for (ScaleNote scaleNote : ScaleNote.values()) {
            for (ChordDescriptor chordDescriptor : ChordDescriptor.values()) {
                for (int beats = 2; beats <= 4; beats++) {
                    ScaleChord scaleChord = new ScaleChord(scaleNote, chordDescriptor);
                    if (chordDescriptor == ChordDescriptor.minor)
                        slashScaleChords.add(scaleChord);
                    Chord chord = new Chord(scaleChord, beats, beatsPerBar, null, anticipationOrDelay);
                    //System.out.println(chord.toString());
                    Chord pChord = Chord.parse(chord.toString(), beatsPerBar);
                    switch (beats) {
                        case 1:                 //  the beats will default to beats per bar if unspecified
                            assertEquals(chord.getScaleChord(), pChord.getScaleChord());
                            assertEquals(chord.getSlashScaleChord(), pChord.getSlashScaleChord());
                            break;
                        default:
                            assertEquals(chord, pChord);
                            break;
                    }
                }
            }
        }
        // }
    }

    @Test
    public void testChordParse() {
        Chord chord = new Chord(new ScaleChord(ScaleNote.D, ChordDescriptor.diminished));
        chord.setSlashScaleChord(new ScaleChord(ScaleNote.G));
        assertEquals(chord, Chord.parse("Ddim/G", 4));
        chord = new Chord(new ScaleChord(ScaleNote.A, ChordDescriptor.diminished));
        chord.setSlashScaleChord(new ScaleChord(ScaleNote.G));
        assertEquals(chord, Chord.parse("Adim/G", 4));
        chord = new Chord(new ScaleChord(ScaleNote.G, ChordDescriptor.suspendedSecond));
        chord.setSlashScaleChord(new ScaleChord(ScaleNote.A));
        assertEquals(chord, Chord.parse("G2/A", 4));
        chord = new Chord(new ScaleChord(ScaleNote.G, ChordDescriptor.add9));
        assertEquals(chord, Chord.parse("Gadd9A", 4));
    }

//    @Override
//    public String getModuleName() {
//        return "com.bsteele.bsteeleMusicApp.BSteeleMusicAppJUnit";
//    }
}