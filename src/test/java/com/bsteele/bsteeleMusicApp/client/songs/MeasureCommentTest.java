package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.shared.songs.MeasureComment;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureCommentTest extends TestCase {

    @Test
    public void testParse() {

        parse("(123)");
        assertEquals("123", measureComment.getComment());

        parse("   (   abc 123   )   ");
        assertEquals("abc 123", measureComment.getComment());

        parse(" "); //  not a comment
        assertNull(measureComment);
        parse("\n"); //  not a comment
        assertNull(measureComment);
        parse(" \t"); //  not a comment
        assertNull(measureComment);
        parse("\t"); //  not a comment
        assertNull(measureComment);
        parse("\t\t  \t\t   "); //  not a comment
        assertNull(measureComment);
        parse(" "); //  not a comment
        assertNull(measureComment);

        //  initial and final spaces not included
        parse("( this is a comment )");
        assertEquals(rawComment.length() - 2, measureComment.toString().length());
        assertEquals("this is a comment", measureComment.getComment());

        parse("this is also a comment )");
        assertEquals(rawComment.length() + 2, measureComment.toString().length());
        assertEquals(rawComment, measureComment.getComment());

        parse("( this is also a bad comment");
        assertEquals(rawComment.length() + 2, measureComment.toString().length());
        assertEquals(rawComment, measureComment.getComment());

        parse("this is also has to be a comment");
        assertEquals(rawComment.length() + 2, measureComment.toString().length());
        assertEquals(rawComment, measureComment.getComment());

        parse("ABC\nDEF");//  not all a comment
        assertEquals(3 + 2, measureComment.toString().length());
        assertEquals(rawComment.substring(0, 3), measureComment.getComment());

        parse("");//  not a comment
        assertNull(measureComment);

        assertNull(MeasureComment.parse(null));
    }

    private void parse(String s) {
        rawComment = s;
        measureComment = MeasureComment.parse(new StringBuffer(s));
    }

    private String rawComment;
    private MeasureComment measureComment;
}