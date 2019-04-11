package com.bsteele.bsteeleMusicApp.shared.util;

import org.junit.Test;

import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MarkedStringTest {

    @Test
    public void reset() {
        MarkedString ms = new MarkedString("1234");
        assertEquals(4, ms.available());
        ms.getNextChar();
        ms.mark();
        assertEquals(3, ms.available());
        assertEquals('2', ms.charAt(0));
        assertEquals('3', ms.charAt(1));
        assertEquals('4', ms.charAt(2));

        try {
            ms.charAt(3);
            fail();
        } catch (IndexOutOfBoundsException ioob) {
            //  expected
        }

        assertEquals('2', ms.getNextChar());
        assertEquals('3', ms.getNextChar());
        assertEquals('4', ms.getNextChar());
        assertEquals(0, ms.available());

        try {
            ms.getNextChar();
            fail();
        } catch (IndexOutOfBoundsException ioob) {
            //  expected
        }

        ms.resetToMark();
        assertEquals(3, ms.available());
        assertEquals('2', ms.getNextChar());
        assertEquals('3', ms.getNextChar());
        assertEquals('4', ms.getNextChar());
        assertEquals(0, ms.available());

        try {
            ms.getNextChar();
            fail();
        } catch (IndexOutOfBoundsException ioob) {
            //  expected
        }

        ms.resetTo(2);
        assertEquals(2, ms.available());
        assertEquals('3', ms.getNextChar());
        assertEquals('4', ms.getNextChar());
        assertEquals(0, ms.available());

        try {
            ms.getNextChar();
            fail();
        } catch (IndexOutOfBoundsException ioob) {
            //  expected
        }

    }

    @Test
    public void isEmpty() {
        MarkedString eb = new MarkedString("s");
        assertFalse(eb.isEmpty());
        eb.getNextChar();
        assertTrue(eb.isEmpty());
    }

    @Test
    public void charAt() {
        MarkedString markedString = new MarkedString("1234");
        assertEquals(4, markedString.available());
        assertEquals('1', markedString.charAt(0));
        assertEquals('2', markedString.charAt(1));
        assertEquals('4', markedString.charAt(3));
        try {
            markedString.charAt(4);
            fail();
        } catch (IndexOutOfBoundsException ioob) {
            //  expected
        }
        markedString.getNextChar();
        assertEquals(3, markedString.available());
        assertEquals('2', markedString.charAt(0));
    }

    @Test
    public void remainingStringLimited() {
        String s = "1234";
        MarkedString markedString = new MarkedString(s);

        String actual = markedString.remainingStringLimited(25);
        logger.fine( actual );
        assertEquals(s,actual);
        assertEquals(s.substring(0,2), markedString.remainingStringLimited(2));
        assertEquals(s.substring(0,4), markedString.remainingStringLimited(4));
        assertEquals("", markedString.remainingStringLimited(0));
    }

    private static Logger logger = Logger.getLogger(MarkedStringTest.class.getName());
}