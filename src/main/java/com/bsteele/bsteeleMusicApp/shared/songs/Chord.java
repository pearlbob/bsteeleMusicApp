package com.bsteele.bsteeleMusicApp.shared.songs;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class Chord implements Comparable<Chord> {

    public Chord(@NotNull ScaleChord scaleChord, int beats, int beatsPerBar,
                 ScaleChord slashScaleChord, ChordAnticipationOrDelay anticipationOrDelay, boolean implicitBeats) {
        this.scaleChord = scaleChord;
        this.beats = beats;
        this.beatsPerBar = beatsPerBar;
        this.slashScaleChord = slashScaleChord;
        this.anticipationOrDelay = anticipationOrDelay;
        this.implicitBeats = implicitBeats;
    }

    public Chord(@NotNull Chord chord) {
        scaleChord = chord.scaleChord;
        beats = chord.beatsPerBar;
        beatsPerBar = chord.beatsPerBar;
        slashScaleChord = chord.slashScaleChord;
        anticipationOrDelay = chord.anticipationOrDelay;
        implicitBeats = chord.implicitBeats;
    }

    static final Chord parse(String s, int beatsPerBar) {
        return parse(new StringBuffer(s), beatsPerBar);
    }

    public static final Chord parse(StringBuffer sb, int beatsPerBar) {
        if (sb == null || sb.length() <= 0)
            return null;

        int beats = beatsPerBar;  //  default only
        ScaleChord scaleChord = ScaleChord.parse(sb);
        if (scaleChord == null)
            return null;

        ChordAnticipationOrDelay anticipationOrDelay = ChordAnticipationOrDelay.parse(sb);


        ScaleChord slashScaleChord = null;
        if (sb.length() > 0 && sb.charAt(0) == '/') {
            sb.delete(0, 1);
            slashScaleChord = ScaleChord.parse(sb);
            //  force the slash chord to be major
            if (slashScaleChord != null && slashScaleChord.getChordDescriptor() != ChordDescriptor.major) {
                slashScaleChord = new ScaleChord(slashScaleChord.getScaleNote(), ChordDescriptor.major);
            }
        }
        if (sb.length() > 0 && sb.charAt(0) == '.') {
            beats = 1;
            while (sb.length() > 0 && sb.charAt(0) == '.') {
                sb.delete(0, 1);
                beats++;
                if (beats >= 12)
                    break;
            }
        }

        if (beats > beatsPerBar)
            //  whoops, too many beats
            ;    //  fixme: notify user

        Chord ret = new Chord(scaleChord, beats, beatsPerBar, slashScaleChord, anticipationOrDelay
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
                slashScaleChord == null ? null : slashScaleChord.transpose(key, halfSteps), anticipationOrDelay, implicitBeats);
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
    public final ScaleChord getSlashScaleChord() {
        return slashScaleChord;
    }

    /**
     * The matching slash chord for this chord.
     * Typically is is the bass inversion.
     *
     * @param slashScaleChord the slash chord to set
     */
    final void setSlashScaleChord(ScaleChord slashScaleChord) {
        this.slashScaleChord = slashScaleChord;
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
        if (slashScaleChord == null && o.slashScaleChord != null)
            return -1;
        if (slashScaleChord != null && o.slashScaleChord == null)
            return 1;
        if (slashScaleChord != null && o.slashScaleChord != null) {
            ret = slashScaleChord.compareTo(o.slashScaleChord);
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
                + (slashScaleChord == null ? "" : "/" + slashScaleChord.toString())
                + anticipationOrDelay.toString();
        if (!implicitBeats && beats < beatsPerBar) {
            int b = 1;
            while (b++ < beats && b < 8)
                ret += ".";
        }
        return ret;
    }

    public String toStringWithoutInversion() {
        String ret = scaleChord.toString()
                //+ (slashScaleChord == null ? "" : "/" + slashScaleChord.toString())
                + anticipationOrDelay.toString();
        if (!implicitBeats && beats < beatsPerBar) {
            int b = 1;
            while (b++ < beats && b < 8)
                ret += ".";
        }
        return ret;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Chord))
            return false;
        Chord oc = (Chord) o;

        if (slashScaleChord == null) {
            if (oc.slashScaleChord != null) return false;
        } else if (!slashScaleChord.equals(oc.slashScaleChord))
            return false;
        return scaleChord.equals(oc.scaleChord)
                && anticipationOrDelay.equals(oc.anticipationOrDelay)
                && beats == oc.beats
                && beatsPerBar == oc.beatsPerBar
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scaleChord, beats, beatsPerBar, slashScaleChord, anticipationOrDelay);
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
    private ScaleChord slashScaleChord;
    private ChordAnticipationOrDelay anticipationOrDelay;

}
