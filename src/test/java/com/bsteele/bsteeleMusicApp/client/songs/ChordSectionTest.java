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
            assertEquals(1, measureNodes.size());
            ArrayList<Measure> measures = measureNodes.get(0).getMeasures();
            assertEquals(8, measures.size());
            assertEquals(ScaleNote.A, measures.get(0).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.B, measures.get(1).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.C, measures.get(2).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.D, measures.get(3).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.Ab, measures.get(4).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.Bb, measures.get(4).getChords().get(1).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.Gs, measures.get(4).getChords().get(0).getSlashScaleChord().getScaleNote());
            assertEquals(ScaleNote.Gs, measures.get(4).getChords().get(1).getSlashScaleChord().getScaleNote());
            assertEquals(ScaleNote.A, measures.get(5).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.Eb, measures.get(6).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.C, measures.get(7).getChords().get(0).getScaleChord().getScaleNote());
        }
        {
            ChordSection chordSection = ChordSection.parse("I: A - - -\n" +
                    "Ab - - G ", 4);
            assertTrue(chordSection != null);
            SectionVersion intro = new SectionVersion(Section.intro);
            assertTrue(chordSection.getSectionVersion().equals(intro));
            ArrayList<MeasureNode> measureNodes = chordSection.getMeasureNodes();
            assertTrue(measureNodes != null);
            assertEquals(1, measureNodes.size());
            ArrayList<Measure> measures = measureNodes.get(0).getMeasures();
            assertEquals(8, measures.size());
            assertEquals(ScaleNote.A, measures.get(0).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.A, measures.get(1).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.A, measures.get(2).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.A, measures.get(3).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.Ab, measures.get(4).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.Ab, measures.get(5).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.Ab, measures.get(6).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.G, measures.get(7).getChords().get(0).getScaleChord().getScaleNote());
        }
        {
            ChordSection chordSection = ChordSection.parse("I: A - - -\n" +
                    "Ab - - X ", 4);
            assertTrue(chordSection != null);
            SectionVersion intro = new SectionVersion(Section.intro);
            assertTrue(chordSection.getSectionVersion().equals(intro));
            ArrayList<MeasureNode> measureNodes = chordSection.getMeasureNodes();
            assertTrue(measureNodes != null);
            assertEquals(1, measureNodes.size());
            ArrayList<Measure> measures = measureNodes.get(0).getMeasures();
            assertEquals(8, measures.size());
            assertEquals(ScaleNote.A, measures.get(0).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.A, measures.get(1).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.A, measures.get(2).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.A, measures.get(3).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.Ab, measures.get(4).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.Ab, measures.get(5).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.Ab, measures.get(6).getChords().get(0).getScaleChord().getScaleNote());
            Measure measure = measures.get(7);
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
            assertEquals(2, measureNodes.size());
            MeasureNode measureNode = measureNodes.get(0);
            assertEquals(4, measureNode.getTotalMeasures());
            ArrayList<Measure> measures = measureNode.getMeasures();
            assertEquals(4, measures.size());
            assertEquals(ScaleNote.A, measures.get(0).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.B, measures.get(1).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.C, measures.get(2).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.D, measures.get(3).getChords().get(0).getScaleChord().getScaleNote());
            measureNode = measureNodes.get(1);
            assertEquals(4 * 4, measureNode.getTotalMeasures());
            measures = measureNode.getMeasures();
            assertEquals(4, measures.size());
            assertEquals(2, measures.get(0).getChords().size());
            assertEquals(ScaleNote.Ab, measures.get(0).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.Bb, measures.get(0).getChords().get(1).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.A, measures.get(1).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.Eb, measures.get(2).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.C, measures.get(3).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(4 + 4 * 4, chordSection.getTotalMeasures());
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
            assertEquals(4 * 4, measureNode.getTotalMeasures());
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
            assertEquals("\n",s);
        }
        {
            String s = "\nT:\n" +
                    "D C AG D\n";
            ChordSection chordSection = ChordSection.parse(s, 4);
            s = s.substring(chordSection.getParseLength());
            assertEquals("\n",s);
            assertTrue(chordSection != null);
            SectionVersion sectionVersion = new SectionVersion(Section.tag);
            assertTrue(chordSection.getSectionVersion().equals(sectionVersion));
            ArrayList<MeasureNode> measureNodes = chordSection.getMeasureNodes();
            assertTrue(measureNodes != null);
            assertEquals(1, measureNodes.size());
            MeasureNode measureNode = measureNodes.get(0);
            assertEquals(4, measureNode.getTotalMeasures());
            ArrayList<Measure> measures = measureNode.getMeasures();
            assertEquals(4, measures.size());
            assertEquals(ScaleNote.D, measures.get(0).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.C, measures.get(1).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.A, measures.get(2).getChords().get(0).getScaleChord().getScaleNote());
            assertEquals(ScaleNote.D, measures.get(3).getChords().get(0).getScaleChord().getScaleNote());
        }
        {
            ChordSection chordSection = ChordSection.parse("I:       A B C D\n\n", 4);
            assertEquals(4, chordSection.getTotalMeasures());
            chordSection = ChordSection.parse("\n\tI:\n       A B C D\n\n", 4);
            assertEquals(4, chordSection.getTotalMeasures());
            chordSection = ChordSection.parse("v: A B C D\n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb\n", 4);
            assertEquals(8, chordSection.getTotalMeasures());
            chordSection = ChordSection.parse("v: A B C D\n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb x4\n", 4);
            assertEquals(4 + 4 * 4, chordSection.getTotalMeasures());
            chordSection = ChordSection.parse("v: \n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb x4\n", 4);
            assertEquals(4 * 4, chordSection.getTotalMeasures());
            chordSection = ChordSection.parse("v: A B C D\n\n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb x4\n", 4);
            assertEquals(4 + 4 * 4, chordSection.getTotalMeasures());
            chordSection = ChordSection.parse("v: A B C D\n\n"
                    + "AbBb/G# Am7 Ebsus4 C7/Bb x4\n" +
                    "G F F# E", 4);
            assertEquals(4 + 4 * 4 + 4, chordSection.getTotalMeasures());
        }
    }

}