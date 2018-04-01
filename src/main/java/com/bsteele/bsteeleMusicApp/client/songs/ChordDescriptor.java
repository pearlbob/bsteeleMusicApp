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
    major("");

    ChordDescriptor(String shortName) {
        this.shortName = shortName;
    }

    public static ChordDescriptor parse(String s) {
        if (s != null && s.length() > 0) {
            for (ChordDescriptor cd : ChordDescriptor.values()) {
                if (cd.getShortName().length() > 0 && s.startsWith(cd.getShortName())) {
                    return cd;
                }
            }
        }
        return ChordDescriptor.major; //  chord without modifier short name
    }

    public String getShortName() {
        return shortName;
    }

    public static final String getRegExp() {
        return regExp;
    }

    private String shortName;
    private static final String regExp;

    static {
        //  build the regexpression to find this class while parsing
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        boolean first = true;
        for (ChordDescriptor cd : ChordDescriptor.values()) {
            if (first)
                first = false;
            else
                sb.append("|");
            sb.append(cd.getShortName());
        }
        sb.append(")");
        regExp = sb.toString();
    }

}
