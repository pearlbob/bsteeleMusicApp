package com.bsteele.bsteeleMusicApp.client.songs;

import javax.annotation.Nonnull;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class SongId implements Comparable<SongId> {
    public SongId() {
        this("UnknownSong");
    }

    public SongId(@Nonnull String songId) {
        this.songId = songId;
    }

    public  final String getSongId() {
        return songId;
    }

    private final String songId;

    /**
     * Compares this object with the specified object for order.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(SongId o) {
        return songId.compareTo(o.songId);
    }
}
