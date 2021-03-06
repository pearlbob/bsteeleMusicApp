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
        setChords("");
        setBeatsPerMinute(100);
        setBeatsPerBar(4);
    }

    /**
     * A convenience constructor used to enforce the minimum requirements for a song.
     * <p>Note that this is the base class for a song object.
     * The split from Song was done for testability reasons.
     * It's much easier to test free of GWT.
     * </p>
     *
     * @param title           song title
     * @param artist          artist the song version is known for
     * @param copyright       copyright of the song
     * @param key             the key the chords represent
     * @param bpm             beats per minute
     * @param beatsPerBar     beats per bar, ie. the top of the time signature
     * @param unitsPerMeasure sheet music units per measure, ie. the bottom of the time signature
     * @param chords          string representation of the song chords on a per section basis
     * @param lyricsToParse   lyrics and the associated section for them
     * @return a song object
     * @throws ParseException parse exception
     */
    static final SongBase createSongBase(@NotNull String title, @NotNull String artist,
                                         @NotNull String copyright,
                                         @NotNull Key key, int bpm, int beatsPerBar, int unitsPerMeasure,
                                         @NotNull String chords, @NotNull String lyricsToParse)
            throws ParseException {
        SongBase song = new SongBase();
        song.setTitle(title);
        song.setArtist(artist);
        song.setCopyright(copyright);
        song.setKey(key);
        song.setUnitsPerMeasure(unitsPerMeasure);
        song.setChords(chords);
        song.setRawLyrics(lyricsToParse);

        song.setBeatsPerMinute(bpm);
        song.setBeatsPerBar(beatsPerBar);

        return song;
    }

    /**
     * Compute the song moments list given the song's current state.
     * Moments are the temporal sequence of measures as the song is to be played.
     * All repeats are expanded.  Measure node such as comments,
     * repeat ends, repeat counts, section headers, etc. are ignored.
     */
    final void computeSongMoments() {
        if (!songMoments.isEmpty())
            return;

        songMoments.clear();
        beatsToMoment.clear();

        if (lyricSections == null)
            return;

        logger.fine("lyricSections size: " + lyricSections.size());
        Integer sectionCount = null;
        HashMap<SectionVersion, Integer> sectionVersionCountMap = new HashMap<>();
        chordSectionBeats.clear();
        int beatNumber = 0;
        for (LyricSection lyricSection : lyricSections) {
            ChordSection chordSection = findChordSection(lyricSection);
            if (chordSection == null)
                continue;

            //  compute section count
            SectionVersion sectionVersion = chordSection.getSectionVersion();
            sectionCount = sectionVersionCountMap.get(sectionVersion);
            if (sectionCount == null) {
                sectionCount = 0;
            }
            sectionCount++;
            sectionVersionCountMap.put(sectionVersion, sectionCount);

            ArrayList<Phrase> phrases = chordSection.getPhrases();
            if (phrases != null) {
                int phraseIndex = 0;
                int sectionVersionBeats = 0;
                for (Phrase phrase : phrases) {
                    if (phrase.isRepeat()) {
                        MeasureRepeat measureRepeat = (MeasureRepeat) phrase;
                        int limit = measureRepeat.getRepeats();
                        for (int repeat = 0; repeat < limit; repeat++) {
                            ArrayList<Measure> measures = measureRepeat.getMeasures();
                            if (measures != null) {
                                int repeatCycleBeats = 0;
                                for (Measure measure : measures) {
                                    repeatCycleBeats += measure.getBeatCount();
                                }
                                int measureIndex = 0;
                                for (Measure measure : measures) {
                                    songMoments.add(new SongMoment(
                                            songMoments.size(),  //  size prior to add
                                            beatNumber, sectionVersionBeats,
                                            lyricSection, chordSection, phraseIndex, phrase,
                                            measureIndex, measure, repeat, repeatCycleBeats, limit, sectionCount));
                                    measureIndex++;
                                    beatNumber += measure.getBeatCount();
                                    sectionVersionBeats += measure.getBeatCount();
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
                                        beatNumber, sectionVersionBeats,
                                        lyricSection, chordSection, phraseIndex, phrase,
                                        measureIndex, measure, 0, 0, 0, sectionCount));
                                measureIndex++;
                                beatNumber += measure.getBeatCount();
                                sectionVersionBeats += measure.getBeatCount();
                            }
                        }
                    }
                    phraseIndex++;
                }

                for (SectionVersion sv : matchingSectionVersions(sectionVersion)) {
                    chordSectionBeats.put(sv, sectionVersionBeats);
                }
            }
        }


        {
            //  generate song moment grid coordinate map for play to display purposes
            songMomentGridCoordinateHashMap.clear();
            chordSectionRows.clear();

            LyricSection lastLyricSection = null;
            int row = 0;
            int baseChordRow = 0;
            int maxChordRow = 0;
            for (SongMoment songMoment : songMoments) {

                if (lastLyricSection != songMoment.getLyricSection()) {
                    if (lastLyricSection != null) {
                        int rows = maxChordRow - baseChordRow + 1;
                        chordSectionRows.put(lastLyricSection.getSectionVersion(), rows);
                        row += rows;
                    }
                    lastLyricSection = songMoment.getLyricSection();
                    GridCoordinate sectionGridCoordinate =
                            getGridCoordinate(new ChordSectionLocation(lastLyricSection.getSectionVersion()));

                    baseChordRow = sectionGridCoordinate.getRow();
                    maxChordRow = baseChordRow;
                }

                //  use the change of chord section rows to trigger moment grid row change
                GridCoordinate gridCoordinate = getGridCoordinate(songMoment.getChordSectionLocation());
                maxChordRow = Math.max(maxChordRow, gridCoordinate.getRow());

                GridCoordinate momentGridCoordinate = new GridCoordinate(
                        row + (gridCoordinate.getRow() - baseChordRow), gridCoordinate.getCol());
                logger.finer(songMoment.toString() + ": " + momentGridCoordinate.toString());
                songMomentGridCoordinateHashMap.put(songMoment, momentGridCoordinate);

                logger.finer("moment: " + songMoment.getMomentNumber()
                        + ": " + songMoment.getChordSectionLocation().toString()
                        + "#" + songMoment.getSectionCount()
                        + " m:" + momentGridCoordinate
                        + " " + songMoment.getMeasure().toMarkup()
                        + (songMoment.getRepeatMax() > 1 ? " " + (songMoment.getRepeat() + 1) + "/" + songMoment.getRepeatMax() : "")
                );
            }
            //  push the last one in
            if (lastLyricSection != null) {
                int rows = maxChordRow - baseChordRow + 1;
                chordSectionRows.put(lastLyricSection.getSectionVersion(), rows);
            }
        }

        {
            //  install the beats to moment lookup entries
            int beat = 0;
            for (SongMoment songMoment : songMoments) {
                int limit = songMoment.getMeasure().getBeatCount();
                for (int b = 0; b < limit; b++)
                    beatsToMoment.put(beat++, songMoment);
            }
        }

    }

    public final GridCoordinate getMomentGridCoordinate(SongMoment songMoment) {
        computeSongMoments();
        return songMomentGridCoordinateHashMap.get(songMoment);
    }

    public final GridCoordinate getMomentGridCoordinate(int momentNumber) {
        SongMoment songMoment = getSongMoment(momentNumber);
        if (songMoment == null)
            return null;
        return songMomentGridCoordinateHashMap.get(songMoment);
    }


    public final void debugSongMoments() {
        computeSongMoments();

        for (SongMoment songMoment : songMoments) {
            GridCoordinate momentGridCoordinate = getMomentGridCoordinate(songMoment.getMomentNumber());
            logger.finer(songMoment.getMomentNumber()
                    + ": " + songMoment.getChordSectionLocation().toString()
                    + "#" + songMoment.getSectionCount()
                    + " m:" + momentGridCoordinate
                    + " " + songMoment.getMeasure().toMarkup()
                    + (songMoment.getRepeatMax() > 1 ? " " + (songMoment.getRepeat() + 1) + "/" + songMoment.getRepeatMax() : "")
            );
        }
    }

    public final String songMomentMeasure(int momentNumber, Key key, int halfStepOffset) {
        computeSongMoments();
        if (momentNumber < 0 || songMoments.isEmpty() || momentNumber > songMoments.size() - 1)
            return "";
        return songMoments.get(momentNumber).getMeasure().transpose(key, halfStepOffset);
    }

    public final String songNextMomentMeasure(int momentNumber, Key key, int halfStepOffset) {
        computeSongMoments();
        if (momentNumber < -1 || songMoments.isEmpty() || momentNumber > songMoments.size() - 2)
            return "";
        return songMoments.get(momentNumber + 1).getMeasure().transpose(key, halfStepOffset);
    }

    public final String songMomentStatus(int beatNumber, int momentNumber) {
        computeSongMoments();
        if (songMoments.isEmpty())
            return "unknown";

        if (momentNumber < 0) {
//            beatNumber %= getBeatsPerBar();
//            if (beatNumber < 0)
//                beatNumber += getBeatsPerBar();
//            beatNumber++;
            return "count in " + -momentNumber;
        }

        SongMoment songMoment = getSongMoment(momentNumber);
        if (songMoment == null)
            return "";

        Measure measure = songMoment.getMeasure();

        beatNumber %= measure.getBeatCount();
        if (beatNumber < 0)
            beatNumber += measure.getBeatCount();
        beatNumber++;

        String ret = songMoment.getChordSection().getSectionVersion().toString()
                + (songMoment.getRepeatMax() > 1
                ? " " + (songMoment.getRepeat() + 1) + "/" + songMoment.getRepeatMax()
                : "");

        if (appOptions.isDebug())
            ret = songMoment.getMomentNumber()
                    + ": " + songMoment.getChordSectionLocation().toString()
                    + "#" + songMoment.getSectionCount()
                    + " "
                    + ret
                    + " b: " + (beatNumber + songMoment.getBeatNumber())
                    + " = " + (beatNumber + songMoment.getSectionBeatNumber())
                    + "/" + getChordSectionBeats(songMoment.getChordSectionLocation().getSectionVersion())
                    + " " + songMomentGridCoordinateHashMap.get(songMoment).toString()
                    ;
        return ret;
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
        logger.finer("chordSectionMap size: " + getChordSectionMap().keySet().size());
        return getChordSectionMap().get(lyricSection.getSectionVersion());
    }

    /**
     * Compute the duration and total beat count for the song.
     */
    private void computeDuration() {
        //  be lazy
        if (duration > 0)
            return;

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
        return getChordSectionMap().get(sectionVersion);
    }

    public final ChordSection getChordSection(@Nonnull ChordSectionLocation chordSectionLocation) {
        if (chordSectionLocation == null)
            return null;
        return getChordSectionMap().get(chordSectionLocation.getSectionVersion());
    }

    public double getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(double lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = (user == null || user.length() <= 0) ? defaultUser : user;
    }

    private ArrayList<MeasureNode> getMeasureNodes() {
        //  lazy eval
        if (measureNodes == null) {
            try {
                parseChords(chords);
            } catch (ParseException e) {
                logger.info("unexpected: " + e.getMessage());
                return null;
            }
        }
        return measureNodes;
    }

    private HashMap<SectionVersion, ChordSection> getChordSectionMap() {
        //  lazy eval
        if (chordSectionMap == null) {
            try {
                parseChords(chords);
            } catch (ParseException e) {
                logger.info("unexpected: " + e.getMessage());
                return null;
            }
        }
        return chordSectionMap;
    }

    private enum UpperCaseState {
        initial,
        flatIsPossible,
        comment,
        normal;
    }

    /**
     * Try to promote lower case characters to uppercase when they appear to be musical notes
     *
     * @param entry the string to process
     * @return the results after promotion to uppercase
     */
    public static final String entryToUppercase(String entry) {
        StringBuilder sb = new StringBuilder();

        UpperCaseState state = UpperCaseState.initial;
        for (int i = 0; i < entry.length(); i++) {
            char c = entry.charAt(i);

            //  map newlines!
            if (c == '\n')
                c = ',';

            switch (state) {
                case flatIsPossible:
                    if (c == 'b') {
                        state = UpperCaseState.initial;
                        sb.append(c);
                        break;
                    }
                    //  fall through
                case initial:
                    if ((c >= 'A' && c <= 'G') || (c >= 'a' && c <= 'g')) {
                        if (i < entry.length() - 1) {
                            char sf = entry.charAt(i + 1);
                            switch (sf) {
                                case 'b':
                                case '#':
                                case MusicConstant.flatChar:
                                case MusicConstant.sharpChar:
                                    i++;
                                    break;
                                default:
                                    sf = 0;
                                    break;
                            }
                            if (i < entry.length() - 1) {
                                String test = entry.substring(i + 1);
                                boolean isChordDescriptor = false;
                                String cdString = "";
                                for (ChordDescriptor chordDescriptor : ChordDescriptor.values()) {
                                    cdString = chordDescriptor.toString();
                                    if (cdString.length() > 0 && test.startsWith(cdString)) {
                                        isChordDescriptor = true;
                                        break;
                                    }
                                }
                                //  a chord descriptor makes a good partition to restart capitalization
                                if (isChordDescriptor) {
                                    sb.append(Character.toUpperCase(c));
                                    if (sf != 0)
                                        sb.append(sf);
                                    sb.append(cdString);
                                    i += cdString.length();
                                    break;
                                } else {
                                    sb.append(Character.toUpperCase(c));
                                    if (sf != 0) {
                                        sb.append(sf);
                                    }
                                    break;
                                }
                            } else {
                                sb.append(Character.toUpperCase(c));
                                if (sf != 0) {
                                    sb.append(sf);
                                }
                                break;
                            }
                        }
                        //  map the chord to upper case
                        c = Character.toUpperCase(c);
                    } else if (c == 'x') {
                        if (i < entry.length() - 1) {
                            char d = entry.charAt(i + 1);
                            if (d >= '1' && d <= '9') {
                                sb.append(c);
                                break;      //  don't cap a repeat repetition declaration
                            }
                        }
                        sb.append(Character.toUpperCase(c));   //  x to X
                        break;
                    } else if (c == '(') {
                        sb.append(c);

                        //  don't cap a comment
                        state = UpperCaseState.comment;
                        break;
                    }
                    state = (c >= 'A' && c <= 'G') ? UpperCaseState.flatIsPossible : UpperCaseState.normal;
                    //  fall through
                case normal:
                    //  reset on sequential reset characters
                    if (Character.isWhitespace(c)
                            || c == '/'
                            || c == '.'
                            || c == ','
                            || c == ':'
                            || c == '#'
                            || c == MusicConstant.flatChar
                            || c == MusicConstant.sharpChar
                            || c == '['
                            || c == ']'
                    )
                        state = UpperCaseState.initial;

                    sb.append(c);
                    break;
                case comment:
                    sb.append(c);
                    if (c == ')')
                        state = UpperCaseState.initial;
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
        this.chords = chords;   //  safety only
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
                    clearCachedValues();

                    logger.finest(logGrid());
                    throw pex;
                }
            }
            this.chords = chordsToJsonTransportString();
        }

        setDefaultCurrentChordLocation();

        logger.finer(logGrid());
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
                logger.finer("parseChordEntry: " + markedString.toString());

                int mark = markedString.mark();

                try {
                    //  if it's a full section (or multiple sections) it will all be handled here
                    chordSection = ChordSection.parse(markedString, beatsPerBar, true);

                    //  look for multiple sections defined at once
                    if (chordSection.getPhrases().isEmpty()) {
                        emptyChordSections.add(chordSection);
                        continue;
                    } else if (!emptyChordSections.isEmpty()) {
                        //  share the common measure sequence items
                        for (ChordSection wasEmptyChordSection : emptyChordSections) {
                            wasEmptyChordSection.setPhrases(chordSection.getPhrases());
                            ret.add(wasEmptyChordSection);
                        }
                        emptyChordSections.clear();
                    }
                    ret.add(chordSection);
                    continue;
                } catch (ParseException pex) {
                    markedString.resetToMark(mark);
                }

                //  see if it's a complete repeat
                try {
                    ret.add(MeasureRepeat.parse(markedString, phaseIndex, beatsPerBar, null));
                    phaseIndex++;
                    continue;
                } catch (ParseException pex) {
                    markedString.resetToMark(mark);
                }
                //  see if it's a phrase
                try {
                    ret.add(Phrase.parse(markedString, phaseIndex, beatsPerBar, getCurrentChordSectionLocationMeasure()));
                    phaseIndex++;
                    continue;
                } catch (ParseException pex) {
                    markedString.resetToMark(mark);
                }
                //  see if it's a single measure
                try {
                    ret.add(Measure.parse(markedString, beatsPerBar, getCurrentChordSectionLocationMeasure()));
                    continue;
                } catch (ParseException pex) {
                    markedString.resetToMark(mark);
                }
                //  see if it's a comment
                try {
                    ret.add(MeasureComment.parse(markedString));
                    phaseIndex++;
                    continue;
                } catch (ParseException pex) {
                    markedString.resetToMark(mark);
                }
                //  the entry not understood, force it to be a comment
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

            //  add trailing empty sections... without a following non-empty section
            for (ChordSection wasEmptyChordSection : emptyChordSections)
                ret.add(wasEmptyChordSection);
        }

        //  try to help add row separations
        //  default to rows of 4 if there are 8 or more measures
        if (ret.size() > 0 && ret.get(0) instanceof ChordSection) {
            ChordSection chordSection = (ChordSection) ret.get(0);
            for (Phrase phrase : chordSection.getPhrases()) {
                boolean hasEndOfRow = false;
                for (Measure measure : phrase.getMeasures()) {
                    if (measure.isComment())
                        continue;
                    if (measure.isEndOfRow()) {
                        hasEndOfRow = true;
                        break;
                    }
                }
                if (!hasEndOfRow && phrase.size() >= 8) {
                    int i = 0;
                    for (Measure measure : phrase.getMeasures()) {
                        if (measure.isComment())
                            continue;
                        i++;
                        if (i % 4 == 0) {
                            measure.setEndOfRow(true);
                        }
                    }
                }
            }
        }

        //  deal with sharps and flats misapplied.
        ArrayList<MeasureNode> transposed = new ArrayList<>();
        for (MeasureNode measureNode : ret) {
            transposed.add(measureNode.transposeToKey(key));
        }
        return transposed;
    }

    private void setDefaultCurrentChordLocation() {
        currentChordSectionLocation = null;

        TreeSet<ChordSection> sortedChordSections = new TreeSet<>(getChordSectionMap().values());
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

    private final HashMap<GridCoordinate, ChordSectionLocation> getGridCoordinateChordSectionLocationMap() {
        getChordSectionLocationGrid();
        return gridCoordinateChordSectionLocationMap;
    }

    private final HashMap<ChordSectionLocation, GridCoordinate> getGridChordSectionLocationCoordinateMap() {
        getChordSectionLocationGrid();
        return gridChordSectionLocationCoordinateMap;
    }

    public final int getChordSectionLocationGridMaxColCount() {
        int maxCols = 0;
        for (GridCoordinate gridCoordinate : getGridCoordinateChordSectionLocationMap().keySet()) {
            maxCols = Math.max(maxCols, gridCoordinate.getCol());
        }
        return maxCols;
    }

    public final Grid<ChordSectionLocation> getChordSectionLocationGrid() {
        //  support lazy eval
        if (chordSectionLocationGrid != null)
            return chordSectionLocationGrid;

        Grid<ChordSectionLocation> grid = new Grid<>();
        chordSectionGridCoorinateMap = new HashMap<>();
        chordSectionGridMatches = new HashMap<>();
        gridCoordinateChordSectionLocationMap = new HashMap<>();
        gridChordSectionLocationCoordinateMap = new HashMap<>();

        //  grid each section
        final int offset = 1;       //  offset of phrase start from section start
        int row = 0;
        int col = offset;

        //  use a separate set to avoid modifying a set
        TreeSet<SectionVersion> sectionVersionsToDo = new TreeSet<>(getChordSectionMap().keySet());
        for (ChordSection chordSection : new TreeSet<>(getChordSectionMap().values())) {
            SectionVersion sectionVersion = chordSection.getSectionVersion();

            //  only do a chord section once.  it might have a duplicate set of phrases and already be listed
            if (!sectionVersionsToDo.contains(sectionVersion))
                continue;
            sectionVersionsToDo.remove(sectionVersion);

            //  start each section on it's own line
            if (col != offset) {
                row++;
            }
            col = 0;

            logger.finest("gridding: " + sectionVersion.toString() + " (" + col + ", " + row + ")");

            {
                //  grid the section header
                TreeSet<SectionVersion> matchingSectionVersions = matchingSectionVersions(sectionVersion);
                GridCoordinate coordinate = new GridCoordinate(row, col);
                for (SectionVersion matchingSectionVersion : matchingSectionVersions) {
                    chordSectionGridCoorinateMap.put(matchingSectionVersion, coordinate);
                    ChordSectionLocation loc = new ChordSectionLocation(matchingSectionVersion);
                    gridChordSectionLocationCoordinateMap.put(loc, coordinate);
                }
                for (SectionVersion matchingSectionVersion : matchingSectionVersions) {
                    //  don't add identity mapping
                    if (matchingSectionVersion.equals(sectionVersion))
                        continue;
                    chordSectionGridMatches.put(matchingSectionVersion, sectionVersion);
                }

                ChordSectionLocation loc = new ChordSectionLocation(matchingSectionVersions);
                gridCoordinateChordSectionLocationMap.put(coordinate, loc);
                gridChordSectionLocationCoordinateMap.put(loc, coordinate);
                grid.set(col, row, loc);
                col = offset;
                sectionVersionsToDo.removeAll(matchingSectionVersions);
            }

            //  allow for empty sections... on entry
            if (chordSection.getPhrases().isEmpty()) {
                row++;
                col = offset;
            } else {
                //  grid each phrase
                for (int phraseIndex = 0; phraseIndex < chordSection.getPhrases().size(); phraseIndex++) {
                    //  start each phrase on it's own line
                    if (col > offset) {
                        row++;
                        col = offset;
                    }

                    Phrase phrase = chordSection.getPhrase(phraseIndex);

                    //  default to max measures per row
                    final int measuresPerline = 8;

                    //  grid each measure of the phrase
                    boolean repeatExtensionUsed = false;
                    int phraseSize = phrase.getMeasures().size();
                    if (phraseSize == 0 && phrase.isRepeat()) {
                        //  special case: deal with empty repeat
                        //  fill row to measures per line
                        col = offset + measuresPerline - 1;
                        {
                            //  add repeat indicator
                            ChordSectionLocation loc = new ChordSectionLocation(sectionVersion, phraseIndex);
                            GridCoordinate coordinate = new GridCoordinate(row, col);
                            gridCoordinateChordSectionLocationMap.put(coordinate, loc);
                            gridChordSectionLocationCoordinateMap.put(loc, coordinate);
                            grid.set(col++, row, loc);
                        }
                    } else {

                        Measure measure = null;

                        //  compute the max number of columns for this phrase
                        int maxCol = offset;
                        {
                            int currentCol = offset;
                            for (int measureIndex = 0; measureIndex < phraseSize; measureIndex++) {
                                measure = phrase.getMeasure(measureIndex);
                                if (measure.isComment())    //  comments get their own row
                                    continue;
                                currentCol++;
                                if (measure.isEndOfRow()) {
                                    if (currentCol > maxCol)
                                        maxCol = currentCol;
                                    currentCol = offset;
                                }
                            }
                            if (currentCol > maxCol)
                                maxCol = currentCol;
                            maxCol = Math.min(maxCol, measuresPerline + 1);
                        }

                        //  place each measure in the grid
                        Measure lastMeasure = null;
                        for (int measureIndex = 0; measureIndex < phraseSize; measureIndex++) {

                            //  place comments on their own line
                            //  don't upset the col location
                            //  expect the output to span the row
                            measure = phrase.getMeasure(measureIndex);
                            if (measure.isComment()) {
                                if (col > offset && lastMeasure != null && !lastMeasure.isComment())
                                    row++;
                                ChordSectionLocation loc = new ChordSectionLocation(sectionVersion, phraseIndex, measureIndex);
                                grid.set(offset, row, loc);
                                GridCoordinate coordinate = new GridCoordinate(row, offset);
                                gridCoordinateChordSectionLocationMap.put(coordinate, loc);
                                gridChordSectionLocationCoordinateMap.put(loc, coordinate);
                                if (measureIndex < phraseSize - 1)
                                    row++;
                                else
                                    col = offset + measuresPerline;    //  prep for next phrase
                                continue;
                            }


                            if ((lastMeasure != null && lastMeasure.isEndOfRow())
                                    || col >= offset + measuresPerline  //  limit line length to the measures per line
                            ) {
                                //  fill the row with nulls if the row is shorter then the others in this phrase
                                while (col < maxCol)
                                    grid.set(col++, row, null);

                                //  put an end of line marker on multiline repeats
                                if (phrase.isRepeat()) {
                                    grid.set(col++, row, new ChordSectionLocation(sectionVersion, phraseIndex,
                                            (repeatExtensionUsed
                                                    ? ChordSectionLocation.Marker.repeatMiddleRight
                                                    : ChordSectionLocation.Marker.repeatUpperRight
                                            )));
                                    repeatExtensionUsed = true;
                                }
                                if (col > offset) {
                                    row++;
                                    col = offset;
                                }
                            }

                            {
                                //  grid the measure with it's location
                                ChordSectionLocation loc = new ChordSectionLocation(sectionVersion, phraseIndex, measureIndex);
                                GridCoordinate coordinate = new GridCoordinate(row, col);
                                gridCoordinateChordSectionLocationMap.put(coordinate, loc);
                                gridChordSectionLocationCoordinateMap.put(loc, coordinate);
                                grid.set(col++, row, loc);
                            }

                            //  put the repeat on the end of the last line of the repeat
                            if (phrase.isRepeat() && measureIndex == phraseSize - 1) {
                                col = maxCol;

                                //  close the multiline repeat marker
                                if (repeatExtensionUsed) {
                                    ChordSectionLocation loc = new ChordSectionLocation(sectionVersion, phraseIndex,
                                            ChordSectionLocation.Marker.repeatLowerRight);
                                    GridCoordinate coordinate = new GridCoordinate(row, col);
                                    gridCoordinateChordSectionLocationMap.put(coordinate, loc);
                                    gridChordSectionLocationCoordinateMap.put(loc, coordinate);
                                    grid.set(col++, row, loc);

                                    repeatExtensionUsed = false;
                                }

                                {
                                    //  add repeat indicator
                                    ChordSectionLocation loc = new ChordSectionLocation(sectionVersion, phraseIndex);
                                    GridCoordinate coordinate = new GridCoordinate(row, col);
                                    gridCoordinateChordSectionLocationMap.put(coordinate, loc);
                                    gridChordSectionLocationCoordinateMap.put(loc, coordinate);
                                    grid.set(col++, row, loc);
                                }
                                row++;
                                col = offset;
                            }

                            lastMeasure = measure;
                        }
                    }
                }
            }
        }

        if (logger.getLevel() != null && logger.getLevel().intValue() <= Level.FINEST.intValue()) {
            logger.fine("gridCoordinateChordSectionLocationMap: ");
            for (GridCoordinate coordinate : new TreeSet<GridCoordinate>(gridCoordinateChordSectionLocationMap.keySet())) {
                logger.fine(" " + coordinate.toString()
                        + " " + gridCoordinateChordSectionLocationMap.get(coordinate)
                        + " -> " + findMeasureNode(gridCoordinateChordSectionLocationMap.get(coordinate)).toMarkup()
                )
                ;
            }
        }

        chordSectionLocationGrid = grid;
        logger.finer(grid.toString());
        return chordSectionLocationGrid;
    }

    /**
     * Find all matches to the given section version, including the given section version itself
     *
     * @param multSectionVersion section version to match
     * @return set of matching section versions
     */
    private TreeSet<SectionVersion> matchingSectionVersions(SectionVersion multSectionVersion) {
        TreeSet<SectionVersion> ret = new TreeSet<>();
        if (multSectionVersion == null)
            return ret;
        ChordSection multChordSection = findChordSection(multSectionVersion);
        if (multChordSection == null)
            return ret;

        for (ChordSection chordSection : new TreeSet<>(getChordSectionMap().values())) {
            if (multSectionVersion.equals(chordSection.getSectionVersion()))
                ret.add(multSectionVersion);
            else if (chordSection.getPhrases().equals(multChordSection.getPhrases())) {
                ret.add(chordSection.getSectionVersion());
            }
        }
        return ret;
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
        songMoments.clear();
        duration = 0;
        totalBeats = 0;
    }

    protected final String chordsToJsonTransportString() {
        StringBuilder sb = new StringBuilder();

        for (ChordSection chordSection : new TreeSet<>(getChordSectionMap().values())) {
            sb.append(chordSection.toJson());
        }
        return sb.toString();
    }

    public final String toMarkup() {
        if (chordsAsMarkup != null)
            return chordsAsMarkup;

        StringBuilder sb = new StringBuilder();

        TreeSet<SectionVersion> sortedSectionVersions = new TreeSet<>(getChordSectionMap().keySet());
        TreeSet<SectionVersion> completedSectionVersions = new TreeSet<>();


        //  markup by section version order
        for (SectionVersion sectionVersion : sortedSectionVersions) {
            //  don't repeat anything
            if (completedSectionVersions.contains(sectionVersion))
                continue;
            completedSectionVersions.add(sectionVersion);

            //  find all section versions with the same chords
            ChordSection chordSection = getChordSectionMap().get(sectionVersion);
            if (chordSection.isEmpty()) {
                //  empty sections stand alone
                sb.append(sectionVersion.toString());
                sb.append(" ");
            } else {
                TreeSet<SectionVersion> currentSectionVersions = new TreeSet<>();
                for (SectionVersion otherSectionVersion : sortedSectionVersions) {
                    if (chordSection.getPhrases().equals(getChordSectionMap().get(otherSectionVersion).getPhrases())) {
                        currentSectionVersions.add(otherSectionVersion);
                        completedSectionVersions.add(otherSectionVersion);
                    }
                }

                //  list the section versions for this chord section
                for (SectionVersion currentSectionVersion : currentSectionVersions) {
                    sb.append(currentSectionVersion.toString());
                    sb.append(" ");
                }
            }

            //  chord section phrases (only) to output
            sb.append(chordSection.phrasesToMarkup());
            sb.append(" ");    //  for human readability only
        }
        chordsAsMarkup = sb.toString();
        return chordsAsMarkup;
    }

    public final String toMarkup(ChordSectionLocation location) {
        StringBuilder sb = new StringBuilder();
        if (location != null) {
            if (location.isSection()) {
                sb.append(location.toString());
                sb.append(" ");
                sb.append(getChordSection(location).phrasesToMarkup());
                return sb.toString();
            } else {
                MeasureNode measureNode = findMeasureNode(location);
                if (measureNode != null)
                    return measureNode.toMarkup();
            }
        }
        return null;
    }

    public final String toEntry(ChordSectionLocation location) {
        StringBuilder sb = new StringBuilder();
        if (location != null) {
            if (location.isSection()) {
                sb.append(getChordSection(location).transposeToKey(key).toEntry());
                return sb.toString();
            } else {
                MeasureNode measureNode = findMeasureNode(location);
                if (measureNode != null)
                    return measureNode.transposeToKey(key).toEntry();
            }
        }
        return null;
    }

    /**
     * Add the given section version to the song chords
     *
     * @param sectionVersion the given section to add
     * @return true if the section version was added
     */
    public final boolean addSectionVersion(SectionVersion sectionVersion) {
        if (sectionVersion == null || getChordSectionMap().containsKey(sectionVersion))
            return false;
        getChordSectionMap().put(sectionVersion, new ChordSection(sectionVersion));
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
            if (ret && phrase.isEmpty())
                return deleteCurrentChordSectionPhrase();
        } else if (location.isPhrase()) {
            return deleteCurrentChordSectionPhrase();
        } else if (location.isSection()) {
            //  find the section prior to the one being deleted
            TreeSet<SectionVersion> sortedSectionVersions = new TreeSet<>(getChordSectionMap().keySet());
            SectionVersion nextSectionVersion = sortedSectionVersions.lower(chordSection.getSectionVersion());
            ret = (getChordSectionMap().remove(chordSection.getSectionVersion()) != null);
            if (ret) {
                //  move deleted current to end of previous section
                if (nextSectionVersion == null) {
                    sortedSectionVersions = new TreeSet<>(getChordSectionMap().keySet());
                    nextSectionVersion = (sortedSectionVersions.isEmpty() ? null : sortedSectionVersions.first());
                }
                if (nextSectionVersion != null) {
                    location = findChordSectionLocation(getChordSectionMap().get(nextSectionVersion));
                }
            }
        }
        return standardEditCleanup(ret, location);
    }

    private final boolean deleteCurrentChordSectionPhrase() {
        ChordSectionLocation location = getCurrentChordSectionLocation();
        ChordSection chordSection = getChordSection(location);
        boolean ret = chordSection.deletePhrase(location.getPhraseIndex());
        if (ret) {
            //  move the current location if required
            if (location.getPhraseIndex() >= chordSection.getPhrases().size()) {
                if (chordSection.getPhrases().isEmpty())
                    location = new ChordSectionLocation(chordSection.getSectionVersion());
                else {
                    int i = chordSection.getPhrases().size() - 1;
                    Phrase phrase = chordSection.getPhrase(i);
                    int m = phrase.getMeasures().size() - 1;
                    location = new ChordSectionLocation(chordSection.getSectionVersion(), i, m);
                }
            }
        }
        return standardEditCleanup(ret, location);
    }

    private final void preMod(MeasureNode measureNode) {
        logger.fine("startingChords(\"" + toMarkup() + "\");");
        logger.fine(" pre(MeasureEditType." + getCurrentMeasureEditType().name()
                + ", \"" + getCurrentChordSectionLocation().toString() + "\""
                + ", \""
                + (getCurrentChordSectionLocationMeasureNode() == null
                ? "null"
                : getCurrentChordSectionLocationMeasureNode().toMarkup()) + "\""
                + ", \"" + (measureNode == null ? "null" : measureNode.toMarkup()) + "\");");
    }

    private final void postMod() {
        logger.fine("resultChords(\"" + toMarkup() + "\");");
        logger.fine("post(MeasureEditType." + getCurrentMeasureEditType().name()
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
                    chordSection = getChordSectionMap().get(SectionVersion.getDefault());
                    if (chordSection == null) {
                        chordSection = ChordSection.getDefault();
                        getChordSectionMap().put(chordSection.getSectionVersion(), chordSection);
                    }
                    break;
            }
        }

        //  default to insert if empty
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
                        getChordSectionMap().put(newChordSection.getSectionVersion(), newChordSection);
                        ret = true;
                        location = new ChordSectionLocation(newChordSection.getSectionVersion());
                        break;
                    case delete:
                        //  find the section prior to the one being deleted
                        TreeSet<SectionVersion> sortedSectionVersions = new TreeSet<>(getChordSectionMap().keySet());
                        SectionVersion nextSectionVersion = sortedSectionVersions.lower(chordSection.getSectionVersion());
                        ret = (getChordSectionMap().remove(chordSection.getSectionVersion()) != null);
                        if (ret) {
                            //  move deleted current to end of previous section
                            if (nextSectionVersion == null) {
                                sortedSectionVersions = new TreeSet<>(getChordSectionMap().keySet());
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
                            setCurrentMeasureEditType(MeasureEditType.append);

                            //  convert repeat to phrase
                            newPhrase = new Phrase(repeat.getMeasures(), location.getPhraseIndex());
                            int phaseIndex = location.getPhraseIndex();
                            if (phaseIndex > 0 && chordSection.getPhrase(phaseIndex - 1).getMeasureNodeType() == MeasureNode.MeasureNodeType.phrase) {

                                //  expect combination of the two phrases
                                Phrase priorPhrase = chordSection.getPhrase(phaseIndex - 1);
                                location = new ChordSectionLocation(chordSection.getSectionVersion(),
                                        phaseIndex - 1, priorPhrase.getMeasures().size() + newPhrase.getMeasures().size() - 1);
                                return standardEditCleanup(chordSection.deletePhrase(phaseIndex)
                                        && chordSection.add(phaseIndex, newPhrase), location);
                            }
                            location = new ChordSectionLocation(chordSection.getSectionVersion(),
                                    location.getPhraseIndex(), newPhrase.getMeasures().size() - 1);
                            logger.fine("new loc: " + location.toString());
                            return standardEditCleanup(chordSection.deletePhrase(newPhrase.getPhraseIndex())
                                    && chordSection.add(newPhrase.getPhraseIndex(), newPhrase), location);
                        }
                        repeat.setRepeats(newRepeat.getRepeats());
                        return standardEditCleanup(true, location);
                    }
                    if (newRepeat.getRepeats() <= 1)
                        return true;    //  no change but no change was asked for

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
                        logger.fine("min: " + minGridCoordinate.toString() + " " + minMeasureNode.toMarkup() + " " + minLocation.getMeasureIndex());
                        logger.fine("max: " + maxGridCoordinate.toString() + " " + maxMeasureNode.toMarkup() + " " + maxLocation.getMeasureIndex());

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
                        if (location.hasPhraseIndex()) {
                            phaseIndex = location.getPhraseIndex() + 1;
                            newLocation = new ChordSectionLocation(chordSection.getSectionVersion(), phaseIndex, newPhrase.size() - 1);
                            return standardEditCleanup(chordSection.add(phaseIndex, newPhrase), newLocation);
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
                                    newLocation = new ChordSectionLocation(chordSection.getSectionVersion(), phaseIndex,
                                            location.getMeasureIndex() + newPhrase.size() - 1);
                                    return standardEditCleanup(phrase.edit(
                                            editType, location.getMeasureIndex(), newPhrase), newLocation);
                                }
                                //  delete the phrase before replacing it
                                phaseIndex = location.getPhraseIndex();
                                if (phaseIndex > 0 && chordSection.getPhrase(phaseIndex - 1).getMeasureNodeType() == MeasureNode.MeasureNodeType.phrase) {
                                    //  expect combination of the two phrases
                                    Phrase priorPhrase = chordSection.getPhrase(phaseIndex - 1);
                                    location = new ChordSectionLocation(chordSection.getSectionVersion(),
                                            phaseIndex - 1, priorPhrase.getMeasures().size() + newPhrase.getMeasures().size());
                                    return standardEditCleanup(chordSection.deletePhrase(phaseIndex)
                                            && chordSection.add(phaseIndex, newPhrase), location);
                                } else {
                                    location = new ChordSectionLocation(chordSection.getSectionVersion(),
                                            phaseIndex, newPhrase.getMeasures().size() - 1);
                                    return standardEditCleanup(chordSection.deletePhrase(phaseIndex)
                                            && chordSection.add(phaseIndex, newPhrase), location);
                                }
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
                            return standardEditCleanup((getChordSectionMap().put(sectionVersion,
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
            clearCachedValues();    //  force lazy re-compute of markup when required, after and edit

            collapsePhrases(location);
            setCurrentChordSectionLocation(location);
            resetLastModifiedDateToNow();

            switch (getCurrentMeasureEditType()) {
                // case replace:
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

    private final void collapsePhrases(ChordSectionLocation location) {
        if (location == null)
            return;
        ChordSection chordSection = getChordSectionMap().get(location.getSectionVersion());
        if (chordSection == null)
            return;
        int limit = chordSection.getPhraseCount();
        Phrase lastPhrase = null;
        for (int i = 0; i < limit; i++) {
            Phrase phrase = chordSection.getPhrase(i);
            if (lastPhrase == null) {
                if (phrase.getMeasureNodeType() == MeasureNode.MeasureNodeType.phrase)
                    lastPhrase = phrase;
                continue;
            }
            if (phrase.getMeasureNodeType() == MeasureNode.MeasureNodeType.phrase) {
                if (lastPhrase != null) {
                    //  two contiguous phrases: join
                    lastPhrase.add(phrase.getMeasures());
                    chordSection.deletePhrase(i);
                    limit--;    //  one less index
                }
                lastPhrase = phrase;
            } else
                lastPhrase = null;
        }
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
        for (ChordSection chordSection : getChordSectionMap().values()) {
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
        return getGridCoordinateChordSectionLocationMap().get(gridCoordinate);
    }

    public final GridCoordinate getGridCoordinate(ChordSectionLocation chordSectionLocation) {
        chordSectionLocation = chordSectionLocation.changeSectionVersion(
                chordSectionGridMatches.get(chordSectionLocation.getSectionVersion()));
        return getGridChordSectionLocationCoordinateMap().get(chordSectionLocation);
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
        return getChordSectionMap().get(sectionVersion);   //  get not type safe!!!!
    }

    private final Measure findMeasure(ChordSectionLocation chordSectionLocation) {
        try {
            return getChordSectionMap().get(chordSectionLocation.getSectionVersion())
                    .getPhrase(chordSectionLocation.getPhraseIndex())
                    .getMeasure(chordSectionLocation.getMeasureIndex());
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            return null;
        }
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
        return findMeasureNode(getGridCoordinateChordSectionLocationMap().get(coordinate));
    }

    public final MeasureNode findMeasureNode(ChordSectionLocation chordSectionLocation) {
        try {
            ChordSection chordSection = getChordSectionMap().get(chordSectionLocation.getSectionVersion());
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
        return getChordSectionMap().get(sectionVersion);
    }

    public final ChordSection findChordSection(MarkedString markedString) throws ParseException {
        SectionVersion sectionVersion = SectionVersion.parse(markedString);
        return getChordSectionMap().get(sectionVersion);
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
        boolean ret = getChordSectionMap().remove(chordSection) != null;
        clearCachedValues();
        return ret;
    }

    @Deprecated
    public final String measureNodesToHtml(@NotNull String tableName, @NotNull Key key, int tran) {
        StringBuilder sb = new StringBuilder();

        sb.append("<table id=\"" + tableName + "\" class=\"" + CssConstants.style + "chordTable\">\n");

        ArrayList<String> innerHtml = null;
        int momentNumber = 0;
        int rowStartSequenceNumber = 0;
        int j = 0;
        LyricSection lyricSection = null;
        SongMoment songMoment;
        computeSongMoments();
        for (int safety = 0; safety < 10000; safety++) {
            songMoment = getSongMoment(momentNumber);
            if (songMoment == null)
                break;
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

            for (ChordSection chordSection : new TreeSet<>(getChordSectionMap().values())) {
                if (chordSection.getSectionVersion().equals(lyricSection.getSectionVersion())) {
                    innerHtml = chordSection.generateInnerHtml(key, tran, true);
                    rowStartSequenceNumber = momentNumber;
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
                        if (rowStartSequenceNumber < momentNumber) {
                            sb.append("<td colspan=\"10\">" +
                                    "<canvas id=\"bassLine" + rowStartSequenceNumber + "-" + (momentNumber - 1)
                                    + "\" width=\"900\" height=\"150\" style=\"border:1px solid #000000;\"/></tr>");
                            rowStartSequenceNumber = momentNumber;
                        }
                        break;
                    default:
                        //  increment for next time
                        momentNumber++;
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
        for (ChordSection chordSection : getChordSectionMap().values()) {
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

        String htmlLyrics = ""; //  table formatted
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

                        if (htmlLyrics.length() > 0) {
                            htmlLyrics += rowEnd;
                        }
                        htmlLyrics += rowStart + version.toString()
                                + "</td><td class=\"" + CssConstants.style + "lyrics" + version.getSection()
                                .getAbbreviation() + "Class\""
                                + ">";
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
                            htmlLyrics += c;
                            whiteSpace = ""; //  ignore trailing white space
                            state = 0;
                            break;
                        default:
                            if (!isSection) {
                                //  deal with bad formatting
                                htmlLyrics += rowStart
                                        + Section.getDefaultVersion().toString()
                                        + "</td><td class=\"" + CssConstants.style + "lyrics"
                                        + Section.getDefaultVersion().getSection().getAbbreviation() + "Class\""
                                        + ">";
                                isSection = true;
                            }
                            htmlLyrics += whiteSpace + c;
                            whiteSpace = "";
                            break;
                    }
                    break;
            }
            markedString.consume(1);     //  consume parsed character
        }

        htmlLyrics = tableStart
                + htmlLyrics
                + rowEnd
                + tableEnd;
        return htmlLyrics;
    }

    private final void parseLyrics() {
        int state = 0;
        String whiteSpace = "";
        StringBuilder lyricsBuilder = new StringBuilder();
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
                            lyricSection.add(new LyricsLine(lyricsBuilder.toString()));
                            lyricsBuilder = new StringBuilder();
                            whiteSpace = ""; //  ignore trailing white space
                            state = 0;
                            break;
                        default:
                            lyricsBuilder.append(whiteSpace).append(c);
                            whiteSpace = "";
                            break;
                    }
                    break;
            }

            markedString.consume(1);
        }
        //  last one is not terminated by another section
        if (lyricSection != null) {
            if (lyricsBuilder.length() > 0) {
                lyricSection.add(new LyricsLine(lyricsBuilder.toString()));
            }
            lyricSections.add(lyricSection);
        }

        //  safety with lazy eval
        clearCachedValues();
    }

    public final void transpose(FlexTable flexTable, int halfSteps, String fontSize) {
        transpose(flexTable, halfSteps, fontSize, false);
    }

    public final void transpose(ChordSection chordSection, FlexTable flexTable, int halfSteps, String fontSize,
                                boolean append) {
        if (chordSection == null || flexTable == null )
            return;
        logger.fine("tran: cs: " + chordSection.toMarkup());
        transpose(chordSection, flexTable, halfSteps, fontSize, append, true);
    }

    /**
     * Install whole chord section transposed into the given flex table
     *
     * @param flexTable the flex table to populate
     * @param halfSteps the number of tranposition halfSteps to apply
     * @param fontSize  the pixel size of the font
     * @param append    true if the chord sections are to be added to an existing flex table data
     */
    private final void transpose(FlexTable flexTable, int halfSteps, String fontSize, boolean append) {
        halfSteps = Util.mod(halfSteps, MusicConstant.halfStepsPerOctave);

        if (!append)
            flexTable.removeAllRows();

        //  install all chord sections
        for (ChordSection chordSection : new TreeSet<>(getChordSectionMap().values())) {
            transpose(chordSection, flexTable, halfSteps, fontSize, true, false);
        }
    }

    /**
     * Install the given chord section into the given flex table
     *
     * @param chordSection    the given chord section
     * @param flexTable       the flex table to populate
     * @param halfSteps       the number of transposition halfSteps to apply
     * @param fontSize        the pixel size of the font
     * @param append          true if the chord section is to be added to existing flex table data
     * @param isSingleSection true if the chord section the only one of this section
     */
    private final void transpose(final ChordSection chordSection, FlexTable flexTable, int halfSteps, String fontSize,
                                 boolean append, boolean isSingleSection) {
        if (chordSection == null)
            return;

        logger.fine("transpose: cs: " + chordSection.toMarkup());


        halfSteps = Util.mod(halfSteps, MusicConstant.halfStepsPerOctave);

        Key newKey = key.nextKeyByHalfStep(halfSteps);

        SectionVersion sectionVersion = chordSection.getSectionVersion();
        GridCoordinate coordinate = getChordSectionGridCoorinateMap().get(sectionVersion);
        if (coordinate == null)
            return;

        //  if it's single, show the whole detail
        if (isSingleSection) {
            sectionVersion = getGridCoordinateChordSectionLocationMap().get(coordinate).getSectionVersion();
            coordinate = getChordSectionGridCoorinateMap().get(sectionVersion);
            if (coordinate == null)
                return;
        }

        int offset = -coordinate.getRow();
        if (append) {
            offset += flexTable.getRowCount();
        } else {
            flexTable.removeAllRows();
        }


        FlexTable.FlexCellFormatter formatter = flexTable.getFlexCellFormatter();

        Grid<ChordSectionLocation> grid = getChordSectionLocationGrid();

        logger.fine(grid.toString());

        int rLimit = grid.getRowCount();    // safety third
        String abbreviation = "";
        boolean firstRow = true;
        for (int r = coordinate.getRow(); r < rLimit; r++) {

            ArrayList<ChordSectionLocation> row = grid.getRow(r);
            int colLimit = row.size();
            String lastValue = "";
            for (int c = 0; c < colLimit; c++) {
                ChordSectionLocation loc = row.get(c);
                final int flexRow = r + offset;

                if (loc == null) {
                    flexTable.setHTML(flexRow, c, " ");
                    continue;
                }

                MeasureNode measureNode = findMeasureNode(loc);

                //  expect comment to span the row
                if (measureNode.isComment() && !measureNode.isRepeat()) {   //fixme: better identification of repeat marker
                    formatter.setColSpan(flexRow, c, MusicConstant.measuresPerDisplayRow);
                }

                String s = "";
                switch (c) {
                    case 0:
                        if (!sectionVersion.equals(loc.getSectionVersion()))
                            return;

                        if (isSingleSection) {
                            //  only do one section as labeled
                            if (firstRow) {
                                s = chordSection.getSectionVersion().toString();
                                abbreviation = chordSection.getSectionVersion().getSection().getAbbreviation();
                            } else
                                continue;
                        } else {
                            //  do all the sections that match
                            s = loc.toString();
                            abbreviation = loc.getSectionVersion().getSection().getAbbreviation();
                        }

                        formatter.addStyleName(flexRow, 0, CssConstants.style + "sectionLabel");
                        break;
                    default:
                        s = measureNode.transpose(newKey, halfSteps);
                        abbreviation = chordSection.getSection().getAbbreviation();
                        break;
                }

                //  enforce the - on repeated measures
                if (appOptions.isDashAllMeasureRepetitions()
                        && c > 0
                        && c <= MusicConstant.measuresPerDisplayRow
                        && s.equals(lastValue)
                        && !(measureNode instanceof MeasureComment)) {
                    s = "-";
                    formatter.addStyleName(flexRow, c, CssConstants.style + "textCenter");
                } else
                    lastValue = s;

                //formatter.setAlignment(flexRow, c, ALIGN_CENTER, ALIGN_BOTTOM);   //  as per Shari, comment out
                flexTable.setHTML(flexRow, c,
                        (fontSize != null ?
                        "<span style=\"font-size: " + fontSize + ";\">"
                                + s
                                + "</span>"
                                : s)
                );
                formatter.addStyleName(flexRow, c, CssConstants.style
                        + (measureNode.isComment() && !measureNode.isRepeat()
                        ? "sectionCommentClass"
                        : "section" + abbreviation + "Class"));
            }

            firstRow = false;
        }
    }

    /**
     * Debug only!
     *
     * @return a string form of the song chord section grid
     */
    public final String logGrid() {

        StringBuilder sb = new StringBuilder("\n");

        calcChordMaps();    //  avoid ConcurrentModificationException
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
        getChordSectionMap().put(chordSection.getSectionVersion(), chordSection);
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
                getChordSectionMap().put(chordSection.getSectionVersion(), chordSection);
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
            getChordSectionMap().put(chordSection.getSectionVersion(), chordSection);
        }

        clearCachedValues();
    }

    /**
     * Set the number of measures displayed per row
     *
     * @param measuresPerRow the number of measurese per row
     * @return true if a change was made
     */
    public boolean setMeasuresPerRow(int measuresPerRow) {
        if (measuresPerRow <= 0)
            return false;

        boolean ret = false;
        for (ChordSection chordSection : new TreeSet<>(getChordSectionMap().values())) {
            ret = chordSection.setMeasuresPerRow(measuresPerRow) || ret;
        }
        if (ret)
            clearCachedValues();
        return ret;
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
                getUser(),
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
     * @param user                 the app user's name
     * @param unitsPerMeasureEntry the inverse of the note duration fraction per entry, for exmple if each beat is
     *                             represented by a quarter note, the units per measure would be 4.
     * @param chordsTextEntry      the string transport form of the song's chord sequence description
     * @param lyricsTextEntry      the string transport form of the song's section sequence and lyrics
     * @return a new song if the fields are valid
     * @throws ParseException exception thrown if the song's fields don't match properly.
     */
    public static final Song checkSong(String title, String artist, String copyright,
                                       Key key, String bpmEntry, String beatsPerBarEntry, String unitsPerMeasureEntry,
                                       String user,
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
            throw new ParseException("BPM has to be a number from " + MusicConstant.minBpm + " to " + MusicConstant.maxBpm, 0);
        }
        int bpm = Integer.parseInt(bpmEntry);
        if (bpm < MusicConstant.minBpm || bpm > MusicConstant.maxBpm) {
            throw new ParseException("BPM has to be a number from " + MusicConstant.minBpm + " to " + MusicConstant.maxBpm, 0);
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
                user,
                chordsTextEntry, lyricsTextEntry);
        newSong.resetLastModifiedDateToNow();

        if (newSong.getChordSections().isEmpty())
            throw new ParseException("The song has no chord sections!", 0);

        for (ChordSection chordSection : newSong.getChordSections()) {
            if (chordSection.isEmpty())
                throw new ParseException("Chord section " + chordSection.getSectionVersion().toString()
                        + " is empty.", 0);
        }

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

        if (newSong.getMessage() == null) {
            for (ChordSection chordSection : newSong.getChordSections()) {
                for (Phrase phrase : chordSection.getPhrases()) {
                    for (Measure measure : phrase.getMeasures()) {
                        if (measure.isComment()) {
                            throw new ParseException("chords should not have comments: see " + chordSection.toString(), 0);
                        }
                    }
                }
            }
        }

        newSong.setMessage(null);

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

    public boolean hasSectionVersion(Section section, int version) {
        if (section == null)
            return false;

        for (SectionVersion sectionVersion : getChordSectionMap().keySet()) {
            if (sectionVersion.getSection() == section && sectionVersion.getVersion() == version)
                return true;
        }
        return false;
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

    public void resetLastModifiedDateToNow() {
        //  for song override
    }

    private final void computeSongId() {
        songId = computeSongId(title, artist, coverArtist);
    }

    public static final SongId computeSongId(String title, String artist, String coverArtist) {
        return new SongId("Song_" + title.replaceAll("\\W+", "")
                + "_by_" + artist.replaceAll("\\W+", "")
                + (coverArtist == null || coverArtist.length() <= 0 ? "" : "_coverBy_" + coverArtist));
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

    public final double getDefaultTimePerBar() {
        if (defaultBpm == 0)
            return 1;
        return beatsPerBar * 60.0 / defaultBpm;
    }

    public final double getSecondsPerBeat() {
        if (defaultBpm == 0)
            return 1;
        return 60.0 / defaultBpm;
    }

    /**
     * Set the song default beats per minute.
     *
     * @param bpm the defaultBpm to set
     */
    public final void setBeatsPerMinute(int bpm) {
        if (bpm < 20)
            bpm = 20;
        else if (bpm > 1000)
            bpm = 1000;
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
        //  never divide by zero
        if (beatsPerBar <= 1)
            beatsPerBar = 2;
        this.beatsPerBar = beatsPerBar;
        clearCachedValues();
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

    public Collection<ChordSection> getChordSections() {
        return getChordSectionMap().values();
    }

    public final Arrangement getDrumArrangement() {
        return drumArrangement;
    }

    public final void setDrumArrangement(Arrangement drumArrangement) {
        this.drumArrangement = drumArrangement;
    }

    public final String getFileName() {
        return fileName == null ? "" : fileName;
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
        computeDuration();
        return duration;
    }

    public final int getTotalBeats() {
        computeDuration();
        return totalBeats;
    }

    public final int getSongMomentsSize() {
        return getSongMoments().size();
    }

    private final ArrayList<SongMoment> getSongMoments() {
        computeSongMoments();
        return songMoments;
    }

    public final SongMoment getSongMoment(int momentNumber) {
        computeSongMoments();
        if (songMoments.isEmpty() || momentNumber < 0 || momentNumber >= songMoments.size())
            return null;
        return songMoments.get(momentNumber);
    }

    public final SongMoment getFirstSongMomentInSection(int momentNumber) {
        SongMoment songMoment = getSongMoment(momentNumber);
        if (songMoment == null)
            return null;

        SongMoment firstSongMoment = songMoment;
        String id = songMoment.getChordSection().getId();
        for (int m = momentNumber - 1; m >= 0; m--) {
            SongMoment sm = songMoments.get(m);
            if (!id.equals(sm.getChordSection().getId())
                    || sm.getSectionCount() != firstSongMoment.getSectionCount())
                return firstSongMoment;
            firstSongMoment = sm;
        }
        return firstSongMoment;
    }

    public final SongMoment getLastSongMomentInSection(int momentNumber) {
        SongMoment songMoment = getSongMoment(momentNumber);
        if (songMoment == null)
            return null;

        SongMoment lastSongMoment = songMoment;
        String id = songMoment.getChordSection().getId();
        int limit = songMoments.size();
        for (int m = momentNumber + 1; m < limit; m++) {
            SongMoment sm = songMoments.get(m);
            if (!id.equals(sm.getChordSection().getId())
                    || sm.getSectionCount() != lastSongMoment.getSectionCount())
                return lastSongMoment;
            lastSongMoment = sm;
        }
        return lastSongMoment;
    }

    public final double getSongTimeAtMoment(int momentNumber) {
        SongMoment songMoment = getSongMoment(momentNumber);
        if (songMoment == null)
            return 0;
        return songMoment.getBeatNumber() * getBeatsPerMinute() / 60.0;
    }

    public static final int getBeatNumberAtTime(int bpm, double songTime) {
        if (bpm <= 0)
            return Integer.MAX_VALUE;       //  we're done with this song play

        int songBeat = (int) Math.round(Math.floor(songTime * bpm / 60.0));
        return songBeat;
    }

    public final int getSongMomentNumberAtSongTime(double songTime) {
        if (getBeatsPerMinute() <= 0)
            return Integer.MAX_VALUE;       //  we're done with this song play

        int songBeat = getBeatNumberAtTime(getBeatsPerMinute(), songTime);
        if (songBeat < 0) {
            return (songBeat - beatsPerBar + 1) / beatsPerBar;    //  constant measure based lead in
        }

        computeSongMoments();
        if (songBeat >= beatsToMoment.size())
            return Integer.MAX_VALUE;       //  we're done with the last measure of this song play

        return beatsToMoment.get(songBeat).getMomentNumber();
    }

    /**
     * Return the first moment on the given row
     *
     * @param rowIndex the given row
     * @return the first moment on the given row
     */
    public final SongMoment getSongMomentAtRow(int rowIndex) {
        if (rowIndex < 0)
            return null;
        computeSongMoments();
        for (SongMoment songMoment : songMoments) {
            //  return the first moment on this row
            if (rowIndex == getMomentGridCoordinate(songMoment).getRow())
                return songMoment;
        }
        return null;
    }

    public final ArrayList<LyricSection> getLyricSections() {
        return lyricSections;
    }

    public final int getFileVersionNumber() {
        return fileVersionNumber;
    }

    public final int getChordSectionBeats(ChordSectionLocation chordSectionLocation) {
        if (chordSectionLocation == null)
            return 0;
        return getChordSectionBeats(chordSectionLocation.getSectionVersion());
    }

    public final int getChordSectionBeats(SectionVersion sectionVersion) {
        if (sectionVersion == null)
            return 0;
        computeSongMoments();
        Integer ret = chordSectionBeats.get(sectionVersion);
        if (ret == null)
            return 0;
        return ret;
    }

    public final int getChordSectionRows(SectionVersion sectionVersion) {
        computeSongMoments();
        Integer ret = chordSectionRows.get(sectionVersion);
        if (ret == null)
            return 0;
        return ret;
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
            for (ChordSection chordSection : getChordSectionMap().values()) {
                for (Phrase phrase : chordSection.getPhrases()) {

                    //  the more different measures, the greater the complexity
                    differentChords.addAll(phrase.getMeasures());

                    //  weight measures by guitar complexity
                    for (Measure measure : phrase.getMeasures())
                        if (!measure.isEasyGuitarMeasure())
                            complexity++;
                }
            }
            //  more chord sections => more complex
            complexity += getChordSectionMap().values().size();

            //  more different chords => more complex
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

    protected final void setChords(String chords) {
        this.chords = chords;
        clearCachedValues();
    }

    protected final void setRawLyrics(String rawLyrics) {
        this.rawLyrics = rawLyrics;
        parseLyrics();
    }

    protected final void setTotalBeats(int totalBeats) {
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
            if (getChordSectionMap().keySet().isEmpty()) {
                currentChordSectionLocation = new ChordSectionLocation(SectionVersion.getDefault());
            } else {
                //  last location
                TreeSet<SectionVersion> sectionVersions = new TreeSet<>(getChordSectionMap().keySet());
                ChordSection lastChordSection = getChordSectionMap().get(sectionVersions.last());
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
                    sortedSectionVersions.addAll(getChordSectionMap().keySet());
                    cs = getChordSectionMap().get(sortedSectionVersions.last());
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
            return o1.compareBySongId(o2);
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
            return o1.compareBySongId(o2);
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

    public static final boolean containsSongTitleAndArtist(Collection<? extends SongBase> collection, SongBase
            song) {
        for (SongBase collectionSong : collection) {
            if (song.compareBySongId(collectionSong) == 0)
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
    public int compareBySongId(SongBase o) {
        if (o == null)
            return -1;
        int ret = getSongId().compareTo(o.getSongId());
        if (ret != 0) {
            return ret;
        }
        return 0;
    }

    public final boolean songBaseSameAs(SongBase o) {
        //  song id built from title with reduced whitespace
        if (!getTitle().equals(o.getTitle()))
            return false;
        if (!getArtist().equals(o.getArtist()))
            return false;
        if (getCoverArtist() != null) {
            if (!getCoverArtist().equals(o.getCoverArtist()))
                return false;
        } else if (o.getCoverArtist() != null) {
            return false;
        }
        if (!getCopyright().equals(o.getCopyright()))
            return false;
        if (!getKey().equals(o.getKey()))
            return false;
        if (defaultBpm != o.defaultBpm)
            return false;
        if (unitsPerMeasure != o.unitsPerMeasure)
            return false;
        if (beatsPerBar != o.beatsPerBar)
            return false;
        if (!toMarkup().equals(o.toMarkup()))
            return false;
        if (!rawLyrics.equals(o.rawLyrics))
            return false;
        if (!metadata.equals(o.metadata))
            return false;
        if (lastModifiedTime != o.lastModifiedTime)
            return false;

        //  hmm, think about these
        if (fileName != o.fileName)
            return false;
        if (fileVersionNumber != o.fileVersionNumber)
            return false;

        return true;

    }

    @Override
    public boolean equals(Object obj) {
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
        hash = (79 * hash + getChordSectionMap().hashCode()) % (1 << 31);
        hash = (79 * hash + Objects.hashCode(this.rawLyrics)) % (1 << 31);
        hash = (79 * hash + Objects.hashCode(this.metadata)) % (1 << 31);
        return hash;
    }

    //  primary values
    private String title = "Unknown";
    private String artist = "Unknown";
    private String user = defaultUser;
    private String coverArtist = "";
    private String copyright = "Unknown";
    private Key key = Key.C;  //  default
    private int defaultBpm = 106;  //  beats per minute
    private int unitsPerMeasure = 4;//  units per measure, i.e. timeSignature numerator
    private int beatsPerBar = 4;  //  beats per bar, i.e. timeSignature denominator
    private double lastModifiedTime;
    private String rawLyrics = "";

    //  meta data
    private transient String fileName;

    //  deprecated values
    private transient int fileVersionNumber = 0;

    //  computed values
    private SongId songId = new SongId();
    private String timeSignature = unitsPerMeasure + "/" + beatsPerBar;//   fixme: doesn't update!!!!
    private transient double duration;    //  units of seconds
    private transient int totalBeats;
    private String chords = "";
    private transient HashMap<SectionVersion, ChordSection> chordSectionMap;
    private ArrayList<MeasureNode> measureNodes;

    private ArrayList<LyricSection> lyricSections = new ArrayList<>();
    private transient HashMap<SectionVersion, GridCoordinate> chordSectionGridCoorinateMap = new HashMap<>();

    //  match to representative section version
    private transient HashMap<SectionVersion, SectionVersion> chordSectionGridMatches = new HashMap<>();

    private transient HashMap<GridCoordinate, ChordSectionLocation> gridCoordinateChordSectionLocationMap;
    private transient HashMap<ChordSectionLocation, GridCoordinate> gridChordSectionLocationCoordinateMap;
    private transient HashMap<SongMoment, GridCoordinate> songMomentGridCoordinateHashMap = new HashMap<>();
    private transient HashMap<SectionVersion, Integer> chordSectionBeats = new HashMap<>();
    private transient HashMap<SectionVersion, Integer> chordSectionRows = new HashMap<>();

    private ChordSectionLocation currentChordSectionLocation;
    private MeasureEditType currentMeasureEditType = MeasureEditType.append;
    private transient Grid<ChordSectionLocation> chordSectionLocationGrid = null;
    private transient int complexity;
    private transient String chordsAsMarkup;
    private transient String message;
    private ArrayList<SongMoment> songMoments = new ArrayList<>();
    private HashMap<Integer, SongMoment> beatsToMoment = new HashMap<>();


    private LegacyDrumSection drumSection = new LegacyDrumSection();
    private Arrangement drumArrangement;    //  default
    private TreeSet<Metadata> metadata = new TreeSet<>();
    private static final AppOptions appOptions = AppOptions.getInstance();
    private static final String defaultUser = "Unknown";

    private static final Logger logger = Logger.getLogger(SongBase.class.getName());

    static {
        // logger.setLevel(Level.FINER);
    }
}
