/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

/**
 *
 * @author bob
 */
public enum Section {
    intro("i"),
    verse("v"),
    preChorus("pc"),
    chorus("c", "ch"),
    bridge("br"),
    coda("co"),
    tag("t"),
    outro("o");

    /**
     * Return length of match at start of string. Match will ignore case.
     *
     * @param s the string to match
     * @return the length of the match. Zero if no match
     */
    public static int matchLength(String s) {
        if (s == null) {
            return 0;
        }
        s = s.substring(0, Math.min(s.length(), maxLength));
        s = s.toLowerCase();

        for (Section sec : Section.values()) {

            if (s.startsWith(sec.lowerCaseName)) {
                return sec.lowerCaseName.length();
            }
            if (s.startsWith(sec.abreviation)) {
                return sec.abreviation.length();
            }
            if (sec.alternateAbreviation != null && s.startsWith(sec.lowerCaseName)) {
                return sec.alternateAbreviation.length();
            }
        }
        return 0;
    }

    private Section(String abreviation) {
        lowerCaseName = name().toLowerCase();
        this.abreviation = abreviation;
    }

    private Section(String abreviation, String alternateAbreviation) {
        this(abreviation);
        this.alternateAbreviation = alternateAbreviation;
    }

    private String lowerCaseName;
    private String abreviation;
    private String alternateAbreviation;
    public static final int maxLength = 10;    //  fixme: compute
}
