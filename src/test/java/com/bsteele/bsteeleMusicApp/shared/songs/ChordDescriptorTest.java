package com.bsteele.bsteeleMusicApp.shared.songs;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ChordDescriptorTest extends TestCase
{

    @Test
    public void testParse() {
        assertEquals(ChordDescriptor.major7, ChordDescriptor.parse(""+MusicConstant.greekCapitalDelta));
        assertEquals(ChordDescriptor.diminished, ChordDescriptor.parse(""+MusicConstant.diminishedCircle));
        assertEquals(ChordDescriptor.major, ChordDescriptor.parse(""));
        assertEquals(ChordDescriptor.minor, ChordDescriptor.parse("m"));
        assertEquals(ChordDescriptor.dominant7, ChordDescriptor.parse("7"));
        assertEquals(ChordDescriptor.major7, ChordDescriptor.parse("maj7"));
        assertEquals(ChordDescriptor.minor7, ChordDescriptor.parse("m7"));
        assertEquals(ChordDescriptor.augmented5, ChordDescriptor.parse("aug5"));
        assertEquals(ChordDescriptor.diminished, ChordDescriptor.parse("dim"));
        assertEquals(ChordDescriptor.suspended4, ChordDescriptor.parse("sus4"));
        assertEquals(ChordDescriptor.power5, ChordDescriptor.parse("5"));
        assertEquals(ChordDescriptor.dominant9, ChordDescriptor.parse("9"));
        assertEquals(ChordDescriptor.dominant13, ChordDescriptor.parse("13"));
        assertEquals(ChordDescriptor.dominant11, ChordDescriptor.parse("11"));
        assertEquals(ChordDescriptor.minor7b5, ChordDescriptor.parse("m7b5"));
        assertEquals(ChordDescriptor.add9, ChordDescriptor.parse("add9"));
        assertEquals(ChordDescriptor.jazz7b9, ChordDescriptor.parse("jazz7b9"));
        assertEquals(ChordDescriptor.sevenSharp5, ChordDescriptor.parse("7#5"));
        assertEquals(ChordDescriptor.sevenFlat5, ChordDescriptor.parse("7b5"));
        assertEquals(ChordDescriptor.sevenSharp9, ChordDescriptor.parse("7#9"));
        assertEquals(ChordDescriptor.sevenFlat9, ChordDescriptor.parse("7b9"));
        assertEquals(ChordDescriptor.major6, ChordDescriptor.parse("6"));
        assertEquals(ChordDescriptor.six9, ChordDescriptor.parse("69"));
        assertEquals(ChordDescriptor.power5, ChordDescriptor.parse("5"));
        assertEquals(ChordDescriptor.diminished7, ChordDescriptor.parse("dim7"));
        assertEquals(ChordDescriptor.augmented, ChordDescriptor.parse("aug"));
        assertEquals(ChordDescriptor.augmented5, ChordDescriptor.parse("aug5"));
        assertEquals(ChordDescriptor.augmented7, ChordDescriptor.parse("aug7"));
        assertEquals(ChordDescriptor.suspended7, ChordDescriptor.parse("sus7"));
        assertEquals(ChordDescriptor.suspended2, ChordDescriptor.parse("sus2"));
        assertEquals(ChordDescriptor.suspended, ChordDescriptor.parse("sus"));
        assertEquals(ChordDescriptor.minor11, ChordDescriptor.parse("m11"));
        assertEquals(ChordDescriptor.minor13, ChordDescriptor.parse("m13"));
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
                logger.info(cd.toString() + ":\t" + cd.chordComponentsToString());
            }
    }

    @Test
    public void testChordDescriptorCompareTo() {

        int expected = -1;
        ChordDescriptor cd1 = ChordDescriptor.dominant7;
            for (ChordDescriptor cd2 : ChordDescriptor.values()) {
                int compareValue = cd2.compareTo(cd1);
                compareValue = (compareValue < 0 ? -1 : (compareValue > 0 ? 1 : 0));

                logger.fine(cd2.toString() + ":\t" + compareValue);
                if ( cd1 == cd2 )
                {
                    assertEquals(0,compareValue);
                    expected = 1;
                }
                else
                    assertTrue( expected == compareValue);
            }
    }

    private static Logger logger = Logger.getLogger(MeasureRepeatTest.class.getName());
}
