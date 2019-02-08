/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.util.Util;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

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
     * A section that introduces the song.  Typically the tempo is
     * set here.
     */
    intro("I", "in", "A section that introduces the song."),
    /**
     * A repeating section of the song that typically has new lyrics
     * for each instance.
     */
    verse("V", "vs", "A repeating section of the song that typically has new lyrics" +
            " for each instance."),
    /**
     * A section that precedes the chorus but may not be used
     * to lead all chorus sections.
     */
    preChorus("PC", "A section that precedes the chorus but may not be used to lead all chorus sections."),
    /**
     * A repeating section of the song that typically has lyrics that repeat
     * to enforce the song's theme.
     */
    chorus("C", "ch", "A repeating section of the song that typically has lyrics that repeat" +
            " to enforce the song's theme."),
    /**
     * A section labeled "A" to be used in contrast the "B" section.
     * A concept borrowed from jazz.
     */
    a("A", "A section labeled \"A\" to be used in contrast the \"B\" section.  A concept borrowed from jazz."),
    /**
     * A section labeled "B" to be used in contrast the "A" section.
     * A concept borrowed from jazz.
     */
    b("B", "A section labeled \"B\" to be used in contrast the \"A\" section.  A concept borrowed from jazz."),
    /**
     * A non-repeating section often used once to break the repeated
     * section patterns prior to the last sections of a song.
     */
    bridge("Br", "A non-repeating section often used once to break the repeated " +
            "section patterns prior to the last sections of a song."),
    /**
     * A section used to jump to for an ending or repeat.
     */
    coda("Co", "coda", "A section used to jump to for an ending or repeat."),
    /**
     * A short section that repeats or closely resembles a number of measures from the end
     * of a previous section.  Typically used to end a song.
     */
    tag("T", "A short section that repeats or closely resembles a number of measures from the end " +
            " of a previous section.  Typically used to end a song."),
    /**
     * The ending section of many songs.
     */
    outro("O", "out", "The ending section of many songs.");

    /**
     * A private convenience constructor for the enumeration.
     *
     * @param originalAbbreviation the default abbreviation for the section
     * @param description          a short description of the section's musical purpose for the user
     */
    private Section(String originalAbbreviation, String description) {
        lowerCaseName = name().toLowerCase();
        formalName = Util.firstToUpper(lowerCaseName);
        this.originalAbbreviation = originalAbbreviation;
        abbreviation = originalAbbreviation.toLowerCase();
        this.alternateAbbreviation = null;
        this.description = description;
    }

    /**
     * A private convenience constructor for the enumeration.
     *
     * @param abbreviation          the default abbreviation for the section
     * @param alternateAbbreviation an alternate abbreviation for the section
     * @param description           a short description of the section's musical purpose for the user
     */
    private Section(String abbreviation, String alternateAbbreviation, String description) {
        this(abbreviation, description);
        this.alternateAbbreviation = alternateAbbreviation.toLowerCase();
    }

    /**
     * a private convenience method to create a section version for this section
     *
     * @param v the variation identification number.  Zero is the default value.
     * @return
     */
    private SectionVersion makeVersion(int v) {
        return new SectionVersion(this, v);
    }

    static final boolean lookahead(StringBuffer sb) {
        final RegExp sectionRegexp = RegExp.compile(sectionRegexpPattern);
        MatchResult m = sectionRegexp.exec(sb.substring(0, Math.min(sb.length(), maxLength)));
        if (m == null)
            return false;
        return getSection(m.getGroup(1)) != null;
    }

    /**
     * Return the section from the found id. Match will ignore case. String has to
     * include the : delimiter and it will be considered part of the section id.
     * Use the returned version.getParseLength() to find how many characters were
     * used in the id.
     *
     * @param sb the string to parse
     * @return the length of the parse. Zero if no parse
     */
    public static final SectionVersion parse(StringBuffer sb) {

        final RegExp sectionRegexp = RegExp.compile(sectionRegexpPattern);
        MatchResult m = sectionRegexp.exec(sb.toString());
        if (m != null) {
            String sectionId = m.getGroup(1);
            String versionId = (m.getGroupCount() >= 2 ? m.getGroup(2) : null);
            int version = 0;
            if (versionId != null && versionId.length() > 0) {
                version = Integer.parseInt(versionId);
            }
            Section section = getSection(sectionId);
            if (section != null) {
                //   consume the section label
                sb.delete(0, sectionId.length()
                        + 1  //  include the :
                );
                return section.makeVersion(version);
            }
        }
        return null;
    }

    private static final Section getSection(String sectionId) {
        sectionId = sectionId.toLowerCase();
        for (Section section : Section.values()) {
            if (sectionId.equals(section.lowerCaseName)
                    || sectionId.equals(section.abbreviation)
                    || (section.alternateAbbreviation != null && sectionId.equals(section.alternateAbbreviation))) {
                return section;
            }
        }
        return null;
    }


    /**
     * Return the abbreviation for the section
     *
     * @return
     */
    public final String getAbbreviation() {
        return originalAbbreviation;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Utility to return the default section.
     *
     * @return the default section
     */
    public static final SectionVersion getDefaultVersion() {
        return Section.verse.makeVersion(0);
    }

    /**
     * A tool to generate part of the enum's documentation.
     *
     * @return a BNF grammar fragment
     */
    public static final String generateGrammar() {
        StringBuilder sb = new StringBuilder();
        sb.append("<Section: (");
        boolean first = true;
        for (Section section : Section.values()) {
            sb.append("\n\t");

            if (first)
                first = false;
            else
                sb.append("| ");
            sb.append("\"").append(section.name()).append("\"");
            sb.append("| \"").append(section.getAbbreviation()).append("\"");
            String lc = section.getAbbreviation().toLowerCase();
            if (!lc.equals(section.name()))
                sb.append("| \"").append(lc).append("\"");
            if (section.alternateAbbreviation != null && !section.alternateAbbreviation.equals(section.name()))
                sb.append("| \"").append(section.alternateAbbreviation).append("\"");
        }
        sb.append("\n\t) >\n");
        return sb.toString();
    }

    public String getFormalName() {
        return formalName;
    }

    private final String lowerCaseName;
    private final String formalName;
    private final String originalAbbreviation;
    private final String abbreviation;
    private String alternateAbbreviation;
    private String description;
    public static final int maxLength = 10;    //  fixme: compute

    public static final String sectionRegexpPattern = "^([a-zA-Z]+)([\\d]?):";  //  has to include the :
}
