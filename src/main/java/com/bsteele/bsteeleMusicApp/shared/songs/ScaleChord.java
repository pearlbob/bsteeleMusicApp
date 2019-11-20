package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.util.MarkedString;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

/**
 * A chord with a scale note and an optional chord descriptor and tension.
 */
public class ScaleChord implements Comparable<ScaleChord> {
    public ScaleChord(@NotNull ScaleNote scaleNote,
                      @NotNull ChordDescriptor chordDescriptor) {
        this.scaleNote = scaleNote;
        this.chordDescriptor = chordDescriptor.deAlias();
    }

    private ScaleChord(@NotNull ScaleNote scaleNote,
                       @NotNull ChordDescriptor chordDescriptor,
                       int parseLength) {
        this.scaleNote = scaleNote;
        this.chordDescriptor = chordDescriptor.deAlias();;
    }

    public ScaleChord(@NotNull ScaleNote scaleNote) {
        this(scaleNote, ChordDescriptor.major);
    }

    static final ScaleChord parse(String s)
            throws ParseException {
        return parse(new MarkedString(s));
    }

    static final ScaleChord parse(MarkedString markedString) throws ParseException {
        if (markedString == null || markedString.isEmpty())
            throw new ParseException("no data to parse", 0);

        ScaleNote retScaleNote = ScaleNote.parse(markedString);
        switch (retScaleNote) {
            case X:
                return new ScaleChord(retScaleNote, ChordDescriptor.major); //  by convention only
            default:
                ChordDescriptor retChordDescriptor = ChordDescriptor.parse(markedString);
                return new ScaleChord(retScaleNote, retChordDescriptor);
        }
    }

    public final ScaleChord transpose(Key key, int halfSteps) {
        return new ScaleChord(scaleNote.transpose(key, halfSteps), chordDescriptor);
    }

    public final ScaleNote getScaleNote() {
        return scaleNote;
    }

    public final ScaleChord getAlias() {
        ScaleNote alias = scaleNote.getAlias();
        if (alias == null)
            return null;
        return new ScaleChord(alias, chordDescriptor);
    }

    public final ChordDescriptor getChordDescriptor() {
        return chordDescriptor;
    }

    public final TreeSet<ChordComponent> getChordComponents() {
        return chordDescriptor.getChordComponents();
    }

    public final boolean contains(ChordComponent chordComponent) {
        return chordDescriptor.getChordComponents().contains(chordComponent);
    }

    public final boolean isEasyGuitarChord() {
        return easyGuitarChords.contains(this);
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
                && chordDescriptor.equals(other.chordDescriptor);
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
                + (chordDescriptor != null ? chordDescriptor.getShortName() : "");
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
    public int compareTo(ScaleChord o) {
        int ret = scaleNote.compareTo(o.scaleNote);
        if (ret != 0)
            return ret;
        ret = chordDescriptor.compareTo(o.chordDescriptor);
        if (ret != 0)
            return ret;
        return 0;
    }

    private static final TreeSet<ScaleChord> easyGuitarChords = new TreeSet<ScaleChord>();


    private ScaleNote scaleNote;
    private ChordDescriptor chordDescriptor;
    private static Logger logger = Logger.getLogger(ScaleChord.class.getName());

    static {
        try {
            //C A G E D and Am Em Dm
            easyGuitarChords.add(ScaleChord.parse("C"));
            easyGuitarChords.add(ScaleChord.parse("A"));
            easyGuitarChords.add(ScaleChord.parse("G"));
            easyGuitarChords.add(ScaleChord.parse("E"));
            easyGuitarChords.add(ScaleChord.parse("D"));
            easyGuitarChords.add(ScaleChord.parse("Am"));
            easyGuitarChords.add(ScaleChord.parse("Em"));
            easyGuitarChords.add(ScaleChord.parse("Dm"));
        } catch (ParseException pex) {
            logger.info("parse exception should never happen: " + pex.getMessage());
        }
    }
}
