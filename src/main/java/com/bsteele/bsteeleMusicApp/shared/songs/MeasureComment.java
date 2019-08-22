package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.util.MarkedString;
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

    static final MeasureComment parse(String s) throws ParseException {
        return parse(new MarkedString(s));
    }

    /**
     * Trash can of measure parsing.  Will consume all that it sees to the end of line.
     *
     * @param markedString the input line to parse
     * @return the comment made of the input
     * @throws ParseException thrown if parsing fails
     */
    static final MeasureComment parse(MarkedString markedString) throws ParseException {
        if (markedString == null || markedString.isEmpty())
            throw new ParseException("no data to parse", 0);

        //  prep a sub string to look for the comment
        int n = markedString.indexOf("\n");    //  all comments end at the end of the line
        String s = "";
        if (n > 0)
            s = markedString.remainingStringLimited(n);
        else
            s = markedString.toString();

        //  properly formatted comment
        final RegExp commentRegExp = RegExp.compile("^\\s*\\(\\s*(.*?)\\s*\\)\\s*");
        MatchResult mr = commentRegExp.exec(s);

        //  consume the comment
        if (mr != null) {
            s = mr.getGroup(1);
            markedString.consume(mr.getGroup(0).length());
        } else {
            throw new ParseException("no well formed comment found", 0);   //  all whitespace
        }

        //  cope with unbalanced leading ('s and trailing )'s
        s = s.replaceAll("^\\(", "").replaceAll("\\)$", "");
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

    @Override
    public boolean isEndOfRow(){
        return true;
    }

    private final String comment;
}
