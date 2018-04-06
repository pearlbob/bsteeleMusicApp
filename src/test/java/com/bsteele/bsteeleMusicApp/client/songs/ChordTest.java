package com.bsteele.bsteeleMusicApp.client.songs;

import org.junit.Test;

import java.util.TreeSet;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ChordTest {

    @Test
    public void setScaleChord() {

        TreeSet<ScaleChord> slashScaleChords = new TreeSet<>();
        for (AnticipationOrDelay anticipationOrDelay : AnticipationOrDelay.values()) {
            for (ScaleNote scaleNote : ScaleNote.values()) {
                for (ChordDescriptor chordDescriptor : ChordDescriptor.values()) {
                    for (int beats = 1; beats <= 4; beats++) {
                        ScaleChord scaleChord = new ScaleChord(scaleNote, chordDescriptor);
                        if (chordDescriptor == ChordDescriptor.minor )
                            slashScaleChords.add(scaleChord);
                        Chord chord = new Chord(scaleChord, beats, null, anticipationOrDelay);
                        System.out.println(chord.toString());
                    }
                }
            }
        }
    }
}