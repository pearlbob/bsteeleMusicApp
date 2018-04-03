package com.bsteele.bsteeleMusicApp.client.songs;

import java.util.ArrayList;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class Arrangement {
    private SongId songId;
    private ArrayList<Part> parts;

    public SongId getSongId() {
        return songId;
    }

    public void setSongId(SongId songId) {
        this.songId = songId;
    }

    public ArrayList<Part> getParts() {
        return parts;
    }

    public void setParts(ArrayList<Part> parts) {
        this.parts = parts;
    }
}
