/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.application.events;

import com.bsteele.bsteeleMusicApp.client.songs.SongUpdate;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author bob
 */
public class SongUpdateEvent extends GwtEvent<SongUpdateEventHandler> {

    public static Type<SongUpdateEventHandler> TYPE = new Type<SongUpdateEventHandler>();

    private final SongUpdate songUpdate;

    public SongUpdateEvent(SongUpdate songUpdate) {
        this.songUpdate = songUpdate;
    }

    public SongUpdateEvent(Song song) {
        this.songUpdate = SongUpdate.createSongUpdate(song);
    }

    @Override
    public Type<SongUpdateEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SongUpdateEventHandler handler) {
        handler.onSongUpdate(this);
    }

    public SongUpdate getSongUpdate() {
        return songUpdate;
    }
}
