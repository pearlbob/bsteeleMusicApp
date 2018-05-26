/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.application.events;

import com.bsteele.bsteeleMusicApp.client.SongUpdate;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author bob
 */
public class AllSongWriteEvent extends GwtEvent<AllSongWriteEventHandler> {

    public static Type<AllSongWriteEventHandler> TYPE = new Type<AllSongWriteEventHandler>();

    public AllSongWriteEvent() {
    }

    @Override
    public Type<AllSongWriteEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AllSongWriteEventHandler handler) {
        handler.onAllSongWrite(this);
    }
}
