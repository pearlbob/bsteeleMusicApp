package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.util.MarkedString;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class Chord implements Comparable<Chord> {

    public Chord(@NotNull ScaleChord scaleChord, int beats, int beatsPerBar,
                 ScaleNote slashScaleNote, ChordAnticipationOrDelay anticipationOrDelay, boolean implicitBeats) {
        this.scaleChord = scaleChord;
        this.beats = beats;
        this.beatsPerBar = beatsPerBar;
        this.slashScaleNote = slashScaleNote;
        this.anticipationOrDelay = anticipationOrDelay;
        this.implicitBeats = implicitBeats;
    }

    public Chord(@NotNull Chord chord) {
        scaleChord = chord.scaleChord;
        beats = chord.beats;
        beatsPerBar = chord.beatsPerBar;
        slashScaleNote = chord.slashScaleNote;
        anticipationOrDelay = chord.anticipationOrDelay;
        implicitBeats = chord.implicitBeats;
    }

    static final Chord parse(String s, int beatsPerBar)
            throws ParseException {
        return parse(new MarkedString(s), beatsPerBar);
    }

    static final Chord parse(final MarkedString markedString, int beatsPerBar) throws ParseException {
        if (markedString == null || markedString.isEmpty())
            throw new ParseException("no data to parse", 0);

        int beats = beatsPerBar;  //  default only
        ScaleChord scaleChord = ScaleChord.parse(markedString);
        if (scaleChord == null)
            return null;

        ChordAnticipationOrDelay anticipationOrDelay = ChordAnticipationOrDelay.parse(markedString);

        ScaleNote slashScaleNote = null;
        //  note: X chords can have a slash chord
        if (!markedString.isEmpty() && markedString.charAt(0) == '/') {
            markedString.consume(1);
            slashScaleNote = ScaleNote.parse(markedString);
        }
        if (!markedString.isEmpty() && markedString.charAt(0) == '.') {
            beats = 1;
            final RegExp fontSizeRegexp = RegExp.compile("^\\.([\\d]+)");
            MatchResult mr = fontSizeRegexp.exec(markedString.remainingStringLimited(3));
            if (mr != null) {
                logger.finest(mr.getGroup(1));
                beats = Integer.parseInt(mr.getGroup(1));
                markedString.consume(mr.getGroup(0).length());
            } else {
                while (!markedString.isEmpty() && markedString.charAt(0) == '.') {
                    markedString.consume(1);
                    beats++;
                    if (beats >= 12)
                        break;
                }
            }
        }

        if (beats > beatsPerBar)
            throw new ParseException("too many beats in the chord", 0); //  whoops

        Chord ret = new Chord(scaleChord, beats, beatsPerBar, slashScaleNote, anticipationOrDelay
                , (beats == beatsPerBar));      //  fixme
        return ret;
    }

    public Chord(ScaleChord scaleChord) {
        this(scaleChord, 4, 4, null, ChordAnticipationOrDelay.none, true);
    }

    public Chord(ScaleChord scaleChord, int beats, int beatsPerBar) {
        this(scaleChord, beats, beatsPerBar, null, ChordAnticipationOrDelay.none, true);
    }

    public Chord transpose(Key key, int halfSteps) {
        return new Chord(scaleChord.transpose(key, halfSteps), beats, beatsPerBar,
                slashScaleNote == null ? null : slashScaleNote.transpose(key, halfSteps), anticipationOrDelay, implicitBeats);
    }

    /**
     * The description of the chord to be played.
     *
     * @return the scale chord
     */
    public ScaleChord getScaleChord() {
        return scaleChord;
    }

//    /**
//     * The description of the chord to be played.
//     *
//     * @param scaleChord the scale chord
//     */
//    public void setScaleChord(ScaleChord scaleChord) {
//        this.scaleChord = scaleChord;
//    }

    /**
     * Duration of the chord in beats
     *
     * @return the beat count
     */
    public final int getBeats() {
        return beats;
    }

    /**
     * Duration of the chord in beats
     *
     * @param beats the beat count
     */
    public final void setBeats(int beats) {
        this.beats = beats;
    }

    /**
     * The matching slash chord for this chord.
     * Typically is is the bass inversion.
     *
     * @return the matching slash chord if one exists
     */
    public final ScaleNote getSlashScaleNote() {
        return slashScaleNote;
    }

    /**
     * The matching slash chord for this chord.
     * Typically is is the bass inversion.
     *
     * @param slashScaleNote the slash chord to set
     */
    final void setSlashScaleNote(ScaleNote slashScaleNote) {
        this.slashScaleNote = slashScaleNote;
    }

    /**
     * Small timing adjustment to alter the feel of the chord.
     *
     * @return the timing adjustment
     */
    public final ChordAnticipationOrDelay getAnticipationOrDelay() {
        return anticipationOrDelay;
    }

    /**
     * Small timing adjustment to alter the feel of the chord
     *
     * @param anticipationOrDelay the timing adjustment
     */
    public final void setAnticipationOrDelay(ChordAnticipationOrDelay anticipationOrDelay) {
        this.anticipationOrDelay = anticipationOrDelay;
    }


    public boolean isImplicitBeats() {
        return implicitBeats;
    }

    public void setImplicitBeats(boolean implicitBeats) {
        this.implicitBeats = implicitBeats;
    }


    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Chord o) {
        int ret = scaleChord.compareTo(o.scaleChord);
        if (ret != 0)
            return ret;
        if (slashScaleNote == null && o.slashScaleNote != null)
            return -1;
        if (slashScaleNote != null && o.slashScaleNote == null)
            return 1;
        if (slashScaleNote != null && o.slashScaleNote != null) {
            ret = slashScaleNote.compareTo(o.slashScaleNote);
            if (ret != 0)
                return ret;
        }
        if (beats != o.beats)
            return beats < o.beats ? -1 : 1;
        ret = anticipationOrDelay.compareTo(o.anticipationOrDelay);
        if (ret != 0)
            return ret;
        if (beatsPerBar != o.beatsPerBar)
            return beatsPerBar < o.beatsPerBar ? -1 : 1;
        return 0;
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
        String ret = scaleChord.toString()
                + (slashScaleNote == null ? "" : "/" + slashScaleNote.toString())
                + anticipationOrDelay.toString();
        if (!implicitBeats && beats < beatsPerBar) {
            if (beats == 1) {
                ret += ".1";
            } else {
                int b = 1;
                while (b++ < beats && b < 12)
                    ret += ".";
            }
        }
        return ret;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Chord))
            return false;
        Chord oc = (Chord) o;

        if (slashScaleNote == null) {
            if (oc.slashScaleNote != null) return false;
        } else if (!slashScaleNote.equals(oc.slashScaleNote))
            return false;
        return scaleChord.equals(oc.scaleChord)
                && anticipationOrDelay.equals(oc.anticipationOrDelay)
                && beats == oc.beats
                && beatsPerBar == oc.beatsPerBar
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scaleChord, beats, beatsPerBar, slashScaleNote, anticipationOrDelay);
    }

    public final int getBeatsPerBar() {
        return beatsPerBar;
    }

    public final void setBeatsPerBar(int beatsPerBar) {
        this.beatsPerBar = beatsPerBar;
    }

    private ScaleChord scaleChord;
    private int beats;
    private int beatsPerBar;
    private boolean implicitBeats = true;
    private ScaleNote slashScaleNote;
    private ChordAnticipationOrDelay anticipationOrDelay;

    private static final Logger logger = Logger.getLogger(Chord.class.getName());
}
