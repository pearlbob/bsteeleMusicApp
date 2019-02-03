package com.bsteele.bsteeleMusicApp.shared;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class UtilTest {

    @Test
    public void stripLeadingWhitespace() {
        Util util = new Util();
        
        assertEquals("abc", util.stripLeadingWhitespace("abc"));
        assertEquals(0,util.getLeadingWhitespaceCount());
        assertEquals("abc", util.stripLeadingWhitespace(" abc"));
        assertEquals(1,util.getLeadingWhitespaceCount());
        assertEquals("a  ", util.stripLeadingWhitespace("  a  "));
        assertEquals(2,util.getLeadingWhitespaceCount());
        assertEquals("abc", util.stripLeadingWhitespace(" \tabc"));
        assertEquals(2,util.getLeadingWhitespaceCount());
        assertEquals("abc", util.stripLeadingWhitespace("\nabc"));
        assertEquals(1,util.getLeadingWhitespaceCount());
        assertEquals(null, util.stripLeadingWhitespace("   "));
        assertEquals(3,util.getLeadingWhitespaceCount());
        assertEquals(null, util.stripLeadingWhitespace(""));
        assertEquals(0,util.getLeadingWhitespaceCount());
        assertEquals(0,util.getLeadingWhitespaceCount());
    }
}