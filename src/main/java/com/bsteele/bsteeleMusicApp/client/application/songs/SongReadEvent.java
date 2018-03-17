/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.application.songs;

import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author bob
 */
public class SongReadEvent extends GwtEvent<SongReadEventHandler> {

    public static Type<SongReadEventHandler> TYPE = new Type<SongReadEventHandler>();

    private final Song song;

    public SongReadEvent(Song song) {
        this.song = song;
    }

    @Override
    public Type<SongReadEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SongReadEventHandler handler) {
        handler.onSongRead(this);
    }

    public Song getSong() {
        return song;
    }
}
