package com.bsteele.bsteeleMusicApp.shared.songs;

import junit.framework.TestCase;
import org.junit.Test;

import java.text.ParseException;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ChordTest extends TestCase {

    @Test
    public void testSetScaleChord() {

        TreeSet<ScaleChord> slashScaleChords = new TreeSet<>();
        for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++)
            for (ChordAnticipationOrDelay anticipationOrDelay : ChordAnticipationOrDelay.values()) {
                logger.fine("anticipationOrDelay: " + anticipationOrDelay.toString());
                for (ScaleNote scaleNote : ScaleNote.values()) {
                    if ( scaleNote==ScaleNote.X)
                        continue;
                    for (ChordDescriptor chordDescriptor : ChordDescriptor.values()) {
                        for (int beats = 2; beats <= 4; beats++) {
                            ScaleChord scaleChord = new ScaleChord(scaleNote, chordDescriptor);
                            if (chordDescriptor == ChordDescriptor.minor)
                                slashScaleChords.add(scaleChord);
                            Chord chord = new Chord(scaleChord, beats, beatsPerBar, null, anticipationOrDelay, true);
                            logger.finer(chord.toString());
                            Chord pChord = null;
                            try {
                                pChord = Chord.parse(chord.toString(), beatsPerBar);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                fail();
                            }
                            if (beats != beatsPerBar) {
                                //  the beats will default to beats per bar if unspecified
                                assertEquals(chord.getScaleChord(), pChord.getScaleChord());
                                assertEquals(chord.getSlashScaleNote(), pChord.getSlashScaleNote());
                            } else
                                assertEquals(chord, pChord);
                        }
                    }
                }
            }
    }

    @Test
    public void testChordParse() {
        try {
            int beatsPerBar = 4;
            Chord chord = new Chord(new ScaleChord(ScaleNote.D, ChordDescriptor.diminished));
            chord.setSlashScaleNote(ScaleNote.G);

            assertEquals(chord, Chord.parse("Ddim/G", beatsPerBar));

            chord = new Chord(new ScaleChord(ScaleNote.A, ChordDescriptor.diminished));
            chord.setSlashScaleNote(ScaleNote.G);
            assertEquals(chord, Chord.parse("Adim/G", beatsPerBar));
            chord = new Chord(new ScaleChord(ScaleNote.G, ChordDescriptor.suspendedSecond));
            chord.setSlashScaleNote(ScaleNote.A);
            assertEquals(chord, Chord.parse("G2/A", beatsPerBar));
            chord = new Chord(new ScaleChord(ScaleNote.G, ChordDescriptor.add9));
            assertEquals(chord, Chord.parse("Gadd9A", beatsPerBar));

            chord = Chord.parse("G.1", beatsPerBar);
            assertEquals("G.1", chord.toString());
            chord = Chord.parse("G.2", beatsPerBar);
            assertEquals("G.", chord.toString());
            chord = Chord.parse("G.3", beatsPerBar);
            assertEquals("G..", chord.toString());
            chord = Chord.parse("G.4", beatsPerBar);
            assertEquals("G", chord.toString());
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testChordTranspose() {
        try {
            int count = 0;
            for (Key key : Key.values())
                for (ScaleNote sn : ScaleNote.values())
                    for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++)
                        for (int halfSteps = -15; halfSteps < 15; halfSteps++)
                            for (ChordDescriptor chordDescriptor : ChordDescriptor.values()) {

                                ScaleNote snHalfSteps = sn.transpose(key, halfSteps);

                                logger.fine(sn + chordDescriptor.getShortName() + " " + halfSteps
                                        + " in key " + key + " " + beatsPerBar + " beats");
                                assertEquals(Chord.parse(snHalfSteps + chordDescriptor.getShortName(), beatsPerBar),
                                        Chord.parse(sn + chordDescriptor.getShortName(), beatsPerBar)
                                                .transpose(key, halfSteps));
                                count++;
                            }
            logger.fine("transpose count: " + count);

            count = 0;
            for (Key key : Key.values())
                for (ScaleNote sn : ScaleNote.values())
                    for (ScaleNote slashSn : ScaleNote.values())
                        for (int beatsPerBar = 2; beatsPerBar <= 4; beatsPerBar++)
                            for (int halfSteps = -15; halfSteps < 15; halfSteps++)
                                for (ChordDescriptor chordDescriptor : ChordDescriptor.values()) {

                                    ScaleNote snHalfSteps = sn.transpose(key, halfSteps);
                                    ScaleNote slashSnHalfSteps = slashSn.transpose(key, halfSteps);

                                    logger.fine(sn + chordDescriptor.getShortName() + "/" + slashSn + " " + halfSteps
                                            + " in key " + key + " " + beatsPerBar + " beats");
                                    assertEquals(Chord.parse(snHalfSteps + chordDescriptor.getShortName()
                                                    + "/" + slashSnHalfSteps, beatsPerBar),
                                            Chord.parse(sn + chordDescriptor.getShortName()
                                                    + "/" + slashSn, beatsPerBar).transpose(key, halfSteps));
                                    count++;
                                }
            logger.fine("transpose slash count: " + count);
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    private Logger logger = Logger.getLogger(ChordTest.class.getName());
}