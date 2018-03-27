package com.bsteele.bsteeleMusicApp.client.songs;

import java.util.AbstractCollection;
import java.util.ArrayList;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public enum Key {
    Gb(-6),
    Db(-5),
    Ab(-4),
    Eb(-3),
    Bb(-2),
    F(-1),
    C(0),
    G(1),
    D(2),
    A(3),
    E(4),
    B(5),
    Fs(6);

    Key(int keyValue) {
        this.keyValue = keyValue;
        keyScaleNote = ScaleNote.valueOf(name());

        diatonics = new ArrayList<>();
        {
            int degree = 0;
            for (int i = 0; i < 7; i++) {
                diatonics.add(getScaleNoteByHalfStep(degree));
                degree += halfStepsToFifth;
            }
        }
    }

    public int getKeyValue() {
        return keyValue;
    }

    public ScaleNote getKeyScaleNote() {
        return keyScaleNote;
    }

    public static final Key getKeyByValue(int keyValue) {
        for (Key key : Key.values())
            if (key.keyValue == keyValue)
                return key;
        return Key.values()[0];     //  default, expected to be C
    }

    public String toHtml() {
        return keyScaleNote.toHtml();
    }

    public static Key guessKey(AbstractCollection<ScaleNote> scaleNotes) {
        Key ret = Key.C;                //  default answer

        //  find the key with the longest greatest match to the major chord
        int maxScore = 0;
        int minKeyValue = Integer.MAX_VALUE;
        for (Key key : Key.values()) {
            //  score by weighted uses of the scale chords
            int score = 0;
            for (int i = 0; i < majorScale.length; i++) {
                if (scaleNotes.contains(key.getMajorScaleByNote(i)))
                    score += guessWeights[i];
            }

            //  find the max score with the minimum key value
            if (score > maxScore
                    || (score == maxScore && Math.abs(key.getKeyValue()) < minKeyValue)) {
                ret = key;
                maxScore = score;
                minKeyValue = Math.abs(key.getKeyValue());
            }
        }
        //  find the key with the longest greatest match to the minor chord
        for (Key key : Key.values()) {
            //  score by weighted uses of the scale chords
            int score = 0;
            for (int i = 0; i < majorScale.length; i++) {
                if (scaleNotes.contains(key.getMinorScaleByNote(i)))
                    score += guessWeights[i];
            }

            //  find the max score with the minimum key value
            if (score > maxScore
                    || (score == maxScore && Math.abs(key.getKeyValue()) < minKeyValue)) {
                ret = key;
                maxScore = score;
                minKeyValue = Math.abs(key.getKeyValue());
            }
        }
        return ret;
    }


    /**
     * Counts from zero.
     *
     * @param note
     * @return
     */
    public ScaleNote getDiatonicByDegree(int note) {
        note %= diatonics.size();
        if (note < 0)
            note += diatonics.size();
        return diatonics.get(note);
    }

    public boolean isDiatonic(ScaleNote scaleNote) {
        return diatonics.contains(scaleNote);
    }

    public ScaleNote getMajorScaleByNote(int note) {
        note %= 7;
        if (note < 0)
            note += 7;
        return getScaleNoteByHalfStep(majorScale[note]);
    }

    public ScaleNote getMinorScaleByNote(int note) {
        note %= 7;
        if (note < 0)
            note += 7;
        return getScaleNoteByHalfStep(minorScale[note]);
    }


    /**
     * Counts from zero.
     *
     * @param halfStep
     * @return
     */
    public ScaleNote getScaleNoteByHalfStep(int halfStep) {

        halfStep += keyValue * halfStepsToFifth + halfStepsFromCtoA;
        ScaleNote ret = (keyValue >= 0)
                ? ScaleNote.getSharpByHalfStep(halfStep)
                : ScaleNote.getFlatByHalfStep(halfStep);

        //  deal with exceptions at +-6
        if (keyValue == 6 && ret.equals(ScaleNote.F))
            return ScaleNote.Es;
        else if (keyValue == -6 && ret.equals(ScaleNote.B))
            return ScaleNote.Cb;
        return ret;
    }


    /**
     * Returns the name of this enum constant in a user friendly format,
     * i.e. as UTF-8
     *
     * @return the name of this enum constant
     */
    @Override
    public String toString() {
        return keyScaleNote.toString();
    }

    private final int keyValue;
    private final ScaleNote keyScaleNote;
    private final ArrayList<ScaleNote> diatonics;

    //                                       1  2  3  4  5  6  7
    private static final int majorScale[] = {0, 2, 4, 5, 7, 9, 11};
    private static final int minorScale[] = {0, 2, 3, 5, 7, 8, 10};

    //                                         1  2  3  4  5  6  7
    private static final int guessWeights[] = {6, 1, 1, 4, 4, 1, 3};
    private static final int halfStepsToFifth = 7;
    private static final int halfStepsFromCtoA = 3;
}
