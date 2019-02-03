package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

import com.bsteele.bsteeleMusicApp.shared.Util;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * A note in a scale has no duration or pitch but represents
 * the relative scale position within the given key scale.
 * <p>
 * Not all scale notes are used as key values.
 * </p>
 * <p>
 * The musical flat character is not allowed as a java enum name so a 'b' is used here.
 * The musical sharp character is not allowed as a java enum name so an 's' is used here.
 * </p>
 */
public enum ScaleNote {
    A(0),
    As(1),
    B(2),
    C(3),
    Cs(4),
    D(5),
    Ds(6),
    E(7),
    F(8),
    Fs(9),
    G(10),
    Gs(11),
    Gb(9),
    Eb(6),
    Db(4),
    Bb(1),
    Ab(11),
    Cb(2),     //  used for Gb (-6) key
    Es(8),     //  used for Fs (+6) key
    Bs(3),     //   for completeness of piano expression
    Fb(7),     //   for completeness of piano expression
    ;

    ScaleNote(int halfStep) {
        this.halfStep = halfStep;

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
                    isSharp = false;
                    isFlat = true;
                    break;
                case "n":
                    mod += MusicConstant.naturalChar;
                    modHtml = MusicConstant.naturalHtml;
                    isSharp = false;
                    isFlat = false;
                    break;
                default:
                    isSharp = false;
                    isFlat = false;
                    break;
                case "s":
                    mod += MusicConstant.sharpChar;
                    modHtml = MusicConstant.sharpHtml;
                    markup = "#";
                    isSharp = true;
                    isFlat = false;
                    break;
            }
            String base = mr.getGroup(1);
            scaleNoteString = base + mod;
            scaleNoteHtml = base + modHtml;
            scaleNoteMarkup = base + markup;
        } else {
            scaleNoteString = name();//    fixme: should throw error, should never happen
            scaleNoteHtml = name();
            scaleNoteMarkup = name();
            isSharp = false;
            isFlat = false;
        }
    }

    /**
     * A utility to map the sharp scale notes to their half step offset.
     * Should use the scale notes from the key under normal situations.
     *
     * @param step the number of half steps from A
     * @return the sharp scale note
     */
    @Deprecated
    static final ScaleNote getSharpByHalfStep(int step) {
        return sharps[Util.mod(step, MusicConstant.halfStepsPerOctave)];
    }

    /**
     * A utility to map the flat scale notes to their half step offset.
     * Should use the scale notes from the key under normal situations.
     *
     * @param step the number of half steps from A
     * @return the sharp scale note
     */
    @Deprecated
    static final ScaleNote getFlatByHalfStep(int step) {
        return flats[Util.mod(step, MusicConstant.halfStepsPerOctave)];
    }

    final static ScaleNote testParse(String s) {
        return parse(new StringBuffer(s));
    }


    /**
     * Return the ScaleNote represented by the given string.
     * Is case sensitive.
     * <p>Ultimately, the markup language will disappear.</p>
     *
     * @param sb string buffer to be parsed
     * @return ScaleNote represented by the string.  Can be null.
     */
    public final static ScaleNote parse(StringBuffer sb) {
        if (sb == null || sb.length() < 1)
            return null;
        char c = sb.charAt(0);
        if (c < 'A' || c > 'G')
            return null;

        StringBuilder scaleNoteString = new StringBuilder();
        scaleNoteString.append(c);
        sb.delete(0, 1);

        //  look for modifier
        if (sb.length() > 0) {
            c = sb.charAt(0);
            switch (c) {
                case 'b':
                case MusicConstant.flatChar:
                    scaleNoteString.append('b');
                    sb.delete(0, 1);
                    break;

                case '#':
                case MusicConstant.sharpChar:
                    scaleNoteString.append('s');
                    sb.delete(0, 1);
                    break;
            }
        }

        return ScaleNote.valueOf(scaleNoteString.toString());
    }

    public final ScaleNote transpose( Key key, int steps ){
        return key.getScaleNoteByHalfStep( halfStep + steps);
    }

    /**
     * Return the halfStep offset from A.
     *
     * @return
     */
    public final int getHalfStep() {
        return halfStep;
    }

    /**
     * Returns the name of this key in a user friendly text format,
     * i.e. as UTF-8
     *
     * @return the name of this enum constant
     */
    @Override
    public final String toString() {
        return scaleNoteString;
    }

    /**
     * Returns the name of this key in an HTML format.
     *
     * @return
     */
    public final String toHtml() {
        return scaleNoteHtml;
    }

    /**
     * Return the key as markup.
     * <p>Ultimately, the markup language will disappear.</p>
     *
     * @return
     */
    @Deprecated
    public final String toMarkup() {
        return scaleNoteMarkup;
    }


    public final boolean isSharp() {
        return isSharp;
    }

    public final boolean isNatural() {
        return !isSharp && !isFlat;
    }

    public final boolean isFlat() {
        return isFlat;
    }

    /**
     * Return the scale note's flat alias if it is a sharp
     * or the sharp alias if the scale note is a flat.
     * White key notes will return null.
     *
     * @return the scale note's alias or null if there is none
     */
    public final ScaleNote getAlias() {
        return alias;
    }

    private final int halfStep;
    private final String scaleNoteString;
    private final String scaleNoteHtml;
    private final String scaleNoteMarkup;
    private ScaleNote alias;
    private final boolean isSharp;
    private final boolean isFlat;

    private static final ScaleNote sharps[] = {
            A, As, B, C, Cs, D, Ds, E, F, Fs, G, Gs
            // 1  2  3  4   5   6  7  8   9  10, 11
    };
    private static final ScaleNote flats[] = {
            A, Bb, B, C, Db, D, Eb, E, F, Gb, G, Ab
            // 1  2  3  4   5   6  7  8   9  10, 11
    };

    static {
        //  find and assign the alias's
        for (ScaleNote sn : ScaleNote.values()) {
            for (ScaleNote other : ScaleNote.values()) {
                if (sn != other && sn.halfStep == other.halfStep)
                    sn.alias = other;
            }
        }
    }

}
