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
     * @return
     */
    boolean isRepeat() {
        return false;
    }

    /**
     * Return true if the node is a comment.  This is typically false.
     *
     * @return
     */
    boolean isComment() {
        return false;
    }

    /**
     * Generate inner HTML for the node representation in the key and transform offet.
     *
     * @param key
     * @param tran
     * @param expandRepeats
     * @return
     */
    public abstract ArrayList<String> generateInnerHtml(@Nonnull Key key, int tran, boolean expandRepeats);


    /**
     * Add the given chord section to the measure node.  Only makes sense if the node is a chord section or a measure sequence item.
     *
     * @param grid
     * @param chordSection
     */
    public abstract void addToGrid(@Nonnull Grid<MeasureNode> grid, @Nonnull ChordSection chordSection);

    /**
     * Transpose the measure node the given number of half steps from the given key.
     * @param key
     * @param halfSteps
     * @return
     */
    public abstract String transpose(@Nonnull Key key, int halfSteps);

    /** Represent the measure node to the user in a string form and from storage encoding.
     *
     * @return the string form created from the measure node's contents.
     */
    abstract  public String toMarkup();

    /**
     * Override of the Java Object class equals() member.
     * @param o the other measure node to compare to
     * @return true if the two measure nodes are equal
     */
    public abstract boolean equals(Object o);

    /**
     * Override of the Java Object class hashCode() member.
     * @return
     */
    public abstract int hashCode();


    /** Gets the block identifier for this measure node type when expressed in HTML.
     *
     * @return the block id
     */
    public String getHtmlBlockId() {
        return "C";
    }

    /**
     * The id to uniquely identify this measureNode in the song.
     *
     * @return the id
     */
    public abstract String getId();
}
