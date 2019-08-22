package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.Grid;
import com.bsteele.bsteeleMusicApp.shared.util.MarkedString;
import com.bsteele.bsteeleMusicApp.shared.util.Util;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import javax.annotation.Nonnull;
import java.text.ParseException;
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
    public ChordSection(@Nonnull SectionVersion sectionVersion, ArrayList<Phrase> phrases) {
        this.sectionVersion = sectionVersion;
        this.phrases = (phrases != null ? phrases : new ArrayList<>());
    }

    public ChordSection(SectionVersion sectionVersion) {
        this.sectionVersion = sectionVersion;
        this.phrases = new ArrayList<>();
    }

    public static final ChordSection getDefault() {
        return new ChordSection(SectionVersion.getDefault(), null);
    }

    @Override
    public boolean isSingleItem() {
        return false;
    }

    final static ChordSection parse(String s, int beatsPerBar)
            throws ParseException {
        return parse(new MarkedString(s), beatsPerBar, false);
    }

    final static ChordSection parse(MarkedString markedString, int beatsPerBar, boolean strict)
            throws ParseException {
        if (markedString == null || markedString.isEmpty())
            throw new ParseException("no data to parse", 0);

        Util.stripLeadingWhitespace(markedString);    //  includes newline
        if (markedString.isEmpty())
            throw new ParseException("no data to parse", 0);

        SectionVersion sectionVersion;
        try {
            sectionVersion = SectionVersion.parse(markedString);
        } catch (ParseException pex) {
            if (strict)
                throw pex;

            //  cope with badly formatted songs
            sectionVersion = new SectionVersion(Section.verse);
        }

        ArrayList<Phrase> phrases = new ArrayList<>();
        ArrayList<Measure> measures = new ArrayList<>();
        ArrayList<Measure> lineMeasures = new ArrayList<>();
        boolean repeatMarker = false;
        Measure lastMeasure = null;
        for (int i = 0; i < 2000; i++)          //  arbitrary safety hard limit
        {
            Util.stripLeadingWhitespace(markedString);
            if (markedString.isEmpty())
                break;

            //  quit if next section found
            if (Section.lookahead(markedString))
                break;

            //  look for a repeat marker
            if (markedString.charAt(0) == '|') {
                if (!measures.isEmpty()) {
                    //  add measures prior to the repeat to the output
                    phrases.add(new Phrase(measures, phrases.size()));
                    measures = new ArrayList<>();
                }
                repeatMarker = true;
                markedString.consume(1);
                continue;
            }

            //  look for a repeat end
            if (markedString.charAt(0) == 'x') {
                repeatMarker = false;
                markedString.consume(1);

                //  look for repeat count
                Util.stripLeadingSpaces(markedString);
                if (markedString == null || markedString.isEmpty())
                    break;

                final RegExp oneOrMoreDigitsRegexp = RegExp.compile("^(\\d+)");
                MatchResult mr = oneOrMoreDigitsRegexp.exec(markedString.toString());
                if (mr != null) {
                    if (!measures.isEmpty()) {
                        //  add measures prior to the single line repeat to the output
                        phrases.add(new Phrase(measures, phrases.size()));
                        measures = new ArrayList<>();
                    }
                    String ns = mr.getGroup(1);
                    markedString.consume(ns.length());
                    int repeatTotal = Integer.parseInt(ns);
                    phrases.add(new MeasureRepeat(lineMeasures, phrases.size(), repeatTotal));
                    lineMeasures = new ArrayList<>();
                }
                continue;
            }

            //  use a newline as the default repeat marker
            if (markedString.charAt(0) == '\n') {
                if (!repeatMarker) {
                    //  add line of measures to output collection
                    for (Measure m : lineMeasures)
                        measures.add(m);
                    lineMeasures = new ArrayList<>();
                }
                //  consume the newline
                markedString.consume(1);
                continue;
            }


            try {
                //  look for a block repeat
                MeasureRepeat measureRepeat = MeasureRepeat.parse(markedString, phrases.size(), beatsPerBar, null);
                if (measureRepeat != null) {
                    //  don't assume every line has an eol
                    for (Measure m : lineMeasures)
                        measures.add(m);
                    lineMeasures = new ArrayList<>();
                    if (!measures.isEmpty()) {
                        phrases.add(new Phrase(measures, phrases.size()));
                    }
                    measureRepeat.setPhraseIndex(phrases.size());
                    phrases.add(measureRepeat);
                    measures = new ArrayList<>();
                    lastMeasure = null;
                    continue;
                }
            } catch (ParseException pex) {
                //  ignore
            }

            try {
                //  look for a phrase
                Phrase phrase = Phrase.parse(markedString, phrases.size(), beatsPerBar, null);
                if (phrase != null) {
                    //  don't assume every line has an eol
                    for (Measure m : lineMeasures)
                        measures.add(m);
                    lineMeasures = new ArrayList<>();
                    if (!measures.isEmpty()) {
                        phrases.add(new Phrase(measures, phrases.size()));
                    }
                    phrase.setPhraseIndex(phrases.size());
                    phrases.add(phrase);
                    measures = new ArrayList<>();
                    lastMeasure = null;
                    continue;
                }
            } catch (ParseException pex) {
                //  ignore
            }

            try {
                //  add a measure to the current line measures
                Measure measure = Measure.parse(markedString, beatsPerBar, lastMeasure);
                lineMeasures.add(measure);
                lastMeasure = measure;
                continue;
            } catch (ParseException pex) {
                //  ignore
            }

            //  consume unused commas
            {
                final RegExp commaRegexp = RegExp.compile(followingCommaRegexpPattern);
                String s = markedString.remainingStringLimited(10);
                logger.finest("s: " + s);
                MatchResult mr = commaRegexp.exec(s);
                if (mr != null) {
                    markedString.consume(mr.getGroup(0).length());
                    continue;
                }
            }

            try {
                //  look for a comment
                MeasureComment measureComment = MeasureComment.parse(markedString);
                lineMeasures.add(measureComment);
                continue;
            } catch (ParseException pex) {
                //  ignore
            }

            //  chordSection has no choice, force junk into a comment
            {
                int n = markedString.indexOf("\n");    //  all comments end at the end of the line
                String s = "";
                if (n > 0)
                    s = markedString.remainingStringLimited(n + 1);
                else
                    s = markedString.toString();

                final RegExp commentRegExp = RegExp.compile("^(\\S+)\\s+");
                MatchResult mr = commentRegExp.exec(s);

                //  consume the comment
                if (mr != null) {
                    s = mr.getGroup(1);
                    markedString.consume(mr.getGroup(0).length());
                    //  cope with unbalanced leading ('s and trailing )'s
                    s = s.replaceAll("^\\(", "").replaceAll("\\)$", "");
                    s = s.trim();   //  in case there is white space inside unbalanced parens

                    MeasureComment measureComment = new MeasureComment(s);
                    lineMeasures.add(measureComment);
                    continue;
                } else
                    logger.info("here: " + s);
            }
            logger.info("can't figure out: " + markedString.toString());
            throw new ParseException("can't figure out: " + markedString.toString(), 0);   //  all whitespace
        }

        //  don't assume every line has an eol
        for (Measure m : lineMeasures)
            measures.add(m);
        if (!measures.isEmpty()) {
            phrases.add(new Phrase(measures, phrases.size()));
        }

        ChordSection ret = new ChordSection(sectionVersion, phrases);
        return ret;
    }


    boolean add(int index, MeasureNode newMeasureNode) {
        if (newMeasureNode == null)
            return false;

        switch (newMeasureNode.getMeasureNodeType()) {
            case repeat:
            case phrase:
                break;
            default:
                return false;
        }

        Phrase newPhrase = (Phrase) newMeasureNode;

        if (phrases == null)
            phrases = new ArrayList<>();

        if (phrases.isEmpty()) {
            phrases.add(newPhrase);
            return true;
        }

        try {
            phrases.add(index, newPhrase);
        } catch (IndexOutOfBoundsException ex) {
            phrases.add(newPhrase);   //  default to the end!
        }
        return true;
    }


    boolean insert(int index, MeasureNode newMeasureNode) {
        if (newMeasureNode == null)
            return false;

        switch (newMeasureNode.getMeasureNodeType()) {
            case repeat:
            case phrase:
                break;
            default:
                return false;
        }

        Phrase newPhrase = (Phrase) newMeasureNode;

        if (phrases == null)
            phrases = new ArrayList<>();

        if (phrases.isEmpty()) {
            phrases.add(newPhrase);
            return true;
        }

        try {
            phrases.add(index, newPhrase);
        } catch (IndexOutOfBoundsException ex) {
            phrases.add(newPhrase);   //  default to the end!
        }
        return true;
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
        for (Phrase phrase : getPhrases()) {
            int i = phrase.findMeasureNodeIndex(measureNode);
            if (i >= 0)
                return index + i;
            index += phrase.size();
        }
        return -1;
    }

    public Phrase findPhrase(MeasureNode measureNode) {
        for (Phrase phrase : getPhrases()) {
            if (phrase == measureNode || phrase.contains(measureNode))
                return phrase;
        }
        return null;
    }

    public int findPhraseIndex(MeasureNode measureNode) {
        for (int i = 0; i < getPhrases().size(); i++) {
            Phrase p = getPhrases().get(i);
            if (measureNode == p || p.contains(measureNode))
                return i;
        }
        return -1;
    }

    public int indexOf(Phrase phrase) {
        for (int i = 0; i < getPhrases().size(); i++) {
            Phrase p = getPhrases().get(i);
            if (phrase == p)
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

    public final boolean deletePhrase(int phraseIndex) {
        try {
            return phrases.remove(phraseIndex) != null;
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            return false;
        }
    }

    public final boolean deleteMeasure(int phraseIndex, int measureIndex) {
        try {
            Phrase phrase = getPhrase(phraseIndex);
            boolean ret = phrase.delete(measureIndex) != null;
            if (ret && phrase.isEmpty())
                return deletePhrase(phraseIndex);
            return ret;
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
        if (sectionVersion.compareTo(o.sectionVersion) != 0)
            return sectionVersion.compareTo(o.sectionVersion);

        if (phrases.size() != o.phrases.size())
            return phrases.size() < o.phrases.size() ? -1 : 1;

        for (int i = 0; i < phrases.size(); i++) {
            int ret = phrases.get(i).toMarkup().compareTo(o.phrases.get(i).toMarkup());
            if (ret != 0)
                return ret;
        }
        return 0;
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

    @Override
    public MeasureNodeType getMeasureNodeType() {
        return MeasureNodeType.section;
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
            for (Phrase phrase : phrases)
                sb.append(phrase.transpose(key, halfSteps));
        return sb.toString();
    }

    @Override
    public String toMarkup() {
        StringBuilder sb = new StringBuilder();
        sb.append(getSectionVersion().toString()).append(" ");
        sb.append(phrasesToMarkup());
        return sb.toString();
    }

    public String phrasesToMarkup() {
        if (phrases == null || phrases.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        for (Phrase phrase : phrases)
            sb.append(phrase.toMarkup());
        return sb.toString();
    }

    @Override
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append(getSectionVersion().toString()).append("\n");
        if (phrases == null || phrases.isEmpty()) {
            sb.append("[]");
        } else
            for (Phrase phrase : phrases) {
                String s = phrase.toJson();
                sb.append(s);
                if (!s.endsWith("\n"))
                    sb.append("\n");
            }
        return sb.toString();
    }

    /**
     * Old style markup
     *
     * @return old style markup
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getSectionVersion().toString()).append("\n");
        if (phrases != null)
            for (Phrase phrase : phrases)
                sb.append(phrase.toString());
        return sb.toString();
    }

    public final SectionVersion getSectionVersion() {
        return sectionVersion;
    }

    public final Section getSection() {
        return sectionVersion.getSection();
    }

    public final ArrayList<Phrase> getPhrases() {
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


    @Override
    boolean isEmpty() {
        return phrases == null || phrases.isEmpty() || phrases.get(0).isEmpty();
    }

    private final SectionVersion sectionVersion;
    private ArrayList<Phrase> phrases;
    //private Integer bpm;
    //private Integer beatsPerBar;
    private static final Logger logger = Logger.getLogger(ChordSection.class.getName());

    private static final String followingCommaRegexpPattern = "^\\s*,";
}
