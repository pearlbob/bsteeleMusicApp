package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.client.util.StringTripleToHtmlTable;
import com.bsteele.bsteeleMusicApp.shared.util.StringTriple;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.logging.Logger;

import static com.bsteele.bsteeleMusicApp.shared.songs.SongBaseTest.createSongBase;

public class SongEntryTest
        extends TestCase {

    @Test
    public void testEntry() {
        {
            assertEquals("A", SongBase.entryToUppercase("a"));
            assertEquals("G", SongBase.entryToUppercase("g"));
            assertEquals("h", SongBase.entryToUppercase("h"));

            assertEquals("Ab", SongBase.entryToUppercase("ab"));
            assertEquals("Gb", SongBase.entryToUppercase("gb"));
            assertEquals("hb", SongBase.entryToUppercase("hb"));

            assertEquals("V:Ab", SongBase.entryToUppercase("V:ab"));
            assertEquals("v:Gb", SongBase.entryToUppercase("v:gb"));
            assertEquals("v:hb", SongBase.entryToUppercase("v:hb"));

            assertEquals("A♭Bb", SongBase.entryToUppercase("a♭bb"));
            assertEquals("G♯A♭", SongBase.entryToUppercase("g" + MusicConstant.sharpChar + "a♭"));
            assertEquals("hb", SongBase.entryToUppercase("hb"));

            assertEquals("Bb/Ab", SongBase.entryToUppercase("bb/ab"));
            assertEquals("BbAAD", SongBase.entryToUppercase("bbaad"));
            assertEquals("BbAADD", SongBase.entryToUppercase("bbaadd"));
            assertEquals("BbAadd9", SongBase.entryToUppercase("bbaadd9"));
            assertEquals("Bb/B", SongBase.entryToUppercase("bb/b"));

            assertEquals("v: Bb/Ab A F#C AG/E", SongBase.entryToUppercase("v: bb/ab a f#c ag/e"));
            assertEquals("v: Bb/Ab A F#C AG/E AGb/E", SongBase.entryToUppercase("v: bb/ab a f#c ag/e agb/e"));

            int beats = 4;
            int beatsPerBar = 4;
            ScaleNote[] slashScaleNotes = new ScaleNote[] { null, ScaleNote.A, ScaleNote.As, ScaleNote.Eb, ScaleNote.Cb };
            for (ScaleNote scaleNote : ScaleNote.values()) {
                for (ChordDescriptor chordDescriptor : ChordDescriptor.values()) {
                    for (ScaleNote slashScaleNote : slashScaleNotes) {
                        ScaleChord scaleChord = new ScaleChord(scaleNote, chordDescriptor);
                        ChordAnticipationOrDelay chordAnticipationOrDelay = ChordAnticipationOrDelay.none;
                        Chord chord = new Chord(scaleChord, beats, beatsPerBar,
                                slashScaleNote, chordAnticipationOrDelay, false);
                        ArrayList<Chord> chords = new ArrayList<>();
                        chords.add(chord);
                        Measure m = new Measure(4, chords);
                        logger.fine(m.toMarkup());

                        //  blindly force all to lower case
                        String actual = SongBase.entryToUppercase(m.toMarkup().toLowerCase());

                        //  fix the cheap hack from above
                        if (chordDescriptor.toString().contains("M"))
                            actual = actual.replace("m", "M");
                        if (chordDescriptor.toString().contains("Δ"))
                            actual = actual.replace("δ", "Δ");
                        if (chordDescriptor.toString().contains("add9"))
                            actual = actual.replace("Add9", "add9");
                        if (chordDescriptor.toString().contains("flat5"))
                            actual = actual.replace("Flat5", "flat5");
                        if (chordDescriptor.toString().contains("dim"))
                            actual = actual.replace("Dim", "dim");
                        if (chordDescriptor.toString().contains("aug"))
                            actual = actual.replace("Aug", "aug");


                        assertEquals(m.toMarkup(), actual);
                    }
                }
            }
        }
    }

    private static Logger logger = Logger.getLogger(SongEntryTest.class.getName());
}