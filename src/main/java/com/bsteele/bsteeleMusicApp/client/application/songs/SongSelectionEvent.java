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
public class SongSelectionEvent extends GwtEvent<SongSelectionEventHandler> {
  
    public static Type<SongSelectionEventHandler> TYPE = new Type<SongSelectionEventHandler>();

    private final Song song;

    public SongSelectionEvent(Song song) {
        this.song = song;
}

    @Override
    public Type<SongSelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SongSelectionEventHandler handler) {
        handler.onSongSelection(this);
    }

    public Song getSong() {
        return song;
    }
}
