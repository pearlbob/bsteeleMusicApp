package com.bsteele.bsteeleMusicApp.shared.util;

public class EntryBuffer {

    public EntryBuffer() {
        stringBuilder = new StringBuilder();
    }

    public EntryBuffer(String s) {
        stringBuilder = new StringBuilder(s);
    }

    public EntryBuffer(StringBuilder sb) {
        stringBuilder = new StringBuilder(sb.toString());
    }

    public EntryBuffer(StringBuffer sb) {
        stringBuilder = new StringBuilder(sb.toString());
    }

    public final void reset() {
        index = 0;
    }

    public final void flush() {
        if (index <= 0)
            return;

        stringBuilder.delete(0, index);
        reset();
    }

    public final void flush(int i) {
        if (i <= 0)
            return;

        stringBuilder.delete(0, i);
        index = (i >= index) ? 0 : index - i;

        reset();
    }

    public final boolean isEmpty() {
        return stringBuilder.length() <= 0;
    }

    public final char getNextChar() throws IndexOutOfBoundsException {
        return stringBuilder.charAt(index++);
    }

    public final char charAt(int i) {
        return stringBuilder.charAt(i);
    }

    public EntryBuffer append(String s) {
        stringBuilder.append(s);
        return this;
    }

    public final int available(){
        int ret = stringBuilder.length() - index;
        return ( ret < 0 ? 0 : ret);
    }

    public final int length(){
        return stringBuilder.length();
    }

    private int index = 0;
    private StringBuilder stringBuilder;
}
