package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.songs.MeasureComment;
import junit.framework.TestCase;
import org.junit.Test;

import java.text.ParseException;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureCommentTest extends TestCase {

    @Test
    public void testParse() {

        parse("  (  123  )   A\n");
        assertTrue(measureComment.isComment());
        assertTrue(measureComment.isSingleItem());
        assertFalse( measureComment.isRepeat());
        assertEquals("123", measureComment.getComment());

        parse("(123)");
        assertTrue(measureComment.isComment());
        assertTrue(measureComment.isSingleItem());
        assertFalse( measureComment.isRepeat());
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
        assertEquals(rawComment.length() - 2, measureComment.getComment().length());
        assertEquals(rawComment.substring(0,rawComment.length()-2), measureComment.getComment());

        parse("( this is also a bad comment");
        assertEquals(rawComment.length() - 2, measureComment.getComment().length());    //  parens no
        assertEquals(rawComment.substring(2), measureComment.getComment());

        parse("this is also has to be a comment");
        assertEquals(rawComment.length() + 2, measureComment.toString().length());      //  parens yes
        assertEquals(rawComment, measureComment.getComment());

        parse("ABC\nDEF");//  not all a comment
        assertEquals(3 + 2, measureComment.toString().length());
        assertEquals(rawComment.substring(0, 3), measureComment.getComment());

        parse("");//  not a comment
        assertNull(measureComment);

        try {
            MeasureComment.parse((String) null);
            fail();
        } catch (ParseException e) {
            //  should throw this exception
        }
    }

    private void parse(String s) {
        rawComment = s;
        try {
            measureComment = MeasureComment.parse(s);
        } catch (ParseException e) {
            measureComment = null;
        }
    }

    private String rawComment;
    private MeasureComment measureComment;
}