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

    public String getShortName() {
        return shortName;
    }

    public static final String getRegExp() {
        return regExp;
    }

    private String shortName;
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
