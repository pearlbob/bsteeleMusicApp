package com.bsteele.bsteeleMusicApp.shared.songs;

import junit.framework.TestCase;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Logger;

import static com.bsteele.bsteeleMusicApp.shared.songs.SongBaseTest.createSongBase;

public class SongEntryTest
        extends TestCase {

    @Test
    public void testEntryToUppercase() {
        {
            int beatsPerBar = 4;
            SongBase a;

            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, beatsPerBar, 4,
                    "verse: A B C D prechorus: D E F F# chorus: G D C G x3",
                    "i:\nv: bob, bob, bob berand\npc: nope\nc: sing chorus here \no: last line of outro");

            assertEquals("GD.C ", MeasureNode.concatMarkup(a.parseChordEntry(SongBase.entryToUppercase("gd.c"))));

            assertEquals("I: []", MeasureNode.concatMarkup(a.parseChordEntry(SongBase.entryToUppercase("i:"))));

            assertEquals("DmE ", MeasureNode.concatMarkup(a.parseChordEntry(SongBase.entryToUppercase("dme"))));
            assertEquals("Dmaj ", MeasureNode.concatMarkup(a.parseChordEntry(SongBase.entryToUppercase("dmaj"))));

            assertEquals("DD ", MeasureNode.concatMarkup(a.parseChordEntry(SongBase.entryToUppercase("dd"))));
            assertEquals("DD (i)", MeasureNode.concatMarkup(a.parseChordEntry(SongBase.entryToUppercase("ddi"))));
            assertEquals("Ddim ", MeasureNode.concatMarkup(a.parseChordEntry(SongBase.entryToUppercase("ddim"))));

            assertEquals("G G", SongBase.entryToUppercase("g g"));

            assertEquals("X", SongBase.entryToUppercase("x"));
            assertEquals("X ", SongBase.entryToUppercase("x "));
            assertEquals("XA", SongBase.entryToUppercase("xa"));
            assertEquals("AX", SongBase.entryToUppercase("ax"));
            assertEquals("Ax2", SongBase.entryToUppercase("ax2"));
            assertEquals("x2", SongBase.entryToUppercase("x2"));

            assertEquals("A", SongBase.entryToUppercase("a"));
            assertEquals("G", SongBase.entryToUppercase("g"));
            assertEquals("h", SongBase.entryToUppercase("h"));

            assertEquals("Ab", SongBase.entryToUppercase("ab"));
            assertEquals("Gb", SongBase.entryToUppercase("gb"));
            assertEquals("hb", SongBase.entryToUppercase("hb"));

            assertEquals("AFoo", SongBase.entryToUppercase("afoo"));//  fixme
            assertEquals("(afoo)", SongBase.entryToUppercase("(afoo)"));
            assertEquals("DCGG(afoo)AX", SongBase.entryToUppercase("dcgg(afoo)ax"));

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
            ScaleNote[] slashScaleNotes = new ScaleNote[]{null, ScaleNote.A, ScaleNote.As, ScaleNote.Eb, ScaleNote.Cb};
            for (ScaleNote scaleNote : ScaleNote.values()) {
                if (scaleNote == ScaleNote.X)
                    continue;
                for (ChordDescriptor chordDescriptor : ChordDescriptor.values()) {
                    for (ScaleNote slashScaleNote : slashScaleNotes) {
                        if (slashScaleNote == ScaleNote.X)
                            continue;
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
                        String cdString = chordDescriptor.deAlias().toString();
                        if (cdString.contains("mmaj") || cdString.contains("maj"))
                            ;       //  leave as is
                        else if (cdString.contains("M"))
                            actual = actual.replace("m", "M");
                        if (cdString.contains("add9"))
                            actual = actual.replace("Add9", "add9");
                        if (cdString.contains("flat5"))
                            actual = actual.replace("Flat5", "flat5");
                        if (cdString.contains("dim"))
                            actual = actual.replace("Dim", "dim");
                        if (cdString.contains("aug"))
                            actual = actual.replace("Aug", "aug");

                        //if ( !m.toMarkup().equals(actual))
                        //    System.out.println("here");
                        assertEquals(m.toMarkup(), actual);
                    }
                }
            }
        }
    }

    @Test
    public void testParseEntry() {
        ArrayList<MeasureNode> measureNodes;
        MeasureNode measureNode;
        MeasureRepeat measureRepeat;
        Phrase phrase;
        Measure measure;
        int beatsPerBar = 4;

        SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "verse: A B C D prechorus: D E F F# chorus: G D C G x3",
                "i:\nv: bob, bob, bob berand\npc: nope\nc: sing chorus here \no: last line of outro");

        try {
            //  single measure
            measureNodes = a.parseChordEntry("A");
            assertEquals(1, measureNodes.size());
            assertEquals(MeasureNode.MeasureNodeType.phrase, measureNodes.get(0).getMeasureNodeType());
            phrase = (Phrase) measureNodes.get(0);
            assertEquals(Phrase.parse("A", 0, beatsPerBar, null), phrase);

            //  short measure
            measureNodes = a.parseChordEntry("G G. Bm");
            assertEquals(1, measureNodes.size());
            assertEquals(MeasureNode.MeasureNodeType.phrase, measureNodes.get(0).getMeasureNodeType());
            phrase = (Phrase) measureNodes.get(0);
            assertEquals(Measure.parse("G", beatsPerBar), phrase.getMeasure(0));
            assertEquals("G.", phrase.getMeasure(1).toMarkup());
            assertEquals("Bm", phrase.getMeasure(2).toMarkup());

            //  chord section
            measureNodes = a.parseChordEntry("v:A");
            assertEquals(1, measureNodes.size());
            measureNode = measureNodes.get(0);
            assertEquals(MeasureNode.MeasureNodeType.section, measureNode.getMeasureNodeType());
            assertEquals(ChordSection.parse("v:A", beatsPerBar), measureNodes.get(0));

            //  phrase
            measureNodes = a.parseChordEntry("A B Cm7");
            assertEquals(1, measureNodes.size());
            assertEquals(MeasureNode.MeasureNodeType.phrase, measureNodes.get(0).getMeasureNodeType());
            phrase = (Phrase) measureNodes.get(0);
            assertEquals(Measure.parse("A", beatsPerBar), phrase.getMeasure(0));
            assertEquals(Measure.parse("Cm7", beatsPerBar), phrase.getMeasure(2));

            //  repeat
            measureNodes = a.parseChordEntry("D Cm7 G E x3");
            assertEquals(1, measureNodes.size());
            measureNode = measureNodes.get(0);
            assertEquals(MeasureNode.MeasureNodeType.repeat, measureNode.getMeasureNodeType());
            measureRepeat = (MeasureRepeat) measureNode;
            assertEquals(Measure.parse("D", beatsPerBar), measureRepeat.getMeasure(0));
            assertEquals(Measure.parse("E", beatsPerBar), measureRepeat.getMeasure(3));
            assertEquals(3, measureRepeat.getRepeats());

            //  repeat with short measure
            measureNodes = a.parseChordEntry("D G G. E x3");
            logger.fine(measureNodes.toString());
            assertEquals(1, measureNodes.size());
            measureNode = measureNodes.get(0);
            assertEquals(MeasureNode.MeasureNodeType.repeat, measureNode.getMeasureNodeType());
            measureRepeat = (MeasureRepeat) measureNode;
            assertEquals("G", measureRepeat.getMeasure(1).toMarkup());
            assertEquals("G.", measureRepeat.getMeasure(2).toMarkup());
            assertEquals(3, measureRepeat.getRepeats());

            //  comment
            measureNodes = a.parseChordEntry("(D Cm7 G E x3)");
            assertEquals(1, measureNodes.size());
            assertEquals(MeasureNode.MeasureNodeType.phrase, measureNodes.get(0).getMeasureNodeType());
            phrase = (Phrase) measureNodes.get(0);
            assertEquals(1, phrase.size());
            measure = phrase.getMeasure(0);
            assertEquals(MeasureNode.MeasureNodeType.comment, measure.getMeasureNodeType());
            MeasureComment measureComment = (MeasureComment) measure;
            assertEquals("D Cm7 G E x3", measureComment.getComment());

        } catch (ParseException pex) {
            fail();
        }

    }


    private String testMarkupString(String chords) {
        int beatsPerBar = 4;
        SongBase a;

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                chords,
                "i:\nv: bob, bob, bob berand\npc: nope\nc: sing chorus here \no: last line of outro");
        String originalTransport = a.chordsToJsonTransportString();
        String originalMarkup = a.toMarkup();
        a.parseChordEntry(a.toMarkup());    //  re-load the markup as the chords

        String parsedMarkup = a.toMarkup();
        String parsedTransport = a.chordsToJsonTransportString();

        //  verify nothing has changed
        logger.info(originalMarkup);
        logger.info(a.toMarkup());
        logger.fine(parsedMarkup);
        assertEquals(originalMarkup, parsedMarkup);

        logger.info(originalTransport);
        logger.fine(parsedTransport);
        assertEquals(originalTransport, parsedTransport);

        return parsedMarkup;
    }

    private void testTransportString(String chords) {
        String parsedMarkup = testMarkupString(chords);
        //  verify the in equals the out after parsing
        assertEquals(chords, parsedMarkup);
    }


    @Test
    public void testTransportString() {
        testMarkupString("C:Am - C -\nGG C GG C x2");
        testTransportString("V: A B C D, B C  PC: D E F F♯  C: [G D, C G ] x3  ");
        testTransportString("V: A B C D  ");
        testTransportString("V: [G D C G ] x3  ");
        testTransportString("V: [G D C G ] x3 D E F G  ");
        testTransportString("V: A B C D [G D C G ] x3 D E F G  ");
        testTransportString("V: A B C D [G D C G ] x3  ");
    }

    @Test
    public void testMarkupString() {
        testMarkupString("V: A B C D\nB C\nPC: D E F F♯\nC: [G D C G ] x3  ");
//        testMarkupString("V: A B C D B C\nPC: D E F F♯\nC: G D C G  x3  ");
//        testMarkupString("V: A B C D\nB C\nPC: D E F F♯\nC: G D C G B C D G x3  ");
//        testMarkupString("V: A B C D\nB C\nPC: D E F F♯\nC: G D C G B C D G A B C D x3  ");
//        testMarkupString("V: A B C D\nB C\nPC: D E F F♯\nC: G D C G |\nB C D G x3  ");
//        testMarkupString("V: A B C D\nB C\nPC: D E F F♯\nC: G D C G |\nB C D G x3  ");
//        testMarkupString("C:Am - C -,GG C GG C x2");
//        testMarkupString("C:Am - C -\nGG C GG C x2");
//        testMarkupString("C:\nG-G- -D-C --C- -D-D x3\nAm - C -\nGG C GG C x2");
//        testMarkupString("V: A B C D B C PC: D E F F♯,  C: [G D C G ,] x3  ");
//        testMarkupString("V: A B C D, B C PC: D E F F♯,  C: [G D C G ] x3  ");
//        testMarkupString("V: A B C D, [G D C G ] x3  ");
//        testMarkupString("V: A B C D, [G D C G ] x3 D E F G,  ");
    }

    @Test
    public void testAllChordsMarkupString() {
        int beatsPerBar = 4;
        SongBase a;


        assertEquals("A7#5", SongBase.entryToUppercase("A7#5"));
        assertEquals("A7#5", SongBase.entryToUppercase("a7#5"));
        assertEquals("Aº7", SongBase.entryToUppercase("aº7"));

        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                generateAllChordsVerse(false),
                "v: bob, bob, bob berand\n");
        String chords = a.toMarkup();
        logger.fine("a: " + chords);
        SongBase b = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                chords.replaceAll("\n",""),
                "v: bob, bob, bob berand\n");
        logger.fine("b: " + b.toMarkup());
        SongBase c = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, beatsPerBar, 4,
                "v:",
                "v: bob, bob, bob berand\n");
        //  enter measures to song c
        ArrayList<MeasureNode> measureNodes =
                c.parseChordEntry(SongBase.entryToUppercase(generateAllChordsVerse(true)
                        //  a tougher test without commas, expects the default behavior is to add commas for 4 measure rows
                        .replaceAll(",","")
                ));
                //c.parseChordEntry(SongBase.entryToUppercase(temp));
        logger.fine( "measureNodes.size(): "+measureNodes.size());
        for (MeasureNode measureNode : measureNodes) {
            logger.finer( "  +"+measureNode.toMarkup());
            assertTrue(c.edit(measureNode));
        }
        logger.fine("c: " + c.toMarkup());

        assertTrue(a.toMarkup().equals(b.toMarkup()));
        assertTrue(a.toMarkup().equals(c.toMarkup()));

        assertTrue(a.equals(b));
        assertTrue(a.equals(c));
    }

    private String generateAllChordsVerse(boolean lowerCase) {
        StringBuilder sb = new StringBuilder("V: ");

        int i = 0;
        for (ScaleNote sn : ScaleNote.values()) {
            if (sn == ScaleNote.X)
                continue;

            for (ChordDescriptor cd : ChordDescriptor.values()) {
                sb.append(" ");
                if (lowerCase) {
                    sb.append(sn.toString().toLowerCase());
                    switch (cd) {
                        default:
                            sb.append(cd.getShortName()//.toLowerCase()
                            );
                            break;
                        //  chord descriptors that only work properly when capitalized
                        case majorNine:
                        case majorSeven:
                            sb.append(cd.getShortName());
                            break;
                    }
                } else {
                    sb.append(sn.toString()).append(cd.getShortName());
                }
                if (++i > 3) {
                    i = 0;
                    sb.append(",\n");
                }
            }
            break;//!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
        }
        logger.finer("gen: " + sb.toString());
        return sb.toString();
    }

    private static Logger logger = Logger.getLogger(SongEntryTest.class.getName());
}