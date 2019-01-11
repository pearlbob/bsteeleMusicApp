package com.bsteele.bsteeleMusicApp.client.songs;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Objects;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureComment extends Measure
{

    public MeasureComment(String comment)
    {
        this.comment = comment;
    }

    protected MeasureComment()
    {
    }

    public static final MeasureComment parse(String s)
    {
        if (s == null || s.length() <= 0)
            return null;


        MatchResult mr;
        //  properly formatted comment
        {
            final RegExp commentRegExp = RegExp.compile("^([ \\t]*\\((.*)\\)[ \\t]*\n)");
            mr = commentRegExp.exec(s);
        }
        if (mr == null) {
            //  properly formatted embedded comment
            final RegExp commentRegExp = RegExp.compile("^([ \\t]*\\((.*)\\)[ \\t]*)");
            mr = commentRegExp.exec(s);
        }

        if (mr == null) {
            // improperly formatted comment
            final RegExp uncommentRegExp = RegExp.compile("^([ \\t]*(.+)[ \\t]*\n)");
            mr = uncommentRegExp.exec(s);
        }

        //  format what we found
        if (mr != null) {
            String comment = mr.getGroup(2);
            MeasureComment ret = new MeasureComment(comment);
            ret.parseLength = mr.getGroup(0).length();
            return ret;
        }

        int n = s.indexOf('\n');    //  all comments end at the end of the line
        if (n < 0)
            n = s.length();         //  all comments end at the end of the file
        if (n <= 0)
            return null;

        MeasureComment ret = new MeasureComment(s.substring(0, n));
        ret.parseLength = n;
        return ret;
    }

    @Override
    public String transpose(@Nonnull Key key, int halfSteps)
    {
        return comment;
    }


    public final String getComment()
    {
        return comment;
    }

    public final void setComment(String comment)
    {
        this.comment = comment;
    }

    @Override
    public ArrayList<String> generateInnerHtml(@Nonnull Key key, int tran, boolean expandRepeats)
    {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(toString());
        return ret;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeasureComment that = (MeasureComment) o;
        return Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(comment);
    }

    @Override
    public String toString()
    {
        return "( " + comment + ") ";
    }

    private String comment = "";
}
