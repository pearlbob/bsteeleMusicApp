/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.shared;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import java.util.ArrayList;
import static java.util.Objects.hash;

/**
 *
 * @author bob
 */
public enum Section {
  intro("I", "in"),
  verse("V"),
  preChorus("PC"),
  chorus("C", "ch"),
  bridge("Br"),
  coda("Co", "coda"),
  tag("T"),
  a("A"),
  b("B"),
  outro("O", "out");

  public class Version implements Comparable<Section.Version> {

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
   * Use the returned verion.getSourceLength() to find how many characters were
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

  public static ArrayList<Section.Version> matchAll(String s) {
    ArrayList<Section.Version> ret = new ArrayList<>();

    //  look for possible candidates
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

  private Section(String originalAbreviation) {
    lowerCaseName = name().toLowerCase();
    this.originalAbreviation = originalAbreviation;
    abreviation = originalAbreviation.toLowerCase();
  }

  private Section(String abreviation, String alternateAbreviation) {
    this(abreviation);
    this.alternateAbreviation = alternateAbreviation.toLowerCase();
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
  private static final RegExp allSectionsRegexp = RegExp.compile("([a-zA-Z]+[0-9]?:)", "gmi");
  private static final Section.Version defaultVersion = Section.verse.makeVersion(0, 0);
}
