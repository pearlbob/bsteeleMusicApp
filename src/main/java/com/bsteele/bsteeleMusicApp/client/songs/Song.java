/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.util.JsonUtil;
import com.bsteele.bsteeleMusicApp.shared.songs.Key;
import com.bsteele.bsteeleMusicApp.shared.songs.SongBase;
import com.bsteele.bsteeleMusicApp.shared.util.StringTriple;
import com.google.gwt.core.client.JsDate;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONException;
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
 * A song is a wrapper class for {@link SongBase} that provides
 * file I/O routines and comparators for various sortings.
 * This is the class most all song interactions should reference.
 * <p>
 * The class is designed to provide all the GWT dependencies
 * away from SongBase so SongBase can be tested without a browser environment.
 * All the musical functions happen in SongBase.
 * </p>
 *
 * @author bob
 */
public class Song extends SongBase implements Comparable<Song> {

    /**
     * Not to be used externally but must remain public due to GWT constraints!
     */
    @Deprecated
    public Song() {
        super();
    }

    /**
     * Create a minimal song to be used internally as a place holder.
     *
     * @return a minimal song
     */
    public static final Song createEmptySong() {
        return createSong("", "",
                "", Key.C, 100, 4, 4,
                "",
                "", "");
    }

    /**
     * A convenience constructor used to enforce the minimum requirements for a song.
     *
     * @param title           title
     * @param artist          artist
     * @param copyright       copyright
     * @param key             key
     * @param bpm             bpm
     * @param beatsPerBar     beatsPerBar
     * @param unitsPerMeasure unitsPerMeasure
     * @param user            the app user's name
     * @param chords          chords
     * @param lyrics          lyrics
     * @return the song created
     */
    public static final Song createSong(@NotNull String title, @NotNull String artist,
                                        @NotNull String copyright,
                                        @NotNull Key key, int bpm, int beatsPerBar, int unitsPerMeasure,
                                        String user,
                                        @NotNull String chords, @NotNull String lyrics) {
        Song song = new Song();
        song.setTitle(title);
        song.setArtist(artist);
        song.setCopyright(copyright);
        song.setKey(key);
        song.setBeatsPerMinute(bpm);
        song.setBeatsPerBar(beatsPerBar);
        song.setUnitsPerMeasure(unitsPerMeasure);
        song.setUser(user);
        song.setChords(chords);
        song.setRawLyrics(lyrics);

        return song;
    }

    /**
     * Copy the song to a new instance.
     *
     * @return the new song
     */
    public final Song copySong() {
        Song ret = createSong(getTitle(), getArtist(),
                getCopyright(), getKey(), getBeatsPerMinute(), getBeatsPerBar(), getUnitsPerMeasure(),
                getUser(),
                toMarkup(), getLyricsAsString());
        ret.setFileName(getFileName());
        ret.setLastModifiedTime(getLastModifiedTime());
        ret.setDuration(getDuration());
        ret.setTotalBeats(getTotalBeats());
        ret.setCurrentChordSectionLocation(getCurrentChordSectionLocation());
        ret.setCurrentMeasureEditType(getCurrentMeasureEditType());
        return ret;
    }

    /**
     * Parse a song from a JSON string.
     *
     * @param jsonString the json string to parse
     * @return the song. Can be null.
     */
    public static final ArrayList<Song> fromJson(String jsonString) {
        ArrayList<Song> ret = new ArrayList<>();
        if (jsonString == null || jsonString.length() <= 0) {
            return ret;
        }

        //  fix for damaged files
        jsonString = jsonString.replaceAll("\": null,","\": \"\",");

        if (jsonString.startsWith("<")) {
            logger.warning("this can't be good: " + jsonString.substring(0, Math.min(25, jsonString.length())));
        }

        try {
            JSONValue jv = JSONParser.parseStrict(jsonString);

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
        } catch (JSONException e) {
            logger.warning(jsonString);
            logger.warning("JSONException: " + e.getMessage());
            return null;
        } catch (Exception e) {
            logger.warning("exception: " + e.getClass().getName());
            logger.warning(jsonString);
            logger.warning(e.getMessage());
            return null;
        }

        logger.fine("fromJson(): " + ret.get(ret.size() - 1));

        return ret;

    }

    /**
     * Parse a song from a JSON object.
     *
     * @param jsonObject the json object to parse
     * @return the song. Can be null.
     * @throws ParseException thrown if parsing fails
     */
    public static final Song fromJsonObject(JSONObject jsonObject)
            throws ParseException {
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
            throws ParseException {
        Song song = null;
        double lastModifiedTime = 0;
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
                        lastModifiedTime = jn.doubleValue();
                    }
                    break;
                case "file":
                    fileName = jv.isString().stringValue();
                    break;
            }
        }
        if (song == null)
            return null;

        if (lastModifiedTime > song.getLastModifiedTime())
            song.setLastModifiedTime(lastModifiedTime);
        song.setFileName(fileName);

        return song;
    }

    private static final Song songFromJsonObject(JSONObject jsonObject) throws ParseException {
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
                    song.setKey(Key.parse(jv.isString().stringValue()));
                    break;
                case "defaultBpm":
                    jn = jv.isNumber();
                    if (jn != null) {
                        song.setDefaultBpm((int) jn.doubleValue());
                    } else {
                        song.setDefaultBpm(Integer.parseInt(jv.isString().stringValue()));
                    }
                    break;
                case "timeSignature":
                    //  most of this is coping with real old events with poor formatting
                    jn = jv.isNumber();
                    if (jn != null) {
                        song.setBeatsPerBar((int) jn.doubleValue());
                        song.setUnitsPerMeasure(4); //  safe default
                    } else {
                        String s = jv.isString().stringValue();

                        final RegExp timeSignatureExp = RegExp.compile("^\\w*(\\d{1,2})\\w*\\/\\w*(\\d)\\w*$");
                        MatchResult mr = timeSignatureExp.exec(s);
                        if (mr != null) {
                            // parse
                            song.setBeatsPerBar(Integer.parseInt(mr.getGroup(1)));
                            song.setUnitsPerMeasure(Integer.parseInt(mr.getGroup(2)));
                        } else {
                            s = s.replaceAll("/.*", "");    //  fixme: info lost
                            if (s.length() > 0) {
                                song.setBeatsPerBar(Integer.parseInt(s));
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
                            sb.append(", ");       //  brutal way to transcribe the new line without the chaos of a newline character
                        }
                        song.setChords(sb.toString());
                    } else {
                        song.setChords(jv.isString().stringValue());
                    }
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
                        song.setRawLyrics(sb.toString());
                    } else {
                        song.setRawLyrics(jv.isString().stringValue());
                    }
                    break;
                case "lastModifiedDate":
                    jn = jv.isNumber();
                    if (jn != null) {
                        song.setLastModifiedTime(jn.doubleValue());
                    }
                    break;
                case "user":
                    song.setUser(jv.isString().stringValue());
                    break;
            }
        }
        return song;
    }

    public static final String toJson(Collection<Song> songs) {
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

    private String toJsonAsFile() {
        return "{ \"file\": " + JsonUtil.encode(getFileName()) + ", \"lastModifiedDate\": " + getLastModifiedTime() + ", \"song\":" +
                " \n"
                + toJson()
                + "}";
    }

    /**
     * Generate the JSON expression of this song.
     *
     * @return the JSON expression of this song
     */
    public final String toJson() {
        StringBuilder sb = new StringBuilder();

        sb.append("{\n")
                .append("\"title\": ")
                .append(JsonUtil.encode(getTitle()))
                .append(",\n")
                .append("\"artist\": ")
                .append(JsonUtil.encode(getArtist()))
                .append(",\n")
                .append("\"user\": ")
                .append(JsonUtil.encode(getUser()))
                .append(",\n")
                .append("\"lastModifiedDate\": ")
                .append(getLastModifiedTime())
                .append(",\n")
                .append("\"copyright\": ")
                .append(JsonUtil.encode(getCopyright()))
                .append(",\n")
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
        for (String s : chordsToJsonTransportString().split("\n")) {
            if (s.length() == 0)  //  json is not happy with empty array elements
                continue;
            if (first) {
                first = false;
            } else {
                sb.append(",\n");
            }
            sb.append("\t");
            sb.append(JsonUtil.encode(s));
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
            sb.append("\t");

            sb.append(JsonUtil.encode(s));
        }
        sb.append("\n    ]\n")
                .append("}\n");

        return sb.toString();
    }

    @Override
    public void resetLastModifiedDateToNow() {
        setLastModifiedTime(JsDate.create().getTime());
    }

    public static final ArrayList<StringTriple> diff(Song a, Song b) {
        ArrayList<StringTriple> ret = SongBase.diff(a, b);
        final int limit = 15;
        if (ret.size() > limit) {
            while (ret.size() > limit) {
                ret.remove(ret.size() - 1);
            }
            ret.add(new StringTriple("+more", "", ""));
        }
        ret.add(0, new StringTriple("file date",
                JsDate.create(a.getLastModifiedTime()).toDateString(),
                JsDate.create(b.getLastModifiedTime()).toDateString()
        ));
        return ret;
    }


    public enum ComparatorType {
        title,
        artist,
        lastModifiedDate,
        lastModifiedDateLast,
        versionNumber,
        complexity;
    }

    public static final Comparator<Song> getComparatorByType(ComparatorType type) {
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
            case complexity:
                return new ComparatorByComplexity();
        }
    }

    /**
     * A comparator that sorts by song title and then artist.
     * Note the title order implied by {@link #compareTo(Song)}.
     */
    public static final class ComparatorByTitle implements Comparator<Song> {

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
        public int compare(Song o1, Song o2) {
            return o1.compareTo(o2);
        }
    }

    /**
     * A comparator that sorts on the artist.
     */
    public static final class ComparatorByArtist implements Comparator<Song> {

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
        public int compare(Song o1, Song o2) {
            int ret = o1.getArtist().compareTo(o2.getArtist());
            if (ret != 0) {
                return ret;
            }
            return o1.compareTo(o2);
        }
    }

    public static final class ComparatorByLastModifiedDate implements Comparator<Song> {

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
        public int compare(Song o1, Song o2) {
            double mod1 = o1.getLastModifiedTime();
            double mod2 = o2.getLastModifiedTime();

            if (mod1 == mod2)
                return o1.compareTo(o2);
            return mod1 < mod2 ? 1 : -1;
        }
    }

    public static final class ComparatorByLastModifiedDateLast implements Comparator<Song> {

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
        public int compare(Song o1, Song o2) {
            return compareByLastModifiedDate(o1, o2);
        }
    }

    public static int compareByLastModifiedDate(Song o1, Song o2) {
        double mod1 = o1.getLastModifiedTime();
        double mod2 = o2.getLastModifiedTime();

        if (mod1 == mod2)
            return o1.compareTo(o2);
        return mod1 < mod2 ? -1 : 1;
    }

    public static final class ComparatorByVersionNumber implements Comparator<Song> {

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
        public int compare(Song o1, Song o2) {
            return compareByVersionNumber(o1, o2);
        }
    }

    public static int compareByVersionNumber(Song o1, Song o2) {
        logger.finest("o1.fileVersionNumber:" + o1.getFileVersionNumber() + ", o2.fileVersionNumber: " + o2.getFileVersionNumber());
        int ret = o1.compareTo(o2);
        if (ret != 0)
            return ret;
        if (o1.getFileVersionNumber() != o2.getFileVersionNumber())
            return o1.getFileVersionNumber() < o2.getFileVersionNumber() ? -1 : 1;
        return compareByLastModifiedDate(o1, o2);
    }

    public static final class ComparatorByComplexity implements Comparator<Song> {

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
        public int compare(Song o1, Song o2) {
            return compareByComplexity(o1, o2);
        }
    }

    public static int compareByComplexity(Song o1, Song o2) {
        if (o1.getComplexity() != o2.getComplexity())
            return o1.getComplexity() < o2.getComplexity() ? -1 : 1;
        return o1.compareTo(o2);
    }


    /**
     * Compare only the title and artist.
     * To be used for general user listing purposes only.
     * <p>Note that leading articles will be rotated to the end.</p>
     *
     * @param o the other song to be compared with this one
     * @return the comparison: -1 implies this &lt; o, 1 implies this &gt; o, 0 implies equal
     */
    @Override
    public int compareTo(Song o) {
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

    private static final Logger logger = Logger.getLogger(Song.class.getName());
}
