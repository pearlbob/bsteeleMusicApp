package com.bsteele.bsteeleMusicApp.shared.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

import java.text.ParseException;
import java.util.TreeSet;

/**
 * A small timing adjustment for a chord to change the feel of the chord.
 * Units are fractions of a beat expressed assuming quarter note beat duration.
 */
public enum ChordAnticipationOrDelay {
    /**
     * Play the chord on time.
     */
    none(""),
    /**
     * Anticipate (push) the chord by an 8th note duration.
     */
    anticipate8th("<8"),
    /**
     * Anticipate (push) the chord by a 16th note duration.
     * This is likely the most common form.
     */
    anticipate16th("<"),
    /**
     * Anticipate (push) the chord by one triplet's duration.
     */
    anticipateTriplet("<3"),
    /**
     * Delay (pull) the chord by an 8th note duration.
     */
    delay8th(">8"),
    /**
     * Delay (pull) the chord by a 16th note duration.
     */
    delay16th(">"),
    /**
     * Delay (pull) the chord by one triplet's duration.
     */
    delayTriplet(">3"),
    ;

    ChordAnticipationOrDelay(String shortName) {
        this.shortName = shortName;
    }

    public static final String generateGrammar() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t//\tChordAnticipationOrDelay\n");
        sb.append("\t(");
        boolean first = true;
        for (ChordAnticipationOrDelay chordAnticipationOrDelay : ChordAnticipationOrDelay.values()) {
            sb.append("\n\t\t");
            String s = chordAnticipationOrDelay.shortName;
            if (s.length() > 0) {
                if (first)
                    first = false;
                else
                    sb.append("| ");
                sb.append("\"").append(s).append("\"");
            }
            sb.append("\t//\t").append(chordAnticipationOrDelay.name());

        }
        sb.append("\n\t)");
        return sb.toString();
    }


    static ChordAnticipationOrDelay parse(StringBuffer sb)
            throws ParseException
    {
        if (sb == null)
            throw new ParseException("no data to parse", 0);
        if (sb.length() > 0)
            for (ChordAnticipationOrDelay a : sortedByShortNameLength) {
                if (sb.length() >= a.shortName.length() && a.shortName.equals(sb.substring(0, a.shortName.length()))) {
                    sb.delete(0, a.shortName.length());
                    return a;
                }
            }
        return none;
    }

    /**
     * Returns the human name of this enum.
     *
     * @return the human name of this enum constant
     */
    @Override
    public String toString() {
        return shortName;
    }

    private final String shortName;
    private static final TreeSet<ChordAnticipationOrDelay> sortedByShortNameLength = new TreeSet<>(
            (a1, a2) -> a2.shortName.compareTo(a1.shortName));

    static {
        for (ChordAnticipationOrDelay a : ChordAnticipationOrDelay.values())
            sortedByShortNameLength.add(a);
    }
}
