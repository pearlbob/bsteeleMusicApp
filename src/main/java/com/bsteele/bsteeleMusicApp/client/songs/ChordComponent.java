package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

import com.bsteele.bsteeleMusicApp.shared.Util;

import java.util.HashMap;
import java.util.TreeSet;

/**
 * Components of a chord expressed in a structured single note form.
 */
public enum ChordComponent {
    root("R", 1, 0),
    minorSecond("m2", 2, 1),
    second("2", 2, 2),
    minorThird("m3", 3, 3),
    third("3", 3, 4),
    fourth("4", 4, 5),
    flatFifth("b5", 5, 6),
    fifth("5", 5, 7),
    sharpFifth("#5", 5, 8),
    sixth("6", 6, 9),
    minorSeventh("m7", 7, 10),
    seventh("7", 7, 11),
    ninth("9", 9, 12 + 4),
    eleventh("11", 11, 12 + 7),
    thirteenth("13", 13, 12 + 11),;

    ChordComponent(String shortName, int scaleNumber, int halfSteps) {
        this.shortName = shortName;
        this.scaleNumber = scaleNumber;
        this.halfSteps = halfSteps;
    }

    public static TreeSet<ChordComponent> parse(String chordComponentString) {
        TreeSet<ChordComponent> ret = new TreeSet<>();
        for (String s : chordComponentString.split("[,. ]")) {
            if (s.length() <= 0)
                continue;

            ChordComponent cc = null;
            for (ChordComponent t : ChordComponent.values())
                if (t.shortName.equals(s)) {
                    cc = t;
                    break;
                }

            //  specials
            if (cc == null)
                switch (s) {
                    case "r":
                    case "1":
                        cc = root;
                        break;
                    case "m5":
                        cc = flatFifth;
                        break;
                    default:
                        throw new IllegalArgumentException("unknown component: <" + s + ">");
                }
            ret.add(cc);
        }
        return ret;
    }

    public static ChordComponent getByHalfStep(int halfStep) {
        return chordComponentByHalfSteps[Util.mod(halfStep, MusicConstant.halfStepsPerOctave)];
    }

    public int getHalfSteps() {
        return halfSteps;
    }

    public String getShortName() {
        return shortName;
    }


    public int getScaleNumber() {
        return scaleNumber;
    }

    private final String shortName;
    private final int scaleNumber;
    private final int halfSteps;

    private static final ChordComponent chordComponentByHalfSteps[] = {
            root,
            minorSecond,
            second,
            minorThird,
            third,
            fourth,
            flatFifth,
            fifth,
            sharpFifth,
            sixth,
            minorSeventh,
            seventh
    };

}

