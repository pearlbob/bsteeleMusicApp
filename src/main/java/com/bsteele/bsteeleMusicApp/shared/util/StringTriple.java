package com.bsteele.bsteeleMusicApp.shared.util;

public class StringTriple {
    public StringTriple(String a, String b, String c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }


    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    public String getC() {
        return c;
    }

    @Override
    public String toString() {
        return "("+a + ": \"" + b + "\", \"" + c + "\")";
    }

    private final String a;
    private final String b;
    private final String c;
}
