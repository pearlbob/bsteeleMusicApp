package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class Chord {

    /**
     * The description of the chord to be played.
     *
     * @return the scale chord
     */
    public ScaleChord getScaleChord() {
        return scaleChord;
    }

    /**
     * The description of the chord to be played.
     *
     * @param scaleChord the scale chord
     */
    public void setScaleChord(ScaleChord scaleChord) {
        this.scaleChord = scaleChord;
    }

    /**
     * Duration of the chord in beats
     *
     * @return the beat count
     */
    public int getBeats() {
        return beats;
    }

    /**
     * Duration of the chord in beats
     *
     * @param beats the beat count
     */
    public void setBeats(int beats) {
        this.beats = beats;
    }

    /**
     * The matching slash chord for this chord.
     * Typically is is the bass inversion.
     *
     * @return
     */
    public ScaleChord getSlashScaleChord() {
        return slashScaleChord;
    }

    /**
     * The matching slash chord for this chord.
     * Typically is is the bass inversion.
     *
     * @param slashScaleChord
     */
    public void setSlashScaleChord(ScaleChord slashScaleChord) {
        this.slashScaleChord = slashScaleChord;
    }

    /**
     * Small timing adjustment to alter the feel of the chord.
     *
     * @return the timing adjustment
     */
    public AnticipationOrDelay getAnticipationOrDelay() {
        return anticipationOrDelay;
    }

    /**
     * Small timing adjustment to alter the feel of the chord
     *
     * @param anticipationOrDelay the timing adjustment
     */
    public void setAnticipationOrDelay(AnticipationOrDelay anticipationOrDelay) {
        this.anticipationOrDelay = anticipationOrDelay;
    }

    private ScaleChord scaleChord;
    private int beats = 4;    //  default only, a typical full measure
    private ScaleChord slashScaleChord;
    private AnticipationOrDelay anticipationOrDelay = AnticipationOrDelay.none;
}
