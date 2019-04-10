package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.util.EntryBuffer;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import javax.annotation.Nonnull;
import java.text.ParseException;
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

    @Override
    boolean isComment() {
        return true;
    }

    @Override
    public MeasureNodeType getMeasureNodeType() {
        return MeasureNodeType.comment;
    }

    /**
     * Trash can of measure parsing.  Will consume all that it sees to the end of line.
     *
     * @param sb the input line to parse
     * @return the comment made of the input
     */
    public static final MeasureComment parse(StringBuffer sb) throws ParseException  {
        if (sb == null || sb.length() <= 0)
            throw new ParseException("no data to parse", 0);

        //  prep a sub string to look for the comment
        int n = sb.indexOf("\n");    //  all comments end at the end of the line
        String s = "";
        if (n > 0)
            s = sb.substring(0, n);
        else
            s = sb.toString();

        //  properly formatted comment
        final RegExp commentRegExp = RegExp.compile("^\\s*\\(\\s*(.*?)\\s*\\)\\s*");
        MatchResult mr = commentRegExp.exec(s);

        //  consume the comment
        if (mr != null) {
            s = mr.getGroup(1);
            sb.delete(0, mr.getGroup(0).length());
        } else {
            //  format what we found if it's not proper
            //  note: consume any non-whitespace string as a comment if you have to
            sb.delete(0, s.length());
            s = s.trim();
            if (s.length() <= 0)
                throw new ParseException("no comment found", 0);   //  all whitespace
        }

        //  cope with unbalanced leading ('s and trailing )'s
        s = s.replaceAll("^\\(","").replaceAll("\\)$","");
        s = s.trim();   //  in case there is white space inside unbalanced parens

        MeasureComment ret = new MeasureComment(s);
        return ret;
    }

    @Override
    public String transpose(@Nonnull Key key, int halfSteps) {
        return toString();
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
    public String toMarkup() {
        return toString();
    }

    @Override
    public String toString() {
        return comment == null || comment.length() <= 0 ? "" : "(" + comment + ")";
    }

    private final String comment;
}
