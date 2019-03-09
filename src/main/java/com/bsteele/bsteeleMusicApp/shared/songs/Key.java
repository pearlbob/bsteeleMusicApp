package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.util.Util;
import com.google.gwt.core.client.GWT;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

/**
 * Representation of the song key used generate the expression of the proper scales.
 * <p>Six flats and six sharps are labeled differently but are otherwise the same key.
 * Seven flats and seven sharps are not included.</p>
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

    public static Key parse(String s) {
        s = s.replaceAll("♭", "b").replaceAll("[♯#]", "s");
        return valueOf(s);
    }

    /**
     * Return the next key that is one half step higher.
     * Of course the keys are cyclic in their relationship.
     *
     * @return the next key
     */
    public final Key nextKeyByHalfStep() {
        return keysByHalfStep[Util.mod(halfStep + 1, keysByHalfStep.length)];
    }

    public final Key nextKeyByHalfStep(int step) {
        return keysByHalfStep[Util.mod(halfStep + step, keysByHalfStep.length)];
    }

    public final Key nextKeyByFifth() {
        return keysByHalfStep[Util.mod(halfStep + 7, keysByHalfStep.length)];
    }

    /**
     * Return the next key that is one half step lower.
     * Of course the keys are cyclic in their relationship.
     *
     * @return the next key down
     */
    public final Key previousKeyByHalfStep() {
        return keysByHalfStep[Util.mod(halfStep - 1, keysByHalfStep.length)];
    }

    public final Key previousKeyByFifth() {
        return keysByHalfStep[Util.mod(halfStep - 7, keysByHalfStep.length)];
    }

    /**
     * Transpose the given scale note by the requested offset.
     *
     * @param scaleNote the scale note to be transcribed
     * @param offset    the offset for the transcription, typically between -6 and +6
     * @return the scale note the key that matches the transposition requested
     */
    public final ScaleNote transpose(ScaleNote scaleNote, int offset) {
        return getScaleNoteByHalfStep(scaleNote.getHalfStep() + offset);
    }

    /**
     * Return an integer value that represents the key.
     *
     * @return an integer value that represents the key
     */
    public final int getKeyValue() {
        return keyValue;
    }

    /**
     * Return the scale note of the key, i.e. the musician's label for the key.
     *
     * @return the scale note of the key
     */
    public final ScaleNote getKeyScaleNote() {
        return keyScaleNote;
    }

    public final ScaleNote getKeyMinorScaleNote() {
        return keyMinorScaleNote;
    }

    /**
     * Return an integer value that represents the key's number of half steps from A.
     *
     * @return the count of half steps from A
     */
    public final int getHalfStep() {
        return keyScaleNote.getHalfStep();
    }

    /**
     * Return the key represented by the given integer value.
     *
     * @param keyValue the given integer value
     * @return the key
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

    public final Key getMinorKey() {
        // the key's tonic
        return getKeyByHalfStep(getHalfStep() + majorScale[6 - 1]);
    }

    /**
     * Return a representation of the key in HTML.
     *
     * @return the HTML
     */
    public final String toHtml() {
        return keyScaleNote.toHtml();
    }

    /**
     * Guess the key from the collection of scale notes in a given song.
     *
     * @param scaleChords the scale chords to guess from
     * @return the roughly calculated key of the given scale notes.
     */
    public static final Key guessKey(AbstractCollection<ScaleChord> scaleChords) {
        Key ret = getDefault();                //  default answer

        //  minimize the chord variations and keep a count of the scale note use
        HashMap<ScaleNote, Integer> useMap = new HashMap<>();
        for (ScaleChord scaleChord : scaleChords) {
            //  minimize the variation by using only the scale note
            ScaleNote scaleNote = scaleChord.getScaleNote();

            //  count the uses
            //  fixme: account for song section repeats
            Integer count = useMap.get(scaleNote);
            useMap.put(scaleNote, (count == null) ? 1 : count + 1);
        }

        //  find the key with the longest greatest parse to the major chord
        int maxScore = 0;
        int minKeyValue = Integer.MAX_VALUE;

        //  find the key with the greatest parse to it's diatonic chords
        {
            Integer count;
            ScaleChord diatonic;
            ScaleNote diatonicScaleNote;
            for (Key key : Key.values()) {
                //  score by weighted uses of the scale chords
                int score = 0;
                for (int i = 0; i < key.majorDiatonics.size(); i++) {
                    diatonic = key.getMajorDiatonicByDegree(i);
                    diatonicScaleNote = diatonic.getScaleNote();
                    if ((count = useMap.get(diatonicScaleNote)) != null)
                        score += count * guessWeights[i];
                    else {
                        if ((diatonic = diatonic.getAlias()) != null) {
                            diatonicScaleNote = diatonic.getScaleNote();
                            if (diatonic != null && (count = useMap.get(diatonicScaleNote)) != null)
                                score += count * guessWeights[i];
                        }
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
        }
        GWT.log("guess: " + ret.toString() + ": score: " + maxScore);
        return ret;
    }


    /**
     * Return the requested diatonic chord by degree.
     * Counts from zero. For example, 0 represents the I chord, 3 represents the IV chord.
     *
     * @param note diatonic note/chord count
     * @return the diatonic scale chord
     */
    public final ScaleChord getMajorDiatonicByDegree(int note) {
        note = Util.mod(note, majorDiatonics.size());
        return majorDiatonics.get(note);
    }

    public final ScaleChord getMajorScaleChord() {
        return majorDiatonics.get(0);
    }

    public final ScaleChord getMinorDiatonicByDegree(int note) {
        note = Util.mod(note, minorDiatonics.size());
        return minorDiatonics.get(note);
    }

    public final ScaleChord getMinorScaleChord() {
        return minorDiatonics.get(0);
    }

    public final boolean isDiatonic(ScaleChord scaleChord) {
        return majorDiatonics.contains(scaleChord);
    }

    public final ScaleNote getMajorScaleByNote(int note) {
        note = Util.mod(note, MusicConstant.notesPerScale);
        return getKeyScaleNoteByHalfStep(majorScale[note]);
    }

    public final ScaleNote getMinorScaleByNote(int note) {
        return getMajorScaleByNote(note + (6 - 1));
    }


    /**
     * Counts from zero.
     *
     * @param halfStep the half step offset count
     * @return the scale note at the offset
     */
    private ScaleNote getKeyScaleNoteByHalfStep(int halfStep) {

        halfStep += keyValue * halfStepsToFifth + halfStepsFromCtoA;
        return getScaleNoteByHalfStep(halfStep);
    }

    public final ScaleNote getScaleNoteByHalfStep(int halfSteps) {

        halfSteps = Util.mod(halfSteps, MusicConstant.halfStepsPerOctave);
        ScaleNote ret = (keyValue >= 0)
                ? ScaleNote.getSharpByHalfStep(halfSteps)
                : ScaleNote.getFlatByHalfStep(halfSteps);

        //  deal with exceptions at +-6
        if (keyValue == 6 && ret.equals(ScaleNote.F))
            return ScaleNote.Es;
        else if (keyValue == -6 && ret.equals(ScaleNote.B))
            return ScaleNote.Cb;
        return ret;
    }

    public static final Key getDefault() {
        return Key.C;
    }


    public final String sharpsFlatsToString() {
        if (keyValue < 0)
            return Integer.toString(Math.abs(keyValue)) + MusicConstant.flatChar;
        if (keyValue > 0)
            return Integer.toString(keyValue) + MusicConstant.sharpChar;
        return "";
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
    //                                       0  1  2  3  4  5  6
    private static final int majorScale[] = {0, 2, 4, 5, 7, 9, 11};
    //private static final int minorScale[] = {0, 2, 3, 5, 7, 8, 10};
    private static final ChordDescriptor diatonic7ChordModifiers[] =
            {
                    ChordDescriptor.major,      //  0 + 1 = 1
                    ChordDescriptor.minor,      //  1 + 1 = 2
                    ChordDescriptor.minor,      //  2 + 1 = 3
                    ChordDescriptor.major,      //  3 + 1 = 4
                    ChordDescriptor.dominant7,  //  4 + 1 = 5
                    ChordDescriptor.minor,      //  5 + 1 = 6
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
    private static final int guessWeights[] = {9, 1, 1, 4, 4, 1, 3};
    private static final int halfStepsToFifth = 7;
    private static final int halfStepsFromCtoA = 3;


    private final int keyValue;
    private final int halfStep;
    private final ScaleNote keyScaleNote;
    private ScaleNote keyMinorScaleNote;
    private ArrayList<ScaleChord> majorDiatonics;
    private ArrayList<ScaleChord> minorDiatonics;

    static {
        //  majorDiatonics need majorScale which is initialized after the constructors
        for (Key key : Key.values()) {

            key.majorDiatonics = new ArrayList<>();
            for (int i = 0; i < MusicConstant.notesPerScale; i++) {
                key.majorDiatonics.add(new ScaleChord(key.getMajorScaleByNote(i), MusicConstant.getMajorDiatonicChordModifier(i)));
            }

            key.minorDiatonics = new ArrayList<>();
            for (int i = 0; i < MusicConstant.notesPerScale; i++) {
                key.minorDiatonics.add(new ScaleChord(key.getMinorScaleByNote(i), MusicConstant.getMinorDiatonicChordModifier(i)));
            }
        }

        for (Key key : Key.values()) {
            key.keyMinorScaleNote = key.getMajorDiatonicByDegree(6 - 1).getScaleNote();
        }
    }

}

/*                     1  2  3  4  5  6  7                 I    II   III  IV   V    VI   VII               0  1  2  3  4  5  6  7  8  9  10 11
-6 Gb (G♭)		scale: G♭ A♭ B♭ C♭ D♭ E♭ F  	majorDiatonics: G♭   A♭m  B♭m  C♭   D♭7  E♭m  Fm7b5 	all notes: A  B♭ C♭ C  D♭ D  E♭ E  F  G♭ G  A♭
-5 Db (D♭)		scale: D♭ E♭ F  G♭ A♭ B♭ C  	majorDiatonics: D♭   E♭m  Fm   G♭   A♭7  B♭m  Cm7b5 	all notes: A  B♭ B  C  D♭ D  E♭ E  F  G♭ G  A♭
-4 Ab (A♭)		scale: A♭ B♭ C  D♭ E♭ F  G  	majorDiatonics: A♭   B♭m  Cm   D♭   E♭7  Fm   Gm7b5 	all notes: A  B♭ B  C  D♭ D  E♭ E  F  G♭ G  A♭
-3 Eb (E♭)		scale: E♭ F  G  A♭ B♭ C  D  	majorDiatonics: E♭   Fm   Gm   A♭   B♭7  Cm   Dm7b5 	all notes: A  B♭ B  C  D♭ D  E♭ E  F  G♭ G  A♭
-2 Bb (B♭)		scale: B♭ C  D  E♭ F  G  A  	majorDiatonics: B♭   Cm   Dm   E♭   F7   Gm   Am7b5 	all notes: A  B♭ B  C  D♭ D  E♭ E  F  G♭ G  A♭
-1 F (F)		scale: F  G  A  B♭ C  D  E  	majorDiatonics: F    Gm   Am   B♭   C7   Dm   Em7b5 	all notes: A  B♭ B  C  D♭ D  E♭ E  F  G♭ G  A♭
 0 C (C)		scale: C  D  E  F  G  A  B  	majorDiatonics: C    Dm   Em   F    G7   Am   Bm7b5 	all notes: A  A♯ B  C  C♯ D  D♯ E  F  F♯ G  G♯
 1 G (G)		scale: G  A  B  C  D  E  F♯ 	majorDiatonics: G    Am   Bm   C    D7   Em   F♯m7b5 	all notes: A  A♯ B  C  C♯ D  D♯ E  F  F♯ G  G♯
 2 D (D)		scale: D  E  F♯ G  A  B  C♯ 	majorDiatonics: D    Em   F♯m  G    A7   Bm   C♯m7b5 	all notes: A  A♯ B  C  C♯ D  D♯ E  F  F♯ G  G♯
 3 A (A)		scale: A  B  C♯ D  E  F♯ G♯ 	majorDiatonics: A    Bm   C♯m  D    E7   F♯m  G♯m7b5 	all notes: A  A♯ B  C  C♯ D  D♯ E  F  F♯ G  G♯
 4 E (E)		scale: E  F♯ G♯ A  B  C♯ D♯ 	majorDiatonics: E    F♯m  G♯m  A    B7   C♯m  D♯m7b5 	all notes: A  A♯ B  C  C♯ D  D♯ E  F  F♯ G  G♯
 5 B (B)		scale: B  C♯ D♯ E  F♯ G♯ A♯ 	majorDiatonics: B    C♯m  D♯m  E    F♯7  G♯m  A♯m7b5 	all notes: A  A♯ B  C  C♯ D  D♯ E  F  F♯ G  G♯
 6 Fs (F♯)		scale: F♯ G♯ A♯ B  C♯ D♯ E♯ 	majorDiatonics: F♯   G♯m  A♯m  B    C♯7  D♯m  E♯m7b5 	all notes: A  A♯ B  C  C♯ D  D♯ E  E♯ F♯ G  G♯









 */
