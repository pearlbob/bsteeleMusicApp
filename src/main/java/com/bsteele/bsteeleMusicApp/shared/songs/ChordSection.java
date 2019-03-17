package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.Grid;
import com.bsteele.bsteeleMusicApp.shared.util.Util;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * A chord section of a song is typically a collection of measures
 * that constitute a portion of the song that is considered musically a unit.
 * Immutable.
 * <p>
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ChordSection extends MeasureNode implements Comparable<ChordSection> {
    public ChordSection(@Nonnull SectionVersion sectionVersion, ArrayList<Phrase> measureSequenceItems) {
        this.sectionVersion = sectionVersion;
        this.phrases = (measureSequenceItems != null ? measureSequenceItems : new ArrayList<>());
    }

    public ChordSection(SectionVersion sectionVersion) {
        this.sectionVersion = sectionVersion;
        this.phrases = new ArrayList<>();
    }

    @Override
    public boolean isSingleItem() {
        return false;
    }

    final static ChordSection parse(String s, int beatsPerBar) {
        return parse(new StringBuffer(s), beatsPerBar);
    }

    public final static ChordSection parse(StringBuffer sb, int beatsPerBar) {
        if (sb == null || sb.length() <= 0)
            return null;

        Util.stripLeadingWhitespace(sb);    //  includes newline
        if (sb.length() <= 0)
            return null;

        SectionVersion sectionVersion = SectionVersion.parse(sb);
        if (sectionVersion == null) {
            //  cope with badly formatted songs
            sectionVersion = new SectionVersion(Section.verse);
        }

        ArrayList<Phrase> measureSequenceItems = new ArrayList<>();
        ArrayList<Measure> measures = new ArrayList<>();
        ArrayList<Measure> lineMeasures = new ArrayList<>();
        boolean repeatMarker = false;
        Measure lastMeasure = null;
        for (int i = 0; i < 2000; i++)          //  arbitrary safety hard limit
        {

            Util.stripLeadingSpaces(sb);
            if (sb.length() <= 0)
                break;

            //  quit if next section found
            if (Section.lookahead(sb))
                break;

            //  look for a repeat marker
            if (sb.charAt(0) == '|') {
                if (!measures.isEmpty()) {
                    //  add measures prior to the repeat to the output
                    measureSequenceItems.add(new Phrase(measures, measureSequenceItems.size()));
                    measures = new ArrayList<>();
                }
                repeatMarker = true;
                sb.delete(0, 1);
                continue;
            }

            //  look for a repeat end
            if (sb.charAt(0) == 'x') {
                repeatMarker = false;
                sb.delete(0, 1);

                //  look for repeat count
                Util.stripLeadingSpaces(sb);
                if (sb == null || sb.length() <= 0)
                    break;

                final RegExp oneOrMoreDigitsRegexp = RegExp.compile("^(\\d+)");
                MatchResult mr = oneOrMoreDigitsRegexp.exec(sb.toString());
                if (mr != null) {
                    if (!measures.isEmpty()) {
                        //  add measures prior to the single line repeat to the output
                        measureSequenceItems.add(new Phrase(measures, measureSequenceItems.size()));
                        measures = new ArrayList<>();
                    }
                    String ns = mr.getGroup(1);
                    sb.delete(0, ns.length());
                    int repeatTotal = Integer.parseInt(ns);
                    measureSequenceItems.add(new MeasureRepeat(lineMeasures, measureSequenceItems.size(), repeatTotal));
                    lineMeasures = new ArrayList<>();
                }
                continue;
            }

            //  use a newline as the default repeat marker
            if (sb.charAt(0) == '\n') {
                if (!repeatMarker) {
                    //  add line of measures to output collection
                    for (Measure m : lineMeasures)
                        measures.add(m);
                    lineMeasures = new ArrayList<>();
                }
                //  consume the newline
                sb.delete(0, 1);
                continue;
            }

            {
                //  add a measure to the current line measures
                Measure measure = Measure.parse(sb, beatsPerBar, lastMeasure);
                if (measure != null) {
                    lineMeasures.add(measure);
                    lastMeasure = measure;
                    continue;
                }
            }
            {
                //  look for a block repeat
                MeasureRepeat measureRepeat = MeasureRepeat.parse(sb, measureSequenceItems.size(), beatsPerBar);
                if (measureRepeat != null) {
                    //  don't assume every line has an eol
                    for (Measure m : lineMeasures)
                        measures.add(m);
                    lineMeasures = new ArrayList<>();
                    if (!measures.isEmpty()) {
                        measureSequenceItems.add(new Phrase(measures, measureSequenceItems.size()));
                    }
                    measures = new ArrayList<>();
                    measureSequenceItems.add(measureRepeat);
                    continue;
                }
            }
            {
                //  look for a comment
                MeasureComment measureComment = MeasureComment.parse(sb);
                if (measureComment != null) {
                    lineMeasures.add(measureComment);
                    continue;
                } else
                    break;      //  fixme: not a measure, we're done
            }
        }

        //  don't assume every line has an eol
        for (Measure m : lineMeasures)
            measures.add(m);
        if (!measures.isEmpty()) {
            measureSequenceItems.add(new Phrase(measures, measureSequenceItems.size()));
        }

        ChordSection ret = new ChordSection(sectionVersion, measureSequenceItems);
        return ret;
    }


    @Override
    public void addToGrid(@Nonnull Grid<MeasureNode> grid, @Nonnull ChordSection chordSection) {
        logger.finest("ChordSection.addToGrid()");

        if (phrases == null || phrases.isEmpty())
            //  initial editing
            grid.addTo(0, grid.getRowCount(), this);
        else
            for (Phrase measureSequenceItem : phrases) {
                grid.addTo(0, grid.getRowCount(), this);
                measureSequenceItem.addToGrid(grid, this);
            }
    }

    @Override
    public ArrayList<String> generateInnerHtml(Key key, int tran, boolean expandRepeats) {
        ArrayList<String> ret = new ArrayList<>();

        for (Phrase measureSequenceItem : phrases) {
            ArrayList<Measure> measures = measureSequenceItem.getMeasures();
            if (measures != null && !measures.isEmpty()) {
                MeasureNode lastMeasureNode = null;
                MeasureNode measureNode = null;
                int measuresOnThisLine = 0;
                for (int i = 0; i < measures.size(); i++) {
                    measureNode = measures.get(i);

                    if (measureNode.isSingleItem()) {
                        if (measureNode.equals(lastMeasureNode))
                            ret.add("-");
                        else
                            ret.addAll(measureNode.generateInnerHtml(key, tran, expandRepeats));
                        lastMeasureNode = measureNode;

                        if (measuresOnThisLine % MusicConstant.measuresPerDisplayRow ==
                                MusicConstant.measuresPerDisplayRow - 1) {
                            ret.add("\n");
                            lastMeasureNode = null;
                            measuresOnThisLine = 0;
                        } else
                            measuresOnThisLine++;
                    } else {
                        //  a group of measures (typically a repeat)
                        ret.addAll(measureNode.generateInnerHtml(key, tran, expandRepeats));
                        lastMeasureNode = null;
                        measuresOnThisLine = 0;
                    }
                }
            }
            ret.add("\n");
        }

        return ret;
    }

    public MeasureNode findMeasureNode(MeasureNode measureNode) {
        for (Phrase measureSequenceItem : getPhrases()) {
            if (measureSequenceItem == measureNode)
                return measureSequenceItem;
            MeasureNode mn = measureSequenceItem.findMeasureNode(measureNode);
            if (mn != null)
                return mn;
        }
        return null;
    }

    public int findMeasureNodeIndex(MeasureNode measureNode) {
        int index = 0;
        for (Phrase measureSequenceItem : getPhrases()) {
            int i = measureSequenceItem.findMeasureNodeIndex(measureNode);
            if (i >= 0)
                return index + i;
            index += measureSequenceItem.size();
        }
        return -1;
    }

    public Phrase findPhrase(MeasureNode measureNode) {
        for (Phrase phrase : getPhrases()) {
            if (phrase.contains(measureNode))
                return phrase;
        }
        return null;
    }

    public int indexOf(Phrase measureSequenceItem) {
        for (int i = 0; i < getPhrases().size(); i++) {
            Phrase msi = getPhrases().get(i);
            if (measureSequenceItem == msi)
                return i;
        }
        return -1;
    }

    public final Measure getMeasure(int phraseIndex, int measureIndex) {
        try {
            Phrase phrase = getPhrase(phraseIndex);
            return phrase.getMeasure(measureIndex);
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            return null;
        }
    }

    public final boolean deleteMeasure(int phraseIndex, int measureIndex) {
        try {
            return getPhrase(phraseIndex).delete(measureIndex) != null;
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            return false;
        }
    }


    public int getTotalMoments() {
        int total = 0;
        for (Phrase measureSequenceItem : phrases) {
            total += measureSequenceItem.getTotalMoments();
        }
        return total;
    }

    /**
     * Return the sectionVersion beats per minute
     * or null to default to the song BPM.
     *
     * @return the sectionVersion BPM or null
     */
    //public final Integer getBeatsPerMinute() {
    //    return bpm;
    //}

    /**
     * Return the sections's number of beats per bar or null to default to the song's number of beats per bar
     *
     * @return the number of beats per bar
     */
//    public final Integer getBeatsPerBar() {
//        return beatsPerBar;
//    }

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
    public int compareTo(ChordSection o) {
        return sectionVersion.compareTo(o.sectionVersion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChordSection that = (ChordSection) o;
        return Objects.equals(sectionVersion, that.sectionVersion)
                && Objects.equals(phrases, that.phrases)
                //&& Objects.equals(bpm, that.bpm)
                //&& Objects.equals(beatsPerBar, that.beatsPerBar)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionVersion.hashCode(), phrases.hashCode()
                //, bpm, beatsPerBar
        );
    }

    @Override
    public String getId() {
        return sectionVersion.getId();
    }

    public MeasureNode lastMeasureNode() {
        if (phrases == null || phrases.isEmpty())
            return this;
        Phrase measureSequenceItem = phrases.get(phrases.size() - 1);
        ArrayList<Measure> measures = measureSequenceItem.getMeasures();
        if (measures == null || measures.isEmpty())
            return measureSequenceItem;
        return measures.get(measures.size() - 1);
    }

    @Override
    public String transpose(@Nonnull Key key, int halfSteps) {
        StringBuilder sb = new StringBuilder();
        sb.append(getSectionVersion().toString());
        if (phrases != null)
            for (Phrase msi : phrases)
                sb.append(msi.transpose(key, halfSteps));
        return sb.toString();
    }

    @Override
    public String toMarkup() {
        StringBuilder sb = new StringBuilder();
        sb.append(getSectionVersion().toString()).append(" ");
        if (phrases != null)
            for (Phrase msi : phrases)
                sb.append(msi.toMarkup());
        return sb.toString();
    }

    @Override
    public String toString() {
        return toMarkup();
    }

    public final SectionVersion getSectionVersion() {
        return sectionVersion;
    }

    final ArrayList<Phrase> getPhrases() {
        return phrases;
    }

    final Phrase getPhrase(int index) throws IndexOutOfBoundsException {
        return phrases.get(index);
    }

    void setPhrases(ArrayList<Phrase> phrases) {
        this.phrases = phrases;
    }

    final public int getPhraseCount() {
        if (phrases == null)
            return 0;
        return phrases.size();
    }

    final public Phrase lastPhrase() {
        if (phrases == null)
            return null;
        return phrases.get(phrases.size() - 1);
    }

    final ChordSectionLocation getChordSectionLocation() {
        //  be lazy
        if (chordSectionLocation == null)
            chordSectionLocation = new ChordSectionLocation(sectionVersion);
        return chordSectionLocation;
    }

    @Override
    boolean isEmpty() {
        return phrases == null || phrases.isEmpty();
    }

    private final SectionVersion sectionVersion;
    private ArrayList<Phrase> phrases;
    private ChordSectionLocation chordSectionLocation;
    //private Integer bpm;
    //private Integer beatsPerBar;
    private static final Logger logger = Logger.getLogger(SectionVersion.class.getName());

}
