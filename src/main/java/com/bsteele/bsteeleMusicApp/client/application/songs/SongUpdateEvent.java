/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.application.songs;

import com.bsteele.bsteeleMusicApp.client.Song;
import com.google.gwt.event.shared.GwtEvent;

/**
 *
 * @author bob
 */
public class SongUpdateEvent extends GwtEvent<SongUpdateEventHandler> {
  
    public static Type<SongUpdateEventHandler> TYPE = new Type<SongUpdateEventHandler>();

    private final Song song;

    public SongUpdateEvent(Song song) {
        this.song = song;
}

    @Override
    public Type<SongUpdateEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SongUpdateEventHandler handler) {
        handler.onSongUpdate(this);
    }

    public Song getSong() {
        return song;
    }
}
