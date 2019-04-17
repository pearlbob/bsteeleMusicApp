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

            startingChords("V: C F C C F F C C [G F C G ] x4  ");
            pre(MeasureEditType.replace, "V:1", "[G F C G ] x4 ", "B ");
            resultChords("V: A C F C C F F C C B  ");
            post(MeasureEditType.replace, "V:0:9", "B");

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
            newPhrase = Phrase.parse("A B C D", 0, beatsPerBar);
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
            assertEquals("V:", a.toMarkup().trim());
            assertEquals("V:", a.getCurrentChordSectionLocation().toString());
            a.setCurrentMeasureEditType(MeasureEditType.append);
            newPhrase = Phrase.parse("A B C D", 0, beatsPerBar);
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
            newPhrase = Phrase.parse("F", 0, beatsPerBar);
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
            newPhrase = Phrase.parse("Db C B A", 0, beatsPerBar);
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
            newRepeat = MeasureRepeat.parse("[]x1", 0, beatsPerBar);
            assertTrue(a.edit(newRepeat));
            logger.fine(a.toMarkup());
            assertEquals("V: C F C C GB F C Dm7 G F C G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals("V:0:11", a.getCurrentChordSectionLocation().toString());
            assertEquals("G", a.getCurrentMeasureNode().toMarkup());

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
            assertTrue(a.deleteCurrentChordSectionLocation());
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
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.deleteCurrentChordSectionLocation());
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
            newRepeat = MeasureRepeat.parse("[ A D C D ] x3", 0, beatsPerBar);
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
            newRepeat = MeasureRepeat.parse("[ A D C D ] x1", 0, beatsPerBar);
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
            newRepeat = MeasureRepeat.parse("[] x1", 0, beatsPerBar);
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
            newRepeat = MeasureRepeat.parse("[ A D C D ] x4", 0, beatsPerBar);
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
            newRepeat = MeasureRepeat.parse("[ ] x3", 0, beatsPerBar);
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
            newRepeat = MeasureRepeat.parse("[ D C G G] x3", 0, beatsPerBar);
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
            assertTrue(a.deleteCurrentChordSectionLocation());
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
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertTrue(a.deleteCurrentChordSectionLocation());
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
        try {
            a.setCurrentChordSectionLocation(ChordSectionLocation.parse(locationString));
        } catch (ParseException pe) {
            fail(pe.getMessage());
        }
        assertEquals(locationString, a.getCurrentChordSectionLocation().toString());
        assertEquals(measureNodeString, a.getCurrentMeasureNode().toMarkup());
        logger.fine("editEntry: " + editEntry);
        ArrayList<MeasureNode> measureNodes = SongBase.parseChordEntry(editEntry, a.getBeatsPerBar());
        for (MeasureNode measureNode : measureNodes) {
            logger.finer("edit: " + measureNode.toMarkup());
            assertTrue(a.edit(measureNode));
        }
    }

    private final void resultChords(String chords) {
        assertEquals(chords, a.toMarkup());
    }

    private final void post(MeasureEditType type, String locationString, String measureNodeString) {
        assertEquals(type, a.getCurrentMeasureEditType());
        assertEquals(locationString, a.getCurrentChordSectionLocation().toString());
        assertEquals(measureNodeString, a.getCurrentMeasureNode().toMarkup());
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
