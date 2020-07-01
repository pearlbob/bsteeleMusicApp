/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.util.JsonUtil;
import com.bsteele.bsteeleMusicApp.shared.songs.Key;
import com.bsteele.bsteeleMusicApp.shared.songs.SongMoment;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import jsinterop.annotations.JsType;

import java.text.ParseException;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Immutable song update data
 * <p>
 * fixme: song update should always have a song
 *
 * @author bob
 */
@JsType
public class SongUpdate {

    public SongMoment getSongMoment() {
        return songMoment;
    }

    public enum State {
        playing,
        idle;
    }

    public SongUpdate() {
        assignSong(Song.createEmptySong());
    }

    public static final SongUpdate createSongUpdate(Song song) {
        SongUpdate ret = new SongUpdate();
        ret.assignSong(song);
        return ret;
    }

    private void assignSong(Song song) {
        this.song = song;
        currentBeatsPerMinute = song.getBeatsPerMinute();
        currentKey = song.getKey();
    }

    public final State getState() {
        return state;
    }


    /**
     * Move the update indicators to the given measureNumber.
     * Should only be used to reposition the moment number.
     *
     * @param m the measureNumber to move to
     */
    public final void setMomentNumber(int m) {
        if (m == momentNumber)
            return;

        beat = 0;

        //  leave negative moment numbers as they are
        if (m < 0) {
            momentNumber = m;
            songMoment = null;
            return;
        }

        //  deal with empty songs
        if (song == null) {
            momentNumber = 0;
            songMoment = null;
            return;
        }
        if (song.getSongMomentsSize() == 0) {
            momentNumber = 0;
            songMoment = null;
            return;
        }

        //  past the end and we're done
        if (m >= song.getSongMomentsSize()) {
            momentNumber = Integer.MAX_VALUE;
            songMoment = null;
            return;
        }

        momentNumber = m;
        songMoment = song.getSongMoment(momentNumber);
    }

    public final Song getSong() {
        return song;
    }

    /**
     * Moment index from start of song starts at zero.
     * The moment number will be negative in play prior to the song start.
     *
     * @return the index of the current moment number in time
     */
    public final int getMomentNumber() {
        return momentNumber;
    }

    /**
     * Return the typical, default duration for the default beats per bar and the beats per minute.
     * Due to variation in measureNumber beats, this should not be used anywhere but pre-roll!
     *
     * @return the typical, default duration
     */
    public final double getDefaultMeasureDuration() {
        if (song == null)
            return 2;
        return song.getBeatsPerBar() * 60.0 / (currentBeatsPerMinute == 0 ? 30 : currentBeatsPerMinute);
    }

    /**
     * Beat number from start of the current measureNumber. Starts at zero and goes to
     * beatsPerBar - 1
     *
     * @return the current beat
     */
    public final int getBeat() {
        return beat;
    }

    public final double getBeatDuration() {
        return 60.0 / song.getDefaultBpm();
    }

    /**
     * @return the beatsPerMeasure
     */
    public final int getBeatsPerMeasure() {
        return beatsPerMeasure;
    }

    /**
     * @return the currentBeatsPerMinute
     */
    public final int getCurrentBeatsPerMinute() {
        return currentBeatsPerMinute > 0 ? currentBeatsPerMinute : song.getBeatsPerMinute();
    }


    public final void setState(State state) {
        this.state = state;
    }

    /**
     * @param song the song to set
     */
    public final void setSong(Song song) {
        this.song = song;
    }

    /**
     * @param beat the beat to set
     */
    final void setBeat(int beat) {
        this.beat = beat;
    }

    /**
     * @param beatsPerMeasure the beatsPerMeasure to set
     */
    public final void setBeatsPerBar(int beatsPerMeasure) {
        this.beatsPerMeasure = beatsPerMeasure;
    }

    /**
     * @param currentBeatsPerMinute the currentBeatsPerMinute to set
     */
    public final void setCurrentBeatsPerMinute(int currentBeatsPerMinute) {
        this.currentBeatsPerMinute = currentBeatsPerMinute;
    }


    public final Key getCurrentKey() {
        return currentKey != null ? currentKey : song.getKey();
    }

    public final void setCurrentKey(Key currentKey) {
        this.currentKey = currentKey;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


    public final String diff(SongUpdate other) {
        if (other == null || other.song == null)
            return "no old song";
        if (!song.equals(other.song))
            return "new song: " + other.song.getTitle() + ", " + other.song.getArtist();
        if (!currentKey.equals(other.currentKey))
            return "new key: " + other.currentKey;
        if (!currentKey.equals(other.currentKey))
            return "new key: " + other.currentKey;
        if (currentBeatsPerMinute != other.currentBeatsPerMinute)
            return "new tempo: " + other.currentBeatsPerMinute;
        return "no change";
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
        StringBuilder sb = new StringBuilder("SongUpdate: ");
        sb.append(getMomentNumber());
        if (songMoment != null) {
            sb.append(" ").append(songMoment.getMomentNumber()).append(" ").append(songMoment.getBeatNumber())
                    .append(" ").append(songMoment.getMeasure().toString());
            if (songMoment.getRepeatMax() > 0)
                sb.append(" ").append((songMoment.getRepeat() + 1)).append("/").append(songMoment.getRepeatMax());
        }
        return sb.toString();
    }

    public static final SongUpdate fromJson(String jsonString) throws ParseException {
        logger.fine(jsonString);
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

    public static final SongUpdate fromJsonObject(JSONObject jo) throws ParseException {
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
                case "currentKey":
                    songUpdate.setCurrentKey(Key.parse(jv.isString().stringValue()));
                    break;
                case "song":
                    songUpdate.setSong(Song.fromJsonObject(jv.isObject()));
                    break;
                //  momentNumber sequencing details should be found by local processing
                case "momentNumber":
                    songUpdate.momentNumber = JsonUtil.toInt(jv);
                    break;
                case "beat":
                    songUpdate.beat = JsonUtil.toInt(jv);
                    break;
                case "beatsPerMeasure":
                    songUpdate.beatsPerMeasure = JsonUtil.toInt(jv);
                    break;
                case "currentBeatsPerMinute":
                    songUpdate.currentBeatsPerMinute = JsonUtil.toInt(jv);
                    break;
                case "user":
                    songUpdate.setUser(jv.toString());
                    break;
            }
        }
        songUpdate.setMomentNumber(songUpdate.momentNumber);
        return songUpdate;
    }

    public final String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n")
                .append("\"state\": \"")
                .append(getState().name())
                .append("\",\n")
                .append("\"currentKey\": \"")
                .append(getCurrentKey().name())
                .append("\",\n");
        if (song != null)
            sb.append("\"song\": ")
                    .append(song.toJson())
                    .append(",\n");
        //  momentNumber sequencing details should be found by local processing
        sb.append("\"momentNumber\": ")
                .append(getMomentNumber())
                .append(",\n")
                .append("\"beat\": ")
                .append(getBeat())
                .append(",\n")
                .append("\"user\": \"")
                .append(getUser())
                .append("\",\n")
                .append("\"beatsPerMeasure\": ")
                .append(getBeatsPerMeasure())
                .append(",\n")
                .append("\"currentBeatsPerMinute\": ")
                .append(getCurrentBeatsPerMinute())
                .append("\n}\n");

        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = (83 * hash + state.hashCode()) % (1 << 31);
        hash = (83 * hash + Objects.hashCode(currentKey)) % (1 << 31);
        hash = (83 * hash + Objects.hashCode(this.song)) % (1 << 31);
        hash = (83 * hash + Objects.hashCode(this.songMoment)) % (1 << 31);
        hash = (83 * hash + this.beat) % (1 << 31);
        hash = (83 * hash + this.beatsPerMeasure) % (1 << 31);
        hash = (83 * hash + this.currentBeatsPerMinute) % (1 << 31);
        hash = (83 * hash + Objects.hashCode(this.user)) % (1 << 31);
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
        if (!this.currentKey.equals(other.currentKey)) {
            return false;
        }
        if (this.beat != other.beat) {
            return false;
        }
        if (this.beatsPerMeasure != other.beatsPerMeasure) {
            return false;
        }
        if (this.currentBeatsPerMinute != other.currentBeatsPerMinute) {
            return false;
        }
        if (this.momentNumber != other.momentNumber) {
            return false;
        }
        if (!this.user.equals(other.user)) {
            return false;
        }
        return true;
    }

    private State state = State.idle;
    private Song song;
    private String user = "no one";
    private int momentNumber;
    private SongMoment songMoment;

    //  play values
    private int beat;
    private int beatsPerMeasure;
    private int currentBeatsPerMinute;
    private Key currentKey;

    private static final Logger logger = Logger.getLogger(SongUpdate.class.getName());
}
