package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * The modifier to a chord specification that describes the basic type of chord.
 * Typical values are major, minor, dominant7, etc.
 */
public enum ChordDescriptor {
    //  longest short names first!
    //  avoid starting descriptors with b, #, s to avoid confusion with scale notes
    dominant13("13", "R 3 5 m7 9 11 13"),
    dominant11("11", "R 3 5 m7 9 11"),
    minor7b5("m7b5", "R m3 b5 m7"),
    add9("add9", "R 2 3 5 7"),
    jazz7b9("jazz7b9", "R m2 3 5"),
    sevenSharp5("7#5", "R 3 #5 m7"),
    flat5("flat5", "R 3 b5"),
    sevenFlat5("7b5", "R 3 b5 m7"),
    sevenSharp9("7#9", "R m3 5 m7"),
    sevenFlat9("7b9", "R m2 3 5 7"),
    dominant9("9", "R 3 5 m7 9"),
    major6("6", "R 3 5 6"),
    diminished7("dim7", "R m3 b5 6"),
    diminished("dim", "R m3 b5"),
    augmented5("aug5", "R 3 #5"),
    augmented7("aug7", "R 3 #5 m7"),
    augmented("aug", "R 3 #5"),
    suspended7("sus7", "R 5 m7"),
    suspended4("sus4", "R 4 5"),
    suspended2("sus2", "R 2 5"),
    suspended("sus", "R 5"),
    minor11("m11", "R m3 5 m7 11"),
    minor13("m13", "R m3 5 m7 13"),
    major7("maj7", "R 3 5 7"),
    majorSeven("M7", "R 3 5 7"),
    suspendedSecond("2", "R 2 5"),     //  alias for  suspended2
    suspendedFourth("4", "R 4 5"),      //  alias for suspended 4
    power5("5", "R 5"),  //  3rd omitted typically to avoid distortions
    minor7("m7", "R m3 5 m7"),
    dominant7("7", "R 3 5 m7"),
    minor("m", "r m3 5"),
    /**
     * Default chord descriptor.
     */
    major("", "r 3 5");

    ChordDescriptor(String shortName, String structure) {
        this.shortName = shortName;
        this.chordComponents = ChordComponent.parse(structure);
    }

    /**
     * Parse the start of the given string for a chord description.
     *
     * @param s the string to parse
     * @return the matching chord descriptor
     */
    public static final ChordDescriptor parse(String s) {
        if (s != null && s.length() > 0) {
            //  special for major7 thanks to John Coltrane
            if (s.charAt(0) == MusicConstant.greekCapitalDelta)
                return ChordDescriptor.major7;

            if (s.charAt(0) == MusicConstant.diminishedCircle)
                return ChordDescriptor.diminished;

            for (ChordDescriptor cd : ChordDescriptor.values()) {
                if (cd.getShortName().length() > 0 && s.startsWith(cd.getShortName())) {
                    return cd;
                }
            }
        }
        return ChordDescriptor.major; //  chord without modifier short name
    }

    public static final ChordDescriptor[] getOtherChordDescriptorsOrdered() {
        return otherChordDescriptorsOrdered;
    }

    public static final ChordDescriptor[] getPrimaryChordDescriptorsOrdered() {
        return primaryChordDescriptorsOrdered;
    }


    public static final ChordDescriptor[] getAllChordDescriptorsOrdered() {
        return allChordDescriptorsOrdered;
    }

    /**
     * The short name for the chord that typically gets used in human documentation such
     * as in the song lyrics or sheet music.  The name will never be null but can be empty.
     *
     * @return short, human readable name for the chord description.
     */
    public final String getShortName() {
        return shortName;
    }

    public final int getParseLength() {
        return shortName.length();
    }

    /**
     * Returns the human name of this enum.
     *
     * @return the human name of this enum constant
     */
    @Override
    public String toString() {
        if (shortName.length() == 0)
            return name();
        return shortName;
    }

    public final String chordComponentsToString() {
        StringBuilder sb = new StringBuilder();

        boolean first = true;
        for (ChordComponent cc : chordComponents) {
            if (first)
                first = false;
            else
                sb.append(" ");
            sb.append(cc.getShortName());
        }
        return sb.toString();
    }

    public final TreeSet<ChordComponent> getChordComponents() {
        return chordComponents;
    }


    private String shortName;
    private final TreeSet<ChordComponent> chordComponents;
    private static final ChordDescriptor[] primaryChordDescriptorsOrdered = {
            //  most common
            major,
            minor,
            dominant7,
    };
    private static final ChordDescriptor[] otherChordDescriptorsOrdered = {
            //  less pop by shortname
            add9,
            augmented,
            augmented5,
            augmented7,
            diminished,
            diminished7,
            jazz7b9,
            major7,
            majorSeven,
            minor11,
            minor13,
            minor7,
            minor7b5,
            flat5,
            sevenFlat5,
            sevenFlat9,
            sevenSharp5,
            sevenSharp9,
            suspended,
            suspended2,
            suspended4,
            suspendedFourth,
            suspended7,

            //  numerically named chords
            power5,
            major6,
            dominant9,
            dominant11,
            dominant13,
    };
    private static final ChordDescriptor[] allChordDescriptorsOrdered;

    static {
        //  compute the ordered list of all chord descriptors
        ArrayList<ChordDescriptor> list = new ArrayList<>();
        for (ChordDescriptor cd : primaryChordDescriptorsOrdered) {
            list.add(cd);
        }
        for (ChordDescriptor cd : otherChordDescriptorsOrdered) {
            list.add(cd);
        }
        allChordDescriptorsOrdered = list.toArray(new ChordDescriptor[0]);
    }

}
