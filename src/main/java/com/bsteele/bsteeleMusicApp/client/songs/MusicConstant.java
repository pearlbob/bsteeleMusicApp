package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.shared.Util;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MusicConstant {
    public static final char flatChar = '\u266D';
    public static final char naturalChar = '\u266E';
    public static final char sharpChar = '\u266F';

    public static final String flatHtml = "&#9837;";
    public static final String naturalHtml = "&#9838;";
    public static final String sharpHtml = "&#9839;";

    public static final int halfStepsPerOctave = 12;
    public static final int notesPerScale = 7;


    //  has to be ahead of it's use since it's static
    private static final ChordDescriptor diatonicChordModifiers[] =
            {
                    ChordDescriptor.major,      //  0 + 1 = 1
                    ChordDescriptor.minor,      //  1 + 1 = 2
                    ChordDescriptor.minor,      //  2 + 1 = 3
                    ChordDescriptor.major,      //  3 + 1 = 4
                    ChordDescriptor.dominant7,  //  4 + 1 = 5
                    ChordDescriptor.minor,      //  5 + 1 = 6
                    ChordDescriptor.minor7b5,   //  6 + 1 = 7
            };

    public static final ChordDescriptor getDiatonicChordModifier(int i) {
        return diatonicChordModifiers[Util.mod(i, diatonicChordModifiers.length)];
    }
}
