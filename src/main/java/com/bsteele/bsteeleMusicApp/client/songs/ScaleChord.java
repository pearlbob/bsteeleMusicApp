package com.bsteele.bsteeleMusicApp.client.songs;

import javax.validation.constraints.NotNull;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

/**
 * A chord with a scale note and an optional chord descriptor and tension.
 */
public class ScaleChord {
    ScaleChord(@NotNull ScaleNote scaleNote, ChordDescriptor chordDescriptor, ChordTension chordTension) {
        this.scaleNote = scaleNote;
        this.chordDescriptor = chordDescriptor;
        this.chordTension = chordTension;
        length = toString().length();
    }

    public static ScaleChord parse(String s) {
        if (s == null || s.length() < 1)
            return null;

        ScaleNote retScaleNote = ScaleNote.parse(s);
        if (retScaleNote == null)
            return null;
        s = s.substring(retScaleNote.toString().length());

        ChordDescriptor retChordDescriptor = ChordDescriptor.major; //  chord without modifier short name
        if (s.length() > 0) {
            for (ChordDescriptor cd : ChordDescriptor.values()) {
                if (s.startsWith(cd.getShortName())) {
                    retChordDescriptor = cd;
                    s = s.substring(cd.getShortName().length());
                    break;
                }
            }
        }

        ChordTension retChordTension = ChordTension.none; //  chord tension without short name
        if (s.length() > 0) {
            for (ChordTension cd : ChordTension.values()) {
                if (s.startsWith(cd.getShortName())) {
                    retChordTension = cd;
                    //s = s.substring(cd.getShortName().length());
                    break;
                }
            }
        }
        return new ScaleChord(retScaleNote, retChordDescriptor, retChordTension);
    }

    public ScaleNote getScaleNote() {
        return scaleNote;
    }

    public ChordDescriptor getChordDescriptor() {
        return chordDescriptor;
    }

    public ChordTension getChordTension() {
        return chordTension;
    }

    public int getLength() {
        return length;
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

}
