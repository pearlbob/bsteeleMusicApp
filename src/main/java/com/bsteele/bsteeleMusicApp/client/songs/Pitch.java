package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * All possible piano pitches and their notational alias's.
 * <p>
 * A pitch has a human readable name based on it's piano based location.
 * A pitch has a frequency but no duration.
 * </p>
 * <p>Black key pitches will have an alias at the same frequency.  This
 * is done to help ease the mapping from keys to pitches.</p>
 */
public enum Pitch {
    /**
     * First tone
     */
    A0,
    As0,
    Bb0,
    B0,
    Bs0,

    Cb1,
    C1,
    Cs1,
    Db1,
    D1,
    Ds1,
    Eb1,
    E1,
    Es1,
    Fb1,
    F1,
    Fs1,
    Gb1,
    G1,
    Gs1,
    Ab1,
    A1,
    As1,
    Bb1,
    B1,
    Bs1,

    Cb2,
    /**
     * Low C
     */
    C2,
    Cs2,
    Db2,
    D2,
    Ds2,
    Eb2,
    E2,
    Es2,
    Fb2,
    F2,
    Fs2,
    Gb2,
    G2,
    Gs2,
    Ab2,
    A2,
    As2,
    Bb2,
    B2,
    Bs2,

    Cb3,
    C3,
    Cs3,
    Db3,
    D3,
    Ds3,
    Eb3,
    E3,
    Es3,
    Fb3,
    F3,
    Fs3,
    Gb3,
    G3,
    Gs3,
    Ab3,
    A3,
    As3,
    Bb3,
    B3,
    Bs3,

    Cb4,
    /**
     * middle C
     */
    C4,
    Cs4,
    Db4,
    D4,
    Ds4,
    Eb4,
    E4,
    Es4,
    Fb4,
    F4,
    Fs4,
    Gb4,
    G4,
    Gs4,
    Ab4,
    /**
     * Concert pitch
     */
    A4,
    As4,
    Bb4,
    B4,
    Bs4,

    Cb5,
    C5,
    Cs5,
    Db5,
    D5,
    Ds5,
    Eb5,
    E5,
    Es5,
    Fb5,
    F5,
    Fs5,
    Gb5,
    G5,
    Gs5,
    Ab5,
    A5,
    As5,
    Bb5,
    B5,
    Bs5,

    /**
     * High C
     */
    Cb6,
    C6,
    Cs6,
    Db6,
    D6,
    Ds6,
    Eb6,
    E6,
    Es6,
    Fb6,
    F6,
    Fs6,
    Gb6,
    G6,
    Gs6,
    Ab6,
    A6,
    As6,
    Bb6,
    B6,
    Bs6,

    Cb7,
    C7,
    Cs7,
    Db7,
    D7,
    Ds7,
    Eb7,
    E7,
    Es7,
    Fb7,
    F7,
    Fs7,
    Gb7,
    G7,
    Gs7,
    Ab7,
    A7,
    As7,
    Bb7,
    B7,
    Bs7,

    Cb8,
    /**
     * last tone
     */
    C8;

    Pitch() {
        //  initialize the final values
        final RegExp pitchRegExp = RegExp.compile("^([A-G][sb]?)([0-8])$");

        MatchResult mr = pitchRegExp.exec(name());
        //   fail early on null!
        scaleNote = ScaleNote.valueOf(mr.getGroup(1));
        int labelNumber = Integer.parseInt(mr.getGroup(2));

        //  cope with the piano numbers stepping forward on C
        //  and the label numbers not stepping with sharps and flats,
        //  making the unusual B sharp and C flat very special
        int n = scaleNote.getHalfStep();
        if (name().startsWith("Bs")) {
            n += labelNumber * MusicConstant.halfStepsPerOctave;
        } else if (name().startsWith("Cb")) {
            n += (labelNumber - 1) * MusicConstant.halfStepsPerOctave;
        } else {
            //  offset from A to C
            int fromC = (n - 3) % MusicConstant.halfStepsPerOctave;
            //  compute halfsteps from A0
            n += ((fromC >= 0) ? labelNumber - 1 : labelNumber) * MusicConstant.halfStepsPerOctave;
        }
        number = n;

        frequency = 440 * Math.pow(2, (double)((number+1)-49)/12);
    }

    /**
     * Get an integer the represents this pitch.
     * @return  the integer represntation of the pitch
     */
    public int getNumber() {
        return number;
    }

    /**
     * Return the frequency of the pitch.
     * @return the frequency of the pitch in Hertz
     */
    public double getFrequency() {
        return frequency;
    }

    /**
     * Return the scale note represented by this pitch.
     * @return  the pitch's scale note
     */
    public ScaleNote getScaleNote() {
        return scaleNote;
    }

    private final ScaleNote scaleNote;
    private final int number;
    private final double frequency;

}
