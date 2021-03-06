package com.bsteele.bsteeleMusicApp.shared.songs;

import junit.framework.TestCase;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ScaleChordTest extends TestCase {

    @Test
    public void testParse() {

        System.out.println("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>title</title>\n" +
                "    <link rel=\"stylesheet\" href=\"style.css\">\n" +
                "    <script src=\"script.js\"></script>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <table border=\"1\" >\n");
        try {
            for (ScaleNote sn : ScaleNote.values()) {
                if ( sn == ScaleNote.X)
                    continue;
                String s = sn.toString();
                ScaleChord sc = null;

                sc = ScaleChord.parse(s);

                assertEquals(sn, sc.getScaleNote());

                s = sn.toString() + "º";
                sc = ScaleChord.parse(s);
                assertEquals(sn, sc.getScaleNote());
                assertEquals(ChordDescriptor.diminished, sc.getChordDescriptor());

                s = sn.toString() + "º7";
                sc = ScaleChord.parse(s);
                assertEquals(sn, sc.getScaleNote());
                assertEquals(ChordDescriptor.diminished7, sc.getChordDescriptor());

               for (ChordDescriptor cd : ChordDescriptor.values()) {
                    s = sn.toString() + cd.getShortName();
                    sc = ScaleChord.parse(s);
                    assertEquals(sn, sc.getScaleNote());
                    ChordDescriptor chordDescriptor = sc.getChordDescriptor();
                    assertEquals(cd.deAlias(), chordDescriptor);

                    s = sn.toString() + cd.getShortName();
                    sc = ScaleChord.parse(s);

                    ScaleChord builtScaleChord = new ScaleChord(sn, cd);

                    assertEquals(sc, builtScaleChord);
                    assertEquals(sn, sc.getScaleNote());
                    assertEquals(cd.deAlias(), sc.getChordDescriptor());

                    ArrayList<ScaleChord> scaleChords = new ArrayList<>();
                    scaleChords.add(builtScaleChord);
                    Key key = Key.guessKey(scaleChords);

                    System.out.println("<tr><td>" + builtScaleChord.toString() + "</td><td>"
                            + chordComponentsToString(builtScaleChord.getChordComponents())
                            + "</td><td>"
                            + key.getKeyScaleNote().toString()
                            + "</td><td>"
                            + chordComponentScaleNotesToString(key, builtScaleChord)
                            + "</td></tr>"
                    );

                }
            }
            System.out.println(
                    "  </table>\n" +
                            "  </body>\n" +
                            "</html>");

        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    private String chordComponentsToString(TreeSet<ChordComponent> chordComponents) {
        StringBuilder sb = new StringBuilder();
        for (ChordComponent chordComponent : chordComponents) {
            if (!chordComponent.equals(chordComponents.first()))
                sb.append(" ");
            sb.append(chordComponent.getShortName());
        }
        return sb.toString();
    }

    private String chordComponentScaleNotesToString(Key key, ScaleChord scaleChord) {
        StringBuilder sb = new StringBuilder();
        TreeSet<ChordComponent> chordComponents = scaleChord.getChordComponents();
        for (ChordComponent chordComponent : chordComponents) {
            if (!chordComponent.equals(chordComponents.first()))
                sb.append(" ");
            sb.append(key.getScaleNoteByHalfStep(key.getHalfStep() + chordComponent.getHalfSteps()));
        }
        return sb.toString();
    }

    @Test
    public void testScaleChordParse() {
        ScaleChord scaleChord;
        try {
            assertEquals(new ScaleChord(ScaleNote.A, ChordDescriptor.dominant13), ScaleChord.parse("A13"));
            assertEquals(new ScaleChord(ScaleNote.F, ChordDescriptor.major), ScaleChord.parse("F"));
            assertEquals(new ScaleChord(ScaleNote.F, ChordDescriptor.major), ScaleChord.parse("FGm"));
            assertEquals(new ScaleChord(ScaleNote.F, ChordDescriptor.minor), ScaleChord.parse("Fm"));
            assertEquals(new ScaleChord(ScaleNote.Fs, ChordDescriptor.minor), ScaleChord.parse("F#m"));
            assertEquals(new ScaleChord(ScaleNote.Fs, ChordDescriptor.minor), ScaleChord.parse("F#mGm"));
            assertEquals(new ScaleChord(ScaleNote.D, ChordDescriptor.diminished), ScaleChord.parse("Ddim/G"));
            assertEquals(new ScaleChord(ScaleNote.A, ChordDescriptor.diminished), ScaleChord.parse("Adim/G"));
            assertEquals(new ScaleChord(ScaleNote.X, ChordDescriptor.major), ScaleChord.parse("X/G"));
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }
}