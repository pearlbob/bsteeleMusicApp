package com.bsteele.bsteeleMusicApp.client.songs;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ChordDescriptorTest {

    @Test
    public void parse() {
        assertEquals(ChordDescriptor.major, ChordDescriptor.parse(""));
        assertEquals(ChordDescriptor.minor, ChordDescriptor.parse("m"));
        assertEquals(ChordDescriptor.dominant7, ChordDescriptor.parse("7"));
        assertEquals(ChordDescriptor.major7, ChordDescriptor.parse("maj7"));
        assertEquals(ChordDescriptor.minor7, ChordDescriptor.parse("m7"));
        assertEquals(ChordDescriptor.augmented5, ChordDescriptor.parse("aug5"));
        assertEquals(ChordDescriptor.diminished, ChordDescriptor.parse("dim"));
        assertEquals(ChordDescriptor.suspended4, ChordDescriptor.parse("sus4"));
        assertEquals(ChordDescriptor.power5, ChordDescriptor.parse("5"));
    }

    @Test
    public void chordComponentsToString() {
        if (false)
            for (ChordDescriptor cd : ChordDescriptor.values()) {
                System.out.println(cd.toString() + ":\t" + cd.chordComponentsToString());
            }
    }
}