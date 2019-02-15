package com.bsteele.bsteeleMusicApp.shared.songs;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class Chord implements Comparable<Chord> {

    public Chord(@NotNull ScaleChord scaleChord, int beats, int beatsPerBar,
                 ScaleChord slashScaleChord, ChordAnticipationOrDelay anticipationOrDelay) {
        this.scaleChord = scaleChord;
        this.beats = beats;
        this.beatsPerBar = beatsPerBar;
        this.slashScaleChord = slashScaleChord;
        this.anticipationOrDelay = anticipationOrDelay;
    }

    public Chord(@NotNull Chord chord) {
        this.scaleChord = chord.scaleChord;
        this.beats = chord.beatsPerBar;
        this.beatsPerBar = chord.beatsPerBar;
        this.slashScaleChord = chord.slashScaleChord;
        this.anticipationOrDelay = chord.anticipationOrDelay;
    }

    static final Chord testParse(String s, int beatsPerBar) {
        return parse(new StringBuffer(s), beatsPerBar);
    }

    public static final Chord parse(StringBuffer sb, int beatsPerBar) {
        if (sb == null || sb.length() <= 0)
            return null;

        int beats = beatsPerBar;  //  default only
        ScaleChord scaleChord = ScaleChord.parse(sb);
        if (scaleChord == null)
            return null;

        ScaleChord slashScaleChord = null;
        if (sb.length() > 0 && sb.charAt(0) == '/') {
            sb.delete(0, 1);
            slashScaleChord = ScaleChord.parse(sb);
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

        Chord ret = new Chord(scaleChord, beats, beatsPerBar, slashScaleChord,
                ChordAnticipationOrDelay.none);      //  fixme
        return ret;
    }

    public Chord(ScaleChord scaleChord) {
        this(scaleChord, 4, 4, null, ChordAnticipationOrDelay.none);
    }

    public Chord(ScaleChord scaleChord, int beats, int beatsPerBar) {
        this(scaleChord, beats, beatsPerBar, null, ChordAnticipationOrDelay.none);
    }

    public Chord transpose(Key key, int halfSteps) {
        return new Chord(scaleChord.transpose(key, halfSteps), beats, beatsPerBar,
                slashScaleChord == null ? null : slashScaleChord.transpose(key, halfSteps), anticipationOrDelay);
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
     * @return
     */
    public final ScaleChord getSlashScaleChord() {
        return slashScaleChord;
    }

    /**
     * The matching slash chord for this chord.
     * Typically is is the bass inversion.
     *
     * @param slashScaleChord
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


    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     *
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
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
        if (beats < beatsPerBar) {
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
        if (beats < beatsPerBar) {
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
    private int beats = 4;    //  default only, a typical full measure
    private int beatsPerBar = beats;
    private ScaleChord slashScaleChord;
    private ChordAnticipationOrDelay anticipationOrDelay = ChordAnticipationOrDelay.none;
}