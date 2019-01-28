package com.bsteele.bsteeleMusicApp.client.songs;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureCommentTest extends TestCase {

    @Test
    public void testParse() {
        String s;
        MeasureComment measureComment;

        s = "(123)";
        measureComment = MeasureComment.parse(s);
        assertEquals("123", measureComment.getComment());
        s = "   (   abc 123   )   ";
        measureComment = MeasureComment.parse(s);
        assertEquals("abc 123", measureComment.getComment());

        s = " ";                  //  not a comment
        measureComment = MeasureComment.parse(s);
        assertEquals(null, measureComment);
        s = "\n";                  //  not a comment
        measureComment = MeasureComment.parse(s);
        assertEquals(null, measureComment);
        s = " \t";                  //  not a comment
        measureComment = MeasureComment.parse(s);
        assertEquals(null, measureComment);
        s = "\t";                  //  not a comment
        measureComment = MeasureComment.parse(s);
        assertEquals(null, measureComment);
        s = "\t\t  \t\t   ";                  //  not a comment
        measureComment = MeasureComment.parse(s);
        assertEquals(null, measureComment);

        s = "( this is a comment )";
        measureComment = MeasureComment.parse(s);
        assertEquals(s.length(), measureComment.getParseLength());
        assertEquals("this is a comment", measureComment.getComment());

        s = "this is also a comment )";
        measureComment = MeasureComment.parse(s);
        assertEquals(s.length(), measureComment.getParseLength());
        assertEquals(s, measureComment.getComment());

        s = "( this is also a bad comment";
        measureComment = MeasureComment.parse(s);
        assertEquals(s.length(), measureComment.getParseLength());
        assertEquals(s, measureComment.getComment());

        s = "this is also has to be a comment";
        measureComment = MeasureComment.parse(s);
        assertEquals(s.length(), measureComment.getParseLength());
        assertEquals(s, measureComment.getComment());

        s = "ABC\nDEF";                  //  not all a comment
        measureComment = MeasureComment.parse(s);
        assertEquals(3, measureComment.getParseLength());
        assertEquals(s.substring(0, 3), measureComment.getComment());

        s = "";                  //  not a comment
        measureComment = MeasureComment.parse(s);
        assertEquals(null, measureComment);

        s = null;                  //  not a comment
        measureComment = MeasureComment.parse(s);
        assertEquals(null, measureComment);
    }
}