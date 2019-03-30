package com.bsteele.bsteeleMusicApp.shared.songs;

import junit.framework.TestCase;
import org.junit.Test;

import javax.validation.constraints.NotNull;
import java.util.logging.Logger;

public class SongEditTest extends TestCase {

    @Test
    public void testEdits() {

        SectionVersion v = SectionVersion.parse("v:");
        SectionVersion iSection = SectionVersion.parse("i:");
        int beatsPerBar = 4;
        ChordSectionLocation location;

        //   current type	current edit loc	entry	replace entry	new edit type	new edit loc	result
        //   section	append	section(s)		replace	section(s)	add or replace section(s), de-dup
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D V: D E F F# c: D C G G",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        logger.finer(a.toMarkup());
         location = new ChordSectionLocation(v);
        a.setCurrentChordSectionLocation(location);
        a.setCurrentMeasureEditType(MeasureEditType.append);
        logger.fine(a.getCurrentChordSectionLocation()+"  "+a.findMeasureNode(a.getCurrentChordSectionLocation())
                +" "+a.getCurrentMeasureEditType().toString()+" "+a.getCurrentChordSectionLocationMeasure());
        assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
        logger.fine(a.toMarkup());
        assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        assertEquals(location, a.getCurrentChordSectionLocation());

        //   repeat	append	section(s)		replace	section(s)	add or replace section(s), de-dup
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D V: D E F F# [ D C B A ]x2 c: D C G G",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        logger.finer(a.toMarkup());
        location = new ChordSectionLocation(v,1);
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        a.setCurrentChordSectionLocation(location);
        a.setCurrentMeasureEditType(MeasureEditType.append);
        logger.fine(a.getCurrentChordSectionLocation()+"  "+a.findMeasureNode(a.getCurrentChordSectionLocation())
                +" "+a.getCurrentMeasureEditType().toString()+" "+a.getCurrentChordSectionLocationMeasure());
        assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
        logger.fine(a.toMarkup());
        assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());

        //   phrase	append	section(s)		replace	section(s)	add or replace section(s), de-dup
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D V: D E F F# [ D C B A ]x2 c: D C G G",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        logger.finer(a.toMarkup());
        a.setCurrentChordSectionLocation(new ChordSectionLocation(v,0));
        a.setCurrentMeasureEditType(MeasureEditType.append);
        logger.fine(a.getCurrentChordSectionLocation()+"  "+a.findMeasureNode(a.getCurrentChordSectionLocation())
                +" "+a.getCurrentMeasureEditType().toString()+" "+a.getCurrentChordSectionLocationMeasure());
        assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
        logger.fine(a.toMarkup());
        assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());

        //   measure	append	section(s)		replace	section(s)	add or replace section(s), de-dup
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D V: D E F F# [ D C B A ]x2 c: D C G G",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        logger.finer(a.toMarkup());
        a.setCurrentChordSectionLocation(new ChordSectionLocation(v,0));
        a.setCurrentMeasureEditType(MeasureEditType.append);
        logger.fine(a.getCurrentChordSectionLocation()+"  "+a.findMeasureNode(a.getCurrentChordSectionLocation())
                +" "+a.getCurrentMeasureEditType().toString()+" "+a.getCurrentChordSectionLocationMeasure());
        assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
        logger.finer(a.toMarkup());
        assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());

        //   section	insert	section(s)		replace	section(s)	add or replace section(s), de-dup
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D V: D E F F# c: D C G G",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        logger.finer(a.toMarkup());
         location = new ChordSectionLocation(v);
        a.setCurrentChordSectionLocation(location);
        a.setCurrentMeasureEditType(MeasureEditType.insert);
        logger.fine(a.getCurrentChordSectionLocation()+"  "+a.findMeasureNode(a.getCurrentChordSectionLocation())
                +" "+a.getCurrentMeasureEditType().toString()+" "+a.getCurrentChordSectionLocationMeasure());
        assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
        logger.fine(a.toMarkup());
        assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        assertEquals(location, a.getCurrentChordSectionLocation());

        //   repeat	insert	section(s)		replace	section(s)	add or replace section(s), de-dup
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D V: D E F F# [ D C B A ]x2 c: D C G G",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        logger.finer(a.toMarkup());
        location = new ChordSectionLocation(v,1);
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        a.setCurrentChordSectionLocation(location);
        a.setCurrentMeasureEditType(MeasureEditType.insert);
        logger.fine(a.getCurrentChordSectionLocation()+"  "+a.findMeasureNode(a.getCurrentChordSectionLocation())
                +" "+a.getCurrentMeasureEditType().toString()+" "+a.getCurrentChordSectionLocationMeasure());
        assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
        logger.fine(a.toMarkup());
        assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());

        //   phrase	insert	section(s)		replace	section(s)	add or replace section(s), de-dup
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D V: D E F F# [ D C B A ]x2 c: D C G G",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        logger.finer(a.toMarkup());
        a.setCurrentChordSectionLocation(new ChordSectionLocation(v,0));
        a.setCurrentMeasureEditType(MeasureEditType.insert);
        logger.fine(a.getCurrentChordSectionLocation()+"  "+a.findMeasureNode(a.getCurrentChordSectionLocation())
                +" "+a.getCurrentMeasureEditType().toString()+" "+a.getCurrentChordSectionLocationMeasure());
        assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
        logger.fine(a.toMarkup());
        assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());

        //   measure	insert	section(s)		replace	section(s)	add or replace section(s), de-dup
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D V: D E F F# [ D C B A ]x2 c: D C G G",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        logger.finer(a.toMarkup());
        a.setCurrentChordSectionLocation(new ChordSectionLocation(v,0));
        a.setCurrentMeasureEditType(MeasureEditType.insert);
        logger.fine(a.getCurrentChordSectionLocation()+"  "+a.findMeasureNode(a.getCurrentChordSectionLocation())
                +" "+a.getCurrentMeasureEditType().toString()+" "+a.getCurrentChordSectionLocationMeasure());
        assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
        logger.finer(a.toMarkup());
        assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());


        //   section	replace	section(s)		replace	section(s)	add or replace section(s), de-dup
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D V: D E F F# c: D C G G",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        logger.finer(a.toMarkup());
        location = new ChordSectionLocation(iSection);
        a.setCurrentChordSectionLocation(location);
        a.setCurrentMeasureEditType(MeasureEditType.replace);
        logger.fine(a.getCurrentChordSectionLocation()+"  "+a.findMeasureNode(a.getCurrentChordSectionLocation())
                +" "+a.getCurrentMeasureEditType().toString()+" "+a.getCurrentChordSectionLocationMeasure());
        assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
        logger.fine(a.toMarkup());
        assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());

        //   repeat	replace	section(s)		replace	section(s)	add or replace section(s), de-dup
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D V: D E F F# [ D C B A ]x2 c: D C G G",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        logger.finer(a.toMarkup());
        a.setCurrentChordSectionLocation(new ChordSectionLocation(v,1));
        a.setCurrentMeasureEditType(MeasureEditType.replace);
        logger.fine(a.getCurrentChordSectionLocation()+"  "+a.findMeasureNode(a.getCurrentChordSectionLocation())
                +" "+a.getCurrentMeasureEditType().toString()+" "+a.getCurrentChordSectionLocationMeasure());
        assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
        logger.finer(a.toMarkup());
        assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());

        //   phrase	replace	section(s)		replace	section(s)	add or replace section(s), de-dup
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D V: D E F F# [ D C B A ]x2 c: D C G G",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        logger.finer(a.toMarkup());
        a.setCurrentChordSectionLocation(new ChordSectionLocation(v,0));
        a.setCurrentMeasureEditType(MeasureEditType.replace);
        logger.fine(a.getCurrentChordSectionLocation()+"  "+a.findMeasureNode(a.getCurrentChordSectionLocation())
                +" "+a.getCurrentMeasureEditType().toString()+" "+a.getCurrentChordSectionLocationMeasure());
        assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
        logger.finer(a.toMarkup());
        assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());

        //   measure	replace	section(s)		replace	section(s)	add or replace section(s), de-dup
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D V: D E F F# [ D C B A ]x2 c: D C G G",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        logger.finer(a.toMarkup());
        a.setCurrentChordSectionLocation(new ChordSectionLocation(v,0,2));
        a.setCurrentMeasureEditType(MeasureEditType.replace);
        logger.fine(a.getCurrentChordSectionLocation()+"  "+a.findMeasureNode(a.getCurrentChordSectionLocation())
                +" "+a.getCurrentMeasureEditType().toString()+" "+a.getCurrentChordSectionLocationMeasure());
        assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
        logger.finer(a.toMarkup());
        assertEquals("I: A B C D  V: A D C D  C: D C G G", a.toMarkup().trim());
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());


        //   section	delete	section(s)	yes	append	measure	delete section
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D V: D E F F# c: D C G G",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        logger.finer(a.toMarkup());
        location = new ChordSectionLocation(iSection);
        a.setCurrentChordSectionLocation(location);
        a.setCurrentMeasureEditType(MeasureEditType.delete);
        logger.fine(a.getCurrentChordSectionLocation()+"  "+a.findMeasureNode(a.getCurrentChordSectionLocation())
                +" "+a.getCurrentMeasureEditType().toString()+" "+a.getCurrentChordSectionLocationMeasure());
        assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
        logger.fine(a.toMarkup());
        assertEquals("V: D E F F♯  C: D C G G", a.toMarkup().trim());
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        assertEquals(new ChordSectionLocation(v), a.getCurrentChordSectionLocation());
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D V: D E F F# c: D C G G",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        logger.finer(a.toMarkup());
        location = new ChordSectionLocation(v);
        a.setCurrentChordSectionLocation(location);
        a.setCurrentMeasureEditType(MeasureEditType.delete);
        logger.fine(a.getCurrentChordSectionLocation()+"  "+a.findMeasureNode(a.getCurrentChordSectionLocation())
                +" "+a.getCurrentMeasureEditType().toString()+" "+a.getCurrentChordSectionLocationMeasure());
        assertTrue(a.edit(ChordSection.parse("v: A D C D", beatsPerBar)));
        logger.fine(a.toMarkup());
        assertEquals("I: A B C D  C: D C G G", a.toMarkup().trim());
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        assertEquals(new ChordSectionLocation(iSection), a.getCurrentChordSectionLocation());

        //   repeat	delete	section(s)	yes	append	measure	delete repeat
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D   V: D E F F# [ D C B A ]x2  c: D C G G",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        logger.finer(a.toMarkup());
        location = new ChordSectionLocation(v);
        a.setCurrentChordSectionLocation(new ChordSectionLocation(v,1));
        a.setCurrentMeasureEditType(MeasureEditType.delete);
        logger.fine(a.getCurrentChordSectionLocation()+"  "+a.findMeasureNode(a.getCurrentChordSectionLocation())
                +" "+a.getCurrentMeasureEditType().toString()+" "+a.getCurrentChordSectionLocationMeasure());
        {
            MeasureRepeat repeat = MeasureRepeat.parse("[ D C B A ]x2", 1, beatsPerBar);
            assertTrue(a.edit(repeat));
        }
        logger.fine(a.toMarkup());
        assertEquals("I: A B C D  V: D E F F♯  C: D C G G", a.toMarkup().trim());
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        assertEquals(ChordSectionLocation.parse("V:0:3"), a.getCurrentChordSectionLocation());

        //   phrase	delete	section(s)	yes	append	measure	delete phrase
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D   V: D E F F# [ D C B A ]x2  c: D C G G",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        logger.finer(a.toMarkup());
        location = new ChordSectionLocation(v,0);
        a.setCurrentChordSectionLocation(location);
        a.setCurrentMeasureEditType(MeasureEditType.delete);
        logger.fine(a.getCurrentChordSectionLocation()+"  "+a.findMeasureNode(a.getCurrentChordSectionLocation())
                +" "+a.getCurrentMeasureEditType().toString()+" "+a.getCurrentChordSectionLocationMeasure());
        assertTrue(a.edit(ChordSection.parse("v:", beatsPerBar)));
        logger.fine(a.toMarkup());
        assertEquals("I: A B C D  C: D C G G", a.toMarkup().trim());
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        assertEquals(ChordSectionLocation.parse("I:"), a.getCurrentChordSectionLocation());

        //   measure	delete	section(s)	yes	append	measure	delete measure
        a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4,
                "i: A B C D   V: D E F F# [ D C B A ]x2  c: D C G G",
                "i:\nv: bob, bob, bob berand\nc: sing chorus here \no: last line of outro");
        logger.finer(a.toMarkup());
        location = new ChordSectionLocation(v,0,1);
        a.setCurrentChordSectionLocation(location);
        a.setCurrentMeasureEditType(MeasureEditType.delete);
        logger.fine(a.getCurrentChordSectionLocation()+"  "+a.findMeasureNode(a.getCurrentChordSectionLocation())
                +" "+a.getCurrentMeasureEditType().toString()+" "+a.getCurrentChordSectionLocationMeasure());
        assertTrue(a.edit(ChordSection.parse("v:", beatsPerBar)));
        logger.fine(a.toMarkup());
        assertEquals("I: A B C D  C: D C G G", a.toMarkup().trim());
        logger.fine(a.getCurrentChordSectionLocation()+" "+a.getCurrentChordSectionLocationMeasure());
        assertEquals(ChordSectionLocation.parse("I:"), a.getCurrentChordSectionLocation());

        //   section	append	repeat		replace	repeat	add to start of section
        //   repeat	append	repeat		replace	repeat	replace repeat
        //   phrase	append	repeat		replace	repeat	append repeat
        //   measure	append	repeat		replace	repeat	append repeat
        //   section	insert	repeat		replace	repeat	add to start of section
        //   repeat	insert	repeat		replace	repeat	insert repeat
        //   phrase	insert	repeat		replace	repeat	insert repeat
        //   measure	insert	repeat		replace	repeat	insert repeat
        //   section	replace	repeat		replace	repeat	replace section content
        //   repeat	replace	repeat		replace	repeat	replace repeat
        //   phrase	replace	repeat		replace	repeat	replace phrase
        //   measure	replace	repeat		replace	repeat	replace measure
        //   section	delete	repeat	yes	append	measure	delete section
        //   repeat	delete	repeat	yes	append	measure	delete repeat
        //   phrase	delete	repeat	yes	append	measure	delete phrase
        //   measure	delete	repeat	yes	append	measure	delete measure
        //   section	append	phrase		 	phrase	append to end of section
        //   repeat	append	phrase		replace	phrase	append to end of repeat
        //   phrase	append	phrase		replace	phrase	append to end of phrase, join phrases
        //   measure	append	phrase		replace	phrase	append to end of measure, join phrases
        //   section	insert	phrase		replace	phrase	insert to start of section
        //   repeat	insert	phrase		replace	phrase	insert to start of repeat content
        //   phrase	insert	phrase		replace	phrase	insert to start of phrase
        //   measure	insert	phrase		replace	phrase	insert at start of measure
        //   section	replace	phrase		replace	phrase	replace section content
        //   repeat	replace	phrase		replace	phrase	replace repeat content
        //   phrase	replace	phrase		replace	phrase	replace
        //   measure	replace	phrase		replace	phrase	replace
        //   section	delete	phrase	yes	append	measure	delete section
        //   repeat	delete	phrase	yes	append	measure	delete repeat
        //   phrase	delete	phrase	yes	append	measure	delete phrase
        //   measure	delete	phrase	yes	append	measure	delete measure
        //   section	append	measure		append	measure	append to end of section
        //   repeat	append	measure		append	measure	append past end of repeat
        //   phrase	append	measure		append	measure	append to end of phrase
        //   measure	append	measure		append	measure	append to end of measure
        //   section	insert	measure		append	measure	insert to start of section
        //   repeat	insert	measure		append	measure	insert prior to start of repeat
        //   phrase	insert	measure		append	measure	insert to start of phrase
        //   measure	insert	measure		append	measure	insert to start of measure
        //   section	replace	measure		append	measure	replace section content
        //   repeat	replace	measure		append	measure	replace repeat
        //   phrase	replace	measure		append	measure	replace phrase
        //   measure	replace	measure		append	measure	replace
        //   section	delete	measure	yes	append	measure	delete section
        //   repeat	delete	measure	yes	append	measure	delete repeat
        //   phrase	delete	measure	yes	append	measure	delete phrase
        //   measure	delete	measure	yes	append	measure	delete measure

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
        song.parseChords(chords);
        song.setRawLyrics(lyrics);

        song.setBeatsPerMinute(bpm);
        song.setBeatsPerBar(beatsPerBar);

        return song;
    }

    private SongBase a;
    private static Logger logger = Logger.getLogger(SongEditTest.class.getName());
}
