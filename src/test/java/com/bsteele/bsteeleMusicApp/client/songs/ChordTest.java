package com.bsteele.bsteeleMusicApp.client.songs;

import com.google.gwt.junit.client.GWTTestCase;
import org.junit.Test;

import java.util.TreeSet;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ChordTest extends GWTTestCase {

    @Test
    public void testSetScaleChord() {

        TreeSet<ScaleChord> slashScaleChords = new TreeSet<>();
        int beatsPerBar = 4;
        //       for (AnticipationOrDelay anticipationOrDelay : AnticipationOrDelay.values()) {
        AnticipationOrDelay anticipationOrDelay = AnticipationOrDelay.none;
        for (ScaleNote scaleNote : ScaleNote.values()) {
            for (ChordDescriptor chordDescriptor : ChordDescriptor.values()) {
                for (int beats = 1; beats <= 4; beats++) {
                    ScaleChord scaleChord = new ScaleChord(scaleNote, chordDescriptor);
                    if (chordDescriptor == ChordDescriptor.minor)
                        slashScaleChords.add(scaleChord);
                    Chord chord = new Chord(scaleChord, beats, beatsPerBar, null, anticipationOrDelay);
                    System.out.println(chord.toString());
                    Chord pChord = Chord.parse(chord.toString(), beatsPerBar);
                    assertEquals(chord, pChord);
                }
            }
        }
        // }
    }

    @Override
    public String getModuleName() {
        return "com.bsteele.bsteeleMusicApp.BSteeleMusicAppJUnit";
    }
}