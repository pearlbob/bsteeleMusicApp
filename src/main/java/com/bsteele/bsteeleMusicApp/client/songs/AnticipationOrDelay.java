package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

/**
 * A small timing adjustment for a chord to change the feel of the chord.
 * Units are fractions of a beat expressed assuming quarter note beat duration.
 */
public enum AnticipationOrDelay {
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
    delayTriplet(">3"),;

    AnticipationOrDelay(String shortName) {
        this.shortName = shortName;
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
}
