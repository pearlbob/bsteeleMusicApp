package com.bsteele.bsteeleMusicApp.client.songs;

import org.junit.Test;

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
                    // System.out.println(s);
                    assertEquals(sn, sc.getScaleNote());
                    assertEquals(cd, sc.getChordDescriptor());
                    assertEquals(ct, sc.getChordTension());
                }
            }
        }
    }
}