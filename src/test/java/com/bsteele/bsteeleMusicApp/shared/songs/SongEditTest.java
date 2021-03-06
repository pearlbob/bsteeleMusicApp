package com.bsteele.bsteeleMusicApp.shared.songs;

import junit.framework.TestCase;
import org.junit.Test;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SongEditTest extends TestCase {

    @Test
    public void testEdits() {

        try {
            SectionVersion v = SectionVersion.parse("v:");
            SectionVersion iSection = SectionVersion.parse("i:");
            int beatsPerBar = 4;
            ChordSectionLocation location;
            ChordSection newSection;
            MeasureRepeat newRepeat;
            Phrase newPhrase;
            Measure newMeasure;

//            startingChords("");
//            pre(MeasureEditType.append, "", "", "i: [A B C D]");
//            resultChords("I: A B C D ");
//            post(MeasureEditType.append, "I:", "I: A B C D");

            startingChords("");
            pre(MeasureEditType.append, "", "", SongBase.entryToUppercase("i: [a b c d]"));
            resultChords("I: A B C D ");
            post(MeasureEditType.append, "I:", "I: A B C D");


            startingChords("I: V: [Am Am/G Am/F♯ FE ] x4  I2: [Am Am/G Am/F♯ FE ] x2  C: F F C C G G F F  O: Dm C B B♭ A  ");
            pre(MeasureEditType.replace, "C:", "C: F F C C G G F F ",
                    "C: F F C C G G C B F F ");
            resultChords("I: V: [Am Am/G Am/F♯ FE ] x4  I2: [Am Am/G Am/F♯ FE ] x2  C: F F C C, G G C B, F F  O: Dm C B B♭ A  ");
            post(MeasureEditType.append, "C:", "C: F F C C, G G C B, F F ");

            startingChords("I: V: [Am Am/G Am/F♯ FE ] x4  I2: [Am Am/G Am/F♯ FE ] x2  C: F F C C G G C B F F  O: Dm C B B♭ A  ");
            pre(MeasureEditType.delete, "C:0:7", "B", "null");
            resultChords("I: V: [Am Am/G Am/F♯ FE ] x4  I2: [Am Am/G Am/F♯ FE ] x2  C: F F C C G G C F F  O: Dm C B B♭ A  ");
            post(MeasureEditType.delete, "C:0:7", "F");

            startingChords("I: V: [Am Am/G Am/F♯ FE ] x4  I2: [Am Am/G Am/F♯ FE ] x2  C: F F C C G G C F F  O: Dm C B B♭ A  ");
            pre(MeasureEditType.delete, "C:0:7", "F", "null");
            resultChords("I: V: [Am Am/G Am/F♯ FE ] x4  I2: [Am Am/G Am/F♯ FE ] x2  C: F F C C G G C F  O: Dm C B B♭ A  ");
            post(MeasureEditType.delete, "C:0:7", "F");

            startingChords("I: V: [Am Am/G Am/F♯ FE ] x4  I2: [Am Am/G Am/F♯ FE ] x2  C: F F C C G G C F  O: Dm C B B♭ A  ");
            pre(MeasureEditType.delete, "C:0:7", "F", "null");
            resultChords("I: V: [Am Am/G Am/F♯ FE ] x4  I2: [Am Am/G Am/F♯ FE ] x2  C: F F C C G G C  O: Dm C B B♭ A  ");
            post(MeasureEditType.delete, "C:0:6", "C");

            startingChords("I: V: [Am Am/G Am/F♯ FE ] x4  I2: [Am Am/G Am/F♯ FE ] x2  C: F F C C G G C  O: Dm C B B♭ A  ");
            pre(MeasureEditType.append, "C:0:6", "C", "G G ");
            resultChords("I: V: [Am Am/G Am/F♯ FE ] x4  I2: [Am Am/G Am/F♯ FE ] x2  C: F F C C G G C G G  O: Dm C B B♭ A  ");
            post(MeasureEditType.append, "C:0:8", "G");

            startingChords("I: V: [Am Am/G Am/F♯ FE ] x4  I2: [Am Am/G Am/F♯ FE ] x2  C: F F C C G G C G G  O: Dm C B B♭ A  ");
            pre(MeasureEditType.replace, "I2:0", "[Am Am/G Am/F♯ FE ] x2 ", "[] x3 ");
            resultChords("I: V: [Am Am/G Am/F♯ FE ] x4  I2: [Am Am/G Am/F♯ FE ] x3  C: F F C C G G C G G  O: Dm C B B♭ A  ");
            post(MeasureEditType.append, "I2:0", "[Am Am/G Am/F♯ FE ] x3 ");

            startingChords("I: V: [Am Am/G Am/F♯ FE ] x4  I2: [Am Am/G Am/F♯ FE ] x3  C: F F C C G G C G G  O: Dm C B B♭ A  ");
            pre(MeasureEditType.replace, "I2:0", "[Am Am/G Am/F♯ FE ] x3 ", "[] x1 ");
            resultChords("I: V: [Am Am/G Am/F♯ FE ] x4  I2: Am Am/G Am/F♯ FE  C: F F C C G G C G G  O: Dm C B B♭ A  ");
            post(MeasureEditType.append, "I2:0:3", "FE");


            startingChords("I: A G D  V: D C G G  V1: Dm  V2: Em  PC: D C G D  C: F7 G7 G Am  ");
            pre(MeasureEditType.delete, "I:0:1", "G", "null");
            resultChords("I: A D  V: D C G G  V1: Dm  V2: Em  PC: D C G D  C: F7 G7 G Am  ");
            post(MeasureEditType.delete, "I:0:1", "D");

            startingChords("I: A G D  V: D C G G  V1: Dm  V2: Em  PC: D C G D  C: F7 G7 G Am  ");
            pre(MeasureEditType.replace, "I:0:1", "G", "G G");
            resultChords("I: A G G D  V: D C G G  V1: Dm  V2: Em  PC: D C G D  C: F7 G7 G Am  ");
            post(MeasureEditType.append, "I:0:2", "G");

            startingChords("V: C F C C F F C C G F C G  ");
            pre(MeasureEditType.append, "V:0:11", "G", "PC: []");
            resultChords("V: C F C C F F C C G F C G  PC: [] ");
            post(MeasureEditType.append, "PC:", "PC: []");
            startingChords("V: C F C C F F C C G F C G  PC: [] ");
            pre(MeasureEditType.replace, "PC:", "PC: []", "PC: []");
            resultChords("V: C F C C F F C C G F C G  PC: [] ");
            post(MeasureEditType.append, "PC:", "PC: []");
            startingChords("V: C F C C F F C C G F C G  PC:  ");
            pre(MeasureEditType.append, "PC:", "PC: []", "O: []");
            resultChords("V: C F C C F F C C G F C G  PC: [] O: [] ");
            post(MeasureEditType.append, "O:", "O: []");
            startingChords("V: C F C C F F C C G F C G  PC: []  O: [] ");
            pre(MeasureEditType.replace, "O:", "O: []", "O: []");
            resultChords("V: C F C C F F C C G F C G  PC: [] O: [] ");
            post(MeasureEditType.append, "O:", "O: []");

            startingChords("V: [C♯m A♭ F A♭ ] x4 C  C: [C G B♭ F ] x4  ");
            pre(MeasureEditType.delete, "V:", "V: [C♯m A♭ F A♭ ] x4 C ", "null");
            resultChords("C: [C G B♭ F ] x4  ");
            post(MeasureEditType.delete, "C:", "C: [C G B♭ F ] x4 ");
            startingChords("C: [C G B♭ F ] x4  ");
            pre(MeasureEditType.delete, "C:", "C: [C G B♭ F ] x4 ", "null");
            resultChords("");
            post(MeasureEditType.append, "V:", null);

            startingChords("V: [C♯m A♭ F A♭ ] x4 C  PC2:  C: T: [C G B♭ F ] x4  ");
            pre(MeasureEditType.delete, "PC2:", "PC2: [C G B♭ F ] x4", "null");
            resultChords("V: [C♯m A♭ F A♭ ] x4 C  C: T: [C G B♭ F ] x4  ");
            post(MeasureEditType.delete, "V:", "V: [C♯m A♭ F A♭ ] x4 C ");
            startingChords("V: [C♯m A♭ F A♭ ] x4 C  C: T: [C G B♭ F ] x4  ");
            pre(MeasureEditType.delete, "V:", "V: [C♯m A♭ F A♭ ] x4 C ", "null");
            resultChords("C: T: [C G B♭ F ] x4  ");
            post(MeasureEditType.delete, "C:", "C: [C G B♭ F ] x4 ");
            startingChords("C: T: [C G B♭ F ] x4  ");
            pre(MeasureEditType.delete, "C:", "C: [C G B♭ F ] x4 ", "null");
            resultChords("T: [C G B♭ F ] x4  ");
            post(MeasureEditType.delete, "T:", "T: [C G B♭ F ] x4 ");
            startingChords("T: [C G B♭ F ] x4  ");
            pre(MeasureEditType.delete, "T:", "T: [C G B♭ F ] x4 ", "null");
            resultChords("");
            post(MeasureEditType.append, "V:", null);

            startingChords("V: C F C C F F C C G F C G  ");
            pre(MeasureEditType.append, "V:0:7", "C", "C PC:");
            resultChords("V: C F C C F F C C C G F C G  PC: []");
            post(MeasureEditType.append, "PC:", "PC: []");
            startingChords("V: C F C C F F C C G F C G  ");
            pre(MeasureEditType.append, "V:0:7", "C", "PC:");
            resultChords("V: C F C C F F C C G F C G  PC: []");
            post(MeasureEditType.append, "PC:", "PC: []");


            startingChords("V: (Prechorus) C (C/) (chorus) [C G B♭ F ] x4 (Tag Chorus)  ");
            pre(MeasureEditType.delete, "V:0:0", "(Prechorus)", "null");
            resultChords("V: C (C/) (chorus) [C G B♭ F ] x4 (Tag Chorus)  ");

            startingChords("V: (Verse) [C♯m A♭ F A♭ ] x4 (Prechorus) C (C/) (chorus) [C G B♭ F ] x4 (Tag Chorus)  ");
            pre(MeasureEditType.delete, "V:0:0", "(Verse)", "null");
            resultChords("V: [C♯m A♭ F A♭ ] x4 (Prechorus) C (C/) (chorus) [C G B♭ F ] x4 (Tag Chorus)  ");
            post(MeasureEditType.delete, "V:0:0", "C♯m");
            a.setCurrentChordSectionLocation(ChordSectionLocation.parse("V:0"));
            assertEquals("[C♯m A♭ F A♭ ] x4 ", a.getCurrentChordSectionLocationMeasureNode().toMarkup());
            a.setCurrentChordSectionLocation(ChordSectionLocation.parse("V:1:0"));
            assertEquals("(Prechorus)", a.getCurrentChordSectionLocationMeasureNode().toMarkup());
            pre(MeasureEditType.delete, "V:1:0", "(Prechorus)", "null");
            resultChords("V: [C♯m A♭ F A♭ ] x4 C (C/) (chorus) [C G B♭ F ] x4 (Tag Chorus)  ");
            post(MeasureEditType.delete, "V:1:0", "C");
            pre(MeasureEditType.delete, "V:1:1", "(C/)", "null");
            resultChords("V: [C♯m A♭ F A♭ ] x4 C (chorus) [C G B♭ F ] x4 (Tag Chorus)  ");
            post(MeasureEditType.delete, "V:1:1", "(chorus)");
            pre(MeasureEditType.delete, "V:1:1", "(chorus)", "null");
            resultChords("V: [C♯m A♭ F A♭ ] x4 C [C G B♭ F ] x4 (Tag Chorus)  ");
            post(MeasureEditType.delete, "V:2:0", "C");
            pre(MeasureEditType.delete, "V:3:0", "(Tag Chorus)", "null");
            resultChords("V: [C♯m A♭ F A♭ ] x4 C [C G B♭ F ] x4  ");
            post(MeasureEditType.delete, "V:2:3", "F");

            startingChords("I: CXCC XCCC CXCC XCCC (bass-only)  V: Cmaj7 Cmaj7 Cmaj7 Cmaj7 Cmaj7 C7 F F Dm G Em Am F G Cmaj7 Cmaj7  C: A♭ A♭ E♭ E♭ B♭ B♭ G G  O: Cmaj7 Cmaj7 Cmaj7 Cmaj7 Cmaj7 C7 F F Dm G Em Am F G Em A7 F F G G Cmaj7 Cmaj7 Cmaj7 Cmaj7 Cmaj7 Cmaj7 (fade)  ");
            pre(MeasureEditType.append, "I:0:4", "(bass-only)", "XCCC ");
            resultChords("I: CXCC XCCC CXCC XCCC (bass-only) XCCC  V: Cmaj7 Cmaj7 Cmaj7 Cmaj7 Cmaj7 C7 F F Dm G Em Am F G Cmaj7 Cmaj7  C: A♭ A♭ E♭ E♭ B♭ B♭ G G  O: Cmaj7 Cmaj7 Cmaj7 Cmaj7 Cmaj7 C7 F F Dm G Em Am F G Em A7 F F G G Cmaj7 Cmaj7 Cmaj7 Cmaj7 Cmaj7 Cmaj7 (fade)  ");
            post(MeasureEditType.append, "I:0:5", "XCCC");

            startingChords("I: V: [Am Am/G Am/F♯ FE ] x4  I2: [Am Am/G Am/F♯ FE ] x2  C: F F C C G G F F  O: Dm C B B♭ A  ");
            pre(MeasureEditType.append, "I:0", "[Am Am/G Am/F♯ FE ] x4 ", "E ");
            resultChords("I: V: [Am Am/G Am/F♯ FE ] x4 E  I2: [Am Am/G Am/F♯ FE ] x2  C: F F C C G G F F  O: Dm C B B♭ A  ");
            post(MeasureEditType.append, "I:1:0", "E");

            startingChords("I: V: O: E♭sus2 B♭ Gm7 Em F F7 G7 G Em Em Em Em Em Em Em Em Em C  C: [Cm F B♭ E♭ ] x3 Cm F  ");
            pre(MeasureEditType.delete, "I:0:14", "Em", "null");
            resultChords("I: V: O: E♭sus2 B♭ Gm7 Em F F7 G7 G Em Em Em Em Em Em Em Em C  C: [Cm F B♭ E♭ ] x3 Cm F  ");
            post(MeasureEditType.delete, "I:0:14", "Em");

            startingChords("I: V: O: E♭sus2 B♭ Gm7 C  C: [Cm F B♭ E♭ ] x3 Cm F  ");
            pre(MeasureEditType.append, "I:0:2", "Gm7", "Em7 ");
            resultChords("I: V: O: E♭sus2 B♭ Gm7 Em7 C  C: [Cm F B♭ E♭ ] x3 Cm F  ");
            post(MeasureEditType.append, "I:0:3", "Em7");

            startingChords("I: V: [Am Am/G Am/F♯ FE ] x4  I2: [Am Am/G Am/F♯ FE ] x2  C: F F C C G G F F  O: Dm C B B♭ A  ");
            pre(MeasureEditType.replace, "V:0:2", "Am/F♯", "Am/G ");
            resultChords("I: V: [Am Am/G Am/G FE ] x4  I2: [Am Am/G Am/F♯ FE ] x2  C: F F C C G G F F  O: Dm C B B♭ A  ");
            post(MeasureEditType.append, "V:0:2", "Am/G");

            startingChords("V: C F C C F F C C G F C G  ");
            pre(MeasureEditType.replace, "V:0:3", "C", "[] x1 ");
            resultChords("V: C F C C F F C C G F C G  ");
            post(MeasureEditType.replace, "V:0:3", "C");

            startingChords("V: C F C C F F C C G F C G  ");
            //                 0 1 2 3 4 5 6 7 8 9 0 1
            pre(MeasureEditType.replace, "V:0:6", "C", "[] x2 ");
            resultChords("V: [C F C C F F C C ] x2 G F C G  ");
            //               0 1 2 3  4 5 6 7      8 9 0 1
            //               0 1 2 3  0 1 2 3      0 1 2 3
            post(MeasureEditType.append, "V:0", "[C F C C F F C C ] x2");

            startingChords("V: C F C C F F C C G F C G  ");
            //                 0 1 2 3 4 5 6 7 8 9 0 1
            pre(MeasureEditType.replace, "V:0:6", "C", "[] x3 ");
            resultChords("V: [C F C C F F C C ] x3 G F C G  ");
            post(MeasureEditType.append, "V:0", "[C F C C F F C C ] x3");

            startingChords("I:  V:  ");
            pre(MeasureEditType.append, "V:", "V: []", "Dm ");
            resultChords("I: [] V: Dm  ");
            post(MeasureEditType.append, "V:0:0", "Dm");

            startingChords("I:  V:  ");
            pre(MeasureEditType.replace, "V:", "V: []", "Dm ");
            resultChords("I: [] V: Dm  ");
            post(MeasureEditType.append, "V:0:0", "Dm");

            startingChords("V: C F F C C F F C C G F C G  ");
            pre(MeasureEditType.delete, "V:", "V: C F F C C F F C C G F C G ", null);
            resultChords("");
            post(MeasureEditType.append, "V:", null);

            startingChords("V: C F C C F F C C G F C G  ");
            pre(MeasureEditType.append, "V:0:11", "G", "- ");
            resultChords("V: C F C C F F C C G F C G G  ");
            post(MeasureEditType.append, "V:0:12", "G");

            startingChords("V: C F C C F F C C G F C G G  ");
            pre(MeasureEditType.append, "V:0:1", "F", "-");
            resultChords("V: C F F C C F F C C G F C G G  ");
            post(MeasureEditType.append, "V:0:2", "F");

            startingChords("V: C F F C C F F C C G F C G G  ");
            pre(MeasureEditType.append, "V:0:2", "F", "  -  ");
            resultChords("V: C F F F C C F F C C G F C G G  ");
            post(MeasureEditType.append, "V:0:3", "F");

            startingChords("V: C F C C F F C C G F C G  ");
            pre(MeasureEditType.append, "V:0:1", "F", "-");
            resultChords("V: C F F C C F F C C G F C G  ");
            post(MeasureEditType.append, "V:0:2", "F");

            startingChords("I:  V:  ");
            pre(MeasureEditType.append, "V:", "V: []", "T: ");
            resultChords("I: [] V: [] T: []");        //  fixme: why is this?
            post(MeasureEditType.append, "T:", "T: []");

            startingChords("V: C F C C F F C C [G F C G ] x4  ");
            pre(MeasureEditType.replace, "V:1", "[G F C G ] x4 ", "B ");
            resultChords("V: C F C C F F C C B  ");
            //               0 1 2 3 4 5 6 7 8
            post(MeasureEditType.append, "V:0:8", "B");

            //  insert into a repeat
            startingChords("V: [C F C C ] x2 F F C C G F C G  ");
            pre(MeasureEditType.insert, "V:0:1", "F", "Dm ");
            resultChords("V: [C Dm F C C ] x2 F F C C G F C G  ");
            post(MeasureEditType.append, "V:0:1", "Dm");

            //  append into the middle
            startingChords("V: C Dm C C F F C C G F C G  ");
            pre(MeasureEditType.append, "V:0:1", "Dm", "Em ");
            resultChords("V: C Dm Em C C F F C C G F C G  ");
            post(MeasureEditType.append, "V:0:2", "Em");

            //  replace second measure
            startingChords("V: C F C C F F C C G F C G  ");  //
            pre(MeasureEditType.replace, "V:0:1", "F", "Dm ");  //
            resultChords("V: C Dm C C F F C C G F C G  ");  //
            post(MeasureEditType.append, "V:0:1", "Dm");  //


            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "",
                    "");
            logger.finer(a.toMarkup());
            newPhrase = Phrase.parse("A B C D", 0, beatsPerBar, null);
            logger.finer(newPhrase.toMarkup());
            assertTrue(a.edit(newPhrase));
            logger.fine(a.toMarkup());
            assertEquals("V: A B C D", a.toMarkup().trim());
            assertEquals("V:0:3", a.getCurrentChordSectionLocation().toString());

            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "",
                    "");
            logger.finer(a.toMarkup());
            newSection = ChordSection.parse("v:", beatsPerBar);
            assertTrue(a.edit(newSection));
            logger.fine(a.toMarkup());
            assertEquals("V: []", a.toMarkup().trim());
            assertEquals("V:", a.getCurrentChordSectionLocation().toString());
            a.setCurrentMeasureEditType(MeasureEditType.append);
            newPhrase = Phrase.parse("A B C D", 0, beatsPerBar, null);
            logger.finer(newPhrase.toMarkup());
            assertTrue(a.edit(newPhrase));
            logger.fine(a.toMarkup());
            assertEquals("V: A B C D", a.toMarkup().trim());
            assertEquals("V:0:3", a.getCurrentChordSectionLocation().toString());
            newMeasure = Measure.parse("E", beatsPerBar);
            logger.finer(newPhrase.toMarkup());
            assertTrue(a.edit(newMeasure));
            logger.fine(a.toMarkup());
            assertEquals("V: A B C D E", a.toMarkup().trim());
            assertEquals("V:0:4", a.getCurrentChordSectionLocation().toString());
            newPhrase = Phrase.parse("F", 0, beatsPerBar, null);
            logger.finer(newPhrase.toMarkup());
            assertTrue(a.edit(newPhrase));
            logger.fine(a.toMarkup());
            assertEquals("V: A B C D E F", a.toMarkup().trim());
            assertEquals("V:0:5", a.getCurrentChordSectionLocation().toString());

            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: nope nope");
            logger.finer(a.toMarkup());
            location = ChordSectionLocation.parse("i:0:3");
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.append);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            newPhrase = Phrase.parse("Db C B A", 0, beatsPerBar, null);
            assertTrue(a.edit(newPhrase));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D D♭ C B A  V: D E F F♯  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals("I:0:7", a.getCurrentChordSectionLocation().toString());
            assertEquals("A", a.getCurrentMeasureNode().toMarkup());

            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "V: C F C C [GB F C Dm7 ] x4 G F C G  ",
                    "v: bob, bob, bob berand");
            logger.finer(a.toMarkup());
            location = ChordSectionLocation.parse("v:1");
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.replace);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            newRepeat = MeasureRepeat.parse("[]x1", 0, beatsPerBar, null);
            assertTrue(a.edit(newRepeat));
            logger.fine(a.toMarkup());
            assertEquals("V: C F C C GB F C Dm7 G F C G", a.toMarkup().trim());
            //                        0 1 2 3 4  5 6 7   8 9 0 1
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals("V:0:7", a.getCurrentChordSectionLocation().toString());
            assertEquals("Dm7", a.getCurrentMeasureNode().toMarkup());

            //   current type	current edit loc	entry	replace entry	new edit type	new edit loc	result
            logger.fine("section	append	section(s)		replace	section(s)	add or replace section(s), de-dup");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = new ChordSectionLocation(v);
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.append);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            newSection = ChordSection.parse("v: A D C D", beatsPerBar);
            assertTrue(a.edit(newSection));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(location, a.getCurrentChordSectionLocation());
            assertEquals(newSection, a.getCurrentMeasureNode());

            logger.fine("repeat	append	section(s)		replace	section(s)	add or replace section(s), de-dup");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# [ D C B A ]x2 c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = new ChordSectionLocation(v, 1);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.append);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());
            location = ChordSectionLocation.parse("i:0:3");
            a.setCurrentChordSectionLocation(location);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            newMeasure = Measure.parse("F", beatsPerBar);
            assertTrue(a.edit(newMeasure));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D F  V: A D C D  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(ChordSectionLocation.parse("i:0:4"), a.getCurrentChordSectionLocation());
            assertEquals(newMeasure.toMarkup(), a.getCurrentChordSectionLocationMeasureNode().toMarkup());

            logger.fine("phrase	append	section(s)		replace	section(s)	add or replace section(s), de-dup");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# [ D C B A ]x2 c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            a.setCurrentChordSectionLocation(new ChordSectionLocation(v, 0));
            a.setCurrentMeasureEditType(MeasureEditType.append);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());

            logger.fine("measure	append	section(s)		replace	section(s)	add or replace section(s), de-dup");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# [ D C B A ]x2 c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            a.setCurrentChordSectionLocation(new ChordSectionLocation(v, 0));
            a.setCurrentMeasureEditType(MeasureEditType.append);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
            logger.finer(a.toMarkup());
            assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());

            logger.fine("section	insert	section(s)		replace	section(s)	add or replace section(s), de-dup");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = new ChordSectionLocation(v);
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.insert);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(location, a.getCurrentChordSectionLocation());

            logger.fine("repeat	insert	section(s)		replace	section(s)	add or replace section(s), de-dup");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# [ D C B A ]x2 c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = new ChordSectionLocation(v, 1);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.insert);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());

            logger.fine("phrase	insert	section(s)		replace	section(s)	add or replace section(s), de-dup");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# [ D C B A ]x2 c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            a.setCurrentChordSectionLocation(new ChordSectionLocation(v, 0));
            a.setCurrentMeasureEditType(MeasureEditType.insert);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());

            logger.fine("measure	insert	section(s)		replace	section(s)	add or replace section(s), de-dup");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# [ D C B A ]x2 c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            a.setCurrentChordSectionLocation(new ChordSectionLocation(v, 0));
            a.setCurrentMeasureEditType(MeasureEditType.insert);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
            logger.finer(a.toMarkup());
            assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());


            logger.fine("section	replace	section(s)		replace	section(s)	add or replace section(s), de-dup");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = new ChordSectionLocation(iSection);
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.replace);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());

            logger.fine("repeat	replace	section(s)		replace	section(s)	add or replace section(s), de-dup");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# [ D C B A ]x2 c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            a.setCurrentChordSectionLocation(new ChordSectionLocation(v, 1));
            a.setCurrentMeasureEditType(MeasureEditType.replace);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
            logger.finer(a.toMarkup());
            assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());

            logger.fine("phrase	replace	section(s)		replace	section(s)	add or replace section(s), de-dup");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# [ D C B A ]x2 c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            a.setCurrentChordSectionLocation(new ChordSectionLocation(v, 0));
            a.setCurrentMeasureEditType(MeasureEditType.replace);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
            logger.finer(a.toMarkup());
            assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());

            logger.fine("measure	replace	section(s)		replace	section(s)	add or replace section(s), de-dup");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# [ D C B A ]x2 c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            a.setCurrentChordSectionLocation(new ChordSectionLocation(v, 0, 2));
            a.setCurrentMeasureEditType(MeasureEditType.replace);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
            logger.finer(a.toMarkup());
            assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());


            logger.fine("section	delete	section(s)	yes	append	measure	delete section");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = new ChordSectionLocation(iSection);
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.delete);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
            logger.fine(a.toMarkup());
            assertEquals("V: D E F F♯  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = new ChordSectionLocation(v);
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.delete);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(new ChordSectionLocation(iSection), a.getCurrentChordSectionLocation());

            logger.fine("repeat  delete  section(s)  yes  append  measure  delete repeat");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D   V: D E F F# [ D C B A ]x2  c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = new ChordSectionLocation(v, 1);
            a.setCurrentChordSectionLocation(location);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            a.setCurrentMeasureEditType(MeasureEditType.delete);
            assertTrue(a.deleteCurrentSelection());
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: D E F F♯  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(ChordSectionLocation.parse("V:0:3"), a.getCurrentChordSectionLocation());

            logger.fine("phrase	delete	section(s)	yes	append	measure	delete phrase");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D   V: D E F F# [ D C B A ]x2  c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = new ChordSectionLocation(v);
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.delete);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.edit(ChordSection.parse("v:", beatsPerBar)));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(ChordSectionLocation.parse("I:"), a.getCurrentChordSectionLocation());

            logger.fine("measure	delete	section(s)	yes	append	measure	delete measure");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D   V: D E F F# [ D C B A ]x2  c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = new ChordSectionLocation(v, 0, 1);
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.delete);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.deleteCurrentSelection());
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: D F F♯ [D C B A ] x2  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(ChordSectionLocation.parse("V:0:1"), a.getCurrentChordSectionLocation());

            logger.fine("section  append  repeat    replace  repeat  add to start of section");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = new ChordSectionLocation(v);
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.append);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            newRepeat = MeasureRepeat.parse("[ A D C D ] x3", 0, beatsPerBar, null);
            assertTrue(a.edit(newRepeat));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: D E F F♯ [A D C D ] x3  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(ChordSectionLocation.parse("V:1"), a.getCurrentChordSectionLocation());

            //   current type	current edit loc	entry	replace entry	new edit type	new edit loc	result
            logger.fine("repeat  append  repeat    replace  repeat  replace repeat");
            //  x1 repeat should be converted to phrase
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: [D E F F#]x3 c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = new ChordSectionLocation(v, 0);
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.replace);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            newRepeat = MeasureRepeat.parse("[ A D C D ] x1", 0, beatsPerBar, null);
            assertTrue(a.edit(newRepeat));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(ChordSectionLocation.parse("V:0"), a.getCurrentChordSectionLocation());

            //  empty x1 repeat appended should be convert repeat to phrase
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: [D E F F#]x3 c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = new ChordSectionLocation(v, 0);
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.append);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            newRepeat = MeasureRepeat.parse("[] x1", 0, beatsPerBar, null);
            assertTrue(a.edit(newRepeat));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: D E F F♯  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(ChordSectionLocation.parse("V:0:3"), a.getCurrentChordSectionLocation());

            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: [D E F F#]x3 c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = new ChordSectionLocation(v, 0);
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.replace);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            newRepeat = MeasureRepeat.parse("[ A D C D ] x4", 0, beatsPerBar, null);
            assertTrue(a.edit(newRepeat));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: [A D C D ] x4  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(ChordSectionLocation.parse("V:0"), a.getCurrentChordSectionLocation());

            logger.fine("phrase  append  repeat    replace  repeat  append repeat");

            logger.fine("measure  append  repeat    replace  repeat  append repeat");
            //  empty repeat replaces current phrase
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = ChordSectionLocation.parse("v:0:3");
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.append);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            newRepeat = MeasureRepeat.parse("[ ] x3", 0, beatsPerBar, null);
            assertTrue(a.edit(newRepeat));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: [D E F F♯ ] x3  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(ChordSectionLocation.parse("v:0"), a.getCurrentChordSectionLocation());
            //  non-empty repeat appends to current section
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = ChordSectionLocation.parse("v:0:3");
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.append);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            newRepeat = MeasureRepeat.parse("[ D C G G] x3", 0, beatsPerBar, null);
            assertTrue(a.edit(newRepeat));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: D E F F♯ [D C G G ] x3  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(ChordSectionLocation.parse("v:1"), a.getCurrentChordSectionLocation());


            logger.fine("section  insert  repeat    replace  repeat  add to start of section");
            logger.fine("repeat  insert  repeat    replace  repeat  insert repeat");
            logger.fine("phrase  insert  repeat    replace  repeat  insert repeat");
            logger.fine("measure  insert  repeat    replace  repeat  insert repeat");
            logger.fine("section  replace  repeat    replace  repeat  replace section content");
            logger.fine("repeat  replace  repeat    replace  repeat  replace repeat");
            logger.fine("phrase  replace  repeat    replace  repeat  replace phrase");
            logger.fine("measure  replace  repeat    replace  repeat  replace measure");
            logger.fine("section  delete  repeat  yes  append  measure  delete section");
            logger.fine("repeat  delete  repeat  yes  append  measure  delete repeat");
            logger.fine("phrase  delete  repeat  yes  append  measure  delete phrase");
            logger.fine("measure  delete  repeat  yes  append  measure  delete measure");
            logger.fine("section  append  phrase       phrase  append to end of section");
            logger.fine("repeat  append  phrase    replace  phrase  append to end of repeat");
            logger.fine("phrase  append  phrase    replace  phrase  append to end of phrase, join phrases");
            logger.fine("measure  append  phrase    replace  phrase  append to end of measure, join phrases");
            logger.fine("section  insert  phrase    replace  phrase  insert to start of section");
            logger.fine("repeat  insert  phrase    replace  phrase  insert to start of repeat content");
            logger.fine("phrase  insert  phrase    replace  phrase  insert to start of phrase");
            logger.fine("measure  insert  phrase    replace  phrase  insert at start of measure");
            logger.fine("section  replace  phrase    replace  phrase  replace section content");
            logger.fine("repeat  replace  phrase    replace  phrase  replace repeat content");
            logger.fine("phrase  replace  phrase    replace  phrase  replace");
            logger.fine("measure  replace  phrase    replace  phrase  replace");
            logger.fine("section  delete  phrase  yes  append  measure  delete section");
            logger.fine("repeat  delete  phrase  yes  append  measure  delete repeat");
            logger.fine("phrase  delete  phrase  yes  append  measure  delete phrase");
            logger.fine("measure  delete  phrase  yes  append  measure  delete measure");
            logger.fine("section  append  measure    append  measure  append to end of section");
            logger.fine("repeat  append  measure    append  measure  append past end of repeat");
            logger.fine("phrase  append  measure    append  measure  append to end of phrase");
            logger.fine("measure  append  measure    append  measure  append to end of measure");
            logger.fine("section  insert  measure    append  measure  insert to start of section");
            logger.fine("repeat  insert  measure    append  measure  insert prior to start of repeat");
            logger.fine("phrase  insert  measure    append  measure  insert to start of phrase");

            logger.fine("measure  insert  measure    append  measure  insert to start of measure");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# [ A D C D ] x3 c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = ChordSectionLocation.parse("v:0:2");
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.insert);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            newMeasure = Measure.parse("Gm", beatsPerBar);
            assertTrue(a.edit(newMeasure));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: D E Gm F F♯ [A D C D ] x3  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(location, a.getCurrentChordSectionLocation());

            logger.fine("section  replace  measure    append  measure  replace section content");
            logger.fine("repeat  replace  measure    append  measure  replace repeat");
            logger.fine("phrase  replace  measure    append  measure  replace phrase");
            logger.fine("measure  replace  measure    append  measure  replace");
            logger.fine("section  delete  measure  yes  append  measure  delete section");
            logger.fine("repeat  delete  measure  yes  append  measure  delete repeat");
            logger.fine("phrase  delete  measure  yes  append  measure  delete phrase");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = ChordSectionLocation.parse("v:0:2");
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.delete);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.deleteCurrentSelection());
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: D E F♯  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(location, a.getCurrentChordSectionLocation());

            logger.fine("measure  delete  measure  yes  append  measure  delete measure");
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = ChordSectionLocation.parse("v:0:2");
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.delete);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.deleteCurrentSelection());
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: D E F♯  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(location, a.getCurrentChordSectionLocation());
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4,
                    "i: A B C D V: D E F F# c: D C G G",
                    "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
            logger.finer(a.toMarkup());
            location = ChordSectionLocation.parse("v:0:2");
            a.setCurrentChordSectionLocation(location);
            a.setCurrentMeasureEditType(MeasureEditType.delete);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            newMeasure = Measure.parse("F", beatsPerBar);
            assertTrue(a.edit(newMeasure));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: D E F♯  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(location, a.getCurrentChordSectionLocation());

        } catch (ParseException pex) {
            pex.printStackTrace();
        }
    }


    private final void startingChords(String chords) {
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                chords,
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
    }

    private final void pre(MeasureEditType type, String locationString, String measureNodeString, String editEntry) {
        a.setCurrentMeasureEditType(type);
        if (locationString != null && !locationString.isEmpty()) {
            try {
                a.setCurrentChordSectionLocation(ChordSectionLocation.parse(locationString));
            } catch (ParseException pe) {

                fail(pe.getMessage());
            }
            assertEquals(locationString, a.getCurrentChordSectionLocation().toString());

            if (measureNodeString != null) {
                assertEquals(measureNodeString.trim(), a.getCurrentMeasureNode().toMarkup().trim());
            }
        }

        logger.fine("editEntry: " + editEntry);
        ArrayList<MeasureNode> measureNodes = a.parseChordEntry(editEntry);
        if (measureNodes.isEmpty()
                && (editEntry == null || editEntry.isEmpty())
                && type == MeasureEditType.delete) {
            assertTrue(a.deleteCurrentSelection());
        } else {
            for (MeasureNode measureNode : measureNodes) {
                logger.finer("edit: " + measureNode.toMarkup());
            }
            assertTrue(a.edit(measureNodes));
        }
    }

    private final void resultChords(String chords) {

        assertEquals(chords.trim(), a.toMarkup().trim());
    }

    private final void post(MeasureEditType type, String locationString, String measureNodeString) {
        assertEquals(type, a.getCurrentMeasureEditType());
        assertEquals(locationString, a.getCurrentChordSectionLocation().toString());
        logger.finer("measureNodeString: " + measureNodeString);
        logger.finer("getCurrentMeasureNode(): " + a.getCurrentMeasureNode());
        if (measureNodeString == null)
            assertTrue(a.getCurrentMeasureNode() == null);
        else {
            assertEquals(measureNodeString.trim(), a.getCurrentMeasureNode().toMarkup().trim());
        }
    }


    /**
     * A convenience constructor used to enforce the minimum requirements for a song.
     *
     * @param title
     * @param artist
     * @param copyright
     * @param key
     * @param bpm
     * @param beatsPerBar
     * @param unitsPerMeasure
     * @param chords
     * @param lyrics
     * @return
     */
    public static final SongBase createSongBase(@NotNull String title, @NotNull String artist,
                                                @NotNull String copyright,
                                                @NotNull Key key, int bpm, int beatsPerBar, int unitsPerMeasure,
                                                @NotNull String chords, @NotNull String lyrics) {
        SongBase song = new SongBase();
        song.setTitle(title);
        song.setArtist(artist);
        song.setCopyright(copyright);
        song.setKey(key);
        song.setUnitsPerMeasure(unitsPerMeasure);
        try {
            song.parseChords(chords);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        song.setRawLyrics(lyrics);

        song.setBeatsPerMinute(bpm);
        song.setBeatsPerBar(beatsPerBar);

        return song;
    }

    private SongBase a;
    private static Logger logger = Logger.getLogger(SongEditTest.class.getName());
}
