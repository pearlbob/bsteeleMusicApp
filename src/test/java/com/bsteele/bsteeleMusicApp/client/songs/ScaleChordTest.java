package com.bsteele.bsteeleMusicApp.client.songs;

import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ScaleChordTest {

    @Test
    public void parseScaleChord() {

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

        for (ScaleNote sn : ScaleNote.values()) {
            String s = sn.toString();
            ScaleChord sc = ScaleChord.parse(s);
            assertEquals(sn, sc.getScaleNote());

            for (ChordDescriptor cd : ChordDescriptor.values()) {
                s = sn.toString() + cd.getShortName();
                sc = ScaleChord.parse(s);
                assertEquals(sn, sc.getScaleNote());
                assertEquals(cd, sc.getChordDescriptor());

                s = sn.toString() + cd.getShortName();
                sc = ScaleChord.parse(s);

                ScaleChord builtScaleChord = new ScaleChord(sn, cd);

                assertEquals(sc, builtScaleChord);
                assertEquals(sn, sc.getScaleNote());
                assertEquals(cd, sc.getChordDescriptor());

                ArrayList<ScaleChord> scaleChords = new ArrayList<>();
                scaleChords.add(builtScaleChord);
                Key key = Key.guessKey(scaleChords);

                System.out.println("<tr><td>" + builtScaleChord.toString() + "</td><td>"
                        + chordComponentsToString(builtScaleChord.getChordComponents())
                        + "</td><td>"
                        +key.getKeyScaleNote().toString()
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
}