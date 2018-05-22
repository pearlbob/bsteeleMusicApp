package com.bsteele.bsteeleMusicApp.client.songs;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureComment extends MeasureNode {

    public MeasureComment(@Nonnull SectionVersion sectionVersion, String comment) {
        super(sectionVersion);
        this.comment = comment;
    }


    public static final MeasureComment parse(@Nonnull SectionVersion sectionVersion, String s) {
        if (s == null || s.length() <= 0)
            return null;

        int n = -1;
        if (s.charAt(0) == '(') {
            n = s.indexOf(')'); //  match a parenthesis
            if ( n > 0 )
                n++;        //  include the right paren
        }
        if (n < 0)
            n = s.indexOf('\n');    //  all comments end at the end of the line
        if (n < 0)
            n = s.length();         //  all comments end at the end of the file
        if (n <= 0)
            return null;

        MeasureComment ret = new MeasureComment(sectionVersion, s.substring(0, n));
        ret.parseLength = n;
        return ret;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "/* "+comment+"*/ ";
    }

    @Override
    public String toHtml() {
        return toString();           // fixme
    }

    private String comment;
}
