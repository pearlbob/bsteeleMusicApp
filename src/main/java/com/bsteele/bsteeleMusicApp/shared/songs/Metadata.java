package com.bsteele.bsteeleMusicApp.shared.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

import javax.annotation.Nonnull;

/**
 * Additional, non-essential, information about a song
 * in the form of name/value pairs.
 * <p>The name should be unique.  Otherwise the old name/value pair will be over-written in a set.</p>
 */
public class Metadata implements Comparable<Metadata> {

    public enum MetadataType {
        generic,
        number,
        date,
        url;
    }

    public Metadata(@Nonnull String name, @Nonnull String value) {
        this(name, value, MetadataType.generic);
    }

    public Metadata(@Nonnull String name, @Nonnull String value, @Nonnull MetadataType type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    /**
     * Metadata name
     *
     * @return metadata name
     */
    public  final String getName() {
        return name;
    }

    /**
     * Metadata value.  Can be null.
     *
     * @return metadata value
     */
    public  final String getValue() {
        return value;
    }

    public  final MetadataType getType() {
        return type;
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
    public int compareTo(Metadata o) {
        int ret = name.compareTo(o.name);
        if (ret != 0)
            return ret;
        ret = type.compareTo(o.type);
        if (ret != 0)
            return ret;
        ret = value.compareTo(o.value);
        if (ret != 0)
            return ret;
        return 0;
    }

    private String name;
    private String value;
    private MetadataType type = MetadataType.generic;
}
