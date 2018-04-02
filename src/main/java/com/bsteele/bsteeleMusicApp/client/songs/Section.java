/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.songs;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import java.util.ArrayList;

import static java.util.Objects.hash;

/**
 * @author bob
 */

/**
 * Song structure is represented by a sequence of these sections.
 * The section names have been borrowed from musical practice in the USA
 * so they will likely be familiar.
 * <p>Sections do not imply semantics but their proper suggested use
 * will aid in song structure readability.
 * </p>
 */
public enum Section {
    /**
     * A section the introduces the song.  Typically the tempo is
     * set here.
     */
    intro("I", "in"),
    /**
     * A repeating section of the song that typically has new lyrics
     * for each instance.
     */
    verse("V"),
    /**
     * A section that precedes the chorus but may not be used
     * to lead all chorus sections.
     */
    preChorus("PC"),
    /**
     *  A repeating section of the song that typically has repeats lyrics
     *  to enforce the song's theme.
     */
    chorus("C", "ch"),
    /**
     * A non-repeating section often used once to break the repeated
     * section patterns prior to the last sections of a song.
     */
    bridge("Br"),
    /**
     * A section used to jump to for an ending or repeat.
     */
    coda("Co", "coda"),
    /**
     * A short section that repeats or closely resembles a number of measures from the end
     * of a previous section.  Typically used to end a song.
     */
    tag("T"),
    /**
     * A section labeled "A" to be used in contrast the "B" section.
     * A concept borrowed from jazz.
     */
    a("A"),
    /**
     * A section labeled "B" to be used in contrast the "A" section.
     * A concept borrowed from jazz.
     */
    b("B"),
    /**
     * The ending section of many songs.
     */
    outro("O", "out");

    /**
     * A version identifier for multiple instances of a given section.
     */
    public class Version implements Comparable<Section.Version> {

        Version() {
            this(0, 0);
        }

        Version(int version, int sourceLength) {
            this.version = version;
            this.sourceLength = sourceLength;
        }

        /**
         * Return the generic section for this section version.
         * @return the generic section
         */
        public Section getSection() {
            return Section.this;
        }

        /**
         *  Return the numeric count for this section version.
         * @return the numeric count
         */
        public int getVersion() {
            return version;
        }

        /**
         * The character length used to parse this section version from the original source.
         * @return the original character length.
         */
        public int getSourceLength() {
            return sourceLength;
        }

        /**
         * The external facing string that represents the section version to the user.
         * @return the string
         */
        @Override
        public String toString() {
            //  note: designed to go to the user display
            return getSection().getAbbreviation() + (version > 0 ? Integer.toString(version) : "");
        }

        @Override
        public int hashCode() {
            //  do not include source length
            return hash(getSection(), version);
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

        @Override
        public int compareTo(Version o) {
            if (getSection() != o.getSection()) {
                return getSection().compareTo(o.getSection());
            }

            if (version != o.version) {
                return version < o.version ? -1 : 1;
            }
            return 0;
        }

        private int version;
        private int sourceLength;
    }

    private Version makeVersion(int v, int sourceLength) {
        return new Version(v, sourceLength);
    }

    /**
     * Return the section from the found id. Match will ignore case. String has to
     * include the : delimiter and it will be considered part of the section id.
     * Use the returned version.getSourceLength() to find how many characters were
     * used in the id.
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

        final RegExp sectionRegexp = RegExp.compile("^([a-zA-Z]+)([0-9]?):");
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
                        || sectionId.equals(sec.abbreviation)
                        || (sec.alternateAbbreviation != null && sectionId.equals(sec.alternateAbbreviation))) {
                    return sec.makeVersion(version, m.getGroup(0).length());
                }
            }
        }
        return null;
    }

    /**
     * Parse the given string for section version's and report all matches in order.
     * @param s the string to parse
     * @return the section versions found
     */
    public static ArrayList<Section.Version> matchAll(String s) {
        ArrayList<Section.Version> ret = new ArrayList<>();

        //  look for possible candidates
        final RegExp allSectionsRegexp = RegExp.compile("([a-zA-Z]+[0-9]?:)", "gmi");
        MatchResult m;
        for (int i = 0; i < 100; i++) //  don't blow up too badly
        {
            m = allSectionsRegexp.exec(s);
            if (m == null) {
                break;
            }
            if (m.getGroupCount() > 1) {
                //  validate the candidates
                Section.Version v = match(m.getGroup(1));
                if (v != null) {
                    ret.add(v);
                }
            }
        }

        return ret;
    }

    private Section(String originalAbbreviation) {
        lowerCaseName = name().toLowerCase();
        this.originalAbbreviation = originalAbbreviation;
        abbreviation = originalAbbreviation.toLowerCase();
    }

    private Section(String abreviation, String alternateAbbreviation) {
        this(abreviation);
        this.alternateAbbreviation = alternateAbbreviation.toLowerCase();
    }

    /**
     * Return the abbreviation for the section
     * @return
     */
    public String getAbbreviation() {
        return originalAbbreviation;
    }

    /**
     * Utility to return the default section.
     * @return the default section
     */
    public static final Section.Version getDefaultVersion() {
        return Section.verse.makeVersion(0, 0);
    }

    private String lowerCaseName;
    private String originalAbbreviation;
    private String abbreviation;
    private String alternateAbbreviation;
    public static final int maxLength = 10;    //  fixme: compute
}
