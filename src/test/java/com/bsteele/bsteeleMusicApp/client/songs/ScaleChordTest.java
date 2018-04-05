package com.bsteele.bsteeleMusicApp.client.songs;

import org.junit.Test;

import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ScaleChordTest {

    @Test
    public void parseScaleChord() {
        for (ScaleNote sn : ScaleNote.values()) {
            String s = sn.toString();
            ScaleChord sc = ScaleChord.parse(s);
            assertEquals(sn, sc.getScaleNote());

            for (ChordDescriptor cd : ChordDescriptor.values()) {
                s = sn.toString() + cd.getShortName();
                sc = ScaleChord.parse(s);
                assertEquals(sn, sc.getScaleNote());
                assertEquals(cd, sc.getChordDescriptor());

                for (ChordTension ct : ChordTension.values()) {
                    s = sn.toString() + cd.getShortName() + ct.getShortName();
                    sc = ScaleChord.parse(s);

                    ScaleChord builtScaleChord = new ScaleChord(sn, cd, ct);

                    assertEquals(sc, builtScaleChord);
                    assertEquals(sn, sc.getScaleNote());
                    assertEquals(cd, sc.getChordDescriptor());
                    assertEquals(ct, sc.getChordTension());

                    System.out.println(builtScaleChord.toString() + ": "
                            + chordComponentsToString(builtScaleChord.getChordComponents()));
                }
            }
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
}