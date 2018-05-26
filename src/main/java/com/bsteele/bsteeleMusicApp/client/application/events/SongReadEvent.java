/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.application.events;

import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.google.gwt.event.shared.GwtEvent;

import java.util.ArrayList;

/**
 * @author bob
 */
public class SongReadEvent extends GwtEvent<SongReadEventHandler> {

    public static Type<SongReadEventHandler> TYPE = new Type<SongReadEventHandler>();

    private final ArrayList<Song> songs;

    public SongReadEvent(Song song) {
        songs = new ArrayList<Song>();
        songs.add(song);
    }

    public SongReadEvent(ArrayList<Song> songs) {
        this.songs = songs;
    }

    @Override
    public Type<SongReadEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SongReadEventHandler handler) {
        handler.onSongRead(this);
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }
}
