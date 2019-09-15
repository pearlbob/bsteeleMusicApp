package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.util.MarkedString;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import javax.annotation.Nonnull;
import java.text.ParseException;
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
public class Measure extends MeasureNode implements Comparable<Measure> {

    /**
     * A convenience constructor to build a typical measure.
     *
     * @param beatCount the beat count for the measure
     * @param chords    the chords to be played over this measure
     */
    public Measure(int beatCount, ArrayList<Chord> chords) {
        this.beatCount = beatCount;
        this.chords = chords;
        allocateTheBeats();
    }

    public Measure(Measure measure) {
        if (measure == null)
            return;

        this.beatCount = measure.beatCount;

        //  deep copy
        ArrayList<Chord> chords = new ArrayList<>(measure.chords.size());
        for (Chord chord : measure.chords) {
            chords.add(new Chord(chord));
        }
        this.chords = chords;

        this.endOfRow = measure.endOfRow;
    }

    protected Measure() {
        beatCount = 0;
    }

    /**
     * Convenience method for testing only
     *
     * @param s           input string
     * @param beatsPerBar beats per bar
     * @return the measure for the parsing
     * @throws ParseException thrown if parsing fails
     */
    static final Measure parse(String s, int beatsPerBar) throws ParseException {
        return parse(new MarkedString(s), beatsPerBar, null);
    }

//    static final Measure parse(MarkedString markedString, int beatsPerBar)
//            throws ParseException {
//        return parse(markedString, beatsPerBar, null);
//    }

    /**
     * Parse a measure from the input string
     *
     * @param markedString input string buffer
     * @param beatsPerBar  beats per bar
     * @param priorMeasure the prior measure, in case of -
     * @return the measure for the parsing
     * @throws ParseException thrown if parsing fails
     */
    static final Measure parse(final MarkedString markedString, final int beatsPerBar, final Measure priorMeasure)
            throws ParseException {
        //  should not be white space, even leading, in a measure

        if (markedString == null || markedString.isEmpty())
            throw new ParseException("no data to parse", 0);

        ArrayList<Chord> chords = new ArrayList<>();
        Measure ret = null;
        for (int i = 0; i < 32; i++)    //  safety
        {
            if (markedString.isEmpty())
                break;

            //  assure this is not a section
            if (Section.lookahead(markedString))
                break;

            int mark = markedString.mark();
            try {
                Chord chord = Chord.parse(markedString, beatsPerBar);
                chords.add(chord);
            } catch (ParseException pex) {
                markedString.resetTo(mark);

                //  see if this is a chord less measure
                if (markedString.charAt(0) == 'X') {
                    ret = new Measure(beatsPerBar, emptyChordList);
                    markedString.getNextChar();
                    break;
                }

                //  see if this is a repeat measure
                if (chords.isEmpty() && markedString.charAt(0) == '-' && priorMeasure != null) {
                    ret = new Measure(beatsPerBar, priorMeasure.getChords());
                    markedString.getNextChar();
                    break;
                }
                break;
            }
        }
        if (ret == null && chords.isEmpty())
            throw new ParseException("no chords found", 0);

        if (ret == null)
            ret = new Measure(beatsPerBar, chords);

        //  process end of row markers
        final RegExp sectionRegexp = RegExp.compile(followingNewLineOrCommaRegexpPattern);
        MatchResult mr = sectionRegexp.exec(markedString.toString());
        if (mr != null) {
            markedString.consume(mr.getGroup(0).length());
            ret.setEndOfRow(true);
        }

        return ret;
    }

    private void allocateTheBeats() {
        // allocate the beats
        //  try to deal with over-specified beats: eg. in 4/4:  E....A...
        if (chords.size() > 0) {
            //  find the total count of beats explicitly specified
            int explicitChords = 0;
            int explicitBeats = 0;
            for (Chord c : chords) {
                if (c.getBeats() < beatCount) {
                    explicitChords++;
                    explicitBeats += c.getBeats();
                }
            }
            //  verify not over specified
            if (explicitBeats + (chords.size() - explicitChords) > beatCount) // fixme: better failure
                return;    //  too many beats!  even if the unspecified chords only got 1

            //  verify not under specified
            if (chords.size() == explicitChords && explicitBeats < beatCount) {
                //  a short measure
                for (Chord c : chords) {
                    c.setImplicitBeats(false);
                }
                beatCount = explicitBeats;
                return;
            }


            if (explicitBeats == 0 && explicitChords == 0 && beatCount % chords.size() == 0) {
                //  spread the implicit beats evenly
                int implicitBeats = beatCount / chords.size();
                for (Chord c : chords) {
                    c.setBeats(implicitBeats);
                    c.setImplicitBeats(true);
                }
            } else {
                //  allocate the remaining beats to the unspecified chords
                //  give left over beats to the first unspecified
                int totalBeats = explicitBeats;
                if (chords.size() > explicitChords) {
                    Chord firstUnspecifiedChord = null;
                    int beatsPerUnspecifiedChord = Math.max(1, (beatCount - explicitBeats) / (chords.size() - explicitChords));
                    for (Chord c : chords) {
                        c.setImplicitBeats(false);
                        if (c.getBeats() == beatCount) {
                            if (firstUnspecifiedChord == null)
                                firstUnspecifiedChord = c;
                            c.setBeats(beatsPerUnspecifiedChord);
                            totalBeats += beatsPerUnspecifiedChord;
                        }
                    }
                    //  dump all the remaining beats on the first unspecified
                    if (firstUnspecifiedChord != null && totalBeats < beatCount) {
                        firstUnspecifiedChord.setImplicitBeats(false);
                        firstUnspecifiedChord.setBeats(beatsPerUnspecifiedChord + (beatCount - totalBeats));
                        totalBeats = beatCount;
                    }
                }
                if (totalBeats == beatCount) {
                    int b = chords.get(0).getBeats();
                    boolean allMatch = true;
                    for (Chord c : chords)
                        allMatch &= (c.getBeats() == b);
                    if (allMatch) {
                        //  reduce the over specification
                        for (Chord c : chords)
                            c.setImplicitBeats(true);
                    } else if (totalBeats > 1) {
                        //  reduce the over specification
                        for (Chord c : chords)
                            if (c.getBeats() == 1)
                                c.setImplicitBeats(true);
                    }
                }
            }
        }
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
     * The chords to be played over this measure.
     *
     * @return the chords
     */
    public final ArrayList<Chord> getChords() {
        return chords;
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
    public ArrayList<String> generateInnerHtml(@Nonnull Key key, int tran, boolean expandRepeats) {
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
    public String transpose(@Nonnull Key key, int halfSteps) {
        if (chords != null && !chords.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Chord chord : chords) {
                sb.append(chord.transpose(key, halfSteps).toString());
            }
            return sb.toString();
        }
        return "X";  // no chords
    }

    public final boolean isEasyGuitarMeasure() {
        return chords != null && chords.size() == 1 && chords.get(0).getScaleChord().isEasyGuitarChord();
    }


    public boolean isEndOfRow() {
        return endOfRow;
    }

    public void setEndOfRow(boolean endOfRow) {
        this.endOfRow = endOfRow;
    }

    @Override
    public String toMarkup() {
        return toMarkup(',');
    }

    @Override
    public String toJson() {
        return toMarkup('\000');
    }

    private final String toMarkup(char endOfRowChar) {
        if (chords != null && !chords.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Chord chord : chords) {
                sb.append(chord.toString());
            }
            if (endOfRowChar > 0 && isEndOfRow())
                sb.append(endOfRowChar);
            return sb.toString();
        }
        return "X";  // no chords
    }

    public static final Measure getDefaultMeasure() {
        return defaultMeasure;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Measure o) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Measure measure = (Measure) o;
        return beatCount == measure.beatCount &&
                Objects.equals(chords, measure.chords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beatCount, chords);
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public MeasureNodeType getMeasureNodeType() {
        return MeasureNodeType.measure;
    }

    @Override
    public String toString() {
        return toMarkup();
    }

    private int beatCount = 4;  //  default only
    private boolean endOfRow = false;
    private ArrayList<Chord> chords = new ArrayList<>();

    public static final ArrayList<Chord> emptyChordList = new ArrayList<>();
    public static final Measure defaultMeasure = new Measure(4, emptyChordList);

    private static final String followingNewLineOrCommaRegexpPattern = "^\\s*(,|\\n)";
}
