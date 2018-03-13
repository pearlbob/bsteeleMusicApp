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
public class SongSubmissionEvent extends GwtEvent<SongSubmissionEventHandler> {
  
    public static Type<SongSubmissionEventHandler> TYPE = new Type<SongSubmissionEventHandler>();

    private final Song song;

    public SongSubmissionEvent(Song song) {
        this.song = song;
}

    @Override
    public Type<SongSubmissionEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SongSubmissionEventHandler handler) {
        handler.onSongSubmission(this);
    }

    public Song getSong() {
        return song;
    }
}
