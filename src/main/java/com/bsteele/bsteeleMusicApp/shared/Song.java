/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import jsinterop.annotations.JsType;

/**
 *
 * @author bob
 */
@JsType
public class Song {

  public Song() {

  }

  public void loadSong(String title, String lyrics, String chords, int beatsPerBar, int beatsPerMinute) {
    songId = "Song" + title.replaceAll("\\W+", "");
    rawLyrics = lyrics;
    parseLyricsToSectionSequence(lyrics);
    parseChordTable(chords);
    setBeatsPerBar(beatsPerBar);
    setBeatsPerMinute(bpm);
  }

  public void parseLyricsToSectionSequence(String rawLyrics) {
    sequence = Section.matchAll(rawLyrics);

    if (sequence.isEmpty()) {
      sequence.add(Section.getDefaultVersion());
    }
    //GWT.log(sequence.toString());
  }

  /**
   * Legacy prep of section sequence for javascript
   *
   * @return
   */
  public String[] getSectionSequenceAsStrings() {

    //  dumb down the section sequence list for javascript
    String ret[] = new String[sequence.size()];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = sequence.get(i).toString();
    }
    return ret;
  }

  public String[][] getChordSection(String sectionId) {
    return jsChordSectionMap.get(sectionId);
  }

  public String getChordSectionId(String sectionId) {
    //  map the section to it's reduced, common section
    for (Section.Version v : displaySectionMap.keySet()) {
      if (v.toString().equals(sectionId)) {
        sectionId = displaySectionMap.get(v).toString();
        break;
      }
    }
    return sectionId;
  }

  public ArrayList<Section.Version> getSectionSequence() {
    return sequence;
  }

  public void parseChordTable(String rawChordTableText) {

    chordSectionMap.clear();

    {
      //  build the initial chord section map
      Grid<String> grid = new Grid<>();
      int row = 0;
      int col = 0;

      int state = 0;
      Section.Version version;
      TreeSet<Section.Version> versionsDeclared = new TreeSet<>();
      String block = "";
      for (int i = 0; i < rawChordTableText.length(); i++) {
        char c = rawChordTableText.charAt(i);
        switch (state) {
          default:
            state = 0;
          case 0:
            //  absorb leading white space
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
              break;
            }
            block = "";
            state++;

          //  fall through
          case 1:
            String token = rawChordTableText.substring(i);
            Section.Version v = Section.match(token.substring(0, 11));
            if (v != null) {
              version = v;
              i += version.getSourceLength() - 1;//   consume the section label

              if (!versionsDeclared.isEmpty() && !grid.isEmpty()) {
                for (Section.Version vd : versionsDeclared) {
                  chordSectionMap.put(vd, grid);
                }
                //  fixme: worry about chords before a section is declared
                versionsDeclared.clear();
                grid = new Grid<>();
              }
              versionsDeclared.add(version);

              row = 0;
              col = 0;
              block = "";
              state = 0;

            } else {
              //  absorb trailing white space
              switch (c) {
                case ' ':
                case '\t':
                  if (block.length() > 0) {
                    grid.add(col, row, block);
                    col++;
                  }
                  block = "";
                  break;
                case '\n':
                case '\r':
                  if (block.length() > 0) {
                    grid.add(col, row, block);
                    col++;
                  }
                  row++;
                  col = 0;
                  block = "";
                  state = 0;
                  break;
                default:
                  block += c;
                  break;
              }
            }
            break;
        }
      }

      //  put the last grid on the end
      if (!versionsDeclared.isEmpty() && !grid.isEmpty()) {
        for (Section.Version vd : versionsDeclared) {
          chordSectionMap.put(vd, grid);
        }
      }

      //  deal with unformatted songs
      if (chordSectionMap.isEmpty()) {
        chordSectionMap.put(Section.getDefaultVersion(), grid);
      }
    }

    //  collect remap sections with identical declarations
    {
      //  build a reverse lookup map
      HashMap<Grid<String>, TreeSet<Section.Version>> reverseMap = new HashMap<>();
      for (Section.Version version : chordSectionMap.keySet()) {
        Grid<String> grid = chordSectionMap.get(version);
        TreeSet<Section.Version> lookup = reverseMap.get(grid);
        if (lookup == null) {
          TreeSet<Section.Version> ts = new TreeSet<>();
          ts.add(version);
          reverseMap.put(grid, ts);
        } else {
          lookup.add(version);
        }
      }
      //  build version mapping to version displayed map
      displaySectionMap.clear();
      for (Grid<String> g : reverseMap.keySet()) {
        TreeSet<Section.Version> mappedVersions = reverseMap.get(g);
        Section.Version first = mappedVersions.first();
        for (Section.Version dv : mappedVersions) {
          //  more convenient to put idenity mapping in for first
          displaySectionMap.put(dv, first);
        }
      }

      //GWT.log(displayMap.toString());
      HashMap<Section.Version, Grid<String>> reducedChordSectionMap = new HashMap<>();
      for (Section.Version version : chordSectionMap.keySet()) {
        reducedChordSectionMap.put(version, chordSectionMap.get(displaySectionMap.get(version)));
      }

      //  install the reduced map
      chordSectionMap.clear();
      chordSectionMap.putAll(reducedChordSectionMap);
      //GWT.log(chordSectionMap.toString());
    }

    //  build map for js
    for (Section.Version v : chordSectionMap.keySet()) {
      jsChordSectionMap.put(v.toString(), chordSectionMap.get(v).getJavasriptCopy());
    }

//        for (Section.Version v : chordSectionMap.keySet()) {
//            GWT.log(" ");
//            GWT.log(v.toString());
//            GWT.log("    " + chordSectionMap.get(v).toString());
//        }
  }

  public String generateHtmlChordTable() {
    return generateHtmlChordTableFromMap(chordSectionMap);
  }

  public String generateHtmlLyricsTable() {
    String tableStart = "<table id=\"lyricsTable\">\n"
            + "<colgroup>\n"
            + "   <col style=\"width:2ch;\">\n"
            + "  <col>\n"
            + "</colgroup>\n";
    String rowStart = "<tr><td class='sectionLabel' >";
    String rowEnd = "&nbsp;</td></tr>\n";   //  empty cells fill better with nbsp
    String tableEnd = "</table>\n";

    String lyrics = ""; //  table formatted
    int state = 0;
    int sectionIndex = 0;
    boolean isSection = false;
    String whiteSpace = "";
    for (int i = 0; i < rawLyrics.length(); i++) {
      char c = rawLyrics.charAt(i);
      switch (state) {
        default:
          state = 0;
        case 0:
          //  absorb leading white space
          if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
            break;
          }
          state++;
        //  fall through
        case 1:
          Section.Version version = Section.match(rawLyrics.substring(i, i + 11));
          if (version != null) {
            i += version.getSourceLength() - 1; //  skip the end of the section id
            isSection = true;

            if (lyrics.length() > 0) {
              lyrics += rowEnd;
            }
            lyrics += rowStart + version.toString() + ":"
                    + "</td><td class=\"lyrics" + version.getSection().getAbreviation() + "Class\""
                    + " id=\"L." + sectionIndex + "\">";
            sectionIndex++;
            whiteSpace = ""; //  ignore white space
            state = 0;
          } else {
            //  absorb trailing white space
            switch (c) {
              case ' ':
              case '\t':
                whiteSpace += c;
                break;
              case '\n':
              case '\r':
                lyrics += c;
                whiteSpace = ""; //  ignore white space
                state = 0;
                break;
              default:
                if (!isSection) {
                  //  deal with bad formatting
                  lyrics += rowStart
                          + Section.getDefaultVersion().toString() + ":"
                          + "</td><td class=\"lyrics" + Section.getDefaultVersion().toString() + "Class\">";
                  isSection = true;
                }
                lyrics += whiteSpace + c;
                whiteSpace = "";
                break;
            }
          }
          break;
      }
    }

    lyrics = tableStart
            + lyrics
            + rowEnd
            + tableEnd;
    //GWT.log(lyrics);
    return lyrics;
  }

  private String generateHtmlChordTableFromMap(HashMap<Section.Version, Grid<String>> map) {

    if (map.isEmpty()) {
      return "";
    }

    String tableStart = "<table id=\"chordTable\" "
            + "><tr><td colspan=\"5\" id=\"chordComment\"> </td></tr>\n";
    String sectionStart = "<tr><td class='sectionLabel' >";
    String rowStart = "\t<tr><td></td>";
    String rowEnd = "</tr>\n";
    String tableEnd = "</table>\n";

    String chordText = ""; //  table formatted

    SortedSet<Section.Version> sortedKeys = new TreeSet<>(map.keySet());
    SortedSet<Section.Version> displayed = new TreeSet<>();
    for (Section.Version version : sortedKeys) {
      if (displayed.contains(version)) {
        continue;
      }

      Grid<String> grid = map.get(version);
      Section.Version displayVersion = displaySectionMap.get(version);

      //  section label
      String start = sectionStart;
      for (Section.Version v : displaySectionMap.keySet()) {
        if (displaySectionMap.get(v) == version) {
          start += v.toString() + ":<br/>";
          displayed.add(v);
        }
      }
      start += "</td>\n";

      //  section data
      for (int r = 0; r < grid.getRowCount(); r++) {

        chordText += start;
        start = rowStart;   //  default to empty row start on subsequent rows

        ArrayList<String> row = grid.getRow(r);
        for (int col = 0; col < row.size(); col++) {
          chordText += "<td class=\"section" + version.getSection().getAbreviation() + "Class\""
                  //+ " style=\"\""
                  + " id=\"C." + displayVersion.toString() + "." + r + "." + col + "\""
                  + " >"
                  + row.get(col) + "</td>\n\t";
        }
        chordText += rowEnd;
      }
    }
    String ret = tableStart + chordText + tableEnd;
    return ret;
  }

  /**
   *
   * @param halfSteps
   * @return
   */
  public String transpose(int halfSteps) {
    if (halfSteps == 0) {
      return generateHtmlChordTable();
    }

    //GWT.log("Song.transpose()  here: " + halfSteps + " to: " + chords);
    HashMap<Section.Version, Grid<String>> tranMap = deepCopy(chordSectionMap);

    for (Section.Version version : tranMap.keySet()) {
      Grid<String> grid = tranMap.get(version);

      int rLimit = grid.getRowCount();
      for (int r = 0; r < rLimit; r++) {
        ArrayList<String> row = grid.getRow(r);
        int colLimit = row.size();
        for (int col = 0; col < colLimit; col++) {
          grid.set(col, r, transposeMeasure(row.get(col), halfSteps));
        }
      }
    }

    //GWT.log(tranMap.toString());
    return generateHtmlChordTableFromMap(tranMap);
  }

  private String transposeMeasure(String m, int halfSteps) {

    int chordNumber = 0;
    String sout = "";

    int state = 0;

    for (int ci = 0; ci < m.length(); ci++) {
      char c = m.charAt(ci);
      switch (state) {
        case 1:	//  chord symbol modifier, one character only
          state = 0;
          if (c == 'b' || c == js_flat) {
            chordNumber -= 1;
            sout += chordNumberToLetter(chordNumber + halfSteps);
            break;
          }
          if (c == '#' || c == js_sharp) {
            chordNumber += 1;
            sout += chordNumberToLetter(chordNumber + halfSteps);
            break;
          }
          if (c == js_natural) {
            sout += chordNumberToLetter(chordNumber + halfSteps);
            break;
          }
          sout += chordNumberToLetter(chordNumber + halfSteps);
        //	fall through
        default:
        case 0:
          if (c == '(') {
            sout += c;
            state = 11;
            break;
          }

          //  don't transpose the section identifiers that happen to look like notes
          String toMatch = m.substring(ci, Math.min(m.length() - ci, Section.maxLength));
          Section.Version version = Section.match(toMatch);
          if (version != null) {
            sout += version.toString();
            ci += version.getSourceLength() - 1; //  skip the end of the section id
            break;
          }

          if (c >= 'A' && c <= 'G') {
            chordNumber = chordLetterToNumber(c);
            state = 1;
            break;
          }
          if (toMatch.startsWith("maj")) {
            sout += "maj";
            ci += 2;
          } else if (toMatch.startsWith("sus")) {
            sout += "sus";
            ci += 2;
          } else if ((c >= '0' && c <= '9')
                  || c == 'm'
                  || c == ' ' || c == '-' || c == '|' || c == '/'
                  || c == '[' || c == ']'
                  || c == '{' || c == '}'
                  || c == '.'
                  || c == '\n'
                  || c == js_delta) {
            sout += c;
          } else {	//  don't parse the rest
            sout += c;
            state = 10;
          }
          break;

        case 10: //	wait for newline
          sout += c;
          break;

        case 11: //	wait for newline or closing paren
          sout += c;
          if (c == ')') {
            state = 0;
          }
          break;
      }
    }
    //  do the last chord
    if (state == 1) {
      sout += chordNumberToLetter(chordNumber + halfSteps);
    }
    //sout += '\n';

    //GWT.log(sout);
    return sout;
  }

  private static HashMap<Section.Version, Grid<String>> deepCopy(HashMap<Section.Version, Grid<String>> map) {
    HashMap<Section.Version, Grid<String>> ret = new HashMap<>();
    for (Section.Version version : map.keySet()) {
      ret.put(version, new Grid<String>().deepCopy(map.get(version)));
      //  fixme: worry about version alteration!
    }
    return ret;
  }

  private static int chordLetterToNumber(char letter) {
    int i = letter - 'A';
    //                            a  a# b  c  c# d  d# e  f f#  g  g#
    //                            0  1  2  3  4  5  6  7  8  9  10 11

    return chordLetterToNumber[i];
  }
  private static final int chordLetterToNumber[] = new int[]{0, 2, 3, 5, 7, 8, 10};

  private static String chordNumberToLetter(int n) {

    n = n % 12;
    if (n < 0) {
      n += 12;
    }
    //                            a     a#    b    c    c#    d    d#    e    f    f#    g    g#
    //                            0     1     2    3    4     5    6     7    8    9     10   11

    return chordNumberToLetter[n];
  }

  /**
   * @return the bpm
   */
  public int getBeatsPerMinute() {
    return bpm;
  }

  /**
   * @param bpm the bpm to set
   */
  private void setBeatsPerMinute(int bpm) {
    this.bpm = bpm;
  }

  /**
   * @return the beatsPerBar
   */
  public int getBeatsPerBar() {
    return beatsPerBar;
  }

  /**
   * @param beatsPerBar the beatsPerBar to set
   */
  private void setBeatsPerBar(int beatsPerBar) {
    this.beatsPerBar = beatsPerBar;
  }

  /**
   * @return the songId
   */
  public String getSongId() {
    return songId;
  }

  private String songId;
  private String rawLyrics;
  private int bpm;  //  beats per minute
  private int beatsPerBar;  //  beats per bar
  private ArrayList<Section.Version> sequence;
  private final HashMap<Section.Version, Grid<String>> chordSectionMap = new HashMap<>();
  private final HashMap<Section.Version, Section.Version> displaySectionMap = new HashMap<>();
  private final HashMap<String, String[][]> jsChordSectionMap = new HashMap<>();
  private static final String chordNumberToLetter[] = new String[]{"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
  private static final char js_flat = '\u266D';
  private static final char js_natural = '\u266E';
  private static final char js_sharp = '\u266F';
  private static final char js_delta = '\u0394';

}