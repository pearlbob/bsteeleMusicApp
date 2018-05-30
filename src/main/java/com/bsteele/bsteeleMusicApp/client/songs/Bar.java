package com.bsteele.bsteeleMusicApp.client.songs;

import java.util.ArrayList;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class Bar {
    private Measure measure;
    private ArrayList<Note> notes;

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public Chord getChordAtBeat(double beat) {
        if (measure == null)
            return null;
        return measure.getChordAtBeat(beat);
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }
}
