/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.shared.Grid;
import com.google.gwt.core.client.GWT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 *
 * @author bob
 */
@JsType
public class Song {

    public Song() {

    }

    public ArrayList<Section.Version> parseIntoSectionSequence(String rawLyrics) {
        ArrayList<Section.Version> sequence = Section.matchAll(rawLyrics);

        if (sequence.isEmpty()) {
            sequence.add(Section.getDefaultVersion());
        }

        GWT.log(sequence.toString());
        return sequence;
    }

    public void parseChordTable(String rawChordTableText) {

        chordSectionMap.clear();
        Grid<String> grid = new Grid<String>();
        int row = 0;
        int col = 0;

        int state = 0;
        Section.Version version = null;
        Section.Version lastVersion = null;
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

                        if (lastVersion != null) {
                            chordSectionMap.put(lastVersion, grid);
                            //  fixme: worry about chords before a section is declared
                        }
                        lastVersion = version;

                        grid = new Grid<String>();
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
        if (lastVersion != null) {
            chordSectionMap.put(lastVersion, grid);
        }

        //  deal with unformatted songs
        if (chordSectionMap.isEmpty()) {
            chordSectionMap.put(Section.getDefaultVersion(), grid);
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

    public String parseIntoLyricsTable(String rawText) {
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
        for (int i = 0; i < rawText.length(); i++) {
            char c = rawText.charAt(i);
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
                    Section.Version version = Section.match(rawText.substring(i, i + 11));
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
                                            +Section.getDefaultVersion().toString()+":"
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
        GWT.log(lyrics);
        return lyrics;
    }

    private String generateHtmlChordTableFromMap(HashMap<Section.Version, Grid> map) {

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

        for (Section.Version version : map.keySet()) {
            Grid grid = map.get(version);
            String start = sectionStart + version.toString() + ":</td>";
            for (int r = 0; r < grid.getRowCount(); r++) {

                chordText += start;
                start = rowStart;

                ArrayList<String> row = grid.getRow(r);
                for (int col = 0; col < row.size(); col++) {
                    chordText += "<td class=\"section" + version.getSection().getAbreviation() + "Class\""
                            //+ " style=\"\""
                            + " id=\"C." + version.toString() + "." + r + "." + col + "\""
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
        HashMap<Section.Version, Grid> tranMap = deepCopy(chordSectionMap);

        for (Section.Version version : tranMap.keySet()) {
            Grid grid = tranMap.get(version);

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

    private static HashMap<Section.Version, Grid> deepCopy(HashMap<Section.Version, Grid> map) {
        HashMap<Section.Version, Grid> ret = new HashMap<>();
        for (Section.Version version : map.keySet()) {
            ret.put(version, new Grid(map.get(version)));
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

    private HashMap<Section.Version, Grid> chordSectionMap = new HashMap<>();
    private static final String chordNumberToLetter[] = new String[]{"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
    private static final char js_flat = '\u266D';
    private static final char js_natural = '\u266E';
    private static final char js_sharp = '\u266F';
    private static final char js_delta = '\u0394';

}
