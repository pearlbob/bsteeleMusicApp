/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.application.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author bob
 */
public class NextSongEvent extends GwtEvent<NextSongEventHandler> {

    public static Type<NextSongEventHandler> TYPE = new Type<NextSongEventHandler>();

    public NextSongEvent() {
    }

    public NextSongEvent(boolean isForward) {
        this.isForward = isForward;
    }

    public boolean isForward() {
        return isForward;
    }

    @Override
    public Type<NextSongEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(NextSongEventHandler handler) {
        handler.onNextSong(this);
    }

    private boolean isForward = true;
}
