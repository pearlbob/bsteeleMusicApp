package com.bsteele.bsteeleMusicApp.client.songs;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Objects;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureComment extends Measure {

    public MeasureComment(String comment) {
        this.comment = comment;
    }

    protected MeasureComment() {
        comment = "";
    }

    public static final MeasureComment parse(String s) {
        if (s == null || s.length() <= 0)
            return null;


        int n = s.indexOf('\n');    //  all comments end at the end of the line
        if (n > 0)
            s = s.substring(0, n);
        int parseLength = s.length();   //  original length to the end of the line

        //  properly formatted comment
        final RegExp commentRegExp = RegExp.compile("^(\\s*\\(\\s*(.*?)\\s*\\)\\s*)$");
        MatchResult mr = commentRegExp.exec(s);

        //  format what we found if it's not proper
        if (mr != null) {
            s = mr.getGroup(2);
            parseLength = mr.getGroup(0).length();
        } else {
            //  note: consume any non-whitespace string as a comment if you have to
            s = s.trim();
            if (s.length() <= 0)
                return null;
        }

        //  fixme: cope with odd leading ('s and trailing )'s

        MeasureComment ret = new MeasureComment(s);
        ret.parseLength = parseLength;
        return ret;
    }

    @Override
    public String transpose(@Nonnull Key key, int halfSteps) {
        return comment;
    }


    public final String getComment() {
        return comment;
    }

    @Override
    public ArrayList<String> generateInnerHtml(@Nonnull Key key, int tran, boolean expandRepeats) {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(toString());
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeasureComment that = (MeasureComment) o;
        return Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(comment);
    }

    @Override
    public String toString() {
        return comment == null || comment.length() <= 0 ? "" : "(" + comment + ")";
    }

    private final String comment;
}
