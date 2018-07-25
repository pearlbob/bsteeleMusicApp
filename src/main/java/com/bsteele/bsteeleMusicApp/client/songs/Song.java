/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.Grid;
import com.bsteele.bsteeleMusicApp.client.legacy.LegacyDrumSection;
import com.bsteele.bsteeleMusicApp.client.util.CssConstants;
import com.bsteele.bsteeleMusicApp.shared.JsonUtil;
import com.bsteele.bsteeleMusicApp.shared.Util;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsDate;
import com.google.gwt.json.client.*;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.FlexTable;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author bob
 */

/**
 * A piece of music to be played according to the structure it contains.
 */
public class Song implements Comparable<Song>
{

    /**
     * Not to be used externally but must remain public due to GWT constraints!
     */
    @Deprecated
    public Song()
    {
        setTitle("");
        setArtist("");
        copyright = "";
        setKey(Key.C);
        unitsPerMeasure = 4;
        rawLyrics = "";
        chords = "";
        parseChordTable(chords);
        parseLyricsToSectionSequence("");
        parseLyrics();
        setBeatsPerMinute(100);
        setBeatsPerBar(4);
    }

    /**
     * Create a minimal song to be used internally as a place holder.
     *
     * @return a minimal song
     */
    public static final Song createEmptySong()
    {
        return createSong("", "",
                "", Key.C, 100, 4, 4,
                "", "");
    }

    /**
     * A convenience constructor used to enforce the minimum requirements for a song.
     *
     * @param title
     * @param artist
     * @param copyright
     * @param key
     * @param bpm
     * @param beatsPerBar
     * @param unitsPerMeasure
     * @param chords
     * @param lyrics
     * @return
     */
    public static final Song createSong(@NotNull String title, @NotNull String artist,
                                        @NotNull String copyright,
                                        @NotNull Key key, int bpm, int beatsPerBar, int unitsPerMeasure,
                                        @NotNull String chords, @NotNull String lyrics)
    {
        Song song = new Song();
        song.setTitle(title);
        song.setArtist(artist);
        song.copyright = copyright;
        song.setKey(key);
        song.unitsPerMeasure = unitsPerMeasure;
        song.rawLyrics = lyrics;
        song.chords = chords;

        song.parseChordTable(chords);
        song.parseLyricsToSectionSequence(lyrics);
        song.parseLyrics();
        song.setBeatsPerMinute(bpm);
        song.setBeatsPerBar(beatsPerBar);
        song.parse();

        return song;
    }

    /**
     * Copy the song to a new instance.
     *
     * @return the new song
     */
    public final Song copySong()
    {
        Song ret = createSong(getTitle(), getArtist(),
                getCopyright(), getKey(), getBeatsPerMinute(), getBeatsPerBar(), getUnitsPerMeasure(),
                getChordsAsString(), getLyricsAsString());
        ret.setFileName(getFileName());
        ret.setLastModifiedDate(getLastModifiedDate());
        ret.duration = duration;
        ret.totalBeats = totalBeats;
        return ret;
    }

    /**
     * Parse a song from a JSON string.
     *
     * @param jsonString
     * @return the song. Can be null.
     */
    public static final ArrayList<Song> fromJson(String jsonString)
    {
        ArrayList<Song> ret = new ArrayList<>();
        if (jsonString == null || jsonString.length() <= 0) {
            return ret;
        }

        JSONValue jv = JSONParser.parseStrict(jsonString);
        if (jv == null) {
            return ret;
        }

        JSONArray ja = jv.isArray();
        if (ja != null) {
            int jaLimit = ja.size();
            for (int i = 0; i < jaLimit; i++) {
                ret.add(Song.fromJsonObject(ja.get(i).isObject()));
            }
        } else {
            JSONObject jo = jv.isObject();
            ret.add(fromJsonObject(jo));
        }

        return ret;

    }

    /**
     * Parse a song from a JSON object.
     *
     * @param jsonObject
     * @return the song. Can be null.
     */
    public static final Song fromJsonObject(JSONObject jsonObject)
    {
        if (jsonObject == null) {
            return null;
        }
        //  file information available
        if (jsonObject.keySet().contains("file"))
            return songFromJsonFileObject(jsonObject);

        //  straight song
        return songFromJsonObject(jsonObject);
    }

    private static final Song songFromJsonFileObject(JSONObject jsonObject)
    {
        Song song = null;
        double lastModifiedDate = 0;
        String fileName = null;

        JSONNumber jn;
        for (String name : jsonObject.keySet()) {
            JSONValue jv = jsonObject.get(name);
            switch (name) {
                case "song":
                    song = songFromJsonObject(jv.isObject());
                    break;
                case "lastModifiedDate":
                    jn = jv.isNumber();
                    if (jn != null) {
                        lastModifiedDate = jn.doubleValue();
                    }
                    break;
                case "file":
                    fileName = jv.isString().stringValue();
                    break;
            }
        }
        if (song == null)
            return null;

        if (lastModifiedDate > 0)
            song.setLastModifiedDate(JsDate.create(lastModifiedDate));
        song.setFileName(fileName);

        return song;
    }

    private static final Song songFromJsonObject(JSONObject jsonObject)
    {
        Song song = Song.createEmptySong();
        JSONNumber jn;
        JSONArray ja;
        for (String name : jsonObject.keySet()) {
            JSONValue jv = jsonObject.get(name);
            switch (name) {
                case "title":
                    song.setTitle(jv.isString().stringValue());
                    //GWT.log("reading: " + song.getTitle());
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
                            // parse
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
                    song.parseLyrics();
                    break;
            }
        }
        song.parse();
        return song;
    }

    public static final String toJson(Collection<Song> songs)
    {
        if (songs == null || songs.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        boolean first = true;
        for (Song song : songs) {
            if (first)
                first = false;
            else
                sb.append(",\n");
            sb.append(song.toJsonAsFile());
        }
        sb.append("]\n");
        return sb.toString();
    }

    private String toJsonAsFile()
    {
        double time = lastModifiedDate == null ? 0 : lastModifiedDate.getTime();
        return "{ \"file\": \"" + JsonUtil.encode(getFileName()) + "\", \"lastModifiedDate\": " + time + ", \"song\":" +
                " \n"
                + toJson()
                + "}";
    }

    /**
     * Generate the JSON expression of this song.
     *
     * @return the JSON expression of this song
     */
    public final String toJson()
    {
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

    @Deprecated
    private void parseLyricsToSectionSequence(String rawLyrics)
    {
        sequence = Section.matchAll(rawLyrics);

        if (sequence.isEmpty()) {
            sequence.add(Section.getDefaultVersion());
        }
        //GWT.log(sequence.toString());
    }

    private void computeSongMoments()
    {
        songMoments = new ArrayList<>();

        if (lyricSections == null)
            return;

        for (LyricSection lyricSection : lyricSections) {
            ChordSection chordSection = findChordSection(lyricSection);
            if (chordSection != null) {
                ArrayList<MeasureSequenceItem> chordMeasureSequenceItems = chordSection.getMeasureSequenceItems();
                if (chordMeasureSequenceItems != null)
                    for (MeasureSequenceItem measureSequenceItem : chordMeasureSequenceItems) {
                        if (measureSequenceItem.isRepeat()) {
                            MeasureRepeat measureRepeat = (MeasureRepeat) measureSequenceItem;
                            int limit = measureRepeat.getRepeats();
                            for (int repeat = 0; repeat < limit; repeat++) {
                                ArrayList<Measure> measures = measureRepeat.getMeasures();
                                if (measures != null)
                                    for (Measure measure : measures) {
                                        songMoments.add(new SongMoment(
                                                songMoments.size(),  //  size prior to add
                                                lyricSection, measureSequenceItem,
                                                measure, repeat, limit));
                                    }
                            }
                        } else {
                            ArrayList<Measure> measures = measureSequenceItem.getMeasures();
                            if (measures != null)
                                for (Measure measure : measures) {
                                    songMoments.add(new SongMoment(
                                            songMoments.size(),  //  size prior to add
                                            lyricSection, measureSequenceItem,
                                            measure, 0, 0));
                                }
                        }
                    }
            }
        }

        //  debug
        if (false) {
            GWT.log(getSongId().toString());
            for (int i = 0; i < songMoments.size(); i++) {
                SongMoment songMoment = songMoments.get(i);
                GWT.log(songMoment.getSequenceNumber() + ": "
                        + " (" + songMoment.getRepeat() + "/" + songMoment.getRepeatMax() + ") "
                        + songMoment.getMeasure().toString()
                );
            }
        }
    }

    private ChordSection findChordSection(LyricSection lyricSection)
    {
        for (ChordSection chordSection : chordSections) {
            if (lyricSection.getSectionVersion().equals(chordSection.getSectionVersion())) {
                return chordSection;
            }
        }
        return null;
    }

    private void computeDuration()
    {  //  fixme: account for repeats!!!!!!!!!!!!!!!!!!!

        duration = 0;
        totalBeats = 0;
        if (beatsPerBar == 0 || defaultBpm == 0 || sequence == null)
            return;
        if (sequence.isEmpty())
            return;
        if (chordSectionInnerHtmlMap.isEmpty())
            return;

        double measureDuration = beatsPerBar * 60.0 / defaultBpm;
        final RegExp repeatExp = RegExp.compile("^\\w*x(\\d+)", "i");
        for (SectionVersion sectionVersion : sequence) {
            Grid<String> sectionGrid = chordSectionInnerHtmlMap.get(sectionVersion);
            if (sectionGrid == null)
                continue;

            for (int row = 0; row < sectionGrid.getRowCount(); row++) {
                ArrayList<String> rowCols = sectionGrid.getRow(row);
                if (rowCols == null)
                    continue;

                int rowBeats = 0;   //  fixme: not correct for multiline repeats !!!!
                int rowDuration = 0;   //  fixme: not correct for multiline repeats !!!!!!!!
                for (int col = 0; col < rowCols.size(); col++) {
                    //  fixme: verify it's a real measure, not | or comment
                    duration += measureDuration;
                    totalBeats += beatsPerBar;
                    rowDuration += measureDuration;
                    rowBeats += beatsPerBar;

                    //  extra for repeats
                    MatchResult mr = repeatExp.exec(rowCols.get(col));
                    if (mr != null) {
                        int n = Integer.parseInt(mr.getGroup(1));
                        n--;
                        duration += n * rowDuration;
                        totalBeats += n * rowBeats;
                    }
                }
            }
        }
    }

    /**
     * Legacy prep of section sequence for javascript
     *
     * @return
     */
    @Deprecated
    public final String[] getSectionSequenceAsStrings()
    {

        //  dumb down the section sequence list for javascript
        String ret[] = new String[sequence.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = sequence.get(i).toString();
        }
        return ret;
    }

    @Deprecated
    public final Grid<String> getChordSectionStrings(SectionVersion sv)
    {
        return chordSectionInnerHtmlMap.get(sv);
    }

    public final ChordSection getChordSection(SectionVersion sv)
    {
        for (ChordSection chordSection : chordSections)
            if (chordSection.getSectionVersion().equals(sv))
                return chordSection;
        return null;
    }

    private final void parse()
    {
        measureNodes = new ArrayList<>();
        chordSections = new TreeSet<>();

        if (chords != null) {
            String s = chords;
            ChordSection chordSection;
            while ((chordSection = ChordSection.parse(s, beatsPerBar)) != null) {
                s = s.substring(chordSection.getParseLength());
                measureNodes.add(chordSection);
                chordSections.add(chordSection);
            }
        }

        //  fixme: temp transform from chordSections to chordSectionInnerHtmlMap
        chordSectionInnerHtmlMap.clear();
        for (ChordSection chordSection : chordSections) {
            //GWT.log(chordSection.toString());

            //  build the chord section map value
            Grid<String> grid = new Grid<>();
            int row = 0;
            int col = 0;

            for (String s : chordSection.generateInnerHtml(key, 0, false))
                switch (s) {
                    case "\n":
                    case "|":
                        if (col == 0)
                            break;
                        row++;
                        col = 0;
                        break;
//                        case "":
//                            break;
                    default:
                        grid.addTo(col++, row, s);
                        break;
                }

            SectionVersion version = chordSection.getSectionVersion();

            chordSectionInnerHtmlMap.put(version, grid);
        }

        computeSongMoments();
        computeDuration();
    }

    public Grid<MeasureNode> getStructuralGrid()
    {
        Grid<MeasureNode> ret = new Grid<>();

        for (ChordSection chordSection : chordSections) {
            chordSection.addToGrid(ret, chordSection);
        }

        return ret;
    }

    public String getStructuralGridAsText()
    {
        StringBuilder sb = new StringBuilder();
        Grid<MeasureNode> grid = getStructuralGrid();
        int rowCount = grid.getRowCount();
        MeasureNode lastChordSection = null;
        for (int r = 0; r < rowCount; r++) {
            ArrayList<MeasureNode> row = grid.getRow(r);
            int colCount = row.size();
            for (int c = 0; c < colCount; c++) {
                MeasureNode mn = row.get(c);
                if (c == 0) {
                    sb.append("\n");
                    if (!mn.equals(lastChordSection))
                        sb.append(mn.toText()).append("\n");
                    lastChordSection = mn;
                    continue;
                }
                sb.append(" ").append(mn.toString());
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    private SectionVersion getStructuralGridSectionVersionAtRow(Grid<MeasureNode> grid, int row)
    {
        int rowCount = grid.getRowCount();
        if (row >= rowCount)
            return null;

        MeasureNode measureNode = grid.getRow(row).get(0);
        if (measureNode instanceof ChordSection) {
            return ((ChordSection) measureNode).getSectionVersion();
        }
        return null;
    }

    public MeasureNode getStructuralMeasureNode(SongChordGridSelection songChordGridSelection)
    {
        if (songChordGridSelection == null)
            return null;
        return getStructuralMeasureNode(songChordGridSelection.getRow(), songChordGridSelection.getCol());
    }

    public MeasureNode getStructuralMeasureNode(int r, int c)
    {
        try {
            Grid<MeasureNode> grid = getStructuralGrid();   //  fixme: cache?
            ArrayList<MeasureNode> row = grid.getRow(r);
            if (row == null)
                return null;
            return row.get(c);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

    public final String measureNodesToString()
    {
        StringBuilder sb = new StringBuilder();

        for (MeasureNode measureNode : measureNodes) {
            sb.append(measureNode.toString()).append(" ");
        }

        return sb.toString();
    }

    public boolean addSectionVersion(SectionVersion sectionVersion)
    {
        if (sectionVersion == null)
            return false;
        for (ChordSection chordSection : chordSections) {
            if (chordSection.getSectionVersion().equals(sectionVersion))
                return false;
        }
        chordSections.add(new ChordSection(sectionVersion));
        return true;
    }

    public boolean measureEdit(@Nonnull MeasureNode refMeasureNode,
                               @Nonnull MeasureSequenceItem.EditLocation editLocation,
                               @Nonnull Measure measure)
    {
        ChordSection chordSection = findChordSection(refMeasureNode);
        if (chordSection == null)
            return false;

        MeasureSequenceItem measureSequenceItem = findMeasureSequenceItem(chordSection, refMeasureNode);
        if (measureSequenceItem == null)
            return false;

        switch (editLocation) {
            case insert:
                return measureSequenceItem.insert(refMeasureNode, measure);
            case replace:
                return measureSequenceItem.replace(refMeasureNode, measure);
            case append:
                return measureSequenceItem.append(refMeasureNode, measure);
        }
        return false;
    }


    public MeasureSequenceItem findMeasureSequenceItem(Measure measure)
    {
        if (measure == null)
            return null;

        ChordSection chordSection = findChordSection(measure);
        if (chordSection == null)
            return null;
        for (MeasureSequenceItem msi : chordSection.getMeasureSequenceItems()) {
            for (Measure m : msi.getMeasures())
                if (m == measure)
                    return msi;
        }
        return null;
    }

    private MeasureSequenceItem findMeasureSequenceItem(ChordSection chordSection, MeasureNode measureNode)
    {
        if (chordSection.getMeasureSequenceItems().isEmpty()) {
            MeasureSequenceItem ret = new MeasureSequenceItem(new ArrayList<>());
            chordSection.getMeasureSequenceItems().add(ret);
            return ret;
        }
        for (MeasureSequenceItem msi : chordSection.getMeasureSequenceItems()) {
            for (Measure measure : msi.getMeasures())
                if (measure == measureNode)
                    return msi;
        }
        return null;
    }


    private ChordSection findChordSection(MeasureNode measureNode)
    {
        for (ChordSection chordSection : chordSections) {
            if (measureNode == chordSection)
                return chordSection;
            for (MeasureSequenceItem measureSequenceItem : chordSection.getMeasureSequenceItems())
                if (measureSequenceItem == measureNode)
                    return chordSection;
            MeasureNode mn = findMeasureSequenceItem(chordSection, measureNode);
            if (mn != null)
                return chordSection;
        }
        return null;
    }

    public SongChordGridSelection findMeasureChordGridLocation(Measure measure)
    {
        Grid<MeasureNode> grid = getStructuralGrid();
        int rowCount = grid.getRowCount();
        for (int r = 0; r < rowCount; r++) {
            ArrayList<MeasureNode> row = grid.getRow(r);
            int colCount = row.size();
            for (int c = 0; c < colCount; c++) {
                MeasureNode mn = row.get(c);
                if (mn == measure) {
                    return new SongChordGridSelection(r, c);
                }
            }
        }

        return null;
    }

    public SongChordGridSelection findSectionVersionChordGridLocation(SectionVersion sectionVersion)
    {
        Grid<MeasureNode> grid = getStructuralGrid();
        int rowCount = grid.getRowCount();
        for (int r = 0; r < rowCount; r++) {
            ArrayList<MeasureNode> row = grid.getRow(r);
            if (row.size() > 0) {
                MeasureNode mn = row.get(0);
                if ( mn instanceof ChordSection
                        && sectionVersion.equals(((ChordSection) mn).getSectionVersion())) {
                    return new SongChordGridSelection(r, 0);
                }
            }
        }

        return null;
    }

    public Measure findMeasure(SongChordGridSelection songChordGridSelection)
    {
        if (songChordGridSelection == null)
            return null;
        Grid<MeasureNode> grid = getStructuralGrid();
        MeasureNode measureNode = grid.get(songChordGridSelection.getCol(), songChordGridSelection.getRow()); //   x,y!
        if (measureNode != null && measureNode instanceof Measure)
            return ((Measure) measureNode);
        return null;
    }

    public boolean measureDelete(Measure measure)
    {
        if (measure == null)
            return false;

        for (ChordSection chordSection : chordSections) {
            for (MeasureSequenceItem msi : chordSection.getMeasureSequenceItems()) {
                for (int i = 0; i < msi.getMeasures().size(); i++) {
                    Measure m = msi.getMeasures().get(i);
                    if (m == measure)
                        return msi.remove(i);
                }
            }
        }
        return false;
    }

    @Deprecated
    private void parseChordTable(String rawChordTableText)
    {

        chordSectionInnerHtmlMap.clear();

        if (rawChordTableText != null && rawChordTableText.length() > 0) {
            //  repair definitions without a final newline
            if (rawChordTableText.charAt(rawChordTableText.length() - 1) != '\n')
                rawChordTableText += "\n";

            //  build the initial chord section map
            Grid<String> grid = new Grid<>();
            int row = 0;
            int col = 0;

            int state = 0;
            SectionVersion version;
            TreeSet<SectionVersion> versionsDeclared = new TreeSet<>();
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
                        SectionVersion v = Section.parse(token.substring(0, Math.min(token.length() - 1, 11)));
                        if (v != null) {
                            version = v;
                            i += version.getParseLength() - 1;//   consume the section label

                            if (!versionsDeclared.isEmpty() && !grid.isEmpty()) {
                                for (SectionVersion vd : versionsDeclared) {
                                    chordSectionInnerHtmlMap.put(vd, grid);
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
                            continue;
                        }
                        state++;

                        //  fall through
                    case 2:
                        //  absorb characters until newline
                        switch (c) {
                            case ' ':
                            case '\t':
                                if (block.length() > 0) {
                                    grid.addTo(col, row, block);
                                    col++;
                                }
                                block = "";
                                break;
                            case '\n':
                            case '\r':
                                if (block.length() > 0) {
                                    grid.addTo(col, row, block);
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

                        break;
                }
            }

            //  put the last grid on the end
            if (!versionsDeclared.isEmpty() && !grid.isEmpty()) {
                for (SectionVersion vd : versionsDeclared) {
                    chordSectionInnerHtmlMap.put(vd, grid);
                }
            }

            //  deal with unformatted events
            if (chordSectionInnerHtmlMap.isEmpty()) {
                chordSectionInnerHtmlMap.put(Section.getDefaultVersion(), grid);
            }
        }

        //  collect remap sections with identical declarations
        {
            //  build a reverse lookup map
            HashMap<Grid<String>, TreeSet<SectionVersion>> reverseMap = new HashMap<>();
            for (SectionVersion version : chordSectionInnerHtmlMap.keySet()) {
                Grid<String> grid = chordSectionInnerHtmlMap.get(version);
                TreeSet<SectionVersion> lookup = reverseMap.get(grid);
                if (lookup == null) {
                    TreeSet<SectionVersion> ts = new TreeSet<>();
                    ts.add(version);
                    reverseMap.put(grid, ts);
                } else {
                    lookup.add(version);
                }
            }
            //  build version mapping to version displayed map
            displaySectionMap.clear();
            for (Grid<String> g : reverseMap.keySet()) {
                TreeSet<SectionVersion> mappedVersions = reverseMap.get(g);
                SectionVersion first = mappedVersions.first();
                for (SectionVersion dv : mappedVersions) {
                    //  more convenient to put idenity mapping in for first
                    displaySectionMap.put(dv, first);
                }
            }

            //GWT.log(displayMap.toString());
            HashMap<SectionVersion, Grid<String>> reducedChordSectionMap = new HashMap<>();
            for (SectionVersion version : chordSectionInnerHtmlMap.keySet()) {
                reducedChordSectionMap.put(version, chordSectionInnerHtmlMap.get(displaySectionMap.get(version)));
            }

            //  install the reduced map
            chordSectionInnerHtmlMap.clear();
            chordSectionInnerHtmlMap.putAll(reducedChordSectionMap);
            //GWT.log(chordSectionInnerHtmlMap.toString());
        }

//        for (Section.Version v : chordSectionInnerHtmlMap.keySet()) {
//            GWT.log(" ");
//            GWT.log(v.toString());
//            GWT.log("    " + chordSectionInnerHtmlMap.get(v).toString());
//        }

        computeDuration();
    }

    public String measureNodesToHtml(@NotNull String tableName, @NotNull Key key, int tran)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("<table id=\"" + tableName + "\" class=\"" + CssConstants.style + "chordTable\">\n");

        ArrayList<String> innerHtml = null;
        int sequenceNumber = 0;
        int rowStartSequenceNumber = 0;
        int j = 0;
        LyricSection lyricSection = null;
        SongMoment songMoment;
        for (int safety = 0; safety < 10000; safety++) {
            if (sequenceNumber >= songMoments.size())
                break;

            songMoment = songMoments.get(sequenceNumber);
            lyricSection = songMoment.getLyricSection();
            SectionVersion sectionVersion = lyricSection.getSectionVersion();
            sb.append("<tr><td class='" + CssConstants.style + "sectionLabel' >").
                    append(sectionVersion.toString()).append("</td><td class=\"")
                    .append(CssConstants.style)
                    .append("lyrics")
                    .append(sectionVersion.getSection().getAbbreviation())
                    .append("Class\"")
                    .append(" colspan=\"10\" >");

            for (LyricsLine lyricsLine : lyricSection.getLyricsLines())
                sb.append(lyricsLine.getLyrics()).append("\n");

            sb.append("</td></tr><tr><td></td>");

            for (ChordSection chordSection : chordSections) {
                if (chordSection.getSectionVersion().equals(lyricSection.getSectionVersion())) {
                    innerHtml = chordSection.generateInnerHtml(key, tran, true);
                    rowStartSequenceNumber = sequenceNumber;
                    j = 0;
                    break;
                }
            }

            //  exhaust the html we've been given
            while (innerHtml != null) {
                String s = innerHtml.get(j++);
                if (j >= innerHtml.size()) {
                    j = 0;
                    innerHtml = null;
                }

                switch (s) {
                    case "":
                        sb.append("<td></td>");
                        break;
                    case "|":
                        sb.append("<td>").append(s).append("</td></tr>\n<tr>");
                        break;
                    case "\n":
                        sb.append("</tr>\n<tr><td></td>");
                        if (rowStartSequenceNumber < sequenceNumber) {
                            sb.append("<td colspan=\"10\">" +
                                    "<canvas id=\"bassLine" + rowStartSequenceNumber + "-" + (sequenceNumber - 1)
                                    + "\" width=\"900\" height=\"150\" style=\"border:1px solid #000000;\"/></tr>");
                            rowStartSequenceNumber = sequenceNumber;
                        }
                        break;
                    default:
                        //  increment for next time
                        sequenceNumber++;
                        if (sequenceNumber < songMoments.size())
                            songMoment = songMoments.get(sequenceNumber);
                        break;
                }
            }
            sb.append("</tr>\n");
        }
        sb.append("</table>\n");

        return sb.toString();
    }


    public final String generateHtmlLyricsTable(String prefix)
    {
        String tableStart = "<table id=\"" + prefix + "LyricsTable\">\n"
                + "<colgroup>\n"
                + "   <col style=\"width:2ch;\">\n"
                + "  <col>\n"
                + "</colgroup>\n";
        String rowStart = "<tr><td class='" + CssConstants.style + "sectionLabel' >";
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
                    SectionVersion version = Section.parse(rawLyrics.substring(i, i + 11));
                    if (version != null) {
                        i += version.getParseLength() - 1; //  skip the end of the section id
                        isSection = true;

                        if (lyrics.length() > 0) {
                            lyrics += rowEnd;
                        }
                        lyrics += rowStart + version.toString()
                                + "</td><td class=\"" + CssConstants.style + "lyrics" + version.getSection()
                                .getAbbreviation() + "Class\""
                                + " id=\"" + prefix + genLyricsId(sectionIndex) + "\">";
                        sectionIndex++;
                        whiteSpace = ""; //  ignore white space
                        state = 0;
                        continue;
                    }
                    state++;
                    //  fall through
                case 2:
                    //  absorb characters to newline
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
                                        + Section.getDefaultVersion().toString()
                                        + "</td><td class=\"" + CssConstants.style + "lyrics"
                                        + Section.getDefaultVersion().getSection().getAbbreviation() + "Class\""
                                        + " id=\"" + prefix + genLyricsId(sectionIndex) + "\">";
                                isSection = true;
                            }
                            lyrics += whiteSpace + c;
                            whiteSpace = "";
                            break;
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

    private final void parseLyrics()
    {
        int state = 0;
        String whiteSpace = "";
        String lyrics = "";
        LyricSection lyricSection = null;

        lyricSections = new ArrayList<>();

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
                    SectionVersion version = Section.parse(rawLyrics.substring(i, i + 11));
                    if (version != null) {
                        i += version.getParseLength() - 1; //  skip the end of the section id

                        if (lyricSection != null)
                            lyricSections.add(lyricSection);

                        lyricSection = new LyricSection();
                        lyricSection.setSectionVersion(version);

                        whiteSpace = ""; //  ignore white space
                        state = 0;
                        continue;
                    }
                    state++;
                    //  fall through
                case 2:
                    //  absorb all characters to newline
                    switch (c) {
                        case ' ':
                        case '\t':
                            whiteSpace += c;
                            break;
                        case '\n':
                        case '\r':
                            if (lyricSection == null) {
                                //  oops, an old unformatted song, force a lyrics section
                                lyricSection = new LyricSection();
                                lyricSection.setSectionVersion(Section.getDefaultVersion());
                            }
                            lyricSection.add(new LyricsLine(lyrics));
                            lyrics = "";
                            whiteSpace = ""; //  ignore trailing white space
                            state = 0;
                            break;
                        default:
                            lyrics += whiteSpace + c;
                            whiteSpace = "";
                            break;
                    }
                    break;
            }
        }
        //  last one is not terminated by another section
        if (lyricSection != null)
            lyricSections.add(lyricSection);
    }

    /**
     * Utility to generate a lyrics section sequence id
     *
     * @param sectionIndex
     * @return a lyrics section sequence id
     */
    public static final String genLyricsId(int sectionIndex)
    {
        return "L." + sectionIndex;
    }

    /**
     * Utility to generate a chord section id
     *
     * @param displaySectionVersion chord section version
     * @param row                   cell row
     * @param col                   cell column
     * @return the generated chord section id
     */
    public static final String genChordId(SectionVersion displaySectionVersion, int row, int col)
    {
        if (displaySectionVersion == null)
            return "";
        return "C." + displaySectionVersion.toString() + '.' + row + '.' + col;
    }

    public final Measure getMeasure(int row, int col)
    {
        // songMoments.equ
        return null;
    }

    public final void transpose(String prefix, FlexTable flexTable, int halfSteps, int fontSize)
    {
        transpose(getStructuralGrid(), prefix, flexTable, halfSteps, fontSize, false);
    }

    public final void transpose(ChordSection chordSection, String prefix, FlexTable flexTable, int halfSteps,
                                int fontSize, boolean append)
    {
        if (chordSection == null || flexTable == null || fontSize <= 0)
            return;
        Grid<MeasureNode> grid = new Grid<>();
        chordSection.addToGrid(grid, chordSection);
        transpose(grid, prefix, flexTable, halfSteps, fontSize, append);
    }

    public final void transpose(Grid<MeasureNode> grid, String prefix, FlexTable flexTable, int halfSteps,
                                int fontSize, boolean append)
    {
        halfSteps = Util.mod(halfSteps, MusicConstant.halfStepsPerOctave);

        Key newKey = key.nextKeyByHalfStep(halfSteps);

        int offset = 0;
        if (append)
            offset = flexTable.getRowCount();
        else
            flexTable.removeAllRows();
        flexTable.getFlexCellFormatter();
        FlexTable.FlexCellFormatter formatter = flexTable.getFlexCellFormatter();

        SectionVersion lastSectionVersion = null;
        int rLimit = grid.getRowCount();
        for (int r = 0; r < rLimit; r++) {
            formatter.addStyleName(r + offset, 0, CssConstants.style + "sectionLabel");

            ArrayList<MeasureNode> row = grid.getRow(r);
            int colLimit = row.size();
            String lastValue = "";
            for (int c = 0; c < colLimit; c++) {
                MeasureNode measureNode = row.get(c);
                SectionVersion sectionVersion = getStructuralGridSectionVersionAtRow(grid, r);

                String s = "";
                switch (c) {
                    case 0:
                        if (!sectionVersion.equals(lastSectionVersion))
                            s = sectionVersion.toString();
                        lastSectionVersion = sectionVersion;
                        break;
                    default:
                        s = measureNode.transpose(newKey, halfSteps);
                        break;
                }
                //  enforce the - on repeated measures
                if (c > 0 && c <= MusicConstant.measuresPerDisplayRow && s.equals(lastValue)) {
                    s = "-";
                    formatter.addStyleName(r + offset, c, CssConstants.style + "textCenter");
                } else
                    lastValue = s;

                flexTable.setHTML(r + offset, c,
                        "<span style=\"font-size: " + fontSize + "px;\">"
                                + s
                                + "</span>"
                );
                formatter.addStyleName(r + offset, c, CssConstants.style
                        + "section"
                        + sectionVersion.getSection().getAbbreviation()
                        + "Class");
                formatter.getElement(r + offset, c).setId(prefix + "." + measureNode.getHtmlBlockId() + "." + r + "."
                        + c);
            }
        }
    }

    private String transposeMeasure(Key newKey, String m, int halfSteps)
    {
        if (halfSteps == 0)
            return m;

        int chordNumber = 0;
        // String chordLetter;
        StringBuilder sb = new StringBuilder();

        int state = 0;

        for (int ci = 0; ci < m.length(); ci++) {
            char c = m.charAt(ci);
            switch (state) {
                default:
                case 0:
                    //  look for comments
                    if (c == '(') {
                        sb.append(c);
                        state = 11;
                        break;
                    }

                    //  don't generateHtml the section identifiers that happen to look like notes
                    String toMatch = m.substring(ci, Math.min(m.length() - ci + 1, Section.maxLength));
                    SectionVersion version = Section.parse(toMatch);
                    if (version != null) {
                        sb.append(version.toString());
                        ci += version.getParseLength() - 1; //  skip the end of the section id
                        break;
                    }

                    Chord chord = Chord.parse(toMatch, beatsPerBar);
                    if (chord != null) {
                        sb.append(chord.transpose(newKey, halfSteps).toString());
                        ci += chord.getParseLength() - 1; //     watch for the increment in the for loop!
                        break;
                    }

                    if (
                            (c >= '0' && c <= '9')
                                    || c == 'm'
                                    || c == ' ' || c == '-' || c == '|' || c == '/'
                                    || c == '[' || c == ']'
                                    || c == '{' || c == '}'
                                    || c == '.'
                                    || c == '<' || c == '>'
                                    || c == '\n'
                                    || c == js_delta)
                    {
                        sb.append(c);
                    } else {    //  don't parse the rest
                        sb.append(c);
                        state = 10;
                    }
                    break;

                case 10: //	wait for newline
                    sb.append(c);
                    break;

                case 11: //	wait for newline or closing paren
                    sb.append(c);
                    if (c == ')') {
                        state = 0;
                    }
                    break;
            }
        }
        //  do the last chord
//        if (state == 1) {
//            chordLetter = chordNumberToLetter(chordNumber, halfSteps);
//            sb.append(chordLetter);
//        }

        return sb.toString();
    }

    public final Song checkSong()
            throws ParseException
    {
        return checkSong(title, artist, copyright,
                key, Integer.toString(defaultBpm), Integer.toString(beatsPerBar), Integer.toString(unitsPerMeasure),
                chords, rawLyrics);
    }

    /**
     * Validate a song entry argument set
     *
     * @param title
     * @param artist
     * @param copyright
     * @param key
     * @param bpmEntry
     * @param beatsPerBarEntry
     * @param unitsPerMeasureEntry
     * @param chordsTextEntry
     * @param lyricsTextEntry
     * @return
     * @throws ParseException
     */
    public static final Song checkSong(String title, String artist, String copyright,
                                       Key key, String bpmEntry, String beatsPerBarEntry, String unitsPerMeasureEntry,
                                       String chordsTextEntry, String lyricsTextEntry)
            throws ParseException
    {
        if (title == null || title.length() <= 0) {
            throw new ParseException("no song title given!", 0);
        }

        if (artist == null || artist.length() <= 0) {
            throw new ParseException("no artist given!", 0);
        }

        if (copyright == null || copyright.length() <= 0) {
            throw new ParseException("no copyright given!", 0);
        }

        if (key == null)
            key = Key.C;  //  punt an error

        if (bpmEntry == null || bpmEntry.length() <= 0) {
            throw new ParseException("no BPM given!", 0);
        }

        //  check bpm
        final RegExp twoOrThreeDigitsRegexp = RegExp.compile("^\\d{2,3}$");
        if (!twoOrThreeDigitsRegexp.test(bpmEntry)) {
            throw new ParseException("BPM has to be a number from " + minBpm + " to " + maxBpm, 0);
        }
        int bpm = Integer.parseInt(bpmEntry);
        if (bpm < minBpm || bpm > maxBpm) {
            throw new ParseException("BPM has to be a number from " + minBpm + " to " + maxBpm, 0);
        }

        //  check beats per bar
        if (beatsPerBarEntry == null || beatsPerBarEntry.length() <= 0) {
            throw new ParseException("no beats per bar given!", 0);
        }
        final RegExp oneOrTwoDigitRegexp = RegExp.compile("^\\d{1,2}$");
        if (!oneOrTwoDigitRegexp.test(beatsPerBarEntry)) {
            throw new ParseException("Beats per bar has to be 2, 3, 4, 6, or 12", 0);
        }
        int beatsPerBar = Integer.parseInt(beatsPerBarEntry);
        switch (beatsPerBar) {
            case 2:
            case 3:
            case 4:
            case 6:
            case 12:
                break;
            default:
                throw new ParseException("Beats per bar has to be 2, 3, 4, 6, or 12", 0);
        }


        if (chordsTextEntry == null || chordsTextEntry.length() <= 0) {
            throw new ParseException("no chords given!", 0);
        }
        if (lyricsTextEntry == null || lyricsTextEntry.length() <= 0) {
            throw new ParseException("no lyrics given!", 0);
        }

        if (unitsPerMeasureEntry == null || unitsPerMeasureEntry.length() <= 0) {
            throw new ParseException("No units per measure given!", 0);
        }
        if (!oneOrTwoDigitRegexp.test(unitsPerMeasureEntry)) {
            throw new ParseException("Units per measure has to be 2, 4, or 8", 0);
        }
        int unitsPerMeasure = Integer.parseInt(unitsPerMeasureEntry);
        switch (unitsPerMeasure) {
            case 2:
            case 4:
            case 8:
                break;
            default:
                throw new ParseException("Units per measure has to be 2, 4, or 8", 0);
        }

        Song newSong = Song.createSong(title, artist,
                copyright, key, bpm, beatsPerBar, unitsPerMeasure,
                chordsTextEntry, lyricsTextEntry);

        //  see that all chord sections have a lyric section
        for (ChordSection chordSection : newSong.getChordSections()) {
            SectionVersion chordSectionVersion = chordSection.getSectionVersion();
            boolean found = false;
            for (LyricSection lyricSection : newSong.getLyricSections()) {
                if (chordSectionVersion.equals(lyricSection.getSectionVersion())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new ParseException("no use found for the declared chord section " + chordSectionVersion
                        .toString(), 0);
            }
        }

        //  see that all lyric sections have a chord section
        for (LyricSection lyricSection : newSong.getLyricSections()) {
            SectionVersion lyricSectionVersion = lyricSection.getSectionVersion();
            boolean found = false;
            for (ChordSection chordSection : newSong.getChordSections()) {
                if (lyricSectionVersion.equals(chordSection.getSectionVersion())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new ParseException("no chords found for the lyric section " + lyricSectionVersion.toString(), 0);
            }
        }

        //  outlaw comments in the chord section
        for (ChordSection chordSection : newSong.getChordSections()) {
            for (MeasureSequenceItem measureSequenceItem : chordSection.getMeasureSequenceItems()) {
                for (Measure measure : measureSequenceItem.getMeasures()) {
                    if (measure instanceof MeasureComment) {
                        throw new ParseException("Comments are not allowed in the chord section.  \""
                                + ((MeasureComment) measure).getComment() + "\" is considered a comment."
                                , 0);
                    }
                }
            }
        }

        //  an early song with default (no) structure?
        if (newSong.getLyricSections().size() == 1 && newSong.getLyricSections().get(0).getSectionVersion().equals
                (Section.getDefaultVersion()))
        {
            throw new ParseException("song looks too simple, is there really no structure?", 0);
        }

        return newSong;
    }


    private static HashMap<SectionVersion, Grid<String>> deepCopy(HashMap<SectionVersion, Grid<String>> map)
    {
        HashMap<SectionVersion, Grid<String>> ret = new HashMap<>();
        for (SectionVersion version : map.keySet()) {
            ret.put(version, new Grid<String>().deepCopy(map.get(version)));
            //  fixme: worry about version alteration!
        }
        return ret;
    }

    private static int chordLetterToNumber(char letter)
    {
        int i = letter - 'A';
        //                            a  a# b  c  c# d  d# e  f f#  g  g#
        //                            0  1  2  3  4  5  6  7  8  9  10 11

        return chordLetterToNumber[i];
    }

    private static final int chordLetterToNumber[] = new int[]{0, 2, 3, 5, 7, 8, 10};

    private String chordNumberToLetter(int n, int halfSteps)
    {
        return key.getScaleNoteByHalfStep(n + halfSteps).toString();
    }

    private final void setTitle(String title)
    {
        //  move the leading "The " to the end

        final RegExp theRegExp = RegExp.compile("^the *", "i");
        if (theRegExp.test(title)) {
            title = theRegExp.replace(title, "") + ", The";
        }
        this.title = title;
        songId = new SongId("Song" + title.replaceAll("\\W+", ""));
    }

    private final void setArtist(String artist)
    {
        //  move the leading "The " to the end
        final RegExp theRegExp = RegExp.compile("^the *", "i");
        if (theRegExp.test(artist)) {
            artist = theRegExp.replace(artist, "") + ", The";
        }
        this.artist = artist;
    }

    /**
     * Set the key for this song.
     *
     * @param key the given key
     */
    public final void setKey(Key key)
    {
        this.key = key;
    }


    /**
     * Return the song default beats per minute.
     *
     * @return the default BPM
     */
    public final int getBeatsPerMinute()
    {
        return defaultBpm;
    }

    /**
     * Set the song default beats per minute.
     *
     * @param bpm the defaultBpm to set
     */
    public final void setBeatsPerMinute(int bpm)
    {
        this.defaultBpm = bpm;
    }

    /**
     * Return the song's number of beats per bar
     *
     * @return the number of beats per bar
     */
    public final int getBeatsPerBar()
    {
        return beatsPerBar;
    }

    /**
     * Set the song's number of beats per bar
     *
     * @param beatsPerBar the beatsPerBar to set
     */
    public void setBeatsPerBar(int beatsPerBar)
    {
        this.beatsPerBar = beatsPerBar;
        computeDuration();
    }


    /**
     * Return an integer that represents the number of notes per measure
     * represented in the sheet music.  Typically this is 4; meaning quarter notes.
     *
     * @return the unitsPerMeasure
     */
    public final int getUnitsPerMeasure()
    {
        return unitsPerMeasure;
    }

    public void setUnitsPerMeasure(int unitsPerMeasure)
    {
        this.unitsPerMeasure = unitsPerMeasure;
    }

    /**
     * Return the song's copyright
     *
     * @return the copyright
     */
    public final String getCopyright()
    {
        return copyright;
    }

    /**
     * Return the song's key
     *
     * @return the key
     */
    public final Key getKey()
    {
        return key;
    }

    /**
     * Return the song's identification string.
     *
     * @return the songId
     */
    public final SongId getSongId()
    {
        return songId;
    }

    /**
     * @return the chordLetterToNumber
     */
    public static final int[] getChordLetterToNumber()
    {
        return chordLetterToNumber;
    }

    /**
     * Return the song's title
     *
     * @return the title
     */
    public final String getTitle()
    {
        return title;
    }

    /**
     * Return the song's artist.
     *
     * @return the artist
     */
    public final String getArtist()
    {
        return artist;
    }

    /**
     * Return the lyrics.
     *
     * @return the rawLyrics
     */
    @Deprecated
    public final String getLyricsAsString()
    {
        return rawLyrics;
    }

    /**
     * Return the chords
     *
     * @return the chords
     */
    @Deprecated
    public final String getChordsAsString()
    {
        return chords;
    }

    /**
     * Return the default beats per minute.
     *
     * @return the defaultBpm
     */
    public final int getDefaultBpm()
    {
        return defaultBpm;
    }

    /**
     * Get the song's default drum section.
     * The section will be played through all of its measures
     * and then repeated as required for the song's duration.
     *
     * @return the drum section
     */
    public final LegacyDrumSection getDrumSection()
    {
        return drumSection;
    }

    /**
     * Set the song's default drum section
     *
     * @param drumSection the drum section
     */
    public final void setDrumSection(LegacyDrumSection drumSection)
    {
        this.drumSection = drumSection;
    }

    public TreeSet<ChordSection> getChordSections()
    {
        return chordSections;
    }

    public final Arrangement getDrumArrangement()
    {
        return drumArrangement;
    }

    public final void setDrumArrangement(Arrangement drumArrangement)
    {
        this.drumArrangement = drumArrangement;
    }

    public final JsDate getLastModifiedDate()
    {
        return lastModifiedDate;
    }

    public final void setLastModifiedDate(JsDate lastModifiedDate)
    {
        this.lastModifiedDate = lastModifiedDate;
    }

    public final String getFileName()
    {
        return fileName;
    }

    public final void setFileName(String fileName)
    {
        this.fileName = fileName;

        final RegExp fileVersionRegExp = RegExp.compile(" \\(([0-9]+)\\).songlyrics$");
        MatchResult mr = fileVersionRegExp.exec(fileName);
        if (mr != null) {
            fileVersionNumber = Integer.parseInt(mr.getGroup(1));
        } else
            fileVersionNumber = 0;
    }

    public final double getDuration()
    {
        return duration;
    }

    public final int getTotalBeats()
    {
        return totalBeats;
    }

    public ArrayList<SongMoment> getSongMoments()
    {
        return songMoments;
    }

    public ArrayList<LyricSection> getLyricSections()
    {
        return lyricSections;
    }


    public enum ComparatorType
    {
        title,
        artist,
        lastModifiedDate,
        lastModifiedDateLast,
        versionNumber;
    }

    public static final Comparator<Song> getComparatorByType(ComparatorType type)
    {
        switch (type) {
            default:
                return new ComparatorByTitle();
            case artist:
                return new ComparatorByArtist();
            case lastModifiedDate:
                return new ComparatorByLastModifiedDate();
            case lastModifiedDateLast:
                return new ComparatorByLastModifiedDateLast();
            case versionNumber:
                return new ComparatorByVersionNumber();
        }
    }

    public static final class ComparatorByTitle implements Comparator<Song>
    {

        /**
         * Compares its two arguments for order.
         *
         * @param o1 the first object to be compared.
         * @param o2 the second object to be compared.
         * @return a negative integer, zero, or a positive integer as the
         * first argument is less than, equal to, or greater than the
         * second.
         * @throws NullPointerException if an argument is null and this
         *                              comparator does not permit null arguments
         * @throws ClassCastException   if the arguments' types prevent them from
         *                              being compared by this comparator.
         */
        @Override
        public int compare(Song o1, Song o2)
        {
            return o1.compareTo(o2);
        }
    }

    public static final class ComparatorByArtist implements Comparator<Song>
    {

        /**
         * Compares its two arguments for order.
         *
         * @param o1 the first object to be compared.
         * @param o2 the second object to be compared.
         * @return a negative integer, zero, or a positive integer as the
         * first argument is less than, equal to, or greater than the
         * second.
         * @throws NullPointerException if an argument is null and this
         *                              comparator does not permit null arguments
         * @throws ClassCastException   if the arguments' types prevent them from
         *                              being compared by this comparator.
         */
        @Override
        public int compare(Song o1, Song o2)
        {
            int ret = o1.getArtist().compareTo(o2.getArtist());
            if (ret != 0) {
                return ret;
            }
            return o1.compareTo(o2);
        }
    }

    public static final class ComparatorByLastModifiedDate implements Comparator<Song>
    {

        /**
         * Compares its two arguments for order my most recent modification date.
         *
         * @param o1 the first object to be compared.
         * @param o2 the second object to be compared.
         * @return a negative integer, zero, or a positive integer as the
         * first argument is less than, equal to, or greater than the
         * second.
         * @throws NullPointerException if an argument is null and this
         *                              comparator does not permit null arguments
         * @throws ClassCastException   if the arguments' types prevent them from
         *                              being compared by this comparator.
         */
        @Override
        public int compare(Song o1, Song o2)
        {
            JsDate mod1 = o1.lastModifiedDate;
            JsDate mod2 = o2.lastModifiedDate;
            if (mod1 != null) {
                if (mod2 != null) {
                    if (mod1.getTime() == mod2.getTime())
                        return o1.compareTo(o2);
                    return mod1.getTime() < mod2.getTime() ? 1 : -1;
                }
                return -1;
            }
            if (mod2 != null) {
                return 1;
            }
            return o1.compareTo(o2);
        }
    }

    public static final class ComparatorByLastModifiedDateLast implements Comparator<Song>
    {

        /**
         * Compares its two arguments for order my most recent modification date.
         *
         * @param o1 the first object to be compared.
         * @param o2 the second object to be compared.
         * @return a negative integer, zero, or a positive integer as the
         * first argument is less than, equal to, or greater than the
         * second.
         * @throws NullPointerException if an argument is null and this
         *                              comparator does not permit null arguments
         * @throws ClassCastException   if the arguments' types prevent them from
         *                              being compared by this comparator.
         */
        @Override
        public int compare(Song o1, Song o2)
        {
            return compareByLastModifiedDate(o1, o2);
        }
    }

    private static int compareByLastModifiedDate(Song o1, Song o2)
    {
        JsDate mod1 = o1.lastModifiedDate;
        JsDate mod2 = o2.lastModifiedDate;
        if (mod1 != null) {
            if (mod2 != null) {
                if (mod1.getTime() == mod2.getTime())
                    return o1.compareTo(o2);
                return mod1.getTime() < mod2.getTime() ? -1 : 1;
            }
            return 1;
        }
        if (mod2 != null) {
            return -1;
        }
        return o1.compareTo(o2);
    }

    public static final class ComparatorByVersionNumber implements Comparator<Song>
    {

        /**
         * Compares its two arguments for order.
         *
         * @param o1 the first object to be compared.
         * @param o2 the second object to be compared.
         * @return a negative integer, zero, or a positive integer as the
         * first argument is less than, equal to, or greater than the
         * second.
         * @throws NullPointerException if an argument is null and this
         *                              comparator does not permit null arguments
         * @throws ClassCastException   if the arguments' types prevent them from
         *                              being compared by this comparator.
         */
        @Override
        public int compare(Song o1, Song o2)
        {
            int ret = o1.compareTo(o2);
            if (ret != 0)
                return ret;
            if (o1.fileVersionNumber != o2.fileVersionNumber)
                return o1.fileVersionNumber < o2.fileVersionNumber ? -1 : 1;
            return compareByLastModifiedDate(o1, o2);
        }
    }

    /**
     * Compare only the title and artist.
     * To be used for listing purposes only.
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Song o)
    {
        int ret = getSongId().compareTo(o.getSongId());
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
    public boolean equals(Object obj)
    {
        //  fixme: song equals should include all fields
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Song)) {
            return false;
        }
        Song o = (Song) obj;

        //  song id built from title with reduced whitespace
        if (!getTitle().equals(o.getTitle()))
            return false;
        if (!getArtist().equals(o.getArtist()))
            return false;
        if (!getCopyright().equals(o.getCopyright()))
            return false;
        if (!getKey().equals(o.getKey()))
            return false;
        if (defaultBpm != o.defaultBpm)
            return false;
        if (unitsPerMeasure != o.unitsPerMeasure)
            return false;
        if (!chords.equals(o.chords))
            return false;
        if (!rawLyrics.equals(o.rawLyrics))
            return false;
        if (!metadata.equals(o.metadata))
            return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        //  fixme: song hashCode should include all fields
        int hash = 7;
        hash = (79 * hash + Objects.hashCode(this.title)) % (1 << 31);
        hash = (79 * hash + Objects.hashCode(this.artist)) % (1 << 31);
        hash = (79 * hash + Objects.hashCode(this.copyright)) % (1 << 31);
        hash = (79 * hash + Objects.hashCode(this.key)) % (1 << 31);
        hash = (79 * hash + this.defaultBpm) % (1 << 31);
        hash = (79 * hash + this.unitsPerMeasure) % (1 << 31);
        hash = (79 * hash + Objects.hashCode(this.chords)) % (1 << 31);
        hash = (79 * hash + Objects.hashCode(this.rawLyrics)) % (1 << 31);
        hash = (79 * hash + Objects.hashCode(this.metadata)) % (1 << 31);
        return hash;
    }

    private String title = "Unknown";
    private SongId songId = new SongId();
    private String artist = "Unknown";
    private String copyright = "Unknown";
    private transient JsDate lastModifiedDate;
    private transient String fileName;
    private transient int fileVersionNumber = 0;
    private Key key = Key.C;  //  default
    private int defaultBpm = 106;  //  beats per minute
    private int unitsPerMeasure = 4;//  units per measure, i.e. timeSignature numerator
    private int beatsPerBar = 4;  //  beats per bar, i.e. timeSignature denominator
    private transient double duration;    //  units of seconds
    private transient int totalBeats;
    private ArrayList<LyricSection> lyricSections = new ArrayList<>();
    private TreeSet<ChordSection> chordSections = new TreeSet<>();
    private ArrayList<SongMoment> songMoments = new ArrayList<>();
    private String chords = "";
    private ArrayList<MeasureNode> measureNodes = new ArrayList<>();
    private String rawLyrics = "";
    private LegacyDrumSection drumSection = new LegacyDrumSection();
    private Arrangement drumArrangement;    //  default
    private TreeSet<Metadata> metadata = new TreeSet<>();

    private ArrayList<SectionVersion> sequence;
    private final HashMap<SectionVersion, Grid<String>> chordSectionInnerHtmlMap = new HashMap<>();
    private final HashMap<SectionVersion, SectionVersion> displaySectionMap = new HashMap<>();
    private static final char js_delta = '\u0394';

    private static final int minBpm = 50;
    private static final int maxBpm = 400;

    private static final Logger logger = Logger.getLogger(Song.class.getName());
}
