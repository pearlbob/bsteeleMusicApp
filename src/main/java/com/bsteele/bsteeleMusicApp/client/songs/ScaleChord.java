package com.bsteele.bsteeleMusicApp.client.songs;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Objects;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

/**
 * A chord with a scale note and an optional chord descriptor and tension.
 */
public class ScaleChord {
    public ScaleChord(@NotNull ScaleNote scaleNote,
                      @NotNull ChordDescriptor chordDescriptor,
                      @NotNull ChordTension chordTension) {
        this.scaleNote = scaleNote;
        this.chordDescriptor = chordDescriptor;
        this.chordTension = chordTension;
        length = toString().length();
    }

    public ScaleChord(@NotNull ScaleNote scaleNote) {
        this(scaleNote, ChordDescriptor.major, ChordTension.none);
    }

    public ScaleChord(@NotNull ScaleNote scaleNote, @NotNull ChordDescriptor chordDescriptor) {
        this(scaleNote, chordDescriptor, ChordTension.none);
    }

    public static ScaleChord parse(String s) {
        if (s == null || s.length() < 1)
            return null;

        ScaleNote retScaleNote = ScaleNote.parse(s);
        if (retScaleNote == null)
            return null;
        s = s.substring(retScaleNote.toString().length());

        ChordDescriptor retChordDescriptor = ChordDescriptor.parse(s);
        s = s.substring(retChordDescriptor.getShortName().length());

        ChordTension retChordTension = ChordTension.parse(s);
        return new ScaleChord(retScaleNote, retChordDescriptor, retChordTension);
    }

    public ScaleNote getScaleNote() {
        return scaleNote;
    }

    public ScaleChord getAlias() {
        ScaleNote alias = scaleNote.getAlias();
        if ( alias == null )
            return null;
        return new ScaleChord(alias, chordDescriptor, chordTension);
    }

    public ChordDescriptor getChordDescriptor() {
        return chordDescriptor;
    }

    public ChordTension getChordTension() {
        return chordTension;
    }

    public static final String getRegExp() {
        return regExp;
    }

    public int getLength() {
        return length;
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
                && chordDescriptor.equals(other.chordDescriptor)
                && chordTension.equals(other.chordTension);
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
        hash = (71 * hash + Objects.hashCode(this.chordTension)) % (1 << 31);
        return hash;
    }

    @Override
    public String toString() {
        return scaleNote.toString()
                + (chordDescriptor != null ? chordDescriptor.getShortName() : "")
                + (chordTension != null ? chordTension.getShortName() : "");
    }

    private ScaleNote scaleNote;
    private ChordDescriptor chordDescriptor;
    private ChordTension chordTension;
    private int length = 0;
    private static final String regExp;

    static {
        //  build the regexpression to find this class while parsing
        regExp = ScaleNote.getRegExp() + ChordDescriptor.getRegExp() + ChordTension.getRegExp();
    }

}
