package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public enum Key {
    C(0),
    G(1),
    D(2),
    A(3),
    E(4),
    B(5),
    Fs(6),
    F(-1),
    Bb(-2),
    Eb(-3),
    Ab(-4),
    Db(-5),
    Gb(-6);

    Key(int keyValue) {
        this.keyValue = keyValue;
        keyScaleNote = ScaleNote.valueOf(name());
    }

    public int getKeyValue() {
        return keyValue;
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

    /**
     * Counts from zero.
     *
     * @param note
     * @return
     */
    public ScaleNote getMajorScaleByNote(int note) {
        note %= 7;
        if (note < 0)
            note += 7;
        return getScalebyHalfStep(majorScale[note]);
    }

    private static final int majorScale[] = {0, 2, 4, 5, 7, 9, 11};

    /**
     * Counts from zero.
     *
     * @param halfStep
     * @return
     */
    public ScaleNote getScalebyHalfStep(int halfStep) {

        halfStep += keyValue * halfStepsToFifth + halfStepsFromCtoA;
        ScaleNote ret =  (keyValue >= 0)
            ? ScaleNote.getSharpByHalfStep(halfStep)
        : ScaleNote.getFlatByHalfStep(halfStep);

        //  deal with exceptions at +-6
        if ( keyValue == 6 && ret.equals(ScaleNote.F))
            return ScaleNote.Es;
        else if ( keyValue == -6 && ret.equals(ScaleNote.B))
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
    private static final int halfStepsToFifth = 7;
    private static final int halfStepsFromCtoA = 3;
}
