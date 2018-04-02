/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.application.events;

import com.bsteele.bsteeleMusicApp.client.SongUpdate;
import com.google.gwt.event.shared.GwtEvent;

/**
 *
 * @author bob
 */
public class SongUpdateEvent extends GwtEvent<SongUpdateEventHandler> {
  
    public static Type<SongUpdateEventHandler> TYPE = new Type<SongUpdateEventHandler>();

    private final SongUpdate songUpdate;

    public SongUpdateEvent(SongUpdate songUpdate) {
        this.songUpdate = songUpdate;
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
