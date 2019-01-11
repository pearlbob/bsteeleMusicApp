package com.bsteele.bsteeleMusicApp.client.songs;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureCommentTest extends TestCase {

    @Test
    public void testGetTotalMeasures() {
    }

    @Test
    public void testGetMeasures() {
    }

    @Test
    public void testParse() {

        String s = "( this is a comment )";
        MeasureComment measureComment = MeasureComment.parse( s);
         assertEquals(s.length(), measureComment.getParseLength());
        assertEquals(" this is a comment ", measureComment.getComment());

        s = "this is also a comment )";
        measureComment = MeasureComment.parse( s);
          assertEquals(s.length(), measureComment.getParseLength());
        assertEquals(s, measureComment.getComment());

        s = "ABC\nDEF";                  //  not all a comment
        measureComment = MeasureComment.parse( s);
        assertEquals(4, measureComment.getParseLength());
        assertEquals(s.substring(0, 3), measureComment.getComment());

        s = "";                  //  not a comment
        measureComment = MeasureComment.parse( s);
        assertEquals(null, measureComment);

        s = null;                  //  not a comment
        measureComment = MeasureComment.parse( s);
        assertEquals(null, measureComment);
    }
}