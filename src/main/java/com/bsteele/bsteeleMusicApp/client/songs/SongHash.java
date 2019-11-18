package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.shared.songs.SongBase;
import com.bsteele.bsteeleMusicApp.shared.songs.SongId;
import com.bsteele.bsteeleMusicApp.shared.util.Md5;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

public class SongHash {
    /**
     * Parse a song from a JSON string.
     *
     * @param jsonString the json string to parse
     * @return the song. Can be null.
     */
    public static final ArrayList<SongHash> fromJson(String jsonString) {
        ArrayList<SongHash> ret = new ArrayList<>();
        if (jsonString == null || jsonString.length() <= 0) {
            return ret;
        }

        if (jsonString.startsWith("<")) {
            logger.warning("this can't be good: " + jsonString.substring(0, Math.min(25, jsonString.length())));
        }

        try {
            JSONValue jv = JSONParser.parseStrict(jsonString);

            JSONArray ja = jv.isArray();
            if (ja != null) {
                int jaLimit = ja.size();
                //logger.info( "ja.size: "+jaLimit);
                for (int i = 0; i < jaLimit; i++) {
                    ret.add(SongHash.fromJsonFileObject(ja.get(i).isObject()));
                }
            } else {
                JSONObject jo = jv.isObject();
                ret.add(fromJsonFileObject(jo));
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

        logger.fine("last fromJson(): " + ret.get(ret.size() - 1));

        return ret;

    }

    private static final SongHash fromJsonFileObject(JSONObject jsonObject)
            throws ParseException {
        SongHash songHash = null;
        String fileName = "unknown";
        double lastModifiedTime = 0;

        JSONNumber jn;
        for (String name : jsonObject.keySet()) {
            JSONValue jv = jsonObject.get(name);
            switch (name) {
                case "song":
                    songHash = fromJsonObject(jv.isObject());
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
        if (songHash == null)
            return null;

        songHash.lastModifiedTime = lastModifiedTime;
        songHash.fileName = fileName;

        return songHash;
    }

    private static final SongHash fromJsonObject(JSONObject jsonObject) throws ParseException {
        SongHash songHash = new SongHash();
        songHash.songJson = jsonObject.toString();
        songHash.hash = Md5.getMd5Digest(songHash.songJson);
        JSONNumber jn;

        for (String name : jsonObject.keySet()) {
            JSONValue jv = jsonObject.get(name);
            switch (name) {
                case "title":
                    songHash.title = jv.isString().stringValue();
                    break;
                case "artist":
                    songHash.artist = jv.isString().stringValue();
                    break;
                case "coverArtist":
                    songHash.coverArtist = jv.isString().stringValue();
                    break;

                case "lastModifiedDate":
                    jn = jv.isNumber();
                    if (jn != null) {
                        songHash.lastModifiedTime = jn.doubleValue();
                    }
                    break;
                case "user":
                    songHash.user = jv.isString().stringValue();
                    break;
            }
        }
        songHash.songId = SongBase.computeSongId(songHash.title, songHash.artist, songHash.coverArtist);
        return songHash;
    }

    public String getFileName() {
        return fileName;
    }

    public double getLastModifiedTime() {
        return lastModifiedTime;
    }

    public String getHash() {
        return hash;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getUser() {
        return user;
    }

    public String getCoverArtist() {
        return coverArtist;
    }

    @Override
    public String toString() {
        Date date = new Date((long) lastModifiedTime);
        return getTitle() + ":" + getArtist() + ":" + getCoverArtist() + ":" + getHash() + ":" + fmt.format(date);
    }

    public SongId getSongId() {
        return songId;
    }

    public String getSongJson() {
        return songJson;
    }

    private String fileName = "unknown";
    private double lastModifiedTime = 0;
    private String hash = "";
    private String title = "Unknown";
    private String artist = "Unknown";
    private static final String defaultUser = "Unknown";
    private String user = "Unknown";
    private String coverArtist = "";
    private SongId songId = null;
    private String songJson = "";


    private static DateTimeFormat fmt = DateTimeFormat.getFormat("yyyyMMdd_HHmmss");

    private static final Logger logger = Logger.getLogger(SongHash.class.getName());
}
