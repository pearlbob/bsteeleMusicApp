/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.client.songs.Section;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.shared.JsonUtil;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import jsinterop.annotations.JsType;

import java.util.ArrayList;
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


    public enum State {
        playing,
        idle;
    }

    public SongUpdate() {
        song = Song.createEmptySong();
    }

//    void copySongUpdate(SongUpdate other) {
//        this.state = other.state;
//        this.eventTime = other.eventTime;
//        this.title = other.title;
//        this.sectionCount = other.sectionCount;
//        this.sectionVersion = other.sectionVersion;
//        this.chordSectionRow = other.chordSectionRow;
//        this.repeatCurrent = other.repeatCurrent;
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
     * @param measure the measure to set
     */
    void setMeasure(int measure) {
        measureSet(measure);
    }

    /**
     * Move the update indicators to the given measure.
     *
     * @param m the measure to move to
     */
    private void measureSet(int m) {
        sectionNumber = 0;
        sectionVersion = Section.getDefaultVersion();
        measureContent = "";
        //  special for first measure
        if (m >= 0
                && song != null
                && song.getSectionSequence() != null
                && song.getSectionSequence().size() > 0) {
            ArrayList<Section.Version> songSectionSequence = song.getSectionSequence();
            sectionVersion = songSectionSequence.get(sectionNumber);
            Grid<String> chordSection = song.getChordSection(sectionVersion);
            if (chordSection != null && !chordSection.isEmpty()) {
                ArrayList<String> chordCols = chordSection.getRow(chordSectionRow);
                if (chordCols != null && chordCols.size() > 0)
                    measureContent = chordCols.get(0);
            }
        }
        sectionId = sectionVersion.toString();
        logger.fine("measureSet() from " + measure + " to " + m);
        chordSectionRow = 0;
        chordSectionColumn = 0;
        repeatCurrent = 0;
        repeatTotal = 0;
        repeatFirstRow = -1;
        repeatLastRow = -1;
        repeatLastCol = -1;
        measure = 0;

        beat = 0;

        //  fixme: performance improvements?
        if (m == 0) {
            measure = 0;  //    we're done from the above
        } else if (m > 0) {
            //  walk forward to correct measure
            while (measure < m)
                if (!nextMeasure())
                    break;
        } else {
            //  leave negative measures as they are
            measure = m;
        }
    }

    public boolean nextMeasure() {
        logger.fine("nextMeasure() from " + measure);
        if (measure < 0) {
            measure++;
            if (measure == 0)
                measureSet(0);
            return true;
        }

        ArrayList<Section.Version> songSectionSequence = song.getSectionSequence();
        if (sectionNumber >= songSectionSequence.size())
            return false;

        //  increment to next the next measure slot
        sectionVersion = songSectionSequence.get(sectionNumber);
        Grid<String> chordSection = song.getChordSection(sectionVersion);
        ArrayList<String> chordCols = chordSection.getRow(chordSectionRow);
        chordSectionColumn++;
        if (this.chordSectionColumn >= chordCols.size()) {
            //  go to the next row
            this.chordSectionColumn = 0;
            this.chordSectionRow++;

            //  repeat if required
            if (repeatTotal > 0
                    && chordSectionRow > repeatLastRow
                    && repeatCurrent < repeatTotal
                    ) {
                repeatCurrent++;
                if (repeatCurrent < repeatTotal)
                    chordSectionRow = repeatFirstRow;
                else {
                    repeatTotal = 0;  //  repeat done
                    repeatFirstRow = -1;
                    repeatLastCol = -1;
                }
            }
            //  go to the next sectionNumber
            if (chordSectionRow >= chordSection.getRowCount()) {
                repeatTotal = 0;
                chordSectionRow = 0;
                sectionNumber++;
            }
        }


        //  validate where we've landed after the increment
        if (sectionNumber >= songSectionSequence.size())
            return false;
        sectionVersion = songSectionSequence.get(sectionNumber);
        chordSection = song.getChordSection(sectionVersion);

        chordCols = chordSection.getRow(chordSectionRow);
        if (chordCols == null) {
            return false;
        }

        measureContent = chordCols.get(chordSectionColumn);
        if (measureContent == null) {
            return false;
        }


        //  look for the repeat extender
        //  note: doesn't have to be at the end of the row due to comments
        if (extenderRegExp.test(measureContent)) {
            //  not currently repeating
            //  mark the first vertical bar row
            if (this.repeatFirstRow < 0)
                this.repeatFirstRow = chordSectionRow;
            return this.nextMeasure();   //  go find the next real measure
        }

        //  look for repeat
        MatchResult mr = repeatRegExp.exec(measureContent);
        if (mr != null) {
            if (repeatTotal == 0) {
                //  not currently repeating
                repeatTotal = Integer.parseInt(mr.getGroup(1));
                repeatCurrent = 0;
                repeatFirstRow = (repeatFirstRow >= 0 ? repeatFirstRow : chordSectionRow);
                repeatLastRow = chordSectionRow;
                repeatLastCol = chordSectionColumn;
            }
            return this.nextMeasure();   //  go find the next real measure
        }

        sectionId = sectionVersion.toString();
        measure++;
        return true;
    }

    /**
     * return event time in seconds
     *
     * @return
     */
    public double getEventTime() {
        return eventTime;
    }

    public Song getSong() {
        return song;
    }

    public int getSectionCount() {
        return sectionCount;
    }

    public int getSectionNumber() {
        return sectionNumber;
    }

    /**
     * Id of the current sectionNumber to be used by the chords display.
     *
     * @return
     */
    public Section.Version getSectionVersion() {
        return sectionVersion;
    }

    /**
     * Current row number in the sectionNumber
     *
     * @return
     */
    public int getChordSectionRow() {
        return chordSectionRow;
    }

    public int getChordSectionColumn() {
        return chordSectionColumn;
    }

    /**
     * Current repeat count if the row(s) repeat
     *
     * @return
     */
    public int getRepeatCurrent() {
        return repeatCurrent;
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
    void setEventTime(double eventTime) {
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
     * @param repeatCurrent the repeatCurrent to set
     */
    void setRepeatCurrent(int repeatCurrent) {
        this.repeatCurrent = repeatCurrent;
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

    public int getRepeatTotal() {
        return repeatTotal;
    }

    public int getRepeatFirstRow() {
        return repeatFirstRow;
    }

    public int getRepeatLastRow() {
        return repeatLastRow;
    }

    public int getRepeatLastCol() {
        return repeatLastCol;
    }


    public String diff(SongUpdate other) {
        if (other == null || other.song == null)
            return "no old song";
        if (!song.equals(other.song))
            return "new song: " + other.song.getTitle() + ", " + other.song.getArtist();
        if (sectionId != null && other.sectionId != null && !sectionId.equals(other.sectionId))
            return "new sectionNumber: " + other.getSectionNumber();
        if (measure != other.measure)
            return "new measure: " + other.measure;
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
        sb.append(getMeasure());
        sb.append(" ").append(getSectionNumber());
        sb.append(" ").append(sectionNumber);
        sb.append(" (").append(chordSectionRow).append(",").append(chordSectionColumn).append(")");
        return sb.toString();
    }

    public static final SongUpdate fromJson(String jsonString) {
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
                    songUpdate.setEventTime(JsonUtil.toDouble(jv));
                    break;
                case "song":
                    songUpdate.setSong(Song.fromJsonObject(jv.isObject()));
                    break;
                //  sectionNumber sequencing details should be found by local processing
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
        songUpdate.measureSet(songUpdate.measure);
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
                .append(",\n");
        if (song != null)
            sb.append("\"song\": ")
                    .append(song.toJson())
                    .append(",\n");
        //  sectionNumber sequencing details should be found by local processing
        sb.append("\"measure\": ")
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
        hash = (83 * hash + Objects.hashCode(this.sectionId)) % (1 << 31);
        hash = (83 * hash + Objects.hashCode(this.sectionVersion)) % (1 << 31);
        hash = (83 * hash + this.chordSectionRow) % (1 << 31);
        hash = (83 * hash + this.chordSectionColumn) % (1 << 31);
        hash = (83 * hash + this.repeatCurrent) % (1 << 31);
        hash = (83 * hash + this.repeatTotal) % (1 << 31);
        hash = (83 * hash + this.repeatFirstRow) % (1 << 31);
        hash = (83 * hash + this.repeatLastRow) % (1 << 31);
        hash = (83 * hash + this.repeatLastCol) % (1 << 31);
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
        if (this.chordSectionColumn != other.chordSectionColumn) {
            return false;
        }
        if (this.repeatCurrent != other.repeatCurrent) {
            return false;
        }
        if (this.repeatTotal != other.repeatTotal) {
            return false;
        }
        if (this.repeatFirstRow != other.repeatFirstRow) {
            return false;
        }
        if (this.repeatLastRow != other.repeatLastRow) {
            return false;
        }
        if (this.repeatLastCol != other.repeatLastCol) {
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
        if (this.sectionNumber != other.sectionNumber) {
            return false;
        }
        return true;
    }

    private State state = State.idle;
    private double eventTime;
    private Song song;
    private int sectionNumber;
    private int sectionCount;
    private String sectionId;
    private Section.Version sectionVersion;
    private int chordSectionRow;
    private int chordSectionColumn;
    private int repeatCurrent;
    private int repeatTotal;
    private int repeatFirstRow = -1;
    private int repeatLastRow;
    private int repeatLastCol;

    private static final RegExp extenderRegExp = RegExp.compile("^\\s*\\|");
    private static final RegExp repeatRegExp = RegExp.compile("x *(?:\\d+\\/)?(\\d+)", "i");


    private int measure;
    private String measureContent;

    private int beat;
    private int beatsPerMeasure;
    private int beatsPerMinute;
    private static final Logger logger = Logger.getLogger(SongUpdate.class.getName());
}
