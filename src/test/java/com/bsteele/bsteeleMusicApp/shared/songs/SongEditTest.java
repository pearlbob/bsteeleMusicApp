package com.bsteele.bsteeleMusicApp.shared.songs;

import junit.framework.TestCase;
import org.junit.Test;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
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
            Measure newMeasure;

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
            location = new ChordSectionLocation(v);
            a.setCurrentChordSectionLocation(new ChordSectionLocation(v, 1));
            a.setCurrentMeasureEditType(MeasureEditType.delete);
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.findMeasureNode(a.getCurrentChordSectionLocation())
                    + " " + a.getCurrentMeasureEditType().toString() + " " + a.getCurrentChordSectionLocationMeasureNode());
            {
                MeasureRepeat repeat = MeasureRepeat.parse("[ D C B A ]x2", 1, beatsPerBar);
                assertTrue(a.edit(repeat));
            }
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
            assertTrue(a.edit(ChordSection.parse("v:", beatsPerBar)));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: D F F♯ [D C B A ] x2  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(ChordSectionLocation.parse("V:"), a.getCurrentChordSectionLocation());

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
            assertEquals("I: A B C D  V: [A D C D ] x3 D E F F♯  C: D C G G", a.toMarkup().trim());
            logger.fine(a.getCurrentChordSectionLocation() + " " + a.getCurrentChordSectionLocationMeasureNode());
            assertEquals(location, a.getCurrentChordSectionLocation());

            logger.fine("repeat  append  repeat    replace  repeat  replace repeat");
            logger.fine("phrase  append  repeat    replace  repeat  append repeat");

            logger.fine("measure  append  repeat    replace  repeat  append repeat");


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
            newMeasure = Measure.parse("G", beatsPerBar);
            assertTrue(a.edit(newMeasure));
            logger.fine(a.toMarkup());
            assertEquals("I: A B C D  V: D E G F F♯ [A D C D ] x3  C: D C G G", a.toMarkup().trim());
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
            newMeasure = null;
            assertTrue(a.edit(newMeasure));
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
            newMeasure = null;
            assertTrue(a.edit(newMeasure));
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
