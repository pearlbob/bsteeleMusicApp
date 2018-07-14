/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.shared.Util;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import java.util.ArrayList;

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
public enum Section
{
    /**
     * A section the introduces the song.  Typically the tempo is
     * set here.
     */
    intro("I", "in"),
    /**
     * A repeating section of the song that typically has new lyrics
     * for each instance.
     */
    verse("V", "vs"),
    /**
     * A section that precedes the chorus but may not be used
     * to lead all chorus sections.
     */
    preChorus("PC"),
    /**
     * A repeating section of the song that typically has repeats lyrics
     * to enforce the song's theme.
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

    private Section(String originalAbbreviation)
    {
        lowerCaseName = name().toLowerCase();
        formalName = Util.firstToUpper(lowerCaseName);
        this.originalAbbreviation = originalAbbreviation;
        abbreviation = originalAbbreviation.toLowerCase();
        this.alternateAbbreviation =null;
    }

    private Section(String abbreviation, String alternateAbbreviation)
    {
        this(abbreviation);
        this.alternateAbbreviation = alternateAbbreviation.toLowerCase();
    }

    private SectionVersion makeVersion(int v, int sourceLength)
    {
        return new SectionVersion(this, v, sourceLength);
    }

    /**
     * Return the section from the found id. Match will ignore case. String has to
     * include the : delimiter and it will be considered part of the section id.
     * Use the returned version.getParseLength() to find how many characters were
     * used in the id.
     *
     * @param s the string to parse
     * @return the length of the parse. Zero if no parse
     */
    public static final SectionVersion parse(String s)
    {
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
                        || (sec.alternateAbbreviation != null && sectionId.equals(sec.alternateAbbreviation)))
                {
                    return sec.makeVersion(version, m.getGroup(0).length());
                }
            }
        }
        return null;
    }

    /**
     * Parse the given string for section version's and report all matches in order.
     *
     * @param s the string to parse
     * @return the section versions found
     */
    public static final ArrayList<SectionVersion> matchAll(String s)
    {
        ArrayList<SectionVersion> ret = new ArrayList<>();

        //  look for possible candidates
        final RegExp allSectionsRegexp = RegExp.compile("([a-zA-Z]+[0-9]?:)", "gmi");
        MatchResult m;
        for (int i = 0; i < 1000; i++) //  don't blow up too badly
        {
            m = allSectionsRegexp.exec(s);
            if (m == null) {
                break;
            }
            if (m.getGroupCount() > 1) {
                //  validate the candidates
                SectionVersion v = parse(m.getGroup(1));
                if (v != null) {
                    ret.add(v);
                }
            }
        }

        return ret;
    }


    /**
     * Return the abbreviation for the section
     *
     * @return
     */
    public final String getAbbreviation()
    {
        return originalAbbreviation;
    }

    /**
     * Utility to return the default section.
     *
     * @return the default section
     */
    public static final SectionVersion getDefaultVersion()
    {
        return Section.verse.makeVersion(0, 0);
    }

    public static final String generateGrammar()
    {
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

    public String getFormalName()
    {
        return formalName;
    }

    private final  String lowerCaseName;
    private final String formalName;
    private final String originalAbbreviation;
    private final String abbreviation;
    private String alternateAbbreviation;
    public static final int maxLength = 10;    //  fixme: compute

}
