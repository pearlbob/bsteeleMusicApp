package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.shared.Util;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public enum Key {
    Gb(-6, 9),
    Db(-5, 4),
    Ab(-4, 11),
    Eb(-3, 6),
    Bb(-2, 1),
    F(-1, 8),
    C(0, 3),
    G(1, 10),
    D(2, 5),
    A(3, 0),
    E(4, 7),
    B(5, 2),
    Fs(6, 9);

    Key(int keyValue, int halfStep) {
        this.keyValue = keyValue;
        this.halfStep = halfStep;
        keyScaleNote = ScaleNote.valueOf(name());
    }

    public Key nextKeyByHalfStep() {
        return keysByHalfStep[Util.mod(halfStep + 1, keysByHalfStep.length)];
    }

    public Key previousKeyByHalfStep() {
        return keysByHalfStep[Util.mod(halfStep - 1, keysByHalfStep.length)];
    }

    public ScaleNote transpose(ScaleNote scaleNote, int offset) {
        return getScaleNoteByHalfStep(scaleNote.getHalfStep() + offset);
    }

    /**
     * Return an integer value that represents the key.
     *
     * @return
     */
    public int getKeyValue() {
        return keyValue;
    }

    /**
     * Return the scale note of the key, i.e. the musician's label for the key.
     *
     * @return
     */
    public ScaleNote getKeyScaleNote() {
        return keyScaleNote;
    }

    public int getHalfStep() {
        return keyScaleNote.getHalfStep();
    }

    /**
     * Return the key represented by the given integer value.
     *
     * @param keyValue
     * @return
     */
    public static final Key getKeyByValue(int keyValue) {
        for (Key key : Key.values())
            if (key.keyValue == keyValue)
                return key;
        return Key.values()[0];     //  not found, so use the default, expected to be C
    }

    public static final Key getKeyByHalfStep(int halfStep) {
        halfStep = Util.mod(halfStep, MusicConstant.halfStepsPerOctave);
        for (Key key : Key.values())
            if (key.halfStep == halfStep)
                return key;
        return Key.values()[0];     //  default, expected to be C
    }

    /**
     * Return a representation of the key in HTML.
     *
     * @return
     */
    public String toHtml() {
        return keyScaleNote.toHtml();
    }

    /**
     * Guess the key from the collection of scale notes in a given song.
     *
     * @param scaleChords
     * @return the roughly calculated key of the given scale notes.
     */
    public static Key guessKey(AbstractCollection<ScaleChord> scaleChords) {
        Key ret = Key.C;                //  default answer

        //  minimize the chord variations and keep a count of their use
        HashMap<ScaleChord, Integer> useMap = new HashMap<>();
        for (ScaleChord scaleChord : scaleChords) {
            //  minimize the variation
            switch (scaleChord.getChordDescriptor()) {
                case major7:
                    scaleChord = new ScaleChord(scaleChord.getScaleNote(), ChordDescriptor.major, ChordTension.none);
                    break;
                case minor7:
                    scaleChord = new ScaleChord(scaleChord.getScaleNote(), ChordDescriptor.minor, ChordTension.none);
                    break;
            }

            //  count the uses
            //  fixme: account for repeats
            Integer count = useMap.get(scaleChord);
            useMap.put(scaleChord, (count == null) ? 1 : count + 1);
        }

        //  find the key with the longest greatest match to the major chord
        int maxScore = 0;
        int minKeyValue = Integer.MAX_VALUE;

        //  find the key with the greatest match to it's diatonic chords
        Integer count;
        for (Key key : Key.values()) {
            //  score by weighted uses of the scale chords
            int score = 0;
            for (int i = 0; i < key.diatonics.size(); i++) {
                ScaleChord diatonic = key.getDiatonicByDegree(i);
                if ((count = useMap.get(diatonic)) != null)
                    score += count * guessWeights[i];
                else {
                    diatonic = diatonic.getAlias();
                    if (diatonic != null && (count = useMap.get(diatonic)) != null)
                        score += count * guessWeights[i];
                }
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
     * Return the requested diatonic chord by degree.
     * Counts from zero. For example, 0 represents the I chord, 3 represents the IV chord.
     *
     * @param note
     * @return
     */
    public ScaleChord getDiatonicByDegree(int note) {
        note = Util.mod(note, diatonics.size());
        return diatonics.get(note);
    }

    public boolean isDiatonic(ScaleChord scaleChord) {
        return diatonics.contains(scaleChord);
    }

    public ScaleNote getMajorScaleByNote(int note) {
        note = Util.mod(note, 7);
        return getKeyScaleNoteByHalfStep(majorScale[note]);
    }

    public ScaleNote getMinorScaleByNote(int note) {
        note %= 7;
        if (note < 0)
            note += 7;
        return getKeyScaleNoteByHalfStep(minorScale[note]);
    }


    /**
     * Counts from zero.
     *
     * @param halfStep
     * @return
     */
    private ScaleNote getKeyScaleNoteByHalfStep(int halfStep) {

        halfStep += keyValue * halfStepsToFifth + halfStepsFromCtoA;
        return getScaleNoteByHalfStep(halfStep);
    }

    public ScaleNote getScaleNoteByHalfStep(int halfStep) {

        halfStep = Util.mod(halfStep, MusicConstant.halfStepsPerOctave);
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


    //                                       1  2  3  4  5  6  7
    private static final int majorScale[] = {0, 2, 4, 5, 7, 9, 11};
    private static final int minorScale[] = {0, 2, 3, 5, 7, 8, 10};
    private static final ChordDescriptor diatonic7ChordModifiers[] =
            {
                    ChordDescriptor.major7,      //  0 + 1 = 1
                    ChordDescriptor.minor7,      //  1 + 1 = 2
                    ChordDescriptor.minor7,      //  2 + 1 = 3
                    ChordDescriptor.major7,      //  3 + 1 = 4
                    ChordDescriptor.dominant7,   //  4 + 1 = 5
                    ChordDescriptor.minor7,      //  5 + 1 = 6
                    ChordDescriptor.minor7b5,   //  6 + 1 = 7
            };
    private static Key keysByHalfStep[] = {
            Key.A,
            Key.Bb,
            Key.B,
            Key.C,
            Key.Db,
            Key.D,
            Key.Eb,
            Key.E,
            Key.F,
            Key.Gb,
            Key.G,
            Key.Ab
    };

    //                                         1  2  3  4  5  6  7
    private static final int guessWeights[] = {6, 1, 1, 4, 4, 1, 3};
    private static final int halfStepsToFifth = 7;
    private static final int halfStepsFromCtoA = 3;


    private final int keyValue;
    private final int halfStep;
    private final ScaleNote keyScaleNote;
    private ArrayList<ScaleChord> diatonics;

    static {
        //  diatonics need majorScale which is initialized after the constructors
        for (Key key : Key.values()) {
            key.diatonics = new ArrayList<>();
            for (int i = 0; i < MusicConstant.notesPerScale; i++) {
                key.diatonics.add(new ScaleChord(key.getMajorScaleByNote(i), MusicConstant.getDiatonicChordModifier(i),
                        ChordTension.none));
            }
        }
    }

}

/*                     1  2  3  4  5  6  7                 I    II   III  IV   V    VI   VII               0  1  2  3  4  5  6  7  8  9  10 11
-6 Gb (G♭)		scale: G♭ A♭ B♭ C♭ D♭ E♭ F  	diatonics: G♭   A♭m  B♭m  C♭   D♭7  E♭m  Fm7b5 	all notes: A  B♭ C♭ C  D♭ D  E♭ E  F  G♭ G  A♭
-5 Db (D♭)		scale: D♭ E♭ F  G♭ A♭ B♭ C  	diatonics: D♭   E♭m  Fm   G♭   A♭7  B♭m  Cm7b5 	all notes: A  B♭ B  C  D♭ D  E♭ E  F  G♭ G  A♭
-4 Ab (A♭)		scale: A♭ B♭ C  D♭ E♭ F  G  	diatonics: A♭   B♭m  Cm   D♭   E♭7  Fm   Gm7b5 	all notes: A  B♭ B  C  D♭ D  E♭ E  F  G♭ G  A♭
-3 Eb (E♭)		scale: E♭ F  G  A♭ B♭ C  D  	diatonics: E♭   Fm   Gm   A♭   B♭7  Cm   Dm7b5 	all notes: A  B♭ B  C  D♭ D  E♭ E  F  G♭ G  A♭
-2 Bb (B♭)		scale: B♭ C  D  E♭ F  G  A  	diatonics: B♭   Cm   Dm   E♭   F7   Gm   Am7b5 	all notes: A  B♭ B  C  D♭ D  E♭ E  F  G♭ G  A♭
-1 F (F)		scale: F  G  A  B♭ C  D  E  	diatonics: F    Gm   Am   B♭   C7   Dm   Em7b5 	all notes: A  B♭ B  C  D♭ D  E♭ E  F  G♭ G  A♭
 0 C (C)		scale: C  D  E  F  G  A  B  	diatonics: C    Dm   Em   F    G7   Am   Bm7b5 	all notes: A  A♯ B  C  C♯ D  D♯ E  F  F♯ G  G♯
 1 G (G)		scale: G  A  B  C  D  E  F♯ 	diatonics: G    Am   Bm   C    D7   Em   F♯m7b5 	all notes: A  A♯ B  C  C♯ D  D♯ E  F  F♯ G  G♯
 2 D (D)		scale: D  E  F♯ G  A  B  C♯ 	diatonics: D    Em   F♯m  G    A7   Bm   C♯m7b5 	all notes: A  A♯ B  C  C♯ D  D♯ E  F  F♯ G  G♯
 3 A (A)		scale: A  B  C♯ D  E  F♯ G♯ 	diatonics: A    Bm   C♯m  D    E7   F♯m  G♯m7b5 	all notes: A  A♯ B  C  C♯ D  D♯ E  F  F♯ G  G♯
 4 E (E)		scale: E  F♯ G♯ A  B  C♯ D♯ 	diatonics: E    F♯m  G♯m  A    B7   C♯m  D♯m7b5 	all notes: A  A♯ B  C  C♯ D  D♯ E  F  F♯ G  G♯
 5 B (B)		scale: B  C♯ D♯ E  F♯ G♯ A♯ 	diatonics: B    C♯m  D♯m  E    F♯7  G♯m  A♯m7b5 	all notes: A  A♯ B  C  C♯ D  D♯ E  F  F♯ G  G♯
 6 Fs (F♯)		scale: F♯ G♯ A♯ B  C♯ D♯ E♯ 	diatonics: F♯   G♯m  A♯m  B    C♯7  D♯m  E♯m7b5 	all notes: A  A♯ B  C  C♯ D  D♯ E  E♯ F♯ G  G♯









 */
