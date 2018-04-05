package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

import java.util.TreeSet;

/**
 * The chord tension that can be added to a chord specification.
 * Typically these are addition high notes in the next octave added to the base chord.
 */
public enum ChordTension {
    //  longest short names first!
    //  7 included in ChordDescriptors
    t9("9"),
    t11("11"),
    t13("13"),
    /**
     * the typical value
     */
    none(""),;

    ChordTension(String shortName) {
        this.shortName = shortName;
        chordComponents    =new  TreeSet<>();
        chordComponents.addAll( ChordComponent.parse(shortName));
    }

    public static ChordTension parse(String s) {
        if (s.length() > 0) {
            for (ChordTension ct : ChordTension.values()) {
                if (ct.getShortName().length() > 0 && s.startsWith(ct.getShortName())) {
                    return ct;
                }
            }
        }
        return ChordTension.none;
    }

    /**
     * The short name for the chord tension that typically gets used in human documentation such
     * as in the song lyrics or sheet music.  The name will never be null but can be empty.
     *
     * @return short, human readable name for the chord description.
     */
    public String getShortName() {
        return shortName;
    }


    public TreeSet<ChordComponent> getChordComponents() {
        return chordComponents;
    }

    /**
     * The RegExp expression to parse all chord tensions. It's possible there will be an empty match,
     * i.e. the chord tension none.
     *
     * @return the RegExp expression
     */
    public static final String getRegExp() {
        return "(|9|11|13)";
    }

    private String shortName;
    private final TreeSet<ChordComponent> chordComponents;

    static {
        //  complete all the components in the tensions
        //  tensions are cumulative
        ChordTension.t11.chordComponents.addAll(t9.getChordComponents());
        ChordTension.t13.chordComponents.addAll(t11.getChordComponents());
    }
}
