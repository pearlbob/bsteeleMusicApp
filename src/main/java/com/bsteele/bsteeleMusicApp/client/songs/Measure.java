package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.Grid;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

/**
 * A measure in a section of a song.
 * Holds the lyrics, the chord changes and their beats.
 * <p>
 * When added, chord beat durations exceeding the measure beat count will be ignored on playback.
 * </p>
 */
public class Measure extends MeasureNode implements Comparable<Measure>
{

    /**
     * A convenience constructor to build a typical measure.
     *
     * @param beatCount the beat count for the measure
     * @param chords    the chords to be played over this measure
     */
    public Measure(int beatCount, ArrayList<Chord> chords)
    {
        setBeatCount(beatCount);
        setChords(chords);
    }

    public Measure(Measure measure)
    {
        if (measure == null)
            return;
        setBeatCount(measure.beatCount);

        //  deep copy
        ArrayList<Chord> chords = new ArrayList<>(measure.chords.size());
        for (Chord chord : measure.chords) {
            chords.add(new Chord(chord));
        }
        setChords(chords);
    }

    protected Measure()
    {
        setBeatCount(0);
    }

    public static final Measure parse(String s, int beatsPerBar)
    {
        return parse(s, beatsPerBar, null);
    }

    public static final Measure parse(String s, int beatsPerBar, Measure lastMeasure)
    {
        //  should not be white space, even leading, in a measure
        if (s == null || s.length() <= 0)
            return null;

        ArrayList<Chord> chords = new ArrayList<>();
        int parseLength = 0;
        Measure ret = null;
        for (int i = 0; i < 8; i++)    //  safety
        {
            if (s.length() <= 0)
                break;

            //  assure this is not a section
            if (Section.parse(s) != null)
                break;

            Chord chord = Chord.parse(s, beatsPerBar);
            if (chord == null) {
                //  see if this is a chord less measure
                if (s.charAt(0) == 'X') {
                    ret = new Measure(beatsPerBar, emptyChordList);
                    parseLength += 1;
                    break;
                }
                //  see if this is a repeat measure
                if (s.charAt(0) == '-' && lastMeasure != null) {
                    ret = new Measure(beatsPerBar, lastMeasure.getChords());
                    parseLength += 1;
                    break;
                }
                break;
            }
            s = s.substring(chord.getParseLength());

            parseLength += chord.getParseLength();
            chords.add(chord);
        }
        if (ret == null && chords.isEmpty())
            return null;

        //  distribute the slash chord to all
        if (chords.size() > 1) {
            ScaleChord slashScaleChord = chords.get(chords.size() - 1).getSlashScaleChord();
            if (slashScaleChord != null) {
                for (Chord chord : chords) {
                    chord.setSlashScaleChord(slashScaleChord);
                }
            }
        }

        if (ret == null)
            ret = new Measure(beatsPerBar, chords);

        // allocate the beats
        if (chords.size() == 2 && chords.get(0).getBeats() == 1 && chords.get(1).getBeats() == 1) {
            //  common case: split the beats even across the two chords
            //  bias to beat 1 on 3 beats to the bar
            int b2 = beatsPerBar / 2;
            chords.get(1).setBeats(b2);
            chords.get(0).setBeats(beatsPerBar - b2);
        }
        if (!chords.isEmpty()) {    // fill the beat count onto the last chord if required
            //  works for one chord as well
            int count = 0;
            for (Chord chord : chords)
                count += chord.getBeats();
            if (count < beatsPerBar) {
                Chord lastChord = chords.get(chords.size() - 1);
                lastChord.setBeats(lastChord.getBeats() + (beatsPerBar - count));
            }
        }

        ret.parseLength = parseLength;
        return ret;
    }

    /**
     * The beat count for the measure should be set prior to chord additions
     * to avoid awkward behavior when chords are added without a count.
     * Defaults to 4.
     *
     * @return the beat count for the measure
     */
    public final int getBeatCount()
    {
        return beatCount;
    }

    /**
     * The beat count for this measure.
     *
     * @param beatCount
     */
    public final void setBeatCount(int beatCount)
    {
        this.beatCount = beatCount;
    }

    /**
     * The chords to be played over this measure.
     *
     * @return the chords
     */
    public final ArrayList<Chord> getChords()
    {
        return chords;
    }

    /**
     * The chords to be played over this measure
     *
     * @param chords the chords
     */
    public final void setChords(ArrayList<Chord> chords)
    {
        this.chords = chords;
    }

    public final Chord getChordAtBeat(double beat)
    {
        if (chords == null || chords.isEmpty())
            return null;

        double beatSum = 0;
        for (Chord chord : chords) {
            beatSum += chord.getBeats();
            if (beat <= beatSum)
                return chord;
        }
        return chords.get(chords.size() - 1);
    }

    @Override
    public ArrayList<String> generateInnerHtml(@Nonnull Key key, int tran, boolean expandRepeats)
    {
        ArrayList<String> ret = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        if (chords != null)
            for (Chord chord : chords) {
                sb.append(chord.transpose(key, tran));
            }
        ret.add(sb.toString());

        return ret;
    }

    @Override
    public void addToGrid(@Nonnull Grid<MeasureNode> grid, @Nonnull ChordSection chordSection)
    {
        grid.add(this);
    }

    @Override
    public String transpose(@Nonnull Key key, int halfSteps)
    {
        StringBuilder sb = new StringBuilder();

        if (chords != null) {
            Chord lastChord = chords.get(chords.size() - 1);
            for (Chord chord : chords) {
                sb.append(chord == lastChord
                        ? chord.transpose(key, halfSteps).toString()
                        : chord.transpose(key, halfSteps).toStringWithoutInversion());
            }
        }
        return sb.toString();
    }

    private final String chordsToString()
    {
        StringBuilder sb = new StringBuilder();

        if (chords != null) {
            Chord lastChord = chords.get(chords.size() - 1);
            for (Chord chord : chords) {
                sb.append(chord == lastChord ? chord.toString() : chord.toStringWithoutInversion());
            }
        }
        return sb.toString();
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     *
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Measure o)
    {
        int limit = Math.min(chords.size(), o.chords.size());
        for (int i = 0; i < limit; i++) {
            int ret = chords.get(i).compareTo(o.chords.get(i));
            if (ret != 0)
                return ret;
        }
        if (chords.size() != o.chords.size())
            return chords.size() < o.chords.size() ? -1 : 1;
        if (beatCount != o.beatCount)
            return beatCount < o.beatCount ? -1 : 1;
        return 0;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Measure measure = (Measure) o;
        return beatCount == measure.beatCount &&
                Objects.equals(chords, measure.chords);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(beatCount, chords);
    }

    @Override
    public String toString()
    {
        return chordsToString();
    }

    private int beatCount = 4;  //  default only
    private ArrayList<Chord> chords = new ArrayList<>();
    public static final ArrayList<Chord> emptyChordList = new ArrayList<>();
}
