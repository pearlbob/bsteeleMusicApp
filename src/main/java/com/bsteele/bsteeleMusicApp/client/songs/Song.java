/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.shared.JsonUtil;
import com.google.gwt.core.client.JsDate;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.logging.Logger;

/**
 * @author bob
 */

/**
 * A piece of music to be played according to the structure it contains.
 */
public class Song
        extends SongBase
        implements Comparable<Song>
{

    /**
     * Not to be used externally but must remain public due to GWT constraints!
     */
    @Deprecated
    public Song()
    {
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
        song.setCopyright(copyright);
        song.setKey(key);
        song.setUnitsPerMeasure(unitsPerMeasure);
        song.setRawLyrics ( lyrics);
        song.setChords (chords);

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
                getStructuralGridAsText(), getLyricsAsString());
        ret.setFileName(getFileName());
        ret.setLastModifiedDate(getLastModifiedDate());
        ret.setDuration( getDuration());
        ret.setTotalBeats( getTotalBeats());
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
                    song.setCopyright(jv.isString().stringValue());
                    break;
                case "key":
                    song.setKey( Key.valueOf(jv.isString().stringValue()));
                    break;
                case "defaultBpm":
                    jn = jv.isNumber();
                    if (jn != null) {
                        song.setDefaultBpm((int) jn.doubleValue());
                    } else {
                        song.setDefaultBpm( Integer.parseInt(jv.isString().stringValue()));
                    }
                    break;
                case "timeSignature":
                    //  most of this is coping with real old events with poor formatting
                    jn = jv.isNumber();
                    if (jn != null) {
                        song.setBeatsPerBar((int) jn.doubleValue());
                        song.setUnitsPerMeasure( 4); //  safe default
                    } else {
                        String s = jv.isString().stringValue();

                        final RegExp timeSignatureExp = RegExp.compile("^\\w*(\\d{1,2})\\w*\\/\\w*(\\d)\\w*$");
                        MatchResult mr = timeSignatureExp.exec(s);
                        if (mr != null) {
                            // parse
                            song.setBeatsPerBar( Integer.parseInt(mr.getGroup(1)));
                            song.setUnitsPerMeasure( Integer.parseInt(mr.getGroup(2)));
                        } else {
                            s = s.replaceAll("/.*", "");    //  fixme: info lost
                            if (s.length() > 0) {
                                song.setBeatsPerBar( Integer.parseInt(s));
                            }
                            song.setUnitsPerMeasure(4); //  safe default
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
                        song.setChords( sb.toString());
                    } else {
                        song.setChords(jv.isString().stringValue());
                    }
                    song.parseChordTable(song.getChords());
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
                        song.setRawLyrics( sb.toString());
                    } else {
                        song.setRawLyrics( jv.isString().stringValue());
                    }
                    song.parseLyricsToSectionSequence(song.getRawLyrics());
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
                .append(getKey().name())
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


    public final Song checkSong()
            throws ParseException
    {
        return checkSong(getTitle(), getArtist(), getCopyright(),
                getKey(), Integer.toString(getDefaultBpm()), Integer.toString(getBeatsPerBar()),
                Integer.toString(getUnitsPerMeasure()),
                getChords(), getRawLyrics());
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


    public final JsDate getLastModifiedDate()
    {
        return lastModifiedDate;
    }

    public final void setLastModifiedDate(JsDate lastModifiedDate)
    {
        this.lastModifiedDate = lastModifiedDate;
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

    public static int compareByLastModifiedDate(Song o1, Song o2)
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
            return compareByVersionNumber(o1, o2);
        }
    }

    public static int compareByVersionNumber(Song o1, Song o2)
    {
        logger.finest("o1.fileVersionNumber:" + o1.getFileVersionNumber() + ", o2.fileVersionNumber: " + o2.getFileVersionNumber());
        int ret = o1.compareTo(o2);
        if (ret != 0)
            return ret;
        if (o1.getFileVersionNumber() != o2.getFileVersionNumber())
            return o1.getFileVersionNumber() < o2.getFileVersionNumber() ? -1 : 1;
        return compareByLastModifiedDate(o1, o2);
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
        if (!(obj instanceof Song)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode()
    {
        //  fixme: song hashCode should include all fields
        return super.hashCode();
    }

    private transient JsDate lastModifiedDate;


    private static final int minBpm = 50;
    private static final int maxBpm = 400;

    private static final char js_delta = '\u0394';

    private static final Logger logger = Logger.getLogger(Song.class.getName());
}
