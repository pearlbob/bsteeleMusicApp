/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 *
 * @author bob
 */
public enum Section {
    intro("I"),
    verse("V"),
    preChorus("PC"),
    chorus("C", "ch"),
    bridge("Br"),
    coda("Co"),
    tag("T"),
    a("A"),
    b("B"),
    outro("O");

    public class Version {

        Version() {
            this(0, 0);
        }

        Version(int version, int sourceLength) {
            this.version = version;
            this.sourceLength = sourceLength;
        }

        public Section getSection() {
            return Section.this;
        }

        public int getVersion() {
            return version;
        }

        public int getSourceLength() {
            return sourceLength;
        }

        @Override
        public String toString() {
            //  note: designed to go to the user display
            return getSection().getAbreviation() + (version > 0 ? Integer.toString(version) : "");
        }

        @Override
        public int hashCode() {
            int hash = getSection().hashCode();
            hash = 37 * hash + this.version;
            //  do not include source length
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Version other = (Version) obj;
            
            //  do not include source length
            return this.getSection() == other.getSection()
                    && this.version == other.version;
        }

        private int version;
        private int sourceLength;
    }

    private Version makeVersion(int v, int sourceLength) {
        return new Version(v, sourceLength);
    }

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
            if (sec.alternateAbreviation != null && s.startsWith(sec.alternateAbreviation)) {
                return sec.alternateAbreviation.length();
            }
        }
        return 0;
    }

    /**
     * Return the section from the found id. Match will ignore case. String has
     * to include the : delimiter
     *
     * @param s the string to match
     * @return the length of the match. Zero if no match
     */
    public static Section.Version match(String s) {
        if (s == null) {
            return null;
        }
        s = s.substring(0, Math.min(s.length(), maxLength + 1));
        s = s.toLowerCase();

        MatchResult m = sectionRegexp.exec(s);
        if (m != null) {
            String sectionId = m.getGroup(1);      //  already lowercase
            String versionId = (m.getGroupCount() >= 2 ? m.getGroup(2) : null);
            int version = 0;
            if (versionId != null && versionId.length() > 0) {
                version = Integer.parseInt(versionId);
            }
            for (Section sec : Section.values()) {
                if (sectionId.equals(sec.lowerCaseName)
                        || sectionId.equals(sec.abreviation)
                        || (sec.alternateAbreviation != null && sectionId.equals(sec.alternateAbreviation))) {
                    return sec.makeVersion(version, m.getGroup(0).length());
                }
            }
        }
        return null;
    }

    private Section(String originalAbreviation) {
        lowerCaseName = name().toLowerCase();
        this.originalAbreviation = originalAbreviation;
        abreviation = originalAbreviation.toLowerCase();
    }

    private Section(String abreviation, String alternateAbreviation) {
        this(abreviation);
        this.alternateAbreviation = alternateAbreviation;
    }

    public String getAbreviation() {
        return originalAbreviation;
    }

    public static final Section.Version getDefaultVersion() {
        return defaultVersion;
    }

    private String lowerCaseName;
    private String originalAbreviation;
    private String abreviation;
    private String alternateAbreviation;
    public static final int maxLength = 10;    //  fixme: compute
    private static final RegExp sectionRegexp = RegExp.compile("^([a-zA-Z]+)([0-9]?):");
    private static final Section.Version defaultVersion = Section.verse.makeVersion(0, 0);
}
