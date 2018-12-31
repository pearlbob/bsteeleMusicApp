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
public class SongRemoveEvent extends GwtEvent<SongRemoveEventHandler> {

    public static Type<SongRemoveEventHandler> TYPE = new Type<SongRemoveEventHandler>();

    private final ArrayList<Song> songs;

    public SongRemoveEvent(Song song) {
        songs = new ArrayList<Song>();
        songs.add(song);
    }

    public SongRemoveEvent(ArrayList<Song> songs) {
        this.songs = songs;
    }

    @Override
    public Type<SongRemoveEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SongRemoveEventHandler handler) {
        handler.onSongRemove(this);
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }
}
