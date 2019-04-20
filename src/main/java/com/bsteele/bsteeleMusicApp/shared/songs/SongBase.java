/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.client.songs.Metadata;
import com.bsteele.bsteeleMusicApp.shared.Grid;
import com.bsteele.bsteeleMusicApp.client.legacy.LegacyDrumSection;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.client.util.CssConstants;
import com.bsteele.bsteeleMusicApp.shared.GridCoordinate;
import com.bsteele.bsteeleMusicApp.shared.util.MarkedString;
import com.bsteele.bsteeleMusicApp.shared.util.StringTriple;
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
import java.util.logging.Level;
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
        setRawLyrics("");
        try {
            parseChords("");
        } catch (ParseException pex) {
            //  ignore
        }
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

        logger.fine("lyricSections size: " + lyricSections.size());
        for (LyricSection lyricSection : lyricSections) {
            ChordSection chordSection = findChordSection(lyricSection);
            if (chordSection != null) {
                ArrayList<Phrase> phrases = chordSection.getPhrases();
                if (phrases != null) {
                    int phraseIndex = 0;
                    for (Phrase phrase : phrases) {
                        if (phrase.isRepeat()) {
                            MeasureRepeat measureRepeat = (MeasureRepeat) phrase;
                            int limit = measureRepeat.getRepeats();
                            for (int repeat = 0; repeat < limit; repeat++) {
                                ArrayList<Measure> measures = measureRepeat.getMeasures();
                                if (measures != null) {
                                    int measureIndex = 0;
                                    for (Measure measure : measures) {
                                        songMoments.add(new SongMoment(
                                                songMoments.size(),  //  size prior to add
                                                lyricSection, chordSection, phraseIndex, phrase,
                                                measureIndex, measure, repeat, limit));
                                        measureIndex++;
                                    }
                                }
                            }
                        } else {
                            ArrayList<Measure> measures = phrase.getMeasures();
                            if (measures != null) {
                                int measureIndex = 0;
                                for (Measure measure : measures) {
                                    songMoments.add(new SongMoment(
                                            songMoments.size(),  //  size prior to add
                                            lyricSection, chordSection, phraseIndex, phrase,
                                            measureIndex, measure, 0, 0));
                                    measureIndex++;
                                }
                            }
                        }
                        phraseIndex++;
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
     * @param lyricSection the given lyrics section
     * @return the corrsesponding chord section
     */
    private final ChordSection findChordSection(LyricSection lyricSection) {
        if (lyricSection == null)
            return null;
        logger.fine("chordSectionMap size: " + chordSectionMap.keySet().size());
        return chordSectionMap.get(lyricSection.getSectionVersion());
    }

    /**
     * Compute the duration and total beat count for the song.
     */
    private void computeDuration() {  //  fixme: account for repeats!!!!!!!!!!!!!!!!!!!

        duration = 0;
        totalBeats = 0;

        ArrayList<SongMoment> moments = getSongMoments();
        if (beatsPerBar == 0 || defaultBpm == 0 || moments == null || moments.isEmpty())
            return;

        for (SongMoment moment : moments) {
            totalBeats += moment.getMeasure().getBeatCount();
        }
        duration = totalBeats * 60.0 / defaultBpm;
    }

    /**
     * Find the chord section for the given section version.
     *
     * @param sectionVersion the given section version
     * @return the chord section
     */
    public final ChordSection getChordSection(SectionVersion sectionVersion) {
        return chordSectionMap.get(sectionVersion);
    }

    public final ChordSection getChordSection(@Nonnull ChordSectionLocation chordSectionLocation) {
        if (chordSectionLocation == null)
            return null;
        return chordSectionMap.get(chordSectionLocation.getSectionVersion());
    }

    private enum UpperCaseState {
        initial,
        flatIsPossible,
        normal;
    }

    public static final String entryToUppercase(String entry) {
        StringBuilder sb = new StringBuilder();

        UpperCaseState state = UpperCaseState.initial;
        for (int i = 0; i < entry.length(); i++) {
            char c = entry.charAt(i);
            switch (state) {
                case flatIsPossible:
                    if (c == 'b') {
                        state = UpperCaseState.initial;
                        sb.append(c);
                        break;
                    }
                    //  fall through
                case initial:
                    if (c >= 'a' && c <= 'g') {
                        String test = entry.substring(i);
                        boolean isChordDescriptor = false;
                        for (ChordDescriptor chordDescriptor : ChordDescriptor.values()) {
                            String cdString = chordDescriptor.toString();
                            if (cdString.length() > 0 && test.startsWith(cdString)) {
                                isChordDescriptor = true;
                                break;
                            }
                        }
                        if (isChordDescriptor == false) {
                            //  map the chord to upper case
                            c = Character.toUpperCase(c);
                        }
                    }
                    state = (c >= 'A' && c <= 'G') ? UpperCaseState.flatIsPossible : UpperCaseState.normal;
                    //  fall through
                case normal:
                    //  reset on sequential reset characters
                    if (Character.isWhitespace(c)
                            || c == '/'
                            || c == ':'
                            || c == '#'
                            || c == MusicConstant.flatChar
                            || c == MusicConstant.sharpChar
                    )
                        state = UpperCaseState.initial;

                    sb.append(c);
                    break;
            }

        }
        return sb.toString();
    }

    /**
     * Parse the current string representation of the song's chords into the song internal structures.
     *
     * @param chords string of chords in markup form
     * @throws ParseException thrown if parsing fails
     */
    protected final void parseChords(final String chords)
            throws ParseException {
        measureNodes = new ArrayList<>();
        chordSectionMap = new HashMap<>();
        clearCachedValues();  //  force lazy eval

        if (chords != null) {
            logger.finer("parseChords for: " + getTitle());
            TreeSet<ChordSection> emptyChordSections = new TreeSet<>();
            MarkedString markedString = new MarkedString(chords);
            ChordSection chordSection;
            while (!markedString.isEmpty()) {
                Util.stripLeadingWhitespace(markedString);
                if (markedString.isEmpty())
                    break;
                logger.finest(markedString.toString());

                try {
                    chordSection = ChordSection.parse(markedString, beatsPerBar, false);
                    if (chordSection.getPhrases().isEmpty())
                        emptyChordSections.add(chordSection);
                    else if (!emptyChordSections.isEmpty()) {
                        //  share the common measure sequence items
                        for (ChordSection wasEmptyChordSection : emptyChordSections) {
                            wasEmptyChordSection.setPhrases(chordSection.getPhrases());
                            chordSectionMap.put(wasEmptyChordSection.getSectionVersion(), wasEmptyChordSection);
                        }
                        emptyChordSections.clear();
                    }
                    measureNodes.add(chordSection);
                    chordSectionMap.put(chordSection.getSectionVersion(), chordSection);
                    clearCachedValues();
                } catch (ParseException pex) {
                    //  try some repair
                    computeSongMoments();
                    computeDuration();

                    logger.finest(logGrid());
                    throw pex;
                }
            }
        }

        setDefaultCurrentChordLocation();

        logger.finest(logGrid());
    }

    /**
     * Will always return something, even if errors have to be commented out
     *
     * @param entry the human generate string entry for chords in markup form
     * @return a list of measure nodes parsed.  note that individual measures are likely to be strung together as a phrase.
     */
    public final ArrayList<MeasureNode> parseChordEntry(final String entry) {
        ArrayList<MeasureNode> ret = new ArrayList<>();

        if (entry != null) {
            logger.finer("parseChordEntry: " + entry);
            TreeSet<ChordSection> emptyChordSections = new TreeSet<>();
            MarkedString markedString = new MarkedString(entry);
            ChordSection chordSection;
            int phaseIndex = 0;
            while (!markedString.isEmpty()) {
                Util.stripLeadingWhitespace(markedString);
                if (markedString.isEmpty())
                    break;
                logger.finest(markedString.toString());

                int mark = markedString.mark();
                try {
                    chordSection = ChordSection.parse(markedString, beatsPerBar, true);

                    //  look for multiple sections defined at once
                    if (chordSection.getPhrases().isEmpty())
                        emptyChordSections.add(chordSection);
                    else if (!emptyChordSections.isEmpty()) {
                        //  share the common measure sequence items
                        for (ChordSection wasEmptyChordSection : emptyChordSections) {
                            wasEmptyChordSection.setPhrases(chordSection.getPhrases());
                        }
                        emptyChordSections.clear();
                    }
                    ret.add(chordSection);
                    continue;
                } catch (ParseException pex) {
                    markedString.resetToMark(mark);
                }
                try {
                    ret.add(MeasureRepeat.parse(markedString, phaseIndex, beatsPerBar, null));
                    phaseIndex++;
                    continue;
                } catch (ParseException pex) {
                    markedString.resetToMark(mark);
                }
                try {
                    ret.add(Phrase.parse(markedString, phaseIndex, beatsPerBar, getCurrentChordSectionLocationMeasure()));
                    phaseIndex++;
                    continue;
                } catch (ParseException pex) {
                    markedString.resetToMark(mark);
                }
                try {
                    ret.add(Measure.parse(markedString, beatsPerBar, getCurrentChordSectionLocationMeasure()));
                    continue;
                } catch (ParseException pex) {
                    markedString.resetToMark(mark);
                }
                try {
                    ret.add(MeasureComment.parse(markedString));
                    phaseIndex++;
                    continue;
                } catch (ParseException pex) {
                    markedString.resetToMark(mark);
                }

                //  entry not understood, force it to be a comment
                {
                    int commentIndex = markedString.indexOf(" ");
                    if (commentIndex < 0) {
                        ret.add(new MeasureComment(markedString.toString()));
                        break;
                    } else {
                        ret.add(new MeasureComment(markedString.remainingStringLimited(commentIndex)));
                        markedString.consume(commentIndex);
                    }
                }
            }
            for (ChordSection wasEmptyChordSection : emptyChordSections)
                ret.add(wasEmptyChordSection);
        }

        return ret;
    }

    private void setDefaultCurrentChordLocation() {
        currentChordSectionLocation = null;

        TreeSet<ChordSection> sortedChordSections = new TreeSet<>(chordSectionMap.values());
        if (sortedChordSections.isEmpty())
            return;

        ChordSection chordSection = sortedChordSections.last();
        if (chordSection != null) {
            ArrayList<Phrase> measureSequenceItems = chordSection.getPhrases();
            if (measureSequenceItems != null && !measureSequenceItems.isEmpty()) {
                Phrase lastPhrase = measureSequenceItems.get(measureSequenceItems.size() - 1);
                currentChordSectionLocation = new ChordSectionLocation(chordSection.getSectionVersion(),
                        measureSequenceItems.size() - 1, lastPhrase.size() - 1);
            }
        }
    }

    private final void calcChordMaps() {
        getChordSectionLocationGrid();  //  use location grid to force them all in lazy eval
    }

    public final Grid<ChordSectionLocation> getChordSectionLocationGrid() {
        //  support lazy eval
        if (chordSectionLocationGrid != null)
            return chordSectionLocationGrid;

        Grid<ChordSectionLocation> grid = new Grid<>();
        chordSectionGridCoorinateMap = new HashMap<>();
        gridCoordinateChordSectionLocationMap = new HashMap<>();
        gridChordSectionLocationCoordinateMap = new HashMap<>();

        //  grid each section
        int row = 0;
        int col = 0;
        int measuresPerline = 4;    //  fixme: should be dynamic, read from each phrase
        final int offset = 1;       //  offset of phrase start from section start
        for (ChordSection chordSection : new TreeSet<>(chordSectionMap.values())) {
            //  start each section on it's own line
            if (col != 0) {
                row++;
                col = 0;
            }
            {
                //  grid the section header
                ChordSectionLocation loc = new ChordSectionLocation(chordSection.getSectionVersion());
                GridCoordinate coordinate = new GridCoordinate(row, col);
                chordSectionGridCoorinateMap.put(chordSection.getSectionVersion(), coordinate);
                gridCoordinateChordSectionLocationMap.put(coordinate, loc);
                gridChordSectionLocationCoordinateMap.put(loc, coordinate);
                grid.addTo(col++, row, loc);
            }

            //  grid each phrase
            for (int phraseIndex = 0; phraseIndex < chordSection.getPhrases().size(); phraseIndex++) {
                //  start each phrase on it's own line
                if (col > offset) {
                    row++;
                    col = 0;
                    grid.addTo(col++, row, null);
                }

                Phrase phrase = chordSection.getPhrase(phraseIndex);

                //  grid each measure of the phrase
                boolean repeatExtensionUsed = false;
                int phraseSize = phrase.getMeasures().size();
                if (phraseSize == 0 && phrase.isRepeat()) {
                    //  special case: deal with empty repeat
                    //  fill row to measures per line
                    while (col < offset + measuresPerline)
                        grid.addTo(col++, row, null);
                    {
                        //  add repeat indicator
                        ChordSectionLocation loc = new ChordSectionLocation(chordSection.getSectionVersion(), phraseIndex);
                        GridCoordinate coordinate = new GridCoordinate(row, col);
                        gridCoordinateChordSectionLocationMap.put(coordinate, loc);
                        gridChordSectionLocationCoordinateMap.put(loc, coordinate);
                        grid.addTo(col++, row, loc);
                    }
                } else {
                    for (int measureIndex = 0; measureIndex < phraseSize; measureIndex++) {

                        //  limit line length to the measures per line
                        if (col >= offset + measuresPerline) {
                            //  put an end of line marker on multiline repeats
                            if (phrase.isRepeat()) {
                                grid.addTo(col++, row, new ChordSectionLocation(chordSection.getSectionVersion(), phraseIndex,
                                        (repeatExtensionUsed
                                                ? ChordSectionLocation.Marker.repeatMiddleRight
                                                : ChordSectionLocation.Marker.repeatUpperRight
                                        )));
                                repeatExtensionUsed = true;
                            }
                            if (measureIndex < phraseSize) {
                                row++;
                                col = 0;
                                grid.addTo(col++, row, null);
                            }
                        }

                        {
                            //  grid the measure with it's location
                            ChordSectionLocation loc = new ChordSectionLocation(chordSection.getSectionVersion(), phraseIndex, measureIndex);
                            GridCoordinate coordinate = new GridCoordinate(row, col);
                            gridCoordinateChordSectionLocationMap.put(coordinate, loc);
                            gridChordSectionLocationCoordinateMap.put(loc, coordinate);
                            grid.addTo(col++, row, loc);
                        }

                        //  put the repeat on the end of the last line of the repeat
                        if (phrase.isRepeat() && measureIndex == phraseSize - 1) {
                            //  fill row to measures per line
                            while (col < offset + measuresPerline)
                                grid.addTo(col++, row, null);

                            //  close the multiline repeat marker
                            if (repeatExtensionUsed) {
                                ChordSectionLocation loc = new ChordSectionLocation(chordSection.getSectionVersion(), phraseIndex,
                                        ChordSectionLocation.Marker.repeatLowerRight);
                                GridCoordinate coordinate = new GridCoordinate(row, col);
                                gridCoordinateChordSectionLocationMap.put(coordinate, loc);
                                gridChordSectionLocationCoordinateMap.put(loc, coordinate);
                                grid.addTo(col++, row, loc);

                                repeatExtensionUsed = false;
                            }
                            {
                                //  add repeat indicator
                                ChordSectionLocation loc = new ChordSectionLocation(chordSection.getSectionVersion(), phraseIndex);
                                GridCoordinate coordinate = new GridCoordinate(row, col);
                                gridCoordinateChordSectionLocationMap.put(coordinate, loc);
                                gridChordSectionLocationCoordinateMap.put(loc, coordinate);
                                grid.addTo(col++, row, loc);
                            }
                        }
                    }
                }
            }
        }

        if (logger.getLevel() != null && logger.getLevel().intValue() <= Level.FINEST.intValue()) {
            logger.fine("gridCoordinateChordSectionLocationMap: ");
            for (GridCoordinate coordinate : gridCoordinateChordSectionLocationMap.keySet()) {
                logger.fine("  " + coordinate.toString() + ": " + gridCoordinateChordSectionLocationMap.get(coordinate));
            }
        }

        chordSectionLocationGrid = grid;
        logger.finer(grid.toString());
        return chordSectionLocationGrid;
    }

    public ChordSectionLocation getLastChordSectionLocation() {
        Grid<ChordSectionLocation> grid = getChordSectionLocationGrid();
        if (grid == null || grid.isEmpty())
            return null;
        ArrayList<ChordSectionLocation> row = grid.getRow(grid.getRowCount() - 1);
        return grid.get(grid.getRowCount() - 1, row.size() - 1);
    }

    private HashMap<SectionVersion, GridCoordinate> getChordSectionGridCoorinateMap() {
        // force grid population from lazy eval
        if (chordSectionLocationGrid == null)
            getChordSectionLocationGrid();
        return chordSectionGridCoorinateMap;
    }

    private final void clearCachedValues() {
        chordSectionLocationGrid = null;
        complexity = 0;
        chordsAsMarkup = null;
        songMoments = null;
    }

    protected final String chordsToTransportString() {
        if (chordsAsMarkup != null)
            return chordsAsMarkup;
        StringBuilder sb = new StringBuilder();

        for (ChordSection chordSection : new TreeSet<>(chordSectionMap.values())) {
            sb.append(chordSection.toString());
            sb.append("\n");
        }
        chordsAsMarkup = sb.toString();
        return chordsAsMarkup;
    }

    public final String toMarkup() {
        if (chordsAsMarkup != null)
            return chordsAsMarkup;

        StringBuilder sb = new StringBuilder();

        TreeSet<SectionVersion> sortedSectionVersions = new TreeSet<>(chordSectionMap.keySet());
        TreeSet<SectionVersion> completedSectionVersions = new TreeSet<>();
        TreeSet<SectionVersion> currentSectionVersions = new TreeSet<>();

        //  markup by section version order
        for (SectionVersion sectionVersion : sortedSectionVersions) {
            //  don't repeat anything
            if (completedSectionVersions.contains(sectionVersion))
                continue;
            completedSectionVersions.add(sectionVersion);

            //  find all section versions with the same chords
            ChordSection chordSection = chordSectionMap.get(sectionVersion);
            currentSectionVersions.clear();
            for (SectionVersion otherSectionVersion : sortedSectionVersions) {
                if (chordSection.getPhrases().equals(chordSectionMap.get(otherSectionVersion).getPhrases())) {
                    currentSectionVersions.add(otherSectionVersion);
                    completedSectionVersions.add(otherSectionVersion);
                }
            }

            //  list the section versions for this chord section
            for (SectionVersion currentSectionVersion : currentSectionVersions) {
                sb.append(currentSectionVersion.toString());
                sb.append(" ");
            }
            //  chord section phrases (only) to output
            sb.append(chordSection.phrasesToMarkup());
            sb.append(" ");    //  for human readability only
        }
        chordsAsMarkup = sb.toString();
        return chordsAsMarkup;
    }

    /**
     * Add the given section version to the song chords
     *
     * @param sectionVersion the given section to add
     * @return true if the section version was added
     */
    public final boolean addSectionVersion(SectionVersion sectionVersion) {
        if (sectionVersion == null || chordSectionMap.containsKey(sectionVersion))
            return false;
        chordSectionMap.put(sectionVersion, new ChordSection(sectionVersion));
        clearCachedValues();
        setCurrentChordSectionLocation(new ChordSectionLocation(sectionVersion));
        setCurrentMeasureEditType(MeasureEditType.append);
        return true;
    }


    private final boolean deleteCurrentChordSectionLocation() {

        setCurrentMeasureEditType(MeasureEditType.delete);  //  tell the world

        preMod(null);

        //  deal with deletes
        ChordSectionLocation location = getCurrentChordSectionLocation();


        //  find the named chord section
        ChordSection chordSection = getChordSection(location);
        if (chordSection == null) {
            postMod();
            return false;
        }

        if (chordSection.getPhrases().isEmpty()) {
            chordSection.getPhrases().add(new Phrase(new ArrayList<>(), 0));
        }

        Phrase phrase;
        try {
            phrase = chordSection.getPhrase(location.getPhraseIndex());
        } catch (IndexOutOfBoundsException iob) {
            phrase = chordSection.getPhrases().get(0);    //  use the default empty list
        }

        boolean ret = false;

        if (location.isMeasure()) {
            ret = phrase.edit(MeasureEditType.delete, location.getMeasureIndex(), null);
        } else if (location.isPhrase()) {
            ret = chordSection.deletePhrase(location.getPhraseIndex());
            if (ret) {
                //  move the current location if required
                if (location.getPhraseIndex() >= chordSection.getPhrases().size()) {
                    if (chordSection.getPhrases().isEmpty())
                        location = new ChordSectionLocation(chordSection.getSectionVersion());
                    else {
                        int i = chordSection.getPhrases().size() - 1;
                        phrase = chordSection.getPhrase(i);
                        int m = phrase.getMeasures().size() - 1;
                        location = new ChordSectionLocation(chordSection.getSectionVersion(), i, m);
                    }
                }
            }
        } else if (location.isSection()) {
            //  find the section prior to the one being deleted
            TreeSet<SectionVersion> sortedSectionVersions = new TreeSet<>(chordSectionMap.keySet());
            SectionVersion nextSectionVersion = sortedSectionVersions.lower(chordSection.getSectionVersion());
            ret = (chordSectionMap.remove(chordSection.getSectionVersion()) != null);
            if (ret) {
                //  move deleted current to end of previous section
                if (nextSectionVersion == null) {
                    sortedSectionVersions = new TreeSet<>(chordSectionMap.keySet());
                    nextSectionVersion = (sortedSectionVersions.isEmpty() ? null : sortedSectionVersions.first());
                }
                if (nextSectionVersion != null) {
                    location = findChordSectionLocation(chordSectionMap.get(nextSectionVersion));
                }
            }
        }
        return standardEditCleanup(ret, location);
    }

    private final void preMod(MeasureNode measureNode) {
        logger.info("startingChords(\"" + toMarkup() + "\");");
        logger.info(" pre(MeasureEditType." + getCurrentMeasureEditType().name()
                + ", \"" + getCurrentChordSectionLocation().toString() + "\""
                + ", \""
                + (getCurrentChordSectionLocationMeasureNode() == null
                ? "null"
                : getCurrentChordSectionLocationMeasureNode().toMarkup()) + "\""
                + ", \"" + (measureNode == null ? "null" : measureNode.toMarkup()) + "\");");
    }

    private final void postMod() {
        logger.info("resultChords(\"" + toMarkup() + "\");");
        logger.info("post(MeasureEditType." + getCurrentMeasureEditType().name()
                + ", \"" + getCurrentChordSectionLocation().toString() + "\""
                + ", \"" + (getCurrentChordSectionLocationMeasureNode() == null
                ? "null"
                : getCurrentChordSectionLocationMeasureNode().toMarkup())
                + "\");");
    }


    public final boolean edit(ArrayList<MeasureNode> measureNodes) {
        if (measureNodes == null || measureNodes.isEmpty())
            return false;

        for (MeasureNode measureNode : measureNodes) {
            if (!edit(measureNode))
                return false;
        }
        return true;
    }

    public final boolean deleteCurrentSelection() {
        setCurrentMeasureEditType(MeasureEditType.delete);
        return edit((MeasureNode) null);
    }

    /**
     * Edit the given measure in or out of the song based on the data from the edit location.
     *
     * @param measureNode the measure in question
     * @return true if the edit was performed
     */
    public final boolean edit(@Nonnull MeasureNode measureNode) {
        MeasureEditType editType = getCurrentMeasureEditType();

        if (editType == MeasureEditType.delete)
            return deleteCurrentChordSectionLocation();

        preMod(measureNode);

        if (measureNode == null) {
            postMod();
            return false;
        }

        ChordSectionLocation location = getCurrentChordSectionLocation();


        //  find the named chord section
        ChordSection chordSection = getChordSection(location);
        if (chordSection == null) {
            switch (measureNode.getMeasureNodeType()) {
                case section:
                    chordSection = (ChordSection) measureNode;
                    break;
                default:
                    chordSection = chordSectionMap.get(SectionVersion.getDefault());
                    if (chordSection == null) {
                        chordSection = ChordSection.getDefault();
                        chordSectionMap.put(chordSection.getSectionVersion(), chordSection);
                    }
                    break;
            }
        }

        //  default to insert if
        if (chordSection.getPhrases().isEmpty()) {
            chordSection.getPhrases().add(new Phrase(new ArrayList<>(), 0));
            //fixme?  editType = MeasureEditType.insert;
        }

        Phrase phrase = null;
        try {
            phrase = chordSection.getPhrase(location.getPhraseIndex());
        } catch (Exception iob) {
            if (!chordSection.isEmpty())
                phrase = chordSection.getPhrases().get(0);    //  use the default empty list
        }

        boolean ret = false;

        //  handle situations by the type of measure node being added
        ChordSectionLocation newLocation;
        ChordSection newChordSection;
        MeasureRepeat newRepeat;
        Phrase newPhrase;
        switch (measureNode.getMeasureNodeType()) {
            case section:
                switch (editType) {
                    default:
                        //  all sections replace themselves
                        newChordSection = (ChordSection) measureNode;
                        chordSectionMap.put(newChordSection.getSectionVersion(), newChordSection);
                        ret = true;
                        location = new ChordSectionLocation(newChordSection.getSectionVersion());
                        break;
                    case delete:
                        //  find the section prior to the one being deleted
                        TreeSet<SectionVersion> sortedSectionVersions = new TreeSet<>(chordSectionMap.keySet());
                        SectionVersion nextSectionVersion = sortedSectionVersions.lower(chordSection.getSectionVersion());
                        ret = (chordSectionMap.remove(chordSection.getSectionVersion()) != null);
                        if (ret) {
                            //  move deleted current to end of previous section
                            if (nextSectionVersion == null) {
                                sortedSectionVersions = new TreeSet<>(chordSectionMap.keySet());
                                nextSectionVersion = sortedSectionVersions.first();
                            }
                            if (nextSectionVersion != null) {
                                location = new ChordSectionLocation(nextSectionVersion);
                            } else
                                ;// fixme: set location to empty location
                        }
                        break;
                }
                return standardEditCleanup(ret, location);

            case repeat:
                newRepeat = (MeasureRepeat) measureNode;
                if (newRepeat.isEmpty()) {
                    //  empty repeat
                    if (phrase.isRepeat()) {
                        //  change repeats
                        MeasureRepeat repeat = (MeasureRepeat) phrase;
                        if (newRepeat.getRepeats() < 2) {
                            //  convert repeat to phrase
                            newPhrase = new Phrase(repeat.getMeasures(), location.getPhraseIndex());
                            location = new ChordSectionLocation(chordSection.getSectionVersion(),
                                    location.getPhraseIndex(), newPhrase.getMeasures().size() - 1);
                            logger.info("new loc: " + location.toString());
                            return standardEditCleanup(chordSection.deletePhrase(newPhrase.getPhraseIndex())
                                    && chordSection.add(newPhrase.getPhraseIndex(), newPhrase), location);
                        }
                        repeat.setRepeats(newRepeat.getRepeats());
                        return standardEditCleanup(true, location);
                    }
                    if (!phrase.isEmpty()) {
                        //  convert phrase line to a repeat
                        GridCoordinate minGridCoordinate = getGridCoordinate(location);
                        minGridCoordinate = new GridCoordinate(minGridCoordinate.getRow(), 1);
                        MeasureNode minMeasureNode = findMeasureNode(minGridCoordinate);
                        ChordSectionLocation minLocation = getChordSectionLocation(minGridCoordinate);
                        GridCoordinate maxGridCoordinate = getGridCoordinate(location);
                        maxGridCoordinate = new GridCoordinate(maxGridCoordinate.getRow(), chordSectionLocationGrid.getRow(maxGridCoordinate.getRow()).size() - 1);
                        MeasureNode maxMeasureNode = findMeasureNode(maxGridCoordinate);
                        ChordSectionLocation maxLocation = getChordSectionLocation(maxGridCoordinate);
                        logger.info("min: " + minGridCoordinate.toString() + " " + minMeasureNode.toMarkup() + " " + minLocation.getMeasureIndex());
                        logger.info("max: " + maxGridCoordinate.toString() + " " + maxMeasureNode.toMarkup() + " " + maxLocation.getMeasureIndex());

                        //  delete the old
                        int phraseIndex = phrase.getPhraseIndex();
                        chordSection.deletePhrase(phraseIndex);
                        //  replace the old early part
                        if (minLocation.getMeasureIndex() > 0) {
                            chordSection.add(phraseIndex, new Phrase(phrase.getMeasures().subList(0, minLocation.getMeasureIndex()),
                                    phraseIndex));
                            phraseIndex++;
                        }
                        //  replace the sub-phrase with a repeat
                        {
                            MeasureRepeat repeat = new MeasureRepeat(phrase.getMeasures().subList(minLocation.getMeasureIndex(),
                                    maxLocation.getMeasureIndex() + 1), phraseIndex, newRepeat.getRepeats());
                            chordSection.add(phraseIndex, repeat);
                            location = new ChordSectionLocation(chordSection.getSectionVersion(), phraseIndex);
                            phraseIndex++;
                        }
                        //  replace the old late part
                        if (maxLocation.getMeasureIndex() < phrase.getMeasures().size() - 1) {
                            chordSection.add(phraseIndex, new Phrase(
                                    phrase.getMeasures().subList(maxLocation.getMeasureIndex() + 1, phrase.getMeasures().size()),
                                    phraseIndex));
                            //phraseIndex++;
                        }
                        return standardEditCleanup(true, location);
                    }
                } else {
                    newPhrase = newRepeat;

                    //  demote x1 repeat to phrase
                    if (newRepeat.getRepeats() < 2)
                        newPhrase = new Phrase(newRepeat.getMeasures(), newRepeat.getPhraseIndex());

                    //  non-empty repeat
                    switch (editType) {
                        case delete:
                            return standardEditCleanup(chordSection.deletePhrase(phrase.getPhraseIndex()), location);
                        case append:
                            newPhrase.setPhraseIndex(phrase.getPhraseIndex() + 1);
                            return standardEditCleanup(chordSection.add(phrase.getPhraseIndex() + 1, newPhrase),
                                    new ChordSectionLocation(chordSection.getSectionVersion(), phrase.getPhraseIndex() + 1));
                        case insert:
                            newPhrase.setPhraseIndex(phrase.getPhraseIndex());
                            return standardEditCleanup(chordSection.add(phrase.getPhraseIndex(), newPhrase), location);
                        case replace:
                            newPhrase.setPhraseIndex(phrase.getPhraseIndex());
                            return standardEditCleanup(chordSection.deletePhrase(phrase.getPhraseIndex())
                                    && chordSection.add(newPhrase.getPhraseIndex(), newPhrase), location);
                    }
                }
                break;

            case phrase:
                newPhrase = (Phrase) measureNode;
                int phaseIndex = 0;
                switch (editType) {
                    case append:
                        if (location == null) {
                            if (chordSection.getPhraseCount() == 0) {
                                //  append as first phrase
                                location = new ChordSectionLocation(chordSection.getSectionVersion(), 0, newPhrase.size() - 1);
                                newPhrase.setPhraseIndex(phaseIndex);
                                return standardEditCleanup(chordSection.add(phaseIndex, newPhrase), location);
                            }

                            //  last of section
                            Phrase lastPhrase = chordSection.lastPhrase();
                            switch (lastPhrase.getMeasureNodeType()) {
                                case phrase:
                                    location = new ChordSectionLocation(chordSection.getSectionVersion(),
                                            lastPhrase.getPhraseIndex(), lastPhrase.size() + newPhrase.size() - 1);
                                    return standardEditCleanup(lastPhrase.add(newPhrase.getMeasures()), location);
                            }
                            phaseIndex = chordSection.getPhraseCount();
                            location = new ChordSectionLocation(chordSection.getSectionVersion(), phaseIndex, lastPhrase.size());
                            newPhrase.setPhraseIndex(phaseIndex);
                            return standardEditCleanup(chordSection.add(phaseIndex, newPhrase), location);
                        }
                        if (chordSection.isEmpty()) {
                            location = new ChordSectionLocation(chordSection.getSectionVersion(), phaseIndex, newPhrase.size() - 1);
                            newPhrase.setPhraseIndex(phaseIndex);
                            return standardEditCleanup(chordSection.add(phaseIndex, newPhrase), location);
                        }

                        if (location.hasMeasureIndex()) {
                            newLocation = new ChordSectionLocation(chordSection.getSectionVersion(),
                                    phrase.getPhraseIndex(), location.getMeasureIndex() + newPhrase.size());
                            return standardEditCleanup(phrase.edit(editType, location.getMeasureIndex(), newPhrase), newLocation);
                        }
                        newLocation = new ChordSectionLocation(chordSection.getSectionVersion(),
                                phrase.getPhraseIndex(), phrase.getMeasures().size() + newPhrase.size() - 1);
                        return standardEditCleanup(phrase.add(newPhrase.getMeasures()), newLocation);

                    case insert:
                        if (location == null) {
                            if (chordSection.getPhraseCount() == 0) {
                                //  append as first phrase
                                location = new ChordSectionLocation(chordSection.getSectionVersion(), 0, newPhrase.size() - 1);
                                newPhrase.setPhraseIndex(phaseIndex);
                                return standardEditCleanup(chordSection.add(phaseIndex, newPhrase), location);
                            }

                            //  first of section
                            Phrase firstPhrase = chordSection.getPhrase(0);
                            switch (firstPhrase.getMeasureNodeType()) {
                                case phrase:
                                    location = new ChordSectionLocation(chordSection.getSectionVersion(),
                                            firstPhrase.getPhraseIndex(), 0);
                                    return standardEditCleanup(firstPhrase.add(newPhrase.getMeasures()), location);
                            }

                            phaseIndex = 0;
                            location = new ChordSectionLocation(chordSection.getSectionVersion(), phaseIndex, firstPhrase.size());
                            newPhrase.setPhraseIndex(phaseIndex);
                            return standardEditCleanup(chordSection.add(phaseIndex, newPhrase), location);
                        }
                        if (chordSection.isEmpty()) {
                            location = new ChordSectionLocation(chordSection.getSectionVersion(), phaseIndex, newPhrase.size() - 1);
                            newPhrase.setPhraseIndex(phaseIndex);
                            return standardEditCleanup(chordSection.add(phaseIndex, newPhrase), location);
                        }

                        if (location.hasMeasureIndex()) {
                            newLocation = new ChordSectionLocation(chordSection.getSectionVersion(),
                                    phrase.getPhraseIndex(), location.getMeasureIndex() + newPhrase.size() - 1);
                            return standardEditCleanup(phrase.edit(editType, location.getMeasureIndex(), newPhrase), newLocation);
                        }

                        //  insert new phrase in front of existing phrase
                        newLocation = new ChordSectionLocation(chordSection.getSectionVersion(),
                                phrase.getPhraseIndex(), newPhrase.size() - 1);
                        return standardEditCleanup(phrase.add(0, newPhrase.getMeasures()), newLocation);
                    case replace:
                        if (location != null) {
                            if (location.hasPhraseIndex()) {
                                if (location.hasMeasureIndex()) {
                                    location = new ChordSectionLocation(chordSection.getSectionVersion(), phaseIndex,
                                            location.getMeasureIndex() + newPhrase.size() - 1);
                                    return standardEditCleanup(phrase.edit(
                                            editType, location.getMeasureIndex(), newPhrase), location);
                                }
                                //  delete the phrase before replacing it
                                phaseIndex = location.getPhraseIndex();
                                location = new ChordSectionLocation(chordSection.getSectionVersion(),
                                        phaseIndex, newPhrase.getMeasures().size() - 1);
                                return standardEditCleanup(chordSection.deletePhrase(phaseIndex)
                                        && chordSection.add(phaseIndex, newPhrase), location);
                            }
                            break;
                        }
                        phaseIndex = (location != null && location.hasPhraseIndex() ? location.getPhraseIndex() : 0);
                        break;
                    default:
                        phaseIndex = (location != null && location.hasPhraseIndex() ? location.getPhraseIndex() : 0);
                        break;
                }
                newPhrase.setPhraseIndex(phaseIndex);
                location = new ChordSectionLocation(chordSection.getSectionVersion(), phaseIndex, newPhrase.size() - 1);
                return standardEditCleanup(chordSection.add(phaseIndex, newPhrase), location);

            case measure:
            case comment:
                //  add measure to current phrase
                if (location.hasMeasureIndex()) {
                    newLocation = location;
                    switch (editType) {
                        case append:
                            newLocation = location.nextMeasureIndexLocation();
                            break;
                    }
                    return standardEditCleanup(phrase.edit(editType, newLocation.getMeasureIndex(), measureNode), newLocation);
                }

                //  add measure to chordSection by creating a new phase
                if (location.hasPhraseIndex()) {
                    ArrayList<Measure> measures = new ArrayList<>();
                    measures.add((Measure) measureNode);
                    newPhrase = new Phrase(measures, location.getPhraseIndex());
                    switch (editType) {
                        case delete:
                            break;
                        case append:
                            newPhrase.setPhraseIndex(phrase.getPhraseIndex());
                            return standardEditCleanup(chordSection.add(phrase.getPhraseIndex(), newPhrase),
                                    location.nextMeasureIndexLocation());
                        case insert:
                            newPhrase.setPhraseIndex(phrase.getPhraseIndex());
                            return standardEditCleanup(chordSection.add(phrase.getPhraseIndex(), newPhrase), location);
                        case replace:
                            newPhrase.setPhraseIndex(phrase.getPhraseIndex());
                            return standardEditCleanup(chordSection.deletePhrase(phrase.getPhraseIndex())
                                    && chordSection.add(newPhrase.getPhraseIndex(), newPhrase), location);
                    }
                }
                break;
        }


        //  edit measure node into location
        switch (editType) {
            case insert:
                switch (measureNode.getMeasureNodeType()) {
                    case repeat:
                    case phrase:
                        ret = chordSection.insert(location.getPhraseIndex(), measureNode);
                        break;
                }
                //  no location change
                standardEditCleanup(ret, location);
                break;

            case append:
                //  promote marker to repeat
                try {
                    Measure refMeasure = phrase.getMeasure(location.getMeasureIndex());
                    if (refMeasure instanceof MeasureRepeatMarker && phrase.isRepeat()) {
                        MeasureRepeat measureRepeat = (MeasureRepeat) phrase;
                        if (refMeasure == measureRepeat.getRepeatMarker()) {
                            //  appending at the repeat marker forces the section to add a sequenceItem list after the repeat
                            int phraseIndex = chordSection.indexOf(measureRepeat) + 1;
                            newPhrase = new Phrase(new ArrayList<>(), phraseIndex);
                            chordSection.getPhrases().add(phraseIndex, newPhrase);
                            phrase = newPhrase;
                        }
                    }
                } catch (IndexOutOfBoundsException iob) {
                    //  ignore attempt
                }

                if (location.isSection()) {
                    switch (measureNode.getMeasureNodeType()) {
                        case section:
                            SectionVersion sectionVersion = location.getSectionVersion();
                            return standardEditCleanup((chordSectionMap.put(sectionVersion,
                                    (ChordSection) measureNode) != null), location.nextMeasureIndexLocation());
                        case phrase:
                        case repeat:
                            return standardEditCleanup(chordSection.add(location.getPhraseIndex(),
                                    (Phrase) measureNode), location);
                    }
                }
                if (location.isPhrase()) {
                    switch (measureNode.getMeasureNodeType()) {
                        case repeat:
                        case phrase:
                            chordSection.getPhrases().add(location.getPhraseIndex(), (Phrase) measureNode);
                            return standardEditCleanup(true, location);
                    }
                    break;
                }

                break;

            case delete:
                //  note: measureNode is ignored, and should be ignored
                if (location.isMeasure()) {
                    ret = (phrase.delete(location.getMeasureIndex()) != null);
                    if (ret) {
                        if (location.getMeasureIndex() < phrase.size()) {
                            location = new ChordSectionLocation(chordSection.getSectionVersion(), location.getPhraseIndex(), location.getMeasureIndex());
                            measureNode = findMeasureNode(location);
                        } else {
                            if (phrase.size() > 0) {
                                int index = phrase.size() - 1;
                                location = new ChordSectionLocation(chordSection.getSectionVersion(), location.getPhraseIndex(), index);
                                measureNode = findMeasureNode(location);
                            } else {
                                chordSection.deletePhrase(location.getPhraseIndex());
                                if (chordSection.getPhraseCount() > 0) {
                                    location = new ChordSectionLocation(chordSection.getSectionVersion(), 0, chordSection.getPhrase(0).size() - 1);
                                    measureNode = findMeasureNode(location);
                                } else {
                                    //  last phase was deleted
                                    location = new ChordSectionLocation(chordSection.getSectionVersion());
                                    measureNode = findMeasureNode(location);
                                }
                            }
                        }
                    }
                } else if (location.isPhrase()) {
                    ret = chordSection.deletePhrase(location.getPhraseIndex());
                    if (ret) {
                        if (location.getPhraseIndex() > 0) {
                            int index = location.getPhraseIndex() - 1;
                            location = new ChordSectionLocation(chordSection.getSectionVersion(), index, chordSection.getPhrase(index).size() - 1);
                            measureNode = findMeasureNode(location);
                        } else if (chordSection.getPhraseCount() > 0) {
                            location = new ChordSectionLocation(chordSection.getSectionVersion(), 0, chordSection.getPhrase(0).size() - 1);
                            measureNode = findMeasureNode(location);
                        } else {
                            //  last one was deleted
                            location = new ChordSectionLocation(chordSection.getSectionVersion());
                            measureNode = findMeasureNode(location);
                        }
                    }
                } else if (location.isSection()) {

                }
                standardEditCleanup(ret, location);
                break;
        }
        postMod();
        return ret;
    }

    private final boolean standardEditCleanup(boolean ret, ChordSectionLocation location) {
        if (ret) {
            chordsAsMarkup = null;  //  force lazy re-compute of markup when required

            setCurrentChordSectionLocation(location);

            switch (getCurrentMeasureEditType()) {
                case replace:
                case delete:
                    if (getCurrentChordSectionLocationMeasureNode() == null)
                        setCurrentMeasureEditType(MeasureEditType.append);
                    break;
                default:
                    setCurrentMeasureEditType(MeasureEditType.append);
                    break;
            }
        }
        postMod();
        return ret;
    }

    /**
     * Find the measure sequence item for the given measure (i.e. the measure's parent container).
     *
     * @param measure the measure referenced
     * @return the measure's sequence item
     */
    public final Phrase findPhrase(Measure measure) {
        if (measure == null)
            return null;

        ChordSection chordSection = findChordSection(measure);
        if (chordSection == null)
            return null;
        for (Phrase msi : chordSection.getPhrases()) {
            for (Measure m : msi.getMeasures())
                if (m == measure)
                    return msi;
        }
        return null;
    }

    /**
     * Find the chord section for the given measure node.
     *
     * @param measureNode he given measure node
     * @return the chord section found
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

    public final ChordSectionLocation findChordSectionLocation(MeasureNode measureNode) {
        if (measureNode == null)
            return null;

        Phrase phrase;
        try {
            ChordSection chordSection = findChordSection(measureNode);
            switch (measureNode.getMeasureNodeType()) {
                case section:
                    return new ChordSectionLocation(chordSection.getSectionVersion());
                case repeat:
                case phrase:
                    phrase = chordSection.findPhrase(measureNode);
                    return new ChordSectionLocation(chordSection.getSectionVersion(), phrase.getPhraseIndex());
                case decoration:
                case comment:
                case measure:
                    phrase = chordSection.findPhrase(measureNode);
                    return new ChordSectionLocation(chordSection.getSectionVersion(), phrase.getPhraseIndex(),
                            phrase.findMeasureNodeIndex(measureNode));
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public final ChordSectionLocation getChordSectionLocation(GridCoordinate gridCoordinate) {
        calcChordMaps();
        return gridCoordinateChordSectionLocationMap.get(gridCoordinate);
    }

    public final ChordSectionLocation getChordSectionLocation(SectionVersion sectionVersion) {
        ChordSection chordSection = getChordSection(sectionVersion);
        if (chordSection == null)
            return null;
        return chordSection.getChordSectionLocation();
    }

    public final GridCoordinate getGridCoordinate(ChordSectionLocation chordSectionLocation) {
        calcChordMaps();

//        logger.finer("gridCoordinateChordSectionLocationMap: ");
//        for ( GridCoordinate coordinate:gridCoordinateChordSectionLocationMap.keySet()){
//            logger.finer("  "+coordinate.toString()+": "+gridCoordinateChordSectionLocationMap.get(coordinate));
//        }

        return gridChordSectionLocationCoordinateMap.get(chordSectionLocation);
    }

    /**
     * Find the chord section for the given type of chord section
     *
     * @param sectionVersion the chord section version to find
     * @return the chord section to found
     */
    public final ChordSection findChordSection(SectionVersion sectionVersion) {
        if (sectionVersion == null)
            return null;
        return chordSectionMap.get(sectionVersion);   //  get not type safe!!!!
    }

    private final Measure findMeasure(ChordSectionLocation chordSectionLocation) {
        try {
            return chordSectionMap.get(chordSectionLocation.getSectionVersion())
                    .getPhrase(chordSectionLocation.getPhraseIndex())
                    .getMeasure(chordSectionLocation.getMeasureIndex());
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            return null;
        }
    }

    private final Measure findMeasure(GridCoordinate coordinate) {
        calcChordMaps();
        return findMeasure(gridCoordinateChordSectionLocationMap.get(coordinate));
    }

    private final Measure getCurrentMeasure() {
        return findMeasure(currentChordSectionLocation);
    }

    private final Measure getCurrentChordSectionLocationMeasure() {
        ChordSectionLocation location = getCurrentChordSectionLocation();
        if (location.hasMeasureIndex()) {
            int index = location.getMeasureIndex();
            if (index > 0) {
                location = new ChordSectionLocation(location.getSectionVersion(), location.getPhraseIndex(), index);
                MeasureNode measureNode = findMeasureNode(location);
                if (measureNode != null) {
                    switch (measureNode.getMeasureNodeType()) {
                        case measure:
                            return (Measure) measureNode;
                    }
                }
            }
        }
        return null;
    }

    public final MeasureNode findMeasureNode(GridCoordinate coordinate) {
        calcChordMaps();
        return findMeasureNode(gridCoordinateChordSectionLocationMap.get(coordinate));
    }

    public final MeasureNode findMeasureNode(ChordSectionLocation chordSectionLocation) {
        try {
            ChordSection chordSection = chordSectionMap.get(chordSectionLocation.getSectionVersion());
            if (chordSectionLocation.isSection())
                return chordSection;

            Phrase phrase = chordSection.getPhrase(chordSectionLocation.getPhraseIndex());
            if (chordSectionLocation.isPhrase()) {
                switch (chordSectionLocation.getMarker()) {
                    default:
                        return MeasureRepeatExtension.get(chordSectionLocation.getMarker());
                    case none:
                        return phrase;
                }
            }

            return phrase.getMeasure(chordSectionLocation.getMeasureIndex());
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            return null;
        }
    }

    final MeasureNode getCurrentMeasureNode() {
        return findMeasureNode(currentChordSectionLocation);
    }


    public final ChordSection findChordSection(String s) throws ParseException {
        SectionVersion sectionVersion = SectionVersion.parse(s);
        return chordSectionMap.get(sectionVersion);
    }

    public final ChordSection findChordSection(MarkedString markedString) throws ParseException {
        SectionVersion sectionVersion = SectionVersion.parse(markedString);
        return chordSectionMap.get(sectionVersion);
    }


    public final boolean chordSectionLocationDelete(ChordSectionLocation chordSectionLocation) {
        try {
            ChordSection chordSection = getChordSection(chordSectionLocation.getSectionVersion());
            if (chordSection.deleteMeasure(chordSectionLocation.getPhraseIndex(), chordSectionLocation.getMeasureIndex())) {
                clearCachedValues();
                setCurrentChordSectionLocation(chordSectionLocation);
                return true;
            }
        } catch (NullPointerException npe) {
        }
        return false;
    }

    public final boolean chordSectionDelete(ChordSection chordSection) {
        if (chordSection == null)
            return false;
        boolean ret = chordSectionMap.remove(chordSection) != null;
        clearCachedValues();
        return ret;
    }

    @Deprecated
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

            for (ChordSection chordSection : new TreeSet<>(chordSectionMap.values())) {
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
            for (Phrase msi : chordSection.getPhrases()) {
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
        MarkedString markedString = new MarkedString(rawLyrics);
        while (!markedString.isEmpty()) {
            char c = markedString.charAt(0);
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
                    try {
                        SectionVersion version = SectionVersion.parse(markedString);
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
                    } catch (ParseException pex) {
                        //  ignore
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
            markedString.consume(1);     //  consume parsed character
        }

        lyrics = tableStart
                + lyrics
                + rowEnd
                + tableEnd;
        //GWT.log(lyrics);
        return lyrics;
    }

    private final void parseLyrics() {
        int state = 0;
        String whiteSpace = "";
        StringBuilder lyrics = new StringBuilder();
        LyricSection lyricSection = null;

        lyricSections = new ArrayList<>();

        MarkedString markedString = new MarkedString(rawLyrics);
        while (!markedString.isEmpty()) {
            char c = markedString.charAt(0);
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
                    try {
                        SectionVersion version = SectionVersion.parse(markedString);
                        if (lyricSection != null)
                            lyricSections.add(lyricSection);

                        lyricSection = new LyricSection();
                        lyricSection.setSectionVersion(version);

                        whiteSpace = ""; //  ignore white space
                        state = 0;
                        continue;
                    } catch (ParseException pex) {
                        //  ignore
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
                            lyricSection.add(new LyricsLine(lyrics.toString()));
                            lyrics = new StringBuilder();
                            whiteSpace = ""; //  ignore trailing white space
                            state = 0;
                            break;
                        default:
                            lyrics.append(whiteSpace).append(c);
                            whiteSpace = "";
                            break;
                    }
                    break;
            }

            markedString.consume(1);
        }
        //  last one is not terminated by another section
        if (lyricSection != null)
            lyricSection.add(new LyricsLine(lyrics.toString()));
        lyricSections.add(lyricSection);

        computeSongMoments();
        computeDuration();
    }

    /**
     * Utility to generate a lyrics section sequence id
     *
     * @param sectionIndex the lyrics section index
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

    public final void transpose(FlexTable flexTable, int halfSteps, int fontSize) {
        transpose(flexTable, halfSteps, fontSize, false);
    }

    public final void transpose(ChordSection chordSection, String prefix, FlexTable flexTable, int halfSteps,
                                int fontSize, boolean append) {
        if (chordSection == null || flexTable == null || fontSize <= 0)
            return;
        transpose(chordSection, flexTable, halfSteps, fontSize, append);
    }

    /**
     * Install whole chord section transposed into the given flex table
     *
     * @param flexTable the flex table to populate
     * @param halfSteps the number of tranposition halfSteps to apply
     * @param fontSize  the pixel size of the font
     * @param append    true if the chord sections are to be added to an existing flex table data
     */
    private final void transpose(FlexTable flexTable, int halfSteps, int fontSize, boolean append) {
        halfSteps = Util.mod(halfSteps, MusicConstant.halfStepsPerOctave);

        if (!append)
            flexTable.removeAllRows();

        //  install all chord sections
        for (ChordSection chordSection : new TreeSet<>(chordSectionMap.values())) {
            transpose(chordSection, flexTable, halfSteps, fontSize, true);
        }
    }

    /**
     * Install the given chord section into the given flex table
     *
     * @param chordSection the given chord section
     * @param flexTable    the flex table to populate
     * @param halfSteps    the number of tranposition halfSteps to apply
     * @param fontSize     the pixel size of the font
     * @param append       true if the chord section is to be added to an existing flex table data
     */
    private final void transpose(ChordSection chordSection, FlexTable flexTable, int halfSteps, int fontSize, boolean append) {
        if (chordSection == null)
            return;

        halfSteps = Util.mod(halfSteps, MusicConstant.halfStepsPerOctave);

        Key newKey = key.nextKeyByHalfStep(halfSteps);

        SectionVersion sectionVersion = chordSection.getSectionVersion();
        GridCoordinate coordinate = getChordSectionGridCoorinateMap().get(sectionVersion);
        if (coordinate == null)
            return;

        int offset = 0;
        if (append)
            offset = 0;
        else {
            flexTable.removeAllRows();
            offset = -coordinate.getRow();
        }

        FlexTable.FlexCellFormatter formatter = flexTable.getFlexCellFormatter();

        Grid<ChordSectionLocation> grid = getChordSectionLocationGrid();

        logger.fine(grid.toString());

        int rLimit = grid.getRowCount();    // safety third
        int rowIndex = 0;
        for (int r = coordinate.getRow(); r < rLimit; r++, rowIndex++) {

            ArrayList<ChordSectionLocation> row = grid.getRow(r);
            int colLimit = row.size();
            String lastValue = "";
            for (int c = 0; c < colLimit; c++) {
                ChordSectionLocation loc = row.get(c);
                if (loc == null)
                    continue;

                MeasureNode measureNode = findMeasureNode(loc);
                final int flexRow = r + offset;

                String s = "";
                switch (c) {
                    case 0:
                        if (!sectionVersion.equals(loc.getSectionVersion()))
                            return;
                        s = sectionVersion.toString();
                        formatter.addStyleName(flexRow, 0, CssConstants.style + "sectionLabel");
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
                    formatter.addStyleName(flexRow, c, CssConstants.style + "textCenter");
                } else
                    lastValue = s;

                //formatter.setAlignment(flexRow, c, ALIGN_CENTER, ALIGN_BOTTOM);   //  as per Shari, comment out
                flexTable.setHTML(flexRow, c,
                        "<span style=\"font-size: " + fontSize + "px;\">"
                                + s
                                + "</span>"
                );
                formatter.addStyleName(flexRow, c, CssConstants.style
                        + (measureNode.isComment() && !measureNode.isRepeat()
                        ? "sectionCommentClass"
                        : "section" + sectionVersion.getSection().getAbbreviation() + "Class"));

                formatter.getElement(flexRow, c).setId((measureNode.isComment() || measureNode.isRepeat() ? "" : "C."));
            }
        }
    }

    /**
     * Debug only!
     *
     * @return a string form of the song chord section grid
     */
    public final String logGrid() {

        StringBuilder sb = new StringBuilder("\n");

        for (int r = 0; r < getChordSectionLocationGrid().getRowCount(); r++) {
            ArrayList<ChordSectionLocation> row = chordSectionLocationGrid.getRow(r);
            for (int c = 0; c < row.size(); c++) {
                ChordSectionLocation loc = row.get(c);
                if (loc == null)
                    continue;
                sb.append("(").append(r).append(",").append(c).append(") ")
                        .append(loc.isMeasure() ? "        " : (loc.isPhrase() ? "    " : ""))
                        .append(loc.toString()).append("  ").append(findMeasureNode(loc).toMarkup() + "\n");
            }
        }
        return sb.toString();
    }

    public final void addRepeat(@Nonnull ChordSectionLocation chordSectionLocation, @Nonnull MeasureRepeat repeat) {
        Measure measure = findMeasure(chordSectionLocation);
        if (measure == null)
            return;

        Phrase measureSequenceItem = findPhrase(measure);
        if (measureSequenceItem == null)
            return;

        ChordSection chordSection = findChordSection(measure);
        ArrayList<Phrase> measureSequenceItems = chordSection.getPhrases();
        int i = measureSequenceItems.indexOf(measureSequenceItem);
        if (i >= 0) {
            measureSequenceItems = new ArrayList<>(measureSequenceItems);
            measureSequenceItems.remove(i);
            repeat.setPhraseIndex(i);
            measureSequenceItems.add(i, repeat);
        } else {
            repeat.setPhraseIndex(measureSequenceItems.size() - 1);
            measureSequenceItems.add(repeat);
        }

        chordSectionDelete(chordSection);
        chordSection = new ChordSection(chordSection.getSectionVersion(), measureSequenceItems);
        chordSectionMap.put(chordSection.getSectionVersion(), chordSection);
        clearCachedValues();
    }

    public final void setRepeat(ChordSectionLocation chordSectionLocation, int repeats) {
        Measure measure = findMeasure(chordSectionLocation);
        if (measure == null)
            return;

        Phrase phrase = findPhrase(measure);
        if (phrase == null)
            return;

        if (phrase instanceof MeasureRepeat) {
            MeasureRepeat measureRepeat = ((MeasureRepeat) phrase);

            if (repeats <= 1) {
                //  remove the repeat
                ChordSection chordSection = findChordSection(measureRepeat);
                ArrayList<Phrase> measureSequenceItems = chordSection.getPhrases();
                int phraseIndex = measureSequenceItems.indexOf(measureRepeat);
                measureSequenceItems.remove(phraseIndex);
                measureSequenceItems.add(phraseIndex, new Phrase(measureRepeat.getMeasures(), phraseIndex));

                chordSectionDelete(chordSection);
                chordSection = new ChordSection(chordSection.getSectionVersion(), measureSequenceItems);
                chordSectionMap.put(chordSection.getSectionVersion(), chordSection);
            } else {
                //  change the count
                measureRepeat.setRepeats(repeats);
            }
        } else {
            //  change sequence items to repeat
            MeasureRepeat measureRepeat = new MeasureRepeat(phrase.getMeasures(), phrase.getPhraseIndex(), repeats);
            ChordSection chordSection = findChordSection(phrase);
            ArrayList<Phrase> measureSequenceItems = chordSection.getPhrases();
            int i = measureSequenceItems.indexOf(phrase);
            measureSequenceItems = new ArrayList<>(measureSequenceItems);
            measureSequenceItems.remove(i);
            measureSequenceItems.add(i, measureRepeat);

            chordSectionDelete(chordSection);
            chordSection = new ChordSection(chordSection.getSectionVersion(), measureSequenceItems);
            chordSectionMap.put(chordSection.getSectionVersion(), chordSection);
        }

        clearCachedValues();
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
                toMarkup(), getRawLyrics());
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

        if (newSong.getChordSections().isEmpty())
            throw new ParseException("The song has no chord sections!", 0);

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

        newSong.setMessage(null);

        if (newSong.getMessage() == null) {
            for (ChordSection chordSection : newSong.getChordSections()) {
                for (Phrase phrase : chordSection.getPhrases()) {
                    for (Measure measure : phrase.getMeasures()) {
                        if (measure.isComment()) {
                            newSong.setMessage("chords should not have comments: see " + chordSection.toString());
                        }
                    }
                }
            }
        }

        if (newSong.getMessage() == null) {
            //  an early song with default (no) structure?
            if (newSong.getLyricSections().size() == 1 && newSong.getLyricSections().get(0).getSectionVersion().equals
                    (Section.getDefaultVersion())) {
                newSong.setMessage("song looks too simple, is there really no structure?");
            }
        }

        return newSong;
    }

    public static final ArrayList<StringTriple> diff(SongBase a, SongBase b) {
        ArrayList<StringTriple> ret = new ArrayList<>();

        if (a.getTitle().compareTo(b.getTitle()) != 0)
            ret.add(new StringTriple("title:", a.getTitle(), b.getTitle()));
        if (a.getArtist().compareTo(b.getArtist()) != 0)
            ret.add(new StringTriple("artist:", a.getArtist(), b.getArtist()));
        if (a.getCoverArtist() != null && b.getCoverArtist() != null && a.getCoverArtist().compareTo(b.getCoverArtist()) != 0)
            ret.add(new StringTriple("cover:", a.getCoverArtist(), b.getCoverArtist()));
        if (a.getCopyright().compareTo(b.getCopyright()) != 0)
            ret.add(new StringTriple("copyright:", a.getCopyright(), b.getCopyright()));
        if (a.getKey().compareTo(b.getKey()) != 0)
            ret.add(new StringTriple("key:", a.getKey().toString(), b.getKey().toString()));
        if (a.getBeatsPerMinute() != b.getBeatsPerMinute())
            ret.add(new StringTriple("BPM:", Integer.toString(a.getBeatsPerMinute()), Integer.toString(b.getBeatsPerMinute())));
        if (a.getBeatsPerBar() != b.getBeatsPerBar())
            ret.add(new StringTriple("per bar:", Integer.toString(a.getBeatsPerBar()), Integer.toString(b.getBeatsPerBar())));
        if (a.getUnitsPerMeasure() != b.getUnitsPerMeasure())
            ret.add(new StringTriple("units/measure:", Integer.toString(a.getUnitsPerMeasure()), Integer.toString(b.getUnitsPerMeasure())));

        //  chords
        for (ChordSection aChordSection : a.getChordSections()) {
            ChordSection bChordSection = b.getChordSection(aChordSection.getSectionVersion());
            if (bChordSection == null) {
                ret.add(new StringTriple("chords missing:", aChordSection.toMarkup(), ""));
            } else if (aChordSection.compareTo(bChordSection) != 0) {
                ret.add(new StringTriple("chords:", aChordSection.toMarkup(), bChordSection.toMarkup()));
            }
        }
        for (ChordSection bChordSection : b.getChordSections()) {
            ChordSection aChordSection = a.getChordSection(bChordSection.getSectionVersion());
            if (aChordSection == null) {
                ret.add(new StringTriple("chords missing:", "", bChordSection.toMarkup()));
            }
        }

        //  lyrics
        {
            int limit = Math.min(a.getLyricSections().size(), b.getLyricSections().size());
            for (int i = 0; i < limit; i++) {

                LyricSection aLyricSection = a.getLyricSections().get(i);
                SectionVersion sectionVersion = aLyricSection.getSectionVersion();
                LyricSection bLyricSection = b.getLyricSections().get(i);
                int lineLimit = Math.min(aLyricSection.getLyricsLines().size(), bLyricSection.getLyricsLines().size());
                for (int j = 0; j < lineLimit; j++) {
                    String aLine = aLyricSection.getLyricsLines().get(j).getLyrics();
                    String bLine = bLyricSection.getLyricsLines().get(j).getLyrics();
                    if (aLine.compareTo(bLine) != 0)
                        ret.add(new StringTriple("lyrics " + sectionVersion.toString(), aLine, bLine));
                }
                lineLimit = aLyricSection.getLyricsLines().size();
                for (int j = bLyricSection.getLyricsLines().size(); j < lineLimit; j++) {
                    String aLine = aLyricSection.getLyricsLines().get(j).getLyrics();
                    ret.add(new StringTriple("lyrics missing " + sectionVersion.toString(), aLine, ""));
                }
                lineLimit = bLyricSection.getLyricsLines().size();
                for (int j = aLyricSection.getLyricsLines().size(); j < lineLimit; j++) {
                    String bLine = bLyricSection.getLyricsLines().get(j).getLyrics();
                    ret.add(new StringTriple("lyrics missing " + sectionVersion.toString(), "", bLine));
                }

            }
        }

        return ret;
    }

    /**
     * Sets the song's title and song id from the given title. Leading "The " articles are rotated to the title end.
     *
     * @param title new title
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


    public final void setCoverArtist(String coverArtist) {
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

    private final void computeSongId() {
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

    public final ArrayList<SongMoment> getSongMoments() {
        if (songMoments == null)
            computeSongMoments();
        return songMoments;
    }

    public final ArrayList<LyricSection> getLyricSections() {
        return lyricSections;
    }

    public final int getFileVersionNumber() {
        return fileVersionNumber;
    }

    /**
     * Compute a relative complexity index for the song
     *
     * @return a relative complexity index
     */
    public final int getComplexity() {
        if (complexity == 0) {
            //  compute the complexity
            TreeSet<Measure> differentChords = new TreeSet<>();
            for (ChordSection chordSection : chordSectionMap.values()) {
                for (Phrase phrase : chordSection.getPhrases()) {

                    //  the more different measures, the greater the complexity
                    differentChords.addAll(phrase.getMeasures());

                    //  weight measures by guitar complexity
                    for (Measure measure : phrase.getMeasures())
                        if (!measure.isEasyGuitarMeasure())
                            complexity++;
                }
            }
            complexity += chordSectionMap.values().size();
            complexity += differentChords.size();
        }
        return complexity;
    }

    protected final void setDuration(double duration) {
        this.duration = duration;
    }

    public final String getRawLyrics() {
        return rawLyrics;
    }

    protected final void setRawLyrics(String rawLyrics) {
        this.rawLyrics = rawLyrics;
        parseLyrics();
    }

    public final void setTotalBeats(int totalBeats) {
        this.totalBeats = totalBeats;
    }

    public final void setDefaultBpm(int defaultBpm) {
        this.defaultBpm = defaultBpm;
    }

    public final String getCoverArtist() {
        return coverArtist;
    }

    public final String getMessage() {
        return message;
    }

    protected final void setMessage(String message) {
        this.message = message;
    }

    public final MeasureEditType getCurrentMeasureEditType() {
        return currentMeasureEditType;
    }

    public final void setCurrentMeasureEditType(MeasureEditType measureEditType) {
        currentMeasureEditType = measureEditType;
        logger.fine("curloc: "
                + (currentChordSectionLocation != null ? currentChordSectionLocation.toString() : "none")
                + " "
                + (currentMeasureEditType != null ? currentMeasureEditType.toString() : "no type"));
    }

    public final ChordSectionLocation getCurrentChordSectionLocation() {
        //  insist on something non-null
        if (currentChordSectionLocation == null) {
            if (chordSectionMap.keySet().isEmpty()) {
                currentChordSectionLocation = new ChordSectionLocation(SectionVersion.getDefault());
            } else {
                //  last location
                TreeSet<SectionVersion> sectionVersions = new TreeSet<>(chordSectionMap.keySet());
                ChordSection lastChordSection = chordSectionMap.get(sectionVersions.last());
                if (lastChordSection.isEmpty())
                    currentChordSectionLocation = new ChordSectionLocation(lastChordSection.getSectionVersion());
                else {
                    Phrase phrase = lastChordSection.lastPhrase();
                    if (phrase.isEmpty())
                        currentChordSectionLocation = new ChordSectionLocation(lastChordSection.getSectionVersion(),
                                phrase.getPhraseIndex());
                    else
                        currentChordSectionLocation = new ChordSectionLocation(lastChordSection.getSectionVersion(),
                                phrase.getPhraseIndex(), phrase.getMeasures().size() - 1);
                }
            }
        }
        return currentChordSectionLocation;
    }

    public final MeasureNode getCurrentChordSectionLocationMeasureNode() {
        return currentChordSectionLocation == null ? null : findMeasureNode(currentChordSectionLocation);
    }


    public final void setCurrentChordSectionLocation(@Nonnull ChordSectionLocation chordSectionLocation) {
        //  try to find something close if the exact location doesn't exist
        if (chordSectionLocation == null) {
            chordSectionLocation = currentChordSectionLocation;
            if (chordSectionLocation == null) {
                chordSectionLocation = getLastChordSectionLocation();
            }
        }
        if (chordSectionLocation != null)
            try {
                ChordSection chordSection = getChordSection(chordSectionLocation);
                ChordSection cs = chordSection;
                if (cs == null) {
                    TreeSet<SectionVersion> sortedSectionVersions = new TreeSet<>();
                    sortedSectionVersions.addAll(chordSectionMap.keySet());
                    cs = chordSectionMap.get(sortedSectionVersions.last());
                }
                if (chordSectionLocation.hasPhraseIndex()) {
                    Phrase phrase = cs.getPhrase(chordSectionLocation.getPhraseIndex());
                    if (phrase == null)
                        phrase = cs.getPhrase(cs.getPhraseCount() - 1);
                    int phraseIndex = phrase.getPhraseIndex();
                    if (chordSectionLocation.hasMeasureIndex()) {
                        int pi = (phraseIndex >= cs.getPhraseCount() ? cs.getPhraseCount() - 1 : phraseIndex);
                        int measureIndex = chordSectionLocation.getMeasureIndex();
                        int mi = (measureIndex >= phrase.size() ? phrase.size() - 1 : measureIndex);
                        if (cs != chordSection || pi != phraseIndex || mi != measureIndex)
                            chordSectionLocation = new ChordSectionLocation(cs.getSectionVersion(), pi, mi);
                    }
                }
            } catch (NullPointerException | IndexOutOfBoundsException ex) {
                chordSectionLocation = null;
            } catch (Exception ex) {
                //  javascript parse error
                logger.fine(ex.getMessage());
                chordSectionLocation = null;
            }

        currentChordSectionLocation = chordSectionLocation;
        logger.fine("curloc: "
                + (currentChordSectionLocation != null ? currentChordSectionLocation.toString() : "none")
                + " "
                + (currentMeasureEditType != null ? currentMeasureEditType.toString() : "no type")
                + " " + (currentChordSectionLocation != null ? findMeasureNode(currentChordSectionLocation) : "none")
        );
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
     * @param o the other song base to compare with
     * @return true if title and artist are equal
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
        if (!toMarkup().equals(o.toMarkup()))
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
        hash = (79 * hash + chordSectionMap.hashCode()) % (1 << 31);
        hash = (79 * hash + Objects.hashCode(this.rawLyrics)) % (1 << 31);
        hash = (79 * hash + Objects.hashCode(this.metadata)) % (1 << 31);
        return hash;
    }

    private String title = "Unknown";
    private SongId songId = new SongId();
    private String artist = "Unknown";
    private String coverArtist = "";
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
    private transient HashMap<SectionVersion, ChordSection> chordSectionMap = new HashMap<>();
    private transient HashMap<SectionVersion, GridCoordinate> chordSectionGridCoorinateMap = new HashMap<>();
    private transient HashMap<GridCoordinate, ChordSectionLocation> gridCoordinateChordSectionLocationMap = new HashMap<>();
    private transient HashMap<ChordSectionLocation, GridCoordinate> gridChordSectionLocationCoordinateMap = new HashMap<>();

    private ChordSectionLocation currentChordSectionLocation;
    private MeasureEditType currentMeasureEditType = MeasureEditType.append;
    private transient Grid<ChordSectionLocation> chordSectionLocationGrid = null;
    private transient int complexity;
    private transient String chordsAsMarkup;
    private transient String message;
    private ArrayList<SongMoment> songMoments = new ArrayList<>();
    private ArrayList<MeasureNode> measureNodes = new ArrayList<>();
    private String rawLyrics = "";
    private LegacyDrumSection drumSection = new LegacyDrumSection();
    private Arrangement drumArrangement;    //  default
    private TreeSet<Metadata> metadata = new TreeSet<>();

    private static final int minBpm = 50;
    private static final int maxBpm = 400;

    private static final Logger logger = Logger.getLogger(SongBase.class.getName());
}
