package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.songs.ChordDescriptor;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ChordDescriptorTest extends TestCase
{

    @Test
    public void testParse() {
        assertEquals(ChordDescriptor.major, ChordDescriptor.testParse(""));
        assertEquals(ChordDescriptor.minor, ChordDescriptor.testParse("m"));
        assertEquals(ChordDescriptor.dominant7, ChordDescriptor.testParse("7"));
        assertEquals(ChordDescriptor.major7, ChordDescriptor.testParse("maj7"));
        assertEquals(ChordDescriptor.minor7, ChordDescriptor.testParse("m7"));
        assertEquals(ChordDescriptor.augmented5, ChordDescriptor.testParse("aug5"));
        assertEquals(ChordDescriptor.diminished, ChordDescriptor.testParse("dim"));
        assertEquals(ChordDescriptor.suspended4, ChordDescriptor.testParse("sus4"));
        assertEquals(ChordDescriptor.power5, ChordDescriptor.testParse("5"));
        assertEquals(ChordDescriptor.dominant9, ChordDescriptor.testParse("9"));
        assertEquals(ChordDescriptor.dominant13, ChordDescriptor.testParse("13"));
        assertEquals(ChordDescriptor.dominant11, ChordDescriptor.testParse("11"));
        assertEquals(ChordDescriptor.minor7b5, ChordDescriptor.testParse("m7b5"));
        assertEquals(ChordDescriptor.add9, ChordDescriptor.testParse("add9"));
        assertEquals(ChordDescriptor.jazz7b9, ChordDescriptor.testParse("jazz7b9"));
        assertEquals(ChordDescriptor.sevenSharp5, ChordDescriptor.testParse("7#5"));
        assertEquals(ChordDescriptor.sevenFlat5, ChordDescriptor.testParse("7b5"));
        assertEquals(ChordDescriptor.sevenSharp9, ChordDescriptor.testParse("7#9"));
        assertEquals(ChordDescriptor.sevenFlat9, ChordDescriptor.testParse("7b9"));
        assertEquals(ChordDescriptor.major6, ChordDescriptor.testParse("6"));
        assertEquals(ChordDescriptor.six9, ChordDescriptor.testParse("69"));
        assertEquals(ChordDescriptor.power5, ChordDescriptor.testParse("5"));
        assertEquals(ChordDescriptor.diminished7, ChordDescriptor.testParse("dim7"));
        assertEquals(ChordDescriptor.augmented, ChordDescriptor.testParse("aug"));
        assertEquals(ChordDescriptor.augmented5, ChordDescriptor.testParse("aug5"));
        assertEquals(ChordDescriptor.augmented7, ChordDescriptor.testParse("aug7"));
        assertEquals(ChordDescriptor.suspended7, ChordDescriptor.testParse("sus7"));
        assertEquals(ChordDescriptor.suspended2, ChordDescriptor.testParse("sus2"));
        assertEquals(ChordDescriptor.suspended, ChordDescriptor.testParse("sus"));
        assertEquals(ChordDescriptor.minor11, ChordDescriptor.testParse("m11"));
        assertEquals(ChordDescriptor.minor13, ChordDescriptor.testParse("m13"));
    }

    @Test
    public void testOrderByShortname() {
        if (false) {
            for (ChordDescriptor cd : ChordDescriptor.getPrimaryChordDescriptorsOrdered()) {
                System.out.println(cd.toString() + ":\t" + cd.chordComponentsToString());
            }
            System.out.println();
            for (ChordDescriptor cd : ChordDescriptor.getOtherChordDescriptorsOrdered()) {
                System.out.println(cd.toString() + ":\t" + cd.chordComponentsToString());
            }
            System.out.println();
            for (ChordDescriptor cd : ChordDescriptor.getAllChordDescriptorsOrdered()) {
                System.out.println("<g:Button ui:field=\"" + cd.name() + "\">" + cd.toString() + "</g:Button>");
            }
            System.out.println();
            for (ChordDescriptor cd : ChordDescriptor.getAllChordDescriptorsOrdered()) {
                System.out.println("chordDescriptorMap.put(ChordDescriptor." + cd.name() + "," + cd.name() + ");");
            }
        }
    }

    @Test
    public void testChordComponentsToString() {
        if (false)
            for (ChordDescriptor cd : ChordDescriptor.values()) {
                System.out.println(cd.toString() + ":\t" + cd.chordComponentsToString());
            }
    }
}
