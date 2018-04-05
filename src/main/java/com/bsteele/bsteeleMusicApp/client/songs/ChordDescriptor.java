package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

import java.util.TreeSet;

/**
 * The modifier to a chord specification that describes the basic type of chord.
 * Typical values are major, minor, dominant7, etc.
 */
public enum ChordDescriptor {
    //  longest short names first!
    major7("maj7", "r 3 5 m7"),
    minor7b5("m7b5", "R m3 b5 m7"),
    minor7("m7", "R m3 5 m7"),
    dominant7("7", "R 3 5 m7"),
    major6("6", "r 3 5 6"),
    power5("5", "R 5"),  //  3rd omitted typically to avoid distortions
    diminished7("dim7", "R m3 m5 6"),
    diminished("dim", "R m3 m5"),
    augmented5("aug5", "R 3 #5"),
    augmented7("aug7", "R 3 #5 m7"),
    suspended7("sus7", "R 5 m7"),
    suspended4("sus4", "r 4 5"),
    suspended("sus", "r m2 5"),
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
    public static ChordDescriptor parse(String s) {
        if (s != null && s.length() > 0) {
            //  special for major7 thanks to John Coltrane
            if (s.startsWith(MusicConstant.greekCapitalDelta))
                return ChordDescriptor.major7;

            for (ChordDescriptor cd : ChordDescriptor.values()) {
                if (cd.getShortName().length() > 0 && s.startsWith(cd.getShortName())) {
                    return cd;
                }
            }
        }
        return ChordDescriptor.major; //  chord without modifier short name
    }

    /**
     * The short name for the chord that typically gets used in human documentation such
     * as in the song lyrics or sheet music.  The name will never be null but can be empty.
     *
     * @return short, human readable name for the chord description.
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * The RegExp expression to parse all chord descriptors. It's possible there will be an empty match,
     * i.e. the major chord descriptor.
     *
     * @return the RegExp expression
     */
    public static final String getRegExp() {
        return regExp;
    }

    /**
     * Returns the human name of this enum.
     *
     * @return the human name of this enum constant
     */
    @Override
    public String toString() {
        if ( shortName.length() == 0)
            return name();
        return shortName;
    }

    public String chordComponentsToString() {
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

    public TreeSet<ChordComponent> getChordComponents() {
        return chordComponents;
    }


    private String shortName;
    private final TreeSet<ChordComponent> chordComponents;
    private static final String regExp;


    static {
        //  build the regex expression to find this class while parsing
        StringBuilder sb = new StringBuilder();
        sb.append("(")
                .append(MusicConstant.greekCapitalDelta);   //  special for major7 thanks to John Coltrane

        for (ChordDescriptor cd : ChordDescriptor.values()) {
            sb.append("|");
            sb.append(cd.getShortName());
        }
        sb.append(")");
        regExp = sb.toString();
    }

}
