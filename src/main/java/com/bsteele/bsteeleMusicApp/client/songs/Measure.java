package com.bsteele.bsteeleMusicApp.client.songs;

import javax.annotation.Nonnull;
import java.util.ArrayList;
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
public class Measure extends MeasureNode {

    /**
     * A convenience constructor to build a typical measure.
     *
     * @param beatCount the beat count for the measure
     * @param chords    the chords to be played over this measure
     */
    public Measure(@Nonnull SectionVersion sectionVersion, int beatCount, ArrayList<Chord> chords) {
        super(sectionVersion);
        setBeatCount(beatCount);
        setChords(chords);
    }

    public static final Measure parse(@Nonnull SectionVersion sectionVersion,
                                      String s, int beatsPerBar) {
        return parse(sectionVersion, s, beatsPerBar, null);
    }

    public static final Measure parse(@Nonnull SectionVersion sectionVersion,
                                      String s, int beatsPerBar, Measure lastMeasure) {
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
                    ret = new Measure(sectionVersion, beatsPerBar, emptyChordList);
                    parseLength += 1;
                    break;
                }
                //  see if this is a repeat measure
                if (s.charAt(0) == '-' && lastMeasure != null) {
                    ret = new Measure(sectionVersion, beatsPerBar, lastMeasure.getChords());
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
            ret = new Measure(sectionVersion, beatsPerBar, chords);

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
    public final int getBeatCount() {
        return beatCount;
    }

    /**
     * The beat count for this measure.
     *
     * @param beatCount
     */
    public final void setBeatCount(int beatCount) {
        this.beatCount = beatCount;
    }

    /**
     * The chords to be played over this measure.
     *
     * @return the chords
     */
    public final ArrayList<Chord> getChords() {
        return chords;
    }

    /**
     * The chords to be played over this measure
     *
     * @param chords the chords
     */
    public final void setChords(ArrayList<Chord> chords) {
        this.chords = chords;
    }

    public final Chord getChordAtBeat(double beat) {
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
    public ArrayList<Measure> getMeasures() {
        if (measures == null) {
            measures = new ArrayList<>();
            measures.add(this);
        }
        return measures;
    }

    @Override
    public String generateHtml(Key key, int tran) {
        StringBuilder sb = new StringBuilder();

        if (chords != null)
            for (Chord chord : chords) {
                sb.append(chord.transpose(key, tran));
            }
        return sb.toString();
    }

    public final boolean isRepeat() {
        return isRepeat;
    }

    private final String chordsToString() {
        StringBuilder sb = new StringBuilder();

        if (chords != null)
            for (Chord chord : chords) {
                sb.append(chord.toString());
            }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Measure measure = (Measure) o;
        return beatCount == measure.beatCount &&
                isRepeat == measure.isRepeat &&
                Objects.equals(chords, measure.chords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beatCount, isRepeat, chords);
    }

    @Override
    public String toString() {
        return chordsToString() + " ";
    }

    @Override
    public int getTotalMeasures() {
        return 1;
    }

    private int beatCount = 4;  //  default only
    private boolean isRepeat = false;
    private ArrayList<Chord> chords = new ArrayList<>();
    public static final ArrayList<Chord> emptyChordList = new ArrayList<>();
}
