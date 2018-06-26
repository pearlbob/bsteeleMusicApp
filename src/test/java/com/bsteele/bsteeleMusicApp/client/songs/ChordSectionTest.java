package com.bsteele.bsteeleMusicApp.client.songs;

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
        {
            ChordSection chordSection = ChordSection.parse("I: A B C D\n" +
                    "AbBb/G# Am7 Ebsus4 C7/Bb", 4);
            assertTrue(chordSection != null);
            SectionVersion intro = new SectionVersion(Section.intro);
            assertTrue(chordSection.getSectionVersion().equals(intro));
            ArrayList<MeasureNode> measureNodes = chordSection.getMeasureNodes();
            assertTrue(measureNodes != null);
            assertEquals(8, measureNodes.size());

            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measureNodes, 0, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.B, measureNodes, 1, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.C, measureNodes, 2, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.D, measureNodes, 3, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.Ab, measureNodes, 4, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.Bb, measureNodes, 4, 0, 1);
            checkMeasureNodesSlashScaleNoteByMeasure(ScaleNote.Gs, measureNodes, 4, 0, 0);
            checkMeasureNodesSlashScaleNoteByMeasure(ScaleNote.Gs, measureNodes, 4, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measureNodes, 5, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.Eb, measureNodes, 6, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.C, measureNodes, 7, 0, 0);
        }
        {
            ChordSection chordSection = ChordSection.parse("I: A - - -\n" +
                    "Ab - - G ", 4);
            assertTrue(chordSection != null);
            SectionVersion intro = new SectionVersion(Section.intro);
            assertTrue(chordSection.getSectionVersion().equals(intro));
            ArrayList<MeasureNode> measureNodes = chordSection.getMeasureNodes();
            assertTrue(measureNodes != null);
            assertEquals(8, measureNodes.size());
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measureNodes, 0, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measureNodes, 1, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measureNodes, 2, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measureNodes, 3, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.Ab, measureNodes, 4, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.Ab, measureNodes, 5, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.Ab, measureNodes, 6, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.G, measureNodes, 7, 0, 0);
        }
        {
            ChordSection chordSection = ChordSection.parse("I: A - - -\n" +
                    "Ab - - X ", 4);
            assertTrue(chordSection != null);
            SectionVersion intro = new SectionVersion(Section.intro);
            assertTrue(chordSection.getSectionVersion().equals(intro));
            ArrayList<MeasureNode> measureNodes = chordSection.getMeasureNodes();
            assertTrue(measureNodes != null);
            assertEquals(8, measureNodes.size());
            ArrayList<Measure> measures = measureNodes.get(0).getMeasures();
            assertEquals(1, measures.size());

            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measureNodes, 0, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measureNodes, 1, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measureNodes, 2, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measureNodes, 3, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.Ab, measureNodes, 4, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.Ab, measureNodes, 5, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.Ab, measureNodes, 6, 0, 0);

            measures = measureNodes.get(7).getMeasures();
            Measure measure = measures.get(0);
            assertEquals(0, measure.getChords().size());
            assertEquals(4, measure.getBeatCount());
        }
        {
            ChordSection chordSection = ChordSection.parse("I: A B C D\n" +
                    "AbBb/G# Am7 Ebsus4 C7/Bb x4", 4);
            assertTrue(chordSection != null);
            SectionVersion intro = new SectionVersion(Section.intro);
            assertTrue(chordSection.getSectionVersion().equals(intro));
            ArrayList<MeasureNode> measureNodes = chordSection.getMeasureNodes();
            assertTrue(measureNodes != null);
            assertEquals(5, measureNodes.size());

            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measureNodes, 0, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.B, measureNodes, 1, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.C, measureNodes, 2, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.D, measureNodes, 3, 0, 0);

            MeasureNode measureNode = measureNodes.get(4);
            assertEquals(16, measureNode.getTotalMoments());
            ArrayList<Measure> measures = measureNode.getMeasures();
            assertEquals(4, measures.size());

            measures = measureNode.getMeasures();
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
            ChordSection chordSection = ChordSection.parse("V:\n" +
                    "            Am Bm7 Em Dsus2 x4\n" +
                    "T:\n" +
                    "D C AG D\n", 4);
            assertTrue(chordSection != null);
            SectionVersion verse = new SectionVersion(Section.verse);
            assertTrue(chordSection.getSectionVersion().equals(verse));
            ArrayList<MeasureNode> measureNodes = chordSection.getMeasureNodes();
            assertTrue(measureNodes != null);
            assertEquals(1, measureNodes.size());
            MeasureNode measureNode = measureNodes.get(0);
            assertEquals(4 * 4, measureNode.getTotalMoments());
            ArrayList<Measure> measures = measureNode.getMeasures();
            assertEquals(4, measures.size());
            assertEquals(ScaleNote.A, measures.get(0).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.B, measures.get(1).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.E, measures.get(2).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.D, measures.get(3).getChords().get(0).getScaleChord().getScaleNote());
        }
        {
            String s = "\nT:D\n";
            ChordSection chordSection = ChordSection.parse(s, 4);
            s = s.substring(chordSection.getParseLength());
            assertEquals("\n", s);
        }
        {
            String s = "\nT:\n" +
                    "D C AG D\n";
            ChordSection chordSection = ChordSection.parse(s, 4);
            s = s.substring(chordSection.getParseLength());
            assertEquals("\n", s);
            assertTrue(chordSection != null);
            SectionVersion sectionVersion = new SectionVersion(Section.tag);
            assertTrue(chordSection.getSectionVersion().equals(sectionVersion));
            ArrayList<MeasureNode> measureNodes = chordSection.getMeasureNodes();
            assertTrue(measureNodes != null);
            assertEquals(4, measureNodes.size());
            MeasureNode measureNode = measureNodes.get(0);
            assertEquals(1, measureNode.getTotalMoments());
            ArrayList<Measure> measures = measureNode.getMeasures();
            assertEquals(1, measures.size());
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.D, measureNodes, 0, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.C, measureNodes, 1, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.A, measureNodes, 2, 0, 0);
            checkMeasureNodesScaleNoteByMeasure(ScaleNote.D, measureNodes, 3, 0, 0);
        }
        {
            ChordSection chordSection = ChordSection.parse("I:       A B C D\n\n", 4);
            assertEquals(4, chordSection.getTotalMoments());
            chordSection = ChordSection.parse("\n\tI:\n       A B C D\n\n", 4);
            assertEquals(4, chordSection.getTotalMoments());
            chordSection = ChordSection.parse("v: A B C D\n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb\n", 4);
            assertEquals(8, chordSection.getTotalMoments());
            chordSection = ChordSection.parse("v: A B C D\n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb x4\n", 4);
            assertEquals(4 + 4 * 4, chordSection.getTotalMoments());
            chordSection = ChordSection.parse("v: \n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb x4\n", 4);
            assertEquals(4 * 4, chordSection.getTotalMoments());
            chordSection = ChordSection.parse("v: A B C D\n\n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb x4\n", 4);
            assertEquals(4 + 4 * 4, chordSection.getTotalMoments());
            chordSection = ChordSection.parse("v: A B C D\n\n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb x4\n" +
                    "G F F# E", 4);
            assertEquals(4 + 4 * 4 + 4, chordSection.getTotalMoments());
        }
    }

    private void checkMeasureNodesScaleNoteByMeasure(ScaleNote scaleNote, ArrayList<MeasureNode> measureNodes,
                                                     int measureNodeN, int measureN, int chordN) {
        ArrayList<Measure> measures = measureNodes.get(measureNodeN).getMeasures();
        assertEquals(1, measures.size());
        assertEquals(scaleNote, measures.get(measureN).getChords().get(chordN).getScaleChord().getScaleNote());
    }

    private void checkMeasureNodesSlashScaleNoteByMeasure(ScaleNote scaleNote, ArrayList<MeasureNode> measureNodes,
                                                          int measureNodeN, int measureN, int chordN) {
        ArrayList<Measure> measures = measureNodes.get(measureNodeN).getMeasures();
        assertEquals(1, measures.size());
        assertEquals(scaleNote, measures.get(measureN).getChords().get(chordN).getSlashScaleChord().getScaleNote());
    }

}