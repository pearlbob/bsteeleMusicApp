/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.shared.JsonUtil;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import java.util.Objects;
import jsinterop.annotations.JsType;

/**
 * Immutable song update data
 * <p>
 * fixme: song update should always have a song
 *
 * @author bob
 */
@JsType
public class SongUpdate {

    public enum State {
        playing,
        idle;
    }

    SongUpdate() {
    }

//    void copySongUpdate(SongUpdate other) {
//        this.state = other.state;
//        this.eventTime = other.eventTime;
//        this.title = other.title;
//        this.sectionCount = other.sectionCount;
//        this.sectionVersion = other.sectionVersion;
//        this.chordSectionRow = other.chordSectionRow;
//        this.chordSectionCurrentRepeat = other.chordSectionCurrentRepeat;
//        this.chordSectionRepeat = other.chordSectionRepeat;
//        this.measure = other.measure;
//        this.beat = other.beat;
//        this.beatsPerMeasure = other.beatsPerMeasure;
//        this.beatsPerMinute = other.beatsPerMinute;
//    }

    public State getState() {
        return state;
    }


    /**
     * return event time in seconds
     *
     * @return
     */
    public long getEventTime() {
        return eventTime;
    }

    public Song getSong() {
        return song;
    }

    public int getSectionCount() {
        return sectionCount;
    }

    /**
     * Id of the current section to be used by the chords display.
     *
     * @return
     */
    public String getSection() {
        return section;
    }

    /**
     * @param section
     */
    public void setSection(String section) {
        this.section = section;
    }

    /**
     * Id of the current section to be used by the chords display.
     *
     * @return
     */
    public int getSectionVersion() {
        return sectionVersion;
    }

    /**
     * @param sectionVersion the sectionVersion to set
     */
    public void setSectionVersion(int sectionVersion) {
        this.sectionVersion = sectionVersion;
    }

    /**
     * Current row number in the section
     *
     * @return
     */
    public int getChordSectionRow() {
        return chordSectionRow;
    }

    /**
     * Current repeat count if the row(s) repeat
     *
     * @return
     */
    public int getChordSectionCurrentRepeat() {
        return chordSectionCurrentRepeat;
    }

    /**
     * Total number of repeats in the current repeat row(s).
     *
     * @return
     */
    public int getChordSectionRepeat() {
        return chordSectionRepeat;
    }

    /**
     * Measure number from start of song Starts at zero.
     *
     * @return
     */
    public int getMeasure() {
        return measure;
    }

    /**
     * Beat number from start of the current measure. Starts at zero and goes to
     * beatsPerBar - 1
     *
     * @return
     */
    public int getBeat() {
        return beat;
    }

    /**
     * @return the beatsPerMeasure
     */
    public int getBeatsPerMeasure() {
        return beatsPerMeasure;
    }

    /**
     * @return the beatsPerMinute
     */
    public int getBeatsPerMinute() {
        return beatsPerMinute;
    }


    void setState(State state) {
        this.state = state;
    }

    /**
     * @param eventTime the eventTime to set
     */
    void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * @param song the song to set
     */
    void setSong(Song song) {
        this.song = song;
    }

    /**
     * @param sectionCount the sectionCount to set
     */
    void setSectionCount(int sectionCount) {
        this.sectionCount = sectionCount;
    }

    /**
     * @param chordSectionRow the chordSectionRow to set
     */
    void setChordSectionRow(int chordSectionRow) {
        this.chordSectionRow = chordSectionRow;
    }

    /**
     * @param chordSectionCurrentRepeat the chordSectionCurrentRepeat to set
     */
    void setChordSectionCurrentRepeat(int chordSectionCurrentRepeat) {
        this.chordSectionCurrentRepeat = chordSectionCurrentRepeat;
    }

    /**
     * @param chordSectionRepeat the chordSectionRepeat to set
     */
    void setChordSectionRepeat(int chordSectionRepeat) {
        this.chordSectionRepeat = chordSectionRepeat;
    }

    /**
     * @param measure the measure to set
     */
    void setMeasure(int measure) {
        this.measure = measure;
    }

    /**
     * @param beat the beat to set
     */
    void setBeat(int beat) {
        this.beat = beat;
    }

    /**
     * @param beatsPerMeasure the beatsPerMeasure to set
     */
    void setBeatsPerBar(int beatsPerMeasure) {
        this.beatsPerMeasure = beatsPerMeasure;
    }

    /**
     * @param beatsPerMinute the beatsPerMinute to set
     */
    void setBeatsPerMinute(int beatsPerMinute) {
        this.beatsPerMinute = beatsPerMinute;
    }

    public String diff(SongUpdate other) {
        if ( other == null || other.song == null )
              return "no old song";
        if (!song.equals(other.song))
            return "new song: " + other.song.getTitle() + ", " + other.song.getArtist();
        if (!section.equals(other.section))
            return "new section: " + other.getSection().toString();
        if (measure != other.measure)
            return "new measure: " + other.measure;
        return "no change";
    }

    public static final SongUpdate fromJson(String jsonString) {
        //GWT.log(jsonString);
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

    public static final SongUpdate fromJsonObject(JSONObject jo) {
        if (jo == null) {
            return null;
        }
        SongUpdate songUpdate = new SongUpdate();
        for (String name : jo.keySet()) {
            JSONValue jv = jo.get(name);
            switch (name) {
                case "state":
                    songUpdate.setState(State.valueOf(jv.isString().stringValue()));
                    break;
                case "eventTime":
                    songUpdate.setEventTime(JsonUtil.toLong(jv));
                    break;
                case "song":
                    songUpdate.setSong(Song.fromJsonObject(jv.isObject()));
                    break;
                case "sectionCount":
                    songUpdate.sectionCount = JsonUtil.toInt(jv);
                    break;
                case "section":
                    songUpdate.section = jv.isString().stringValue();
                    break;
                case "sectionVersion":
                    songUpdate.sectionVersion = JsonUtil.toInt(jv);
                    break;
                case "chordSectionRow":
                    songUpdate.chordSectionRow = JsonUtil.toInt(jv);
                    break;
                case "chordSectionCurrentRepeat":
                    songUpdate.chordSectionCurrentRepeat = JsonUtil.toInt(jv);
                    break;
                case "chordSectionRepeat":
                    songUpdate.chordSectionRepeat = JsonUtil.toInt(jv);
                    break;
                case "measure":
                    songUpdate.measure = JsonUtil.toInt(jv);
                    break;
                case "beat":
                    songUpdate.beat = JsonUtil.toInt(jv);
                    break;
                case "beatsPerMeasure":
                    songUpdate.beatsPerMeasure = JsonUtil.toInt(jv);
                    break;
                case "beatsPerMinute":
                    songUpdate.beatsPerMinute = JsonUtil.toInt(jv);
                    break;
            }

        }
        return songUpdate;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n")
                .append("\"state\": \"")
                .append(getState().name())
                .append("\",\n")
                .append("\"eventTime\": ")
                .append(getEventTime())
                .append(",\n")
                .append("\"song\": ")
                .append(song.toJson())
                .append(",\n")
                .append("\"sectionCount\": ")
                .append(getSectionCount())
                .append(",\n")
                .append("\"section\": \"")
                .append(JsonUtil.encode(getSection()))
                .append("\",\n")
                .append("\"sectionVersion\": ")
                .append(getSectionVersion())
                .append(",\n")
                .append("\"chordSectionRow\": ")
                .append(getChordSectionRow())
                .append(",\n")
                .append("\"chordSectionCurrentRepeat\": ")
                .append(getChordSectionCurrentRepeat())
                .append(",\n")
                .append("\"chordSectionRepeat\": ")
                .append(getChordSectionRepeat())
                .append(",\n")
                .append("\"measure\": ")
                .append(getMeasure())
                .append(",\n")
                .append("\"beat\": ")
                .append(getBeat())
                .append(",\n")
                .append("\"beatsPerMeasure\": ")
                .append(getBeatsPerMeasure())
                .append(",\n")
                .append("\"beatsPerMinute\": ")
                .append(getBeatsPerMinute())
                .append("\n}\n");

        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = (83 * hash + state.hashCode()) % (1 << 31);
        hash = (83 * hash + (int) this.eventTime) % (1 << 31);
        hash = (83 * hash + Objects.hashCode(this.song)) % (1 << 31);
        hash = (83 * hash + this.sectionCount) % (1 << 31);
        hash = (83 * hash + Objects.hashCode(this.section)) % (1 << 31);
        hash = (83 * hash + this.sectionVersion) % (1 << 31);
        hash = (83 * hash + this.chordSectionRow) % (1 << 31);
        hash = (83 * hash + this.chordSectionCurrentRepeat) % (1 << 31);
        hash = (83 * hash + this.chordSectionRepeat) % (1 << 31);
        hash = (83 * hash + this.measure) % (1 << 31);
        hash = (83 * hash + this.beat) % (1 << 31);
        hash = (83 * hash + this.beatsPerMeasure) % (1 << 31);
        hash = (83 * hash + this.beatsPerMinute) % (1 << 31);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SongUpdate other = (SongUpdate) obj;
        if (song != null) {
            if (!this.song.equals(other.song)) {
                return false;
            }
        } else if (other.song != null)
            return false;
        if (!this.state.equals(other.state)) {
            return false;
        }
        if (this.eventTime != other.eventTime) {
            return false;
        }
        if (this.sectionCount != other.sectionCount) {
            return false;
        }
        if (this.sectionVersion != other.sectionVersion) {
            return false;
        }
        if (this.chordSectionRow != other.chordSectionRow) {
            return false;
        }
        if (this.chordSectionCurrentRepeat != other.chordSectionCurrentRepeat) {
            return false;
        }
        if (this.chordSectionRepeat != other.chordSectionRepeat) {
            return false;
        }
        if (this.measure != other.measure) {
            return false;
        }
        if (this.beat != other.beat) {
            return false;
        }
        if (this.beatsPerMeasure != other.beatsPerMeasure) {
            return false;
        }
        if (this.beatsPerMinute != other.beatsPerMinute) {
            return false;
        }
        if (!Objects.equals(this.section, other.section)) {
            return false;
        }
        return true;
    }

    private State state = State.idle;
    private long eventTime;
    private Song song;
    private int sectionCount;
    private String section;
    private int sectionVersion;
    private int chordSectionRow;
    private int chordSectionCurrentRepeat;
    private int chordSectionRepeat;
    private int measure;

    private int beat;
    private int beatsPerMeasure;
    private int beatsPerMinute;

}
