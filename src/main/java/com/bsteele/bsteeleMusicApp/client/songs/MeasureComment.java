package com.bsteele.bsteeleMusicApp.client.songs;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Objects;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureComment extends MeasureNode {

    public MeasureComment(@Nonnull SectionVersion sectionVersion, String comment) {
        super(sectionVersion);
        this.comment = comment;
    }


    public static final MeasureComment parse(@NotNull SectionVersion sectionVersion, String s) {
        if (s == null || s.length() <= 0)
            return null;

        int n = -1;
        if (s.charAt(0) == '(') {
            n = s.indexOf(')'); //  match a parenthesis
            if (n > 0)
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


    public final String getComment() {
        return comment;
    }

    public final void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String generateHtml(@NotNull SongMoment songMoment, @NotNull Key key, int tran) {
        return toString();
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
        return "( " + comment + ") ";
    }

    private String comment;
}
