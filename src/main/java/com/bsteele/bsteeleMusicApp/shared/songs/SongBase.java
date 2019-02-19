/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.client.songs.Metadata;
import com.bsteele.bsteeleMusicApp.shared.Grid;
import com.bsteele.bsteeleMusicApp.client.legacy.LegacyDrumSection;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.client.songs.SongChordGridSelection;
import com.bsteele.bsteeleMusicApp.client.util.CssConstants;
import com.bsteele.bsteeleMusicApp.shared.util.Util;
import com.google.gwt.core.client.GWT;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.FlexTable;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * @author bob
 */

/**
 * A piece of music to be played according to the structure it contains.
 * The song base class has been separated from the song class to allow most of the song
 * mechanics to be tested in the shared code environment where debugging is easier.
 */
public class SongBase {

    /**
     * Not to be used externally
     */
    protected SongBase() {
        setTitle("");
        setArtist("");
        setCoverArtist(null);
        copyright = "";
        setKey(Key.C);
        unitsPerMeasure = 4;
        rawLyrics = "";
        chords = "";
        parse();
        parseLyrics();
        setBeatsPerMinute(100);
        setBeatsPerBar(4);
    }

    /**
     * Compute the song moments list given the song's current state.
     * Moments are the temporal sequence of measures as the song is to be played.
     * All repeats are expanded.  Measure node such as comments,
     * repeat ends, repeat counts, section headers, etc. are ignored.
     */
    private final void computeSongMoments() {
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
                                                lyricSection, chordSection, measureSequenceItem,
                                                measure, repeat, limit));
                                    }
                            }
                        } else {
                            ArrayList<Measure> measures = measureSequenceItem.getMeasures();
                            if (measures != null)
                                for (Measure measure : measures) {
                                    songMoments.add(new SongMoment(
                                            songMoments.size(),  //  size prior to add
                                            lyricSection, chordSection, measureSequenceItem,
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

    /**
     * Find the corrsesponding chord section for the given lyrics section
     *
     * @param lyricSection
     * @return
     */
    private final ChordSection findChordSection(LyricSection lyricSection) {
        return chordSectionMap.get(lyricSection.getSectionVersion());
    }

    /**
     * Compute the duration and total beat count for the song.
     */
    private void computeDuration() {  //  fixme: account for repeats!!!!!!!!!!!!!!!!!!!

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


    @Deprecated
    public final Grid<String> getChordSectionStrings(SectionVersion sv) {
        return chordSectionInnerHtmlMap.get(sv);
    }

    /**
     * Find the chord section for the given section version.
     *
     * @param sv
     * @return
     */
    public final ChordSection getChordSection(SectionVersion sv) {
        return chordSectionMap.get(sv);
    }

    /**
     * Parse the current string representation of the song's chords into the song internal strucutures.
     */
    private final void parse() {
        measureNodes = new ArrayList<>();
        chordSectionMap = new HashMap<>();
        clearStructuralGrid();  //  force lazy eval

        //GWT.log( "title: "+getTitle());

        if (chords != null) {
            StringBuffer sb = new StringBuffer(chords);
            ChordSection chordSection;
            while ((chordSection = ChordSection.parse(sb, beatsPerBar)) != null) {
                measureNodes.add(chordSection);
                chordSectionMap.put(chordSection.getSectionVersion(), chordSection);
                clearStructuralGrid();
            }
        }

        //  fixme: temp transform from chordSections to chordSectionInnerHtmlMap
        chordSectionInnerHtmlMap.clear();
        for (ChordSection chordSection : new TreeSet<ChordSection>(chordSectionMap.values())) {
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

    /**
     * Compute the nominal display structural grid from the current chord section.
     *
     * @return
     */
    public final Grid<MeasureNode> getStructuralGrid() {
        if (structuralGrid != null)
            return structuralGrid;

        structuralGrid = new Grid<>();

        for (ChordSection chordSection : new TreeSet<>(chordSectionMap.values())) {
            chordSection.addToGrid(structuralGrid, chordSection);
        }

        return structuralGrid;
    }

    private final void clearStructuralGrid() {
        structuralGrid = null;
    }

    public final String toMarkup() {
        StringBuilder sb = new StringBuilder();

        for (ChordSection chordSection : new TreeSet<>(chordSectionMap.values())) {
            sb.append(chordSection.toMarkup());
        }
        return sb.toString();
    }

    @Deprecated
    private final SectionVersion getStructuralGridSectionVersionAtRow(Grid<MeasureNode> grid, int row) {
        int rowCount = grid.getRowCount();
        if (row >= rowCount)
            return null;

        MeasureNode measureNode = grid.getRow(row).get(0);
        if (measureNode instanceof ChordSection) {
            return ((ChordSection) measureNode).getSectionVersion();
        }
        return null;
    }

    /**
     * @param songChordGridSelection
     * @return
     */
    @Deprecated
    public final MeasureNode getStructuralMeasureNode(SongChordGridSelection songChordGridSelection) {
        if (songChordGridSelection == null)
            return null;
        return getStructuralMeasureNode(songChordGridSelection.getRow(), songChordGridSelection.getCol());
    }

    /**
     * Find the measure node for the given row and column in the structural grid
     *
     * @param r
     * @param c
     * @return
     */
    public final MeasureNode getStructuralMeasureNode(int r, int c) {
        try {
            Grid<MeasureNode> grid = getStructuralGrid();
            ArrayList<MeasureNode> row = grid.getRow(r);
            if (row == null)
                return null;
            return row.get(c);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

    /**
     * Add the given section version to the song chords
     *
     * @param sectionVersion
     * @return
     */
    public final boolean addSectionVersion(SectionVersion sectionVersion) {
        if (sectionVersion == null || chordSectionMap.containsKey(sectionVersion))
            return false;
        chordSectionMap.put(sectionVersion, new ChordSection(sectionVersion));
        clearStructuralGrid();
        return true;
    }

    /**
     * Edit the given measure in or out of the song based on the data from the edit location.
     *
     * @param refMeasureNode the referenced location in the song
     * @param editLocation   the type of edit to be made: insert, append, delete, etc.
     * @param measure        the measure in question
     * @return
     */
    public final boolean measureEdit(@Nonnull MeasureNode refMeasureNode,
                                     @Nonnull MeasureSequenceItem.EditLocation editLocation,
                                     @Nonnull Measure measure) {
        ChordSection chordSection = findChordSection(refMeasureNode);
        if (chordSection == null)
            return false;

        if (chordSection.getMeasureSequenceItems().isEmpty()) {
            chordSection.getMeasureSequenceItems().add(new MeasureSequenceItem(new ArrayList<>()));
        }

        MeasureSequenceItem measureSequenceItem = chordSection.findMeasureSequenceItem(refMeasureNode);
        if (measureSequenceItem == null) {
            if (chordSection.getMeasureSequenceItems().size() != 1)
                return false;
            //  deal with empty section
            measureSequenceItem = chordSection.getMeasureSequenceItems().get(0);    //  use the default empty list
        }

        switch (editLocation) {
            case insert:
                return measureSequenceItem.insert(refMeasureNode, measure);
            case replace:
                return measureSequenceItem.replace(refMeasureNode, measure);
            case append:
                if (refMeasureNode instanceof MeasureRepeatMarker) {
                    MeasureRepeat measureRepeat = (MeasureRepeat) measureSequenceItem;
                    if (refMeasureNode == measureRepeat.getRepeatMarker()) {
                        //  appending at the repeat marker forces the section to add a sequenceItem list after the repeat
                        MeasureSequenceItem newMeasureSequenceItem = new MeasureSequenceItem(new ArrayList<>());
                        chordSection.getMeasureSequenceItems().add(chordSection.indexOf(measureRepeat) + 1, newMeasureSequenceItem);
                        measureSequenceItem = newMeasureSequenceItem;
                    }
                }
                boolean ret = measureSequenceItem.append(refMeasureNode, measure);
                if (ret)
                    clearStructuralGrid();
                return ret;
        }
        return false;
    }

    /**
     * Find the measure sequence item for the given measure (i.e. the measure's parent container).
     *
     * @param measure
     * @return
     */
    public final MeasureSequenceItem findMeasureSequenceItem(Measure measure) {
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

    /**
     * Find the chord section for the given measure node.
     *
     * @param measureNode
     * @return
     */
    private final ChordSection findChordSection(MeasureNode measureNode) {
        if (measureNode == null)
            return null;

        String id = measureNode.getId();
        for (ChordSection chordSection : chordSectionMap.values()) {
            if (id != null && id.equals(chordSection.getId()))
                return chordSection;
            MeasureNode mn = chordSection.findMeasureNode(measureNode);
            if (mn != null)
                return chordSection;
        }
        return null;
    }

    /**
     * Find the structural grid location for the given measure
     *
     * @param measureNode
     * @return
     */
    public final SongChordGridSelection findChordGridLocationForMeasureNode(MeasureNode measureNode) {
        Grid<MeasureNode> grid = getStructuralGrid();
        int rowCount = grid.getRowCount();
        for (int r = 0; r < rowCount; r++) {
            ArrayList<MeasureNode> row = grid.getRow(r);
            int colCount = row.size();
            for (int c = 0; c < colCount; c++) {
                MeasureNode mn = row.get(c);
                if (mn == measureNode) {
                    return new SongChordGridSelection(r, c);
                }
            }
        }

        return null;
    }

    /**
     * Find the structural grid location for the given section version.
     *
     * @param sectionVersion
     * @return
     */
    public final SongChordGridSelection findSectionVersionChordGridLocation(SectionVersion sectionVersion) {
        Grid<MeasureNode> grid = getStructuralGrid();
        int rowCount = grid.getRowCount();
        for (int r = 0; r < rowCount; r++) {
            ArrayList<MeasureNode> row = grid.getRow(r);
            if (row.size() > 0) {
                MeasureNode mn = row.get(0);
                if (mn instanceof ChordSection
                        && sectionVersion.equals(((ChordSection) mn).getSectionVersion())) {
                    return new SongChordGridSelection(r, 0);
                }
            }
        }

        return null;
    }

    public final Measure findMeasure(SongChordGridSelection songChordGridSelection) {
        if (songChordGridSelection == null)
            return null;
        Grid<MeasureNode> grid = getStructuralGrid();
        MeasureNode measureNode = grid.get(songChordGridSelection.getCol(), songChordGridSelection.getRow()); //   x,y!
        if (measureNode != null && measureNode instanceof Measure)
            return ((Measure) measureNode);
        return null;
    }

    public final Measure findMeasure(StringBuffer sb) {
        ChordSection chordSection = findChordSection(sb);
        if (chordSection == null)
            return null;
        return chordSection.findMeasure(sb);
    }

    public final ChordSection findChordSection(SongChordGridSelection songChordGridSelection) {
        if (songChordGridSelection == null)
            return null;
        Grid<MeasureNode> grid = getStructuralGrid();
        MeasureNode measureNode = grid.get(songChordGridSelection.getCol(), songChordGridSelection.getRow()); //   x,y!
        if (measureNode != null && measureNode instanceof ChordSection)
            return ((ChordSection) measureNode);
        return null;
    }

    public final ChordSection findChordSection(StringBuffer sb) {
        SectionVersion sectionVersion = SectionVersion.parse(sb);
        if (sectionVersion == null)
            return null;
        return chordSectionMap.get(sectionVersion);
    }

    public final Measure findChordSectionLocation(ChordSectionLocation chordSectionLocation) {
        ChordSection chordSection = findChordSection(chordSectionLocation.getChordSection());
        if (chordSection == null)
            return null;

        int count = chordSectionLocation.getIndex() - 1;
        if (count < 0)
            return null;
        for (MeasureSequenceItem msi : chordSection.getMeasureSequenceItems()) {
            if (count < msi.getMeasures().size()) {
                return msi.getMeasures().get(count);
            }
            count -= msi.getMeasures().size();
        }

        return null;
    }

    public final boolean measureDelete(Measure measure) {
        if (measure == null)
            return false;

        for (ChordSection chordSection : chordSectionMap.values()) {
            for (MeasureSequenceItem msi : chordSection.getMeasureSequenceItems()) {
                if (msi.delete(measure))
                    return true;
            }
        }
        return false;
    }

    public final boolean chordSectionDelete(ChordSection chordSection) {
        if (chordSection == null)
            return false;
        return chordSectionMap.remove(chordSection) != null;
    }

    public final String measureNodesToHtml(@NotNull String tableName, @NotNull Key key, int tran) {
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

            for (ChordSection chordSection : new TreeSet<ChordSection>(chordSectionMap.values())) {
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

    public final void guessTheKey() {

        //  fixme: key guess based on chords section or lyrics?
        TreeSet<ScaleChord> treeSet = new TreeSet<>();
        treeSet.addAll(findScaleChordsUsed().keySet());

        setKey(Key.guessKey(treeSet));
    }


    public final HashMap<ScaleChord, Integer> findScaleChordsUsed() {
        HashMap<ScaleChord, Integer> ret = new HashMap<>();
        for (ChordSection chordSection : chordSectionMap.values()) {
            for (MeasureSequenceItem msi : chordSection.getMeasureSequenceItems()) {
                for (Measure m : msi.getMeasures()) {
                    for (Chord chord : m.getChords()) {
                        ScaleChord scaleChord = chord.getScaleChord();
                        Integer chordCount = ret.get(scaleChord);
                        ret.put(scaleChord, chordCount == null ? 1 : chordCount + 1);
                    }
                }
            }
        }
        return ret;
    }


    public final String generateHtmlLyricsTable(String prefix) {
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
        StringBuffer sb = new StringBuffer(rawLyrics);
        while (sb.length() > 0) {
            char c = sb.charAt(0);
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
                    SectionVersion version = SectionVersion.parse(sb);
                    if (version != null) {
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
            sb.delete(0, 1);     //  consume parsed character
        }

        lyrics = tableStart
                + lyrics
                + rowEnd
                + tableEnd;
        //GWT.log(lyrics);
        return lyrics;
    }

    protected final void parseLyrics() {
        int state = 0;
        String whiteSpace = "";
        String lyrics = "";
        LyricSection lyricSection = null;

        lyricSections = new ArrayList<>();

        StringBuffer sb = new StringBuffer(rawLyrics);
        while (sb.length() > 0) {
            char c = sb.charAt(0);
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
                    SectionVersion version = SectionVersion.parse(sb);
                    if (version != null) {
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

            sb.delete(0, 1);
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
    public static final String genLyricsId(int sectionIndex) {
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
    public static final String genChordId(SectionVersion displaySectionVersion, int row, int col) {
        if (displaySectionVersion == null)
            return "";
        return "C." + displaySectionVersion.toString() + '.' + row + '.' + col;
    }

    public final Measure getMeasure(int row, int col) {
        Grid<MeasureNode> grid = getStructuralGrid();
        MeasureNode mn = grid.get(row, col);
        if (mn == null)
            return null;
        if (mn instanceof Measure)
            return (Measure) mn;
        return null;
    }

    public final void transpose(String prefix, FlexTable flexTable, int halfSteps, int fontSize) {
        transpose(getStructuralGrid(), prefix, flexTable, halfSteps, fontSize, false);
    }

    public final void transpose(ChordSection chordSection, String prefix, FlexTable flexTable, int halfSteps,
                                int fontSize, boolean append) {
        if (chordSection == null || flexTable == null || fontSize <= 0)
            return;
        Grid<MeasureNode> grid = new Grid<>();
        chordSection.addToGrid(grid, chordSection);
        transpose(grid, prefix, flexTable, halfSteps, fontSize, append);
    }

    private final void transpose(Grid<MeasureNode> grid, String prefix, FlexTable flexTable, int halfSteps,
                                 int fontSize, boolean append) {
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
                if (c > 0
                        && c <= MusicConstant.measuresPerDisplayRow
                        && s.equals(lastValue)
                        && !(measureNode instanceof MeasureComment)) {
                    s = "-";
                    formatter.addStyleName(r + offset, c, CssConstants.style + "textCenter");
                } else
                    lastValue = s;

                //formatter.setAlignment(r + offset, c, ALIGN_CENTER, ALIGN_BOTTOM);
                flexTable.setHTML(r + offset, c,
                        "<span style=\"font-size: " + fontSize + "px;\"" +
                                " id=\"" + findChordGridLocationForMeasureNode(measureNode) + "\">"
                                + s
                                + "</span>"
                );
                formatter.addStyleName(r + offset, c, CssConstants.style
                        + (measureNode.isComment()
                        ? "sectionCommentClass"
                        : "section" + sectionVersion.getSection().getAbbreviation() + "Class"));
            }
        }
    }


    public final void addRepeat(@Nonnull SongChordGridSelection songChordGridSelection, @Nonnull MeasureRepeat repeat) {
        Measure measure = findMeasure(songChordGridSelection);
        if (measure == null)
            return;

        MeasureSequenceItem measureSequenceItem = findMeasureSequenceItem(measure);
        if (measureSequenceItem == null)
            return;

        ChordSection chordSection = findChordSection(measure);
        ArrayList<MeasureSequenceItem> measureSequenceItems = chordSection.getMeasureSequenceItems();
        int i = measureSequenceItems.indexOf(measureSequenceItem);
        if (i >= 0) {
            measureSequenceItems = new ArrayList<>(measureSequenceItems);
            measureSequenceItems.remove(i);
            measureSequenceItems.add(i, repeat);
        } else {
            measureSequenceItems.add(repeat);
        }


        chordSectionDelete(chordSection);
        chordSection = new ChordSection(chordSection.getSectionVersion(), measureSequenceItems);
        chordSectionMap.put(chordSection.getSectionVersion(), chordSection);
        clearStructuralGrid();
    }


    public final void setRepeat(SongChordGridSelection songChordGridSelection, int repeats) {
        Measure measure = findMeasure(songChordGridSelection);
        if (measure == null)
            return;

        MeasureSequenceItem measureSequenceItem = findMeasureSequenceItem(measure);
        if (measureSequenceItem == null)
            return;

        if (measureSequenceItem instanceof MeasureRepeat) {
            MeasureRepeat measureRepeat = ((MeasureRepeat) measureSequenceItem);

            if (repeats <= 1) {
                //  remove the repeat
                ChordSection chordSection = findChordSection(measureRepeat);
                ArrayList<MeasureSequenceItem> measureSequenceItems = chordSection.getMeasureSequenceItems();
                int i = measureSequenceItems.indexOf(measureRepeat);
                measureSequenceItems.remove(i);
                measureSequenceItems.add(i, new MeasureSequenceItem(measureRepeat.getMeasures()));

                chordSectionDelete(chordSection);
                chordSection = new ChordSection(chordSection.getSectionVersion(), measureSequenceItems);
                chordSectionMap.put(chordSection.getSectionVersion(), chordSection);
                clearStructuralGrid();
            } else {
                //  change the count
                measureRepeat.setRepeats(repeats);
            }
        } else {
            //  change sequence items to repeat
            MeasureRepeat measureRepeat = new MeasureRepeat(measureSequenceItem.getMeasures(), repeats);
            ChordSection chordSection = findChordSection(measureSequenceItem);
            ArrayList<MeasureSequenceItem> measureSequenceItems = chordSection.getMeasureSequenceItems();
            int i = measureSequenceItems.indexOf(measureSequenceItem);
            measureSequenceItems = new ArrayList<>(measureSequenceItems);
            measureSequenceItems.remove(i);
            measureSequenceItems.add(i, measureRepeat);

            chordSectionDelete(chordSection);
            chordSection = new ChordSection(chordSection.getSectionVersion(), measureSequenceItems);
            chordSectionMap.put(chordSection.getSectionVersion(), chordSection);
            clearStructuralGrid();
        }
    }


    /**
     * Checks a song for completeness.
     *
     * @return a new song constructed with the song's current fields.
     * @throws ParseException exception thrown if the song's fields don't match properly.
     */
    public final Song checkSong()
            throws ParseException {
        return checkSong(getTitle(), getArtist(), getCopyright(),
                getKey(), Integer.toString(getDefaultBpm()), Integer.toString(getBeatsPerBar()),
                Integer.toString(getUnitsPerMeasure()),
                getChords(), getRawLyrics());
    }

    /**
     * Validate a song entry argument set
     *
     * @param title                the song's title
     * @param artist               the artist associated with this song or at least this song version
     * @param copyright            the copyright notice associated with the song
     * @param key                  the song's musical key
     * @param bpmEntry             the song's number of beats per minute
     * @param beatsPerBarEntry     the song's default number of beats per par
     * @param unitsPerMeasureEntry the inverse of the note duration fraction per entry, for exmple if each beat is
     *                             represented by a quarter note, the units per measure would be 4.
     * @param chordsTextEntry      the string transport form of the song's chord sequence description
     * @param lyricsTextEntry      the string transport form of the song's section sequence and lyrics
     * @return a new song if the fields are valid
     * @throws ParseException exception thrown if the song's fields don't match properly.
     */
    public static final Song checkSong(String title, String artist, String copyright,
                                       Key key, String bpmEntry, String beatsPerBarEntry, String unitsPerMeasureEntry,
                                       String chordsTextEntry, String lyricsTextEntry)
            throws ParseException {
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

        //  an early song with default (no) structure?
        if (newSong.getLyricSections().size() == 1 && newSong.getLyricSections().get(0).getSectionVersion().equals
                (Section.getDefaultVersion())) {
            newSong.setMessage("song looks too simple, is there really no structure?");
        }

        return newSong;
    }

    private static HashMap<SectionVersion, Grid<String>> deepCopy(HashMap<SectionVersion, Grid<String>> map) {
        HashMap<SectionVersion, Grid<String>> ret = new HashMap<>();
        for (SectionVersion version : map.keySet()) {
            ret.put(version, new Grid<String>().deepCopy(map.get(version)));
            //  fixme: worry about version alteration!
        }
        return ret;
    }

    /**
     * Sets the song's title and song id from the given title. Leading "The " articles are rotated to the title end.
     *
     * @param title
     */
    protected final void setTitle(@Nonnull String title) {
        //  move the leading "The " to the end
        final RegExp theRegExp = RegExp.compile("^the +", "i");
        if (theRegExp.test(title)) {
            title = theRegExp.replace(title, "") + ", The";
        }
        this.title = title;
        computeSongId();
    }

    /**
     * Sets the song's artist
     *
     * @param artist artist's name
     */
    protected final void setArtist(@Nonnull String artist) {
        //  move the leading "The " to the end
        final RegExp theRegExp = RegExp.compile("^the +", "i");
        if (theRegExp.test(artist)) {
            artist = theRegExp.replace(artist, "") + ", The";
        }
        this.artist = artist;
        computeSongId();
    }


    public void setCoverArtist(String coverArtist) {
        if (coverArtist != null) {
            //  move the leading "The " to the end
            final RegExp theRegExp = RegExp.compile("^the +", "i");
            if (theRegExp.test(coverArtist)) {
                coverArtist = theRegExp.replace(coverArtist, "") + ", The";
            }
        }
        this.coverArtist = coverArtist;
        computeSongId();
    }

    private void computeSongId() {
        songId = new SongId("Song_" + title.replaceAll("\\W+", "")
                + "_by_" + artist.replaceAll("\\W+", "")
                + (coverArtist == null ? "" : "_coverBy_" + coverArtist));
    }

    /**
     * Sets the copyright for the song.  All songs should have a copyright.
     *
     * @param copyright copyright for the song
     */
    protected final void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    /**
     * Set the key for this song.
     *
     * @param key the given key
     */
    public final void setKey(Key key) {
        this.key = key;
    }


    /**
     * Return the song default beats per minute.
     *
     * @return the default BPM
     */
    public final int getBeatsPerMinute() {
        return defaultBpm;
    }

    /**
     * Set the song default beats per minute.
     *
     * @param bpm the defaultBpm to set
     */
    public final void setBeatsPerMinute(int bpm) {
        this.defaultBpm = bpm;
    }

    /**
     * Return the song's number of beats per bar
     *
     * @return the number of beats per bar
     */
    public final int getBeatsPerBar() {
        return beatsPerBar;
    }

    /**
     * Set the song's number of beats per bar
     *
     * @param beatsPerBar the beatsPerBar to set
     */
    public final void setBeatsPerBar(int beatsPerBar) {
        this.beatsPerBar = beatsPerBar;
        computeDuration();
    }


    /**
     * Return an integer that represents the number of notes per measure
     * represented in the sheet music.  Typically this is 4; meaning quarter notes.
     *
     * @return the unitsPerMeasure
     */
    public final int getUnitsPerMeasure() {
        return unitsPerMeasure;
    }

    protected final void setUnitsPerMeasure(int unitsPerMeasure) {
        this.unitsPerMeasure = unitsPerMeasure;
    }

    /**
     * Return the song's copyright
     *
     * @return the copyright
     */
    public final String getCopyright() {
        return copyright;
    }

    /**
     * Return the song's key
     *
     * @return the key
     */
    public final Key getKey() {
        return key;
    }

    /**
     * Return the song's identification string largely consisting of the title and artist name.
     *
     * @return the songId
     */
    public final SongId getSongId() {
        return songId;
    }

    /**
     * Return the song's title
     *
     * @return the title
     */
    public final String getTitle() {
        return title;
    }

    /**
     * Return the song's artist.
     *
     * @return the artist
     */
    public final String getArtist() {
        return artist;
    }

    /**
     * Return the lyrics.
     *
     * @return the rawLyrics
     */
    @Deprecated
    public final String getLyricsAsString() {
        return rawLyrics;
    }

    /**
     * Return the chords
     *
     * @return the chords
     */
    @Deprecated
    public final String getChordsAsString() {
        return chords;
    }

    /**
     * Return the default beats per minute.
     *
     * @return the defaultBpm
     */
    public final int getDefaultBpm() {
        return defaultBpm;
    }

    /**
     * Get the song's default drum section.
     * The section will be played through all of its measures
     * and then repeated as required for the song's duration.
     *
     * @return the drum section
     */
    public final LegacyDrumSection getDrumSection() {
        return drumSection;
    }

    /**
     * Set the song's default drum section
     *
     * @param drumSection the drum section
     */
    public final void setDrumSection(LegacyDrumSection drumSection) {
        this.drumSection = drumSection;
    }

    protected Collection<ChordSection> getChordSections() {
        return chordSectionMap.values();
    }

    public final Arrangement getDrumArrangement() {
        return drumArrangement;
    }

    public final void setDrumArrangement(Arrangement drumArrangement) {
        this.drumArrangement = drumArrangement;
    }

    public final String getFileName() {
        return fileName;
    }

    public final void setFileName(String fileName) {
        this.fileName = fileName;

        final RegExp fileVersionRegExp = RegExp.compile(" \\(([0-9]+)\\).songlyrics$");
        MatchResult mr = fileVersionRegExp.exec(fileName);
        if (mr != null) {
            fileVersionNumber = Integer.parseInt(mr.getGroup(1));
        } else
            fileVersionNumber = 0;
        //logger.info("setFileName(): "+fileVersionNumber);
    }

    public final double getDuration() {
        return duration;
    }

    public final int getTotalBeats() {
        return totalBeats;
    }

    public ArrayList<SongMoment> getSongMoments() {
        return songMoments;
    }

    public ArrayList<LyricSection> getLyricSections() {
        return lyricSections;
    }

    public int getFileVersionNumber() {
        return fileVersionNumber;
    }

    protected void setDuration(double duration) {
        this.duration = duration;
    }

    public String getChords() {
        return chords;
    }

    protected void setChords(String chords) {
        this.chords = chords;
        parse();
    }

    public String getRawLyrics() {
        return rawLyrics;
    }

    protected void setRawLyrics(String rawLyrics) {
        this.rawLyrics = rawLyrics;
    }

    public void setTotalBeats(int totalBeats) {
        this.totalBeats = totalBeats;
    }

    public void setDefaultBpm(int defaultBpm) {
        this.defaultBpm = defaultBpm;
    }

    public String getCoverArtist() {
        return coverArtist;
    }

    public String getMessage() {
        return message;
    }

    protected void setMessage(String message) {
        this.message = message;
    }


    public static final class ComparatorByTitle implements Comparator<SongBase> {

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
        public int compare(SongBase o1, SongBase o2) {
            return o1.defaultCompareTo(o2);
        }
    }

    public static final class ComparatorByArtist implements Comparator<SongBase> {

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
        public int compare(SongBase o1, SongBase o2) {
            int ret = o1.getArtist().compareTo(o2.getArtist());
            if (ret != 0) {
                return ret;
            }
            return o1.defaultCompareTo(o2);
        }

    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return title + (fileVersionNumber > 0 ? ":(" + fileVersionNumber + ")" : "") + " by " + artist;
    }

    public static final boolean containsSongTitleAndArtist(Collection<? extends SongBase> collection, SongBase song) {
        for (SongBase collectionSong : collection) {
            if (song.defaultCompareTo(collectionSong) == 0)
                return true;
        }
        return false;
    }

    /**
     * Compare only the title and artist.
     * To be used for listing purposes only.
     *
     * @param o
     * @return
     */
    private int defaultCompareTo(SongBase o) {
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

    public final boolean songBaseSameAs(SongBase o) {
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
    public boolean equals(Object obj) {
        //  fixme: song equals should include all fields
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SongBase)) {
            return false;
        }
        SongBase o = (SongBase) obj;

        return songBaseSameAs(o);
    }

    @Override
    public int hashCode() {
        //  fixme: song hashCode should include all fields
        int hash = 7;
        hash = (79 * hash + Objects.hashCode(this.title)) % (1 << 31);
        hash = (79 * hash + Objects.hashCode(this.artist)) % (1 << 31);
        hash = (79 * hash + Objects.hashCode(this.coverArtist)) % (1 << 31);
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
    private String coverArtist;
    private String copyright = "Unknown";
    private transient String fileName;
    private transient int fileVersionNumber = 0;
    private Key key = Key.C;  //  default
    private int defaultBpm = 106;  //  beats per minute
    private int unitsPerMeasure = 4;//  units per measure, i.e. timeSignature numerator
    private int beatsPerBar = 4;  //  beats per bar, i.e. timeSignature denominator
    private transient double duration;    //  units of seconds
    private transient int totalBeats;
    private ArrayList<LyricSection> lyricSections = new ArrayList<>();
    private HashMap<SectionVersion, ChordSection> chordSectionMap = new HashMap<>();
    private MeasureNode currentMeasureNode;
    private Grid<MeasureNode> structuralGrid = null;
    private transient String message;
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

    private static final Logger logger = Logger.getLogger(SongBase.class.getName());
}
