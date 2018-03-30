package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public enum ChordDescriptor {
    //  longest short names first!
    major7("maj7"),
    minor7b5("m7b5"),
    minor7("m7"),
    dominant7("7"),
    power5("5"),
    diminished7("dim7"),
    diminished("dim"),
    augmented5("aug5"),
    augmented7("aug7"),
    suspended7("sus7"),
    suspended("sus"),
    minor("m"),
    major("")
            ;

    ChordDescriptor(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    private String shortName;

}
