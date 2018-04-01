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

    public String getShortName() {
        return shortName;
    }
    public static final String getRegExp() {
        return "(|9|11|13)";
    }

    private String shortName;
}
