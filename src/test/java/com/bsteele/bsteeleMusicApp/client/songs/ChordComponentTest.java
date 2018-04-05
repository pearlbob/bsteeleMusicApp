package com.bsteele.bsteeleMusicApp.client.songs;

import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeSet;

import static org.junit.Assert.*;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ChordComponentTest {

    @Test
    public void parse() {
        ArrayList<ChordComponent> list = new ArrayList<>();
        list.addAll(ChordComponent.parse("r"));
        assertEquals(ChordComponent.root, list.get(0));
        list.clear();
        list.addAll(ChordComponent.parse("1"));
        assertEquals(ChordComponent.root, list.get(0));
        list.clear();
        list.addAll(ChordComponent.parse("R m2  m3 3 4 b5   5 #5 6 m7 7"));

        assertEquals(ChordComponent.root, list.get(0));
        assertEquals(ChordComponent.minorSecond, list.get(1));
        assertEquals(ChordComponent.minorThird, list.get(2));
        assertEquals(ChordComponent.third, list.get(3));
        assertEquals(ChordComponent.fourth, list.get(4));
        assertEquals(ChordComponent.flatFifth, list.get(5));
        assertEquals(ChordComponent.fifth, list.get(6));
        assertEquals(ChordComponent.sharpFifth, list.get(7));
        assertEquals(ChordComponent.sixth, list.get(8));
        assertEquals(ChordComponent.minorSeventh, list.get(9));
        assertEquals(ChordComponent.seventh, list.get(10));
        list.clear();

        TreeSet<ChordComponent> set = ChordComponent.parse("R 3 5 7");
        assertTrue(set.contains(ChordComponent.root));
        assertTrue(set.contains(ChordComponent.third));
        assertTrue(set.contains(ChordComponent.fifth));
        assertTrue(set.contains(ChordComponent.seventh));
        assertFalse(set.contains(ChordComponent.minorThird));
        assertFalse(set.contains(ChordComponent.fourth));
        assertFalse(set.contains(ChordComponent.sixth));
        assertFalse(set.contains(ChordComponent.minorSeventh));
    }
}