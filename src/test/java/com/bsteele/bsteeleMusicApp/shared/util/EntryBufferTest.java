package com.bsteele.bsteeleMusicApp.shared.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class EntryBufferTest {

    @Test
    public void reset() {
        EntryBuffer eb = new EntryBuffer("1234");
        assertEquals(4, eb.length());
        eb.getNextChar();
        assertEquals(3, eb.available());
        eb.flush();
        assertEquals(3, eb.length());
        assertEquals('2', eb.getNextChar());
        assertEquals('3', eb.getNextChar());
        assertEquals('4', eb.getNextChar());
        assertEquals(0, eb.available());
        eb.flush();
        assertEquals(0, eb.length());

        try {
            eb.getNextChar();
            fail();
        } catch (IndexOutOfBoundsException ioob) {
            //  expected
        }

    }

    @Test
    public void isEmpty() {
        EntryBuffer eb = new EntryBuffer();
        assertTrue(eb.isEmpty());
        eb.append("s");
        assertFalse(eb.isEmpty());
        eb.getNextChar();
        eb.flush();
        assertTrue(eb.isEmpty());
    }

    @Test
    public void charAt() {
        EntryBuffer eb = new EntryBuffer("1234");
        assertEquals(4, eb.length());
        assertEquals('1', eb.charAt(0));
        assertEquals('2', eb.charAt(1));
        assertEquals('4', eb.charAt(3));
        try {
            eb.charAt(4);
            fail();
        } catch (IndexOutOfBoundsException ioob) {
            //  expected
        }
        eb.getNextChar();
        eb.flush();
        assertEquals(3, eb.length());
        assertEquals('2', eb.charAt(0));
    }

    @Test
    public void append() {
        EntryBuffer eb = new EntryBuffer("12");
        assertEquals('1', eb.charAt(0));
        assertEquals('2', eb.charAt(1));
        eb.append("34");
        assertEquals('1', eb.charAt(0));
        assertEquals('2', eb.charAt(1));
        assertEquals('4', eb.charAt(3));
    }
}