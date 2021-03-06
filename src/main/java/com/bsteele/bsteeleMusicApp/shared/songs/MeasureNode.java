package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.Grid;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * Base class for all measure node trees, i.e. the song chords.
 * Designed to simplify the walk of the song's sequence of measures and sequences of measures.
 * <p>
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public abstract class MeasureNode {

    /**
     * Returns true if the node is a single item and not a collection or measures.
     * This is tyically true if the node is a measure.
     *
     * @return true if the node is a single item
     */
    boolean isSingleItem() {
        return true;
    }

    /**
     * Return true if the node represents a collection of measures that are to be repeated a prescribed number of repeats.
     *
     * @return true if is a repeat
     */
    boolean isRepeat() {
        return false;
    }

    /**
     * Return true if the node is a comment.  This is typically false.
     *
     * @return true if a comment
     */
    boolean isComment() {
        return false;
    }

    /**
     * Return true if the measure node is a collection and it's empty
     *
     * @return if the measure node contains no measures
     */
    boolean isEmpty() {
        return false;
    }

    /**
     * Generate inner HTML for the node representation in the key and transform offet.
     *
     * @param key           the key the measure is in
     * @param tran          the number of transposition steps to take
     * @param expandRepeats true if repeats are to be expanded
     * @return the generated HTML
     */
    public abstract ArrayList<String> generateInnerHtml(@Nonnull Key key, int tran, boolean expandRepeats);


    /**
     * Transpose the measure node the given number of half steps from the given key.
     *
     * @param key       the original key
     * @param halfSteps the number of halfsteps to be transposed
     * @return a string representation of the transpositions
     */
    public abstract String transpose(@Nonnull Key key, int halfSteps);

    /**
     * If required, transpose the measure node to the given key.
     * This is used to represent the scale note(s) in the proper expression
     * of flats or sharps based on the key.
     * <p>Note that the key of C is considered sharp.</p>
     *
     * @param key the original key
     * @return the transposition
     */
    public abstract MeasureNode transposeToKey(@Nonnull Key key);

    /**
     * Represent the measure node to the user in a string form and from storage encoding.
     *
     * @return the string form created from the measure node's contents.
     */
    abstract public String toMarkup();

    /**
     * Represent the measure node to the user in a string form and entry ready.
     *
     * @return the string form created from the measure node's contents.
     */
    abstract public String toEntry();

    /**
     * Set the measures per row to the given value;
     * @param measuresPerRow the requested number of measures per row
     * @return true if adjustment was required
     */
    abstract boolean setMeasuresPerRow(int measuresPerRow );


    public static final String concatMarkup(ArrayList<MeasureNode> measureNodes) {
        StringBuilder sb = new StringBuilder();
        if (measureNodes != null)
            for (MeasureNode measureNode : measureNodes)
                sb.append(measureNode.toMarkup());
        return sb.toString();
    }

    /**
     * Override of the Java Object class equals() member.
     *
     * @param o the other measure node to compare to
     * @return true if the two measure nodes are equal
     */
    public abstract boolean equals(Object o);

    /**
     * Override of the Java Object class hashCode() member.
     *
     * @return the hashcode
     */
    public abstract int hashCode();


    /**
     * Gets the block identifier for this measure node type when expressed in HTML.
     *
     * @return the block id
     */
    public String getHtmlBlockId() {
        return "C";
    }

    /**
     * Export to JSON
     *
     * @return JSON formatted string without character escapes
     */
    public abstract String toJson();

    /**
     * The id to uniquely identify this measureNode in the song.
     *
     * @return the id
     */
    public abstract String getId();

    public enum MeasureNodeType {
        section,
        repeat,
        phrase,
        measure,
        comment,
        decoration;
    }

    public abstract MeasureNodeType getMeasureNodeType();

}
