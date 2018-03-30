package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * A note in a scale has no duration or pitch but represents
 * the relative scale position within the given key.
 * <p>
 * Small note: Not all scale notes are used as key values.
 * </p>
 * <p>
 * Small note: The musical flat character is not allowed as a java enum name so a 'b' is used here.
 * The musical sharp character is not allowed as a java enum name so an 's' is used here.
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
        final RegExp keyRegexp = RegExp.compile("^([A-G])([sb]?)$");  //  workaround for RegExp is not serializable.
        MatchResult mr = keyRegexp.exec(name());
        if (mr != null) {
            String mod = "";
            String modHtml = "";
            String markup = "";
            switch (mr.getGroup(2)) {
                case "b":
                    mod += MusicConstant.flatChar;
                    modHtml = MusicConstant.flatHtml;
                    markup = "b";
                    break;
                case "n":
                    mod += MusicConstant.naturalChar;
                    modHtml = MusicConstant.naturalHtml;
                    break;
                case "s":
                    mod += MusicConstant.sharpChar;
                    modHtml = MusicConstant.sharpHtml;
                    markup = "#";
                    break;
            }
            String base = mr.getGroup(1);
            keyString = base + mod;
            keyHtml = base + modHtml;
            keyMarkup = base + markup;
        } else {
            keyString = name();//    fixme: should throw error, should never happen
            keyHtml = name();
            keyMarkup = name();
        }
    }

     static ScaleNote getSharpByHalfStep(int halfStep) {
        halfStep %= 12;
        if (halfStep < 0)
            halfStep += 12;
        return sharps[halfStep];
    }

     static ScaleNote getFlatByHalfStep(int halfStep) {
        halfStep %= 12;
        if (halfStep < 0)
            halfStep += 12;
        return flats[halfStep];
    }

    /**
     * Return the ScaleNote represented by the given string.
     * Is case sensitive.
     * <p>Ultimately, the markup language will disappear.</p>
     * @param s  string to be parsed
     * @return ScaleNote represented by the string.  Can be null.
     */
    public static ScaleNote parse(String s) {
        if (s == null || s.length() < 1)
            return null;
        char c = s.charAt(0);
        if (c < 'A' || c > 'G')
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append(c);

        //  look for modifier
        if (s.length() > 1) {
            c = s.charAt(1);
            switch (c) {
                case 'b':
                case MusicConstant.flatChar:
                    sb.append('b');
                    break;

                case '#':
                case MusicConstant.sharpChar:
                    sb.append('s');
                    break;
            }
        }

        return ScaleNote.valueOf(sb.toString());
    }

    /**
     * Returns the name of this key in a user friendly text format,
     * i.e. as UTF-8
     *
     * @return the name of this enum constant
     */
    @Override
    public String toString() {
        return keyString;
    }

    /**
     * Returns the name of this key in an HTML format.
     * @return
     */
    public String toHtml() {
        return keyHtml;
    }

    /**
     * Return the key as markup.
     * <p>Ultimately, the markup language will disappear.</p>
     * @return
     */
    @Deprecated
    public String toMarkup() {
        return keyMarkup;
    }

    private final String keyString;
    private final String keyHtml;
    private final String keyMarkup;

    private static final ScaleNote sharps[] = {
            A, As, B, C, Cs, D, Ds, E, F, Fs, G, Gs
            // 1  2  3  4   5   6  7  8   9  10, 11
    };
    private static final ScaleNote flats[] = {
            A, Bb, B, C, Db, D, Eb, E, F, Gb, G, Ab
            // 1  2  3  4   5   6  7  8   9  10, 11
    };
}
