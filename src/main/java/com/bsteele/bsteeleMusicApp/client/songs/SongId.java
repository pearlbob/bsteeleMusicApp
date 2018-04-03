package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class SongId {
    public SongId() {
        this("UnknownSong");
    }

    public SongId(String songId) {
        this.songId = songId;
    }

    public String getSongId() {
        return songId;
    }

    private final String songId;

}
