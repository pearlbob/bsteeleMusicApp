package com.bsteele.bsteeleMusicApp.shared.util;

public class MarkedString {


    public MarkedString(String s) {
        if (s == null)
            string = "";
        else
            string = s;
    }

    public int mark() {
        mark = index;
        return mark;
    }

    public final void resetToMark(int m) {
        mark = m;
        resetToMark();
    }

    public final int getMark() {
        return mark;
    }

    public final void resetToMark() {
        index = mark;
    }

    public final void resetTo(int i) {
        index = i;
    }

    public final boolean isEmpty() {
        return string.length() <= 0 || index >= string.length();
    }

    public final char getNextChar() throws IndexOutOfBoundsException {
        return string.charAt(index++);
    }

    public final int indexOf(String s) {
        return string.substring(index).indexOf(s);
    }

    public final String remainingStringLimited(int limitLength) {
        int i = index + limitLength;
        i = Math.min(i, string.length());
        return string.substring(index, i);
    }

    /**
     * return character at location relative to current index
     *
     * @param i character location
     * @return character at index plus location specified
     * @throws IndexOutOfBoundsException
     */
    public final char charAt(int i) throws IndexOutOfBoundsException {
        return string.charAt(index + i);
    }

    public final void consume(int n) {
        index += n;
    }

    public final int available() {
        int ret = string.length() - index;
        return (ret < 0 ? 0 : ret);
    }

    @Override
    public String toString() {
        return string.substring(index);
    }

    private int index = 0;
    private int mark = 0;
    private final String string;
}
