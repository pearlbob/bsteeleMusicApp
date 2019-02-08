package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.songs.ChordSection;
import com.bsteele.bsteeleMusicApp.shared.songs.Measure;
import com.bsteele.bsteeleMusicApp.shared.songs.MeasureComment;
import com.bsteele.bsteeleMusicApp.shared.songs.MeasureNode;
import com.bsteele.bsteeleMusicApp.shared.songs.MeasureSequenceItem;
import com.bsteele.bsteeleMusicApp.shared.songs.ScaleNote;
import com.bsteele.bsteeleMusicApp.shared.songs.Section;
import com.bsteele.bsteeleMusicApp.shared.songs.SectionVersion;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ChordSectionTest extends TestCase {

    @Test
    public void testChordSectionParse() {
        ArrayList<MeasureSequenceItem> measureSequenceItems;
        MeasureSequenceItem measureSequenceItem;
        ArrayList<Measure> measures;

        {
            //  lost : ?
            ChordSection chordSection = ChordSection.testParse(
                    "V: \n"
                    +"A C#m F#m F#m/E\n"
                    +"G Bm F#m G GBm  x3\n"
                    +"A C#m F#m F#m/E\n"
                    +"G G Bm Bm\n"
                    , 4);
            assertNotNull(chordSection);
            Measure m = chordSection.getMeasureSequenceItems().get(0).getMeasures().get(0);
            assert (m instanceof Measure);
        }
        {
            //  invented garbage is comment
            ChordSection chordSection = ChordSection.testParse(
                    "ia: EDCBA (single notes rapid)", 4);
            assertNotNull(chordSection);
            Measure m = chordSection.getMeasureSequenceItems().get(0).getMeasures().get(0);
            assert (m instanceof MeasureComment);
        }
        {
            ChordSection chordSection = ChordSection.testParse(
                    "v:Am Am Am AmDm\n"
                            + "Dm Dm Dm DmAm 2x\n"
                            + "\n", 4);
            assertNotNull(chordSection);
            measures = chordSection.getMeasureSequenceItems().get(0).getMeasures();
            assertNotNull(measures);
            assert (!measures.isEmpty());
            Measure m = measures.get(measures.size() - 1);
            assert (m instanceof MeasureComment);
        }
        {
            //  infinite loop?
            ChordSection chordSection = ChordSection.testParse("o:AGEDCAGEDCAGA (organ descending scale)", 4);
            assertTrue(chordSection != null);
            SectionVersion outro = new SectionVersion(Section.outro);
            assertTrue(chordSection.getSectionVersion().equals(outro));
        }

        {
            //  failure to parse a leading dot
            ChordSection chordSection = ChordSection.testParse("I: G .G Bm Bm  x2", 4);
            assertTrue(chordSection != null);
            SectionVersion intro = new SectionVersion(Section.intro);
            assertTrue(chordSection.getSectionVersion().equals(intro));
            measureSequenceItems = chordSection.getMeasureSequenceItems();
            assertTrue(measureSequenceItems != null);
            assertEquals(1, measureSequenceItems.size());
            measures = measureSequenceItems.get(0).getMeasures();
            assertTrue(measures != null);
            assertEquals(2, measures.size());
            assertEquals("(.G Bm Bm  x2)", measures.get(1).toString());
        }
        {
            ChordSection chordSection = ChordSection.testParse("I: A B C D\n" +
                    "AbBb/G# Am7 Ebsus4 C7/Bb", 4);
            assertTrue(chordSection != null);
            SectionVersion intro = new SectionVersion(Section.intro);
            assertTrue(chordSection.getSectionVersion().equals(intro));
            measureSequenceItems = chordSection.getMeasureSequenceItems();
            assertTrue(measureSequenceItems != null);
            assertEquals(1, measureSequenceItems.size());
            measures = measureSequenceItems.get(0).getMeasures();
            assertTrue(measures != null);
            assertEquals(8, measures.size());

            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measures, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.B, measures, 1, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.C, measures, 2, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.D, measures, 3, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.Ab, measures, 4, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.Bb, measures, 4, 1);
            checkMeasureNodesSlashScaleNoteByMeasure(ScaleNote.Gs, measures, 4, 0);
            checkMeasureNodesSlashScaleNoteByMeasure(ScaleNote.Gs, measures, 4, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measures, 5, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.Eb, measures, 6, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.C, measures, 7, 0);
        }
        {
            ChordSection chordSection = ChordSection.testParse("I: A - - -\n" +
                    "Ab - - G ", 4);
            assertTrue(chordSection != null);
            SectionVersion intro = new SectionVersion(Section.intro);
            assertTrue(chordSection.getSectionVersion().equals(intro));
            measureSequenceItems = chordSection.getMeasureSequenceItems();
            assertTrue(measureSequenceItems != null);
            assertEquals(1, measureSequenceItems.size());
            measures = measureSequenceItems.get(0).getMeasures();
            assertTrue(measures != null);
            assertEquals(8, measures.size());
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measures, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measures, 1, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measures, 2, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measures, 3, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.Ab, measures, 4, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.Ab, measures, 5, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.Ab, measures, 6, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.G, measures, 7, 0);
        }
        {
            ChordSection chordSection = ChordSection.testParse("I: A - - -\n" +
                    "Ab - - X ", 4);
            assertTrue(chordSection != null);
            SectionVersion intro = new SectionVersion(Section.intro);
            assertTrue(chordSection.getSectionVersion().equals(intro));
            measureSequenceItems = chordSection.getMeasureSequenceItems();
            assertTrue(measureSequenceItems != null);
            assertEquals(1, measureSequenceItems.size());
            measures = measureSequenceItems.get(0).getMeasures();
            assertTrue(measures != null);
            assertEquals(8, measures.size());

            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measures, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measures, 1, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measures, 2, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measures, 3, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.Ab, measures, 4, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.Ab, measures, 5, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.Ab, measures, 6, 0);

            Measure measure = measures.get(7);
            assertEquals(0, measure.getChords().size());
            assertEquals(4, measure.getBeatCount());
        }
        {
            ChordSection chordSection = ChordSection.testParse("I: A B C D\n" +
                    "AbBb/G# Am7 Ebsus4 C7/Bb x4", 4);
            assertTrue(chordSection != null);
            SectionVersion intro = new SectionVersion(Section.intro);
            assertTrue(chordSection.getSectionVersion().equals(intro));
            measureSequenceItems = chordSection.getMeasureSequenceItems();
            assertTrue(measureSequenceItems != null);
            assertEquals(2, measureSequenceItems.size());
            measures = measureSequenceItems.get(0).getMeasures();
            assertTrue(measures != null);
            assertEquals(4, measures.size());

            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measures, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.B, measures, 1, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.C, measures, 2, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.D, measures, 3, 0);

            measureSequenceItem = chordSection.getMeasureSequenceItems().get(1);//    the repeat
            assertEquals(16, measureSequenceItem.getTotalMoments());
            measures = measureSequenceItem.getMeasures();
            assertEquals(4, measures.size());

            assertEquals(4, measures.size());
            assertEquals(2, measures.get(0).getChords().size());
            assertEquals(ScaleNote.Ab, measures.get(0).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.Bb, measures.get(0).getChords().get(1).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.A, measures.get(1).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.Eb, measures.get(2).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.C, measures.get(3).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(4 + 4 * 4, chordSection.getTotalMoments());
        }
        {
            ChordSection chordSection = ChordSection.testParse("V:\n" +
                    "            Am Bm7 Em Dsus2 x4\n" +
                    "T:\n" +                //  note: tag should be ignored on a single chord section parse
                    "D C AG D\n", 4);
            assertTrue(chordSection != null);
            SectionVersion verse = new SectionVersion(Section.verse);
            assertTrue(chordSection.getSectionVersion().equals(verse));
            measureSequenceItems = chordSection.getMeasureSequenceItems();
            measureSequenceItem = measureSequenceItems.get(0);
            measures = measureSequenceItem.getMeasures();
            assertTrue(measures != null);
            assertEquals(4, measures.size());

            assertEquals(4 * 4, measureSequenceItem.getTotalMoments());
            measures = measureSequenceItems.get(0).getMeasures();
            assertEquals(4, measures.size());
            assertEquals(ScaleNote.A, measures.get(0).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.B, measures.get(1).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.E, measures.get(2).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.D, measures.get(3).getChords().get(0).getScaleChord().getScaleNote());
        }
        {
            StringBuffer sb = new StringBuffer("\nT:D\n");
            ChordSection chordSection = ChordSection.parse(sb, 4);
            assertTrue(sb.length() == 0);
        }
        {
            StringBuffer sb = new StringBuffer("\nT:\n" +
                    "D C AG D\n");
            ChordSection chordSection = ChordSection.parse(sb, 4);
            assertTrue(sb.length() == 0);
            assertTrue(chordSection != null);
            SectionVersion sectionVersion = new SectionVersion(Section.tag);
            assertTrue(chordSection.getSectionVersion().equals(sectionVersion));
            measureSequenceItems = chordSection.getMeasureSequenceItems();
            assertTrue(measureSequenceItems != null);
            assertEquals(1, measureSequenceItems.size());
            measureSequenceItem = measureSequenceItems.get(0);
            assertEquals(4, measureSequenceItem.getTotalMoments());
            measures = measureSequenceItem.getMeasures();
            assertTrue(measures != null);
            assertEquals(4, measures.size());
            MeasureNode measureNode = measures.get(0);


            checkMeasureNodesScaleNoteByMeasure(ScaleNote.D, measures, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.C, measures, 1, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measures, 2, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.D, measures, 3, 0);
        }
        {
            ChordSection chordSection = ChordSection.testParse("I:       A B C D\n\n", 4);
            assertEquals(4, chordSection.getTotalMoments());
            chordSection = ChordSection.testParse("\n\tI:\n       A B C D\n\n", 4);
            assertEquals(4, chordSection.getTotalMoments());
            chordSection = ChordSection.testParse("v: A B C D\n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb\n", 4);
            assertEquals(8, chordSection.getTotalMoments());
            chordSection = ChordSection.testParse("v: A B C D\n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb x4\n", 4);
            assertEquals(4 + 4 * 4, chordSection.getTotalMoments());
            chordSection = ChordSection.testParse("v: \n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb x4\n", 4);
            assertEquals(4 * 4, chordSection.getTotalMoments());
            chordSection = ChordSection.testParse("v: A B C D\n\n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb x4\n", 4);
            assertEquals(4 + 4 * 4, chordSection.getTotalMoments());
            chordSection = ChordSection.testParse("v: A B C D\n\n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb x4\n" +
                    "G F F# E", 4);
            assertEquals(4 + 4 * 4 + 4, chordSection.getTotalMoments());
        }
    }

    private void checkMeasureNodesScaleNoteByMeasure(ScaleNote scaleNote, ArrayList<Measure> measures,
                                                     int measureN, int chordN) {
        assertEquals(scaleNote, measures.get(measureN).getChords().get(chordN).getScaleChord().getScaleNote());
    }

    private void checkMeasureNodesSlashScaleNoteByMeasure(ScaleNote scaleNote, ArrayList<Measure> measures,
                                                          int measureN, int chordN) {
        assertEquals(scaleNote, measures.get(measureN).getChords().get(chordN).getSlashScaleChord().getScaleNote());
    }

}