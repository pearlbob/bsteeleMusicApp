package com.bsteele.bsteeleMusicApp.client.songs;

import com.google.gwt.core.client.GWT;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeSet;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

/**
 * A chord with a scale note and an optional chord descriptor and tension.
 */
public class ScaleChord implements Comparable<ScaleChord> {
    public ScaleChord(@NotNull ScaleNote scaleNote,
                      @NotNull ChordDescriptor chordDescriptor ) {
        this.scaleNote = scaleNote;
        this.chordDescriptor = chordDescriptor;
        length = toString().length();
    }

    public ScaleChord(@NotNull ScaleNote scaleNote) {
        this(scaleNote, ChordDescriptor.major);
    }

    public static ScaleChord parse(String s) {
        if (s == null || s.length() < 1)
            return null;

        ScaleNote retScaleNote = ScaleNote.parse(s);
        if (retScaleNote == null)
            return null;
        s = s.substring(retScaleNote.toString().length());

        ChordDescriptor retChordDescriptor = ChordDescriptor.parse(s);
        //s = s.substring(retChordDescriptor.getShortName().length());

        return new ScaleChord(retScaleNote, retChordDescriptor);
    }

    public ScaleNote getScaleNote() {
        return scaleNote;
    }

    public ScaleChord getAlias() {
        ScaleNote alias = scaleNote.getAlias();
        if (alias == null)
            return null;
        return new ScaleChord(alias, chordDescriptor);
    }

    public ChordDescriptor getChordDescriptor() {
        return chordDescriptor;
    }

    public TreeSet<ChordComponent> getChordComponents() {
        return chordDescriptor.getChordComponents();
    }

    public int getLength() {
        return length;
    }


    public static final HashMap<ScaleChord, Integer> findScaleChordsUsed(String s) {
        HashMap<ScaleChord, Integer> scaleChordMap = new HashMap<>();

        int pos = 0;
        while (pos < s.length()) {
            ScaleChord scaleChord = ScaleChord.parse(s.substring(pos));
            if (scaleChord != null) {
                //GWT.log("parse: " + scaleChord.toString());
                if (!scaleChordMap.containsKey(scaleChord))
                    scaleChordMap.put(scaleChord, 1);
                else
                    scaleChordMap.put(scaleChord, scaleChordMap.get(scaleChord) + 1);
                pos += scaleChord.getLength();
            } else
                pos++;
        }
        return scaleChordMap;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ScaleChord))
            return false;
        ScaleChord other = (ScaleChord) obj;
        return scaleNote.equals(other.scaleNote)
                && chordDescriptor.equals(other.chordDescriptor) ;
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link HashMap}.
     */
    @Override
    public int hashCode() {
        int hash = 17;
        hash = (71 * hash + Objects.hashCode(this.scaleNote)) % (1 << 31);
        hash = (71 * hash + Objects.hashCode(this.chordDescriptor)) % (1 << 31);
        return hash;
    }

    @Override
    public String toString() {
        return scaleNote.toString()
                + (chordDescriptor != null ? chordDescriptor.getShortName() : "") ;
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
    public int compareTo(ScaleChord o) {
        int ret = scaleNote.compareTo(o.scaleNote);
        if (ret != 0)
            return ret;
        ret = chordDescriptor.compareTo(o.chordDescriptor);
        if (ret != 0)
            return ret;
        return 0;
    }

    private ScaleNote scaleNote;
    private ChordDescriptor chordDescriptor;
    private int length = 0;
}
