package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class Chord {

    public Chord(ScaleChord scaleChord, int beats, int beatsPerBar, ScaleChord slashScaleChord, AnticipationOrDelay anticipationOrDelay) {
        this.scaleChord = scaleChord;
        this.beats = beatsPerBar;
        this.beatsPerBar = beatsPerBar;
        this.slashScaleChord = slashScaleChord;
        this.anticipationOrDelay = anticipationOrDelay;
    }

    public static final Chord parse(String s, int beatsPerBar) {
        if (s == null || s.length() <= 0)
            return null;

        int beats = beatsPerBar;  //  default only
        ScaleChord scaleChord = ScaleChord.parse(s);
        if (scaleChord == null)
            return null;
        int parseLength = scaleChord.getParseLength();
        s = s.substring(parseLength);
        ScaleChord slashScaleChord = null;
        if (s.length() > 0 && s.charAt(0) == '/') {
            s = s.substring(1);
            parseLength++;
            slashScaleChord = ScaleChord.parse(s);
            if (slashScaleChord != null) {
                parseLength += slashScaleChord.getParseLength();
                s = s.substring(slashScaleChord.getParseLength());
            }
        }
        if ( s.length() > 0 && s.charAt(0) == '.') {
            beats = 1;
            while (s.length() > 0 && s.charAt(0) == '.') {
                s = s.substring(1);
                parseLength++;
                beats++;
                if (beats >= 8)
                    break;
            }
        }
        Chord ret = new Chord(scaleChord, beats, beatsPerBar, slashScaleChord,
                AnticipationOrDelay.none);      //  fixme
        ret.parseLength = parseLength;
        return ret;
    }

    public Chord(ScaleChord scaleChord) {
        this(scaleChord, 4, 4, null, AnticipationOrDelay.none);
    }

    public Chord(ScaleChord scaleChord, int beats, int beatsPerBar) {
        this(scaleChord, beats, beatsPerBar, null, AnticipationOrDelay.none);
    }

    /**
     * The description of the chord to be played.
     *
     * @return the scale chord
     */
    public ScaleChord getScaleChord() {
        return scaleChord;
    }

//    /**
//     * The description of the chord to be played.
//     *
//     * @param scaleChord the scale chord
//     */
//    public void setScaleChord(ScaleChord scaleChord) {
//        this.scaleChord = scaleChord;
//    }

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
    void setSlashScaleChord(ScaleChord slashScaleChord) {
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


    public int getParseLength() {
        return parseLength;
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        String ret = scaleChord.toString()
                + (slashScaleChord == null ? "" : "/" + slashScaleChord.toString())
                + anticipationOrDelay.toString();
        if ( beats < beatsPerBar ) {
            int b = 1;
            while (b++ < beats && b < 8)
                ret += ".";
        }
        return ret;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Chord))
            return false;
        Chord oc = (Chord) o;

        if (slashScaleChord == null) {
            if (oc.slashScaleChord != null) return false;
        } else if (!slashScaleChord.equals(oc.slashScaleChord))
            return false;
        return scaleChord.equals(oc.scaleChord)
                && anticipationOrDelay.equals(oc.anticipationOrDelay)
                && beats == oc.beats
                ;
    }

    public int getBeatsPerBar() {
        return beatsPerBar;
    }

    public void setBeatsPerBar(int beatsPerBar) {
        this.beatsPerBar = beatsPerBar;
    }

    private ScaleChord scaleChord;
    private int beats = 4;    //  default only, a typical full measure
    private int beatsPerBar = beats;
    private ScaleChord slashScaleChord;
    private AnticipationOrDelay anticipationOrDelay = AnticipationOrDelay.none;
    private transient int parseLength;


}
