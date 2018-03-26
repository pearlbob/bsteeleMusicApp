package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * A note in a scale has no duration or pitch but represents
 * the relative scale position within the key.
 * <p>
 *     Small note: not all scale notes are used as key values.
 * </p>
 */
public enum ScaleNote {
    A,
    As,
    B,
    C,
    Cs,
    D,
    Ds,
    E,
    F,
    Fs,
    G,
    Gs,
    Gb,
    Eb,
    Db,
    Bb,
    Ab,
    Cb,     //  used for Gb (-6) key
    Es;     //  used for Fs (+6) key

    ScaleNote() {
        RegExp keyRegexp = RegExp.compile("^([A-Z])([sb]?)$");
        MatchResult mr = keyRegexp.exec(name());
        if (mr != null) {
            String mod = "";
            String modHtml = "";
            switch (mr.getGroup(2)) {
                case "b":
                    mod += MusicConstant.flatChar;
                    modHtml = MusicConstant.flatHtml;
                    break;
                case "n":
                    mod += MusicConstant.naturalChar;
                    modHtml = MusicConstant.naturalHtml;
                    break;
                case "s":
                    mod += MusicConstant.sharpChar;
                    modHtml = MusicConstant.sharpHtml;
                    break;
            }
            keyString = mr.getGroup(1) + mod;
            keyHtml = mr.getGroup(1) + modHtml;
        } else {
            keyString = name();//    fixme: should throw error, should never happen
            keyHtml = name();
        }
    }

    public static ScaleNote getSharpByHalfStep(int halfStep) {
        halfStep %= 12;
        if (halfStep < 0)
            halfStep += 12;
        return sharps[halfStep];
    }

    public static ScaleNote getFlatByHalfStep(int halfStep) {
        halfStep %= 12;
        if (halfStep < 0)
            halfStep += 12;
        return flats[halfStep];
    }

    /**
     * Returns the name of this enum constant in a user friendly format,
     * i.e. as UTF-8
     *
     * @return the name of this enum constant
     */
    @Override
    public String toString() {
        return keyString;
    }

    public String toHtml() {
        return keyHtml;
    }

    private final String keyString;
    private final String keyHtml;

    private static final ScaleNote sharps[] = {
            A, As, B, C, Cs, D, Ds, E, F, Fs, G, Gs
            // 1  2  3  4   5   6  7  8   9  10, 11
    };
    private static final ScaleNote flats[] = {
            A, Bb, B, C, Db, D, Eb, E, F, Gb, G, Ab
            // 1  2  3  4   5   6  7  8   9  10, 11
    };

}
