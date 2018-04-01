/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.Grid;
import com.bsteele.bsteeleMusicApp.shared.JsonUtil;
import com.bsteele.bsteeleMusicApp.shared.Util;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author bob
 */
public class Song implements Comparable<Song> {

    public Song() {
    }

    public static final Song createEmptySong() {
        return createSong("", "",
                "", Key.C, 100, 4, 4,
                "", "");
    }

    public static final Song createSong(String title, String artist,
                                        String copyright,
                                        Key key, int bpm, int beatsPerBar, int unitsPerMeasure,
                                        String chords, String lyrics) {
        Song song = new Song();
        song.setTitle(title);
        song.setArtist(artist);
        song.copyright = copyright;
        song.setKey(key);
        song.setBeatsPerMinute(bpm);
        song.setBeatsPerBar(beatsPerBar);
        song.unitsPerMeasure = unitsPerMeasure;
        song.rawLyrics = lyrics;
        song.chords = chords;
        song.parseChordTable(chords);
        song.parseLyricsToSectionSequence(lyrics);

        return song;
    }

    public final Song copySong() {
        return createSong(getTitle(), getArtist(),
                getCopyright(), getKey(), getBeatsPerMinute(), getBeatsPerBar(), getUnitsPerMeasure(),
                getChordsAsString(), getLyricsAsString());
    }

    public static final Song fromJson(String jsonString) {
        if (jsonString == null || jsonString.length() <= 0) {
            return null;
        }
        JSONObject jo;
        {
            JSONValue jv = JSONParser.parseStrict(jsonString);
            if (jv == null) {
                return null;
            }
            jo = jv.isObject();
        }
        return fromJsonObject(jo);
    }

    public static final Song fromJsonObject(JSONObject jo) {
        if (jo == null) {
            return null;
        }
        Song song = new Song();
        JSONNumber jn;
        JSONArray ja;
        for (String name : jo.keySet()) {
            JSONValue jv = jo.get(name);
            switch (name) {
                case "title":
                    song.setTitle(jv.isString().stringValue());
                    break;
                case "artist":
                    song.setArtist(jv.isString().stringValue());
                    break;
                case "copyright":
                    song.copyright = jv.isString().stringValue();
                    break;
                case "key":
                    song.key = Key.valueOf(jv.isString().stringValue());
                    break;
                case "defaultBpm":
                    jn = jv.isNumber();
                    if (jn != null) {
                        song.defaultBpm = (int) jn.doubleValue();
                    } else {
                        song.defaultBpm = Integer.parseInt(jv.isString().stringValue());
                    }
                    break;
                case "timeSignature":
                    //  most of this is coping with real old events with poor formatting
                    jn = jv.isNumber();
                    if (jn != null) {
                        song.beatsPerBar = (int) jn.doubleValue();
                        song.unitsPerMeasure = 4; //  safe default
                    } else {
                        String s = jv.isString().stringValue();

                    final RegExp timeSignatureExp = RegExp.compile("^\\w*(\\d{1,2})\\w*\\/\\w*(\\d)\\w*$");
                        MatchResult mr = timeSignatureExp.exec(s);
                        if (mr != null) {
                            // match
                            song.beatsPerBar = Integer.parseInt(mr.getGroup(1));
                            song.unitsPerMeasure = Integer.parseInt(mr.getGroup(2));
                        } else {
                            s = s.replaceAll("/.*", "");    //  fixme: info lost
                            if (s.length() > 0) {
                                song.beatsPerBar = Integer.parseInt(s);
                            }
                            song.unitsPerMeasure = 4; //  safe default
                        }
                    }
                    break;
                case "chords":
                    ja = jv.isArray();
                    if (ja != null) {
                        int jaLimit = ja.size();
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < jaLimit; i++) {
                            sb.append(ja.get(i).isString().stringValue());
                            sb.append("\n");
                        }
                        song.chords = sb.toString();
                    } else {
                        song.chords = jv.isString().stringValue();
                    }
                    song.parseChordTable(song.chords);
                    break;
                case "lyrics":
                    ja = jv.isArray();
                    if (ja != null) {
                        int jaLimit = ja.size();
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < jaLimit; i++) {
                            sb.append(ja.get(i).isString().stringValue());
                            sb.append("\n");
                        }
                        song.rawLyrics = sb.toString();
                    } else {
                        song.rawLyrics = jv.isString().stringValue();
                    }
                    song.parseLyricsToSectionSequence(song.rawLyrics);
                    break;
            }

        }
        return song;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n")
                .append("\"title\": \"")
                .append(JsonUtil.encode(getTitle()))
                .append("\",\n")
                .append("\"artist\": \"")
                .append(JsonUtil.encode(getArtist()))
                .append("\",\n")
                .append("\"copyright\": \"")
                .append(JsonUtil.encode(getCopyright()))
                .append("\",\n")
                .append("\"key\": \"")
                .append(key.name())
                .append("\",\n")
                .append("\"defaultBpm\": ")
                .append(getDefaultBpm())
                .append(",\n")
                .append("\"timeSignature\": \"")
                .append(getBeatsPerBar())
                .append("/")
                .append(getUnitsPerMeasure())
                .append("\",\n")
                .append("\"chords\": \n")
                .append("    [\n");

        //  chord content
        boolean first = true;
        for (String s : getChordsAsString().split("\n")) {
            if (first) {
                first = false;
            } else {
                sb.append(",\n");
            }
            sb.append("\t\"");
            sb.append(JsonUtil.encode(s));
            sb.append("\"");
        }
        sb.append("\n    ],\n")
                .append("\"lyrics\": \n")
                .append("    [\n");
        //  lyrics content
        first = true;
        for (String s : getLyricsAsString().split("\n")) {
            if (first) {
                first = false;
            } else {
                sb.append(",\n");
            }
            sb.append("\t\"");
            sb.append(JsonUtil.encode(s));
            sb.append("\"");
        }
        sb.append("\n    ]\n")
                .append("}\n");

        return sb.toString();
    }

    private void parseLyricsToSectionSequence(String rawLyrics) {
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

    public String[][] getJsChordSection(String sectionId) {
        return jsChordSectionMap.get(sectionId);
    }

    public Grid<String> getChordSection(Section.Version sv) {
        return chordSectionMap.get(sv);
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

    private void parseChordTable(String rawChordTableText) {

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

            //  deal with unformatted events
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
        final String style = "com-bsteele-bsteeleMusicApp-client-resources-AppResources-Style-";
        String tableStart = "<table id=\"lyricsTable\">\n"
                + "<colgroup>\n"
                + "   <col style=\"width:2ch;\">\n"
                + "  <col>\n"
                + "</colgroup>\n";
        String rowStart = "<tr><td class='" + style + "sectionLabel' >";
        String rowEnd = "</td></tr>\n";
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
                                + "</td><td class=\"" + style + "lyrics" + version.getSection().getAbreviation() + "Class\""
                                + " id=\"" + genLyicsId(sectionIndex) + "\">";
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
                                whiteSpace = ""; //  ignore trailing white space
                                state = 0;
                                break;
                            default:
                                if (!isSection) {
                                    //  deal with bad formatting
                                    lyrics += rowStart
                                            + Section.getDefaultVersion().toString() + ":"
                                            + "</td><td class=\"" + style + "lyrics" + Section.getDefaultVersion().toString() + "Class\""
                                            + " id=\"" + genLyicsId(sectionIndex) + "\">";
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

    public static final String genLyicsId(int sectionIndex) {
        return "L." + sectionIndex;
    }

    private String generateHtmlChordTableFromMap(HashMap<Section.Version, Grid<String>> map) {

        if (map.isEmpty()) {
            return "";
        }

        final String style = "com-bsteele-bsteeleMusicApp-client-resources-AppResources-Style-";
        String tableStart = "<table id=\"chordTable\" "
                + "class=\"" + style + "chordTable\" "
                + ">\n";
        String sectionStart = "<tr><td class=\"" + style + "sectionLabel\" >";
        String rowStart = "\t<tr><td class=\"" + style + "sectionLabel\" ></td>";
        String rowEnd = "</tr>\n";
        String tableEnd = "</table>\n";

        StringBuilder chordText = new StringBuilder(); //  table formatted

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

                chordText.append(start);
                start = rowStart;   //  default to empty row start on subsequent rows

                ArrayList<String> row = grid.getRow(r);
                final RegExp endOfChordLineExp = RegExp.compile("^\\w*(x|\\|)", "i");
                for (int col = 0; col < row.size(); col++) {
                    chordText.append("<td class=\"" + style + "section").append(version.getSection().getAbreviation())
                            .append("Class\" ");
                    String content = row.get(col);
                    if (endOfChordLineExp.test(content)) {
                        chordText.append(" style=\"border-right: 0px solid black;\"");
                    }
                    chordText.append(" id=\"")
                            .append(genChordId(displayVersion, r, col))
                            .append("\" >")
                            .append(content).append("</td>\n\t");
                }
                chordText.append(rowEnd);
            }
        }
        String ret = tableStart + chordText + tableEnd;
        return ret;
    }

    public static final String genChordId(Section.Version displaySectionVersion, int row, int col) {
        if (displaySectionVersion == null)
            return "";
        return "C." + displaySectionVersion.toString() + '.' + row + '.' + col;
    }

    /**
     * @param halfSteps
     * @return
     */
    public String transpose(int halfSteps) {
        halfSteps = Util.mod(halfSteps,MusicConstant.halfStepsPerOctave);
        if (halfSteps == 0) {
            return generateHtmlChordTable();
        }

        //GWT.log("Song.transpose()  here: " + halfSteps + " to: " + chords);
        HashMap<Section.Version, Grid<String>> tranMap = deepCopy(chordSectionMap);

        for (Section.Version version : tranMap.keySet()) {
            Grid<String> grid = tranMap.get(version);

            TreeSet<String> transChords = new TreeSet<>();
            int rLimit = grid.getRowCount();
            for (int r = 0; r < rLimit; r++) {
                ArrayList<String> row = grid.getRow(r);
                int colLimit = row.size();
                for (int col = 0; col < colLimit; col++) {
                    grid.set(col, r, transposeMeasure(transChords, row.get(col), halfSteps));
                }
            }
            //GWT.log(transChords.toString());
        }

        //GWT.log(tranMap.toString());
        return generateHtmlChordTableFromMap(tranMap);
    }

    private String transposeMeasure(TreeSet<String> chords, String m, int halfSteps) {

        int chordNumber = 0;
        String chordLetter;
        String sout = "";

        int state = 0;

        for (int ci = 0; ci < m.length(); ci++) {
            char c = m.charAt(ci);
            switch (state) {
                case 1:    //  chord symbol modifier, one character only
                    state = 0;
                    if (c == 'b' || c == js_flat) {
                        chordNumber -= 1;
                        chordLetter = chordNumberToLetter(chordNumber, halfSteps);
                        sout += chordLetter;
                        chords.add(chordLetter);
                        break;
                    }
                    if (c == '#' || c == js_sharp) {
                        chordNumber += 1;
                        chordLetter = chordNumberToLetter(chordNumber, halfSteps);
                        sout += chordLetter;
                        chords.add(chordLetter);
                        break;
                    }
                    if (c == js_natural) {
                        chordLetter = chordNumberToLetter(chordNumber, halfSteps);
                        sout += chordLetter;
                        chords.add(chordLetter);
                        break;
                    }
                    chordLetter = chordNumberToLetter(chordNumber, halfSteps);
                    sout += chordLetter;
                    chords.add(chordLetter);
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
                    } else {    //  don't parse the rest
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
            chordLetter = chordNumberToLetter(chordNumber, halfSteps);
            sout += chordLetter;
            chords.add(chordLetter);
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

    private String chordNumberToLetter(int n, int halfSteps) {
        return key.getScaleNoteByHalfStep(n+halfSteps).toString();
    }

    private void setTitle(String title) {
        //  move the leading "The " to the end

       final RegExp theRegExp = RegExp.compile("^the *", "i");
        if (theRegExp.test(title)) {
            title = theRegExp.replace(title, "") + ", The";
        }
        this.title = title;
        songId = "Song" + title.replaceAll("\\W+", "");
    }

    private void setArtist(String artist) {
        //  move the leading "The " to the end
        final RegExp theRegExp = RegExp.compile("^the *", "i");
        if (theRegExp.test(artist)) {
            artist = theRegExp.replace(artist, "") + ", The";
        }
        this.artist = artist;
    }

    public void setKey(Key key) {
        this.key = key;
    }


    /**
     * @return the defaultBpm
     */
    public int getBeatsPerMinute() {
        return defaultBpm;
    }

    /**
     * @param bpm the defaultBpm to set
     */
    public void setBeatsPerMinute(int bpm) {
        this.defaultBpm = bpm;
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
     * @return the unitsPerMeasure
     */
    public int getUnitsPerMeasure() {
        return unitsPerMeasure;
    }

    /**
     * @return the copyright
     */
    public String getCopyright() {
        return copyright;
    }


    public Key getKey() {
        return key;
    }

    /**
     * @return the songId
     */
    public String getSongId() {
        return songId;
    }

    /**
     * @return the chordLetterToNumber
     */
    public static int[] getChordLetterToNumber() {
        return chordLetterToNumber;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * @return the rawLyrics
     */
    public String getLyricsAsString() {
        return rawLyrics;
    }

    /**
     * @return the chords
     */
    public String getChordsAsString() {
        return chords;
    }

    /**
     * @return the defaultBpm
     */
    public int getDefaultBpm() {
        return defaultBpm;
    }

    /**
     * @return the sequence
     */
    public ArrayList<Section.Version> getSequence() {
        return sequence;
    }

    /**
     * @return the chordSectionMap
     */
    public HashMap<Section.Version, Grid<String>> getChordSectionMap() {
        return chordSectionMap;
    }

    /**
     * @return the displaySectionMap
     */
    public HashMap<Section.Version, Section.Version> getDisplaySectionMap() {
        return displaySectionMap;
    }

    @Override
    public int compareTo(Song o) {
        int ret = getTitle().compareTo(o.getTitle());
        if (ret != 0) {
            return ret;
        }
        ret = getArtist().compareTo(o.getArtist());
        if (ret != 0) {
            return ret;
        }

//    //  more?  if so, changes in lyrics will be a new "song"
//    ret = getLyricsAsString().compareTo(o.getLyricsAsString());
//    if (ret != 0) {
//      return ret;
//    }
//    ret = getChordsAsString().compareTo(o.getChordsAsString());
//    if (ret != 0) {
//      return ret;
//    }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Song) {
            return compareTo((Song) obj) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = (79 * hash + Objects.hashCode(this.title)) % (1 << 31);
        hash = (79 * hash + Objects.hashCode(this.artist)) % (1 << 31);
        return hash;
    }

    private String title = "Unknown";
    private String songId = "Unknown";
    private String artist = "Unknown";
    private String copyright = "Unknown";
    private Key key = Key.C;  //  default
    private int defaultBpm = 106;  //  beats per minute
    private int beatsPerBar = 4;  //  beats per bar, i.e. timeSignature
    private int unitsPerMeasure = 4;//  units per measure, i.e. timeSignature
    private String rawLyrics = "";
    private String chords = "";

    private ArrayList<Section.Version> sequence;
    private final HashMap<Section.Version, Grid<String>> chordSectionMap = new HashMap<>();
    private final HashMap<Section.Version, Section.Version> displaySectionMap = new HashMap<>();
    private final HashMap<String, String[][]> jsChordSectionMap = new HashMap<>();
    private static final char flat = (char) 9837;
    private static final char sharp = (char) 9839;
    private static final String chordNumberToLetterSharps[] = new String[]{
            "A", "A" + sharp, "B", "C", "C" + sharp, "D", "D" + sharp, "E", "F", "F" + sharp, "G", "G" + sharp};
    private static final String chordNumberToLetterFlats[] = new String[]{
            "A", "B" + flat, "B", "C", "D" + flat, "D", "E" + flat, "E", "F", "G" + flat, "G", "A" + flat};
    private static final char js_flat = '\u266D';
    private static final char js_natural = '\u266E';
    private static final char js_sharp = '\u266F';
    private static final char js_delta = '\u0394';
}
