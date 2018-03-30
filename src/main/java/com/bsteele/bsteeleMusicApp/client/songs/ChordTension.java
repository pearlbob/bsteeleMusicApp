package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public enum ChordTension {
    //  longest short names first!
    //  7 included in ChordDescriptors
    t9("9"),
    t11("11"),
    t13("13"),
    none(""),
    ;

    ChordTension(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    private String shortName;
}
