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

    public MeasureComment(@Nonnull SectionVersion sectionVersion, String comment)
    {
        super(sectionVersion);
        this.comment = comment;
    }


    public static final MeasureComment parse(@NotNull SectionVersion sectionVersion, String s)
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
            MeasureComment ret = new MeasureComment(sectionVersion, comment);
            ret.parseLength = mr.getGroup(0).length();
            return ret;
        }

        int n = s.indexOf('\n');    //  all comments end at the end of the line
        if (n < 0)
            n = s.length();         //  all comments end at the end of the file
        if (n <= 0)
            return null;

        MeasureComment ret = new MeasureComment(sectionVersion, s.substring(0, n));
        ret.parseLength = n;
        return ret;
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
    public String generateHtml(@NotNull SongMoment songMoment, @NotNull Key key, int tran)
    {
        return toString();
    }

    @Override
    public ArrayList<String> generateInnerHtml(@Nonnull Key key, int tran)
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

    private String comment;
}
