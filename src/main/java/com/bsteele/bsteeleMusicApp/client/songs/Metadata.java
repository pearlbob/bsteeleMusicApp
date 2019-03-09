package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

import com.bsteele.bsteeleMusicApp.client.util.JsonUtil;

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

    public final String toJson(){
        return  JsonUtil.encode(name)+"="+ JsonUtil.encode(value);
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
