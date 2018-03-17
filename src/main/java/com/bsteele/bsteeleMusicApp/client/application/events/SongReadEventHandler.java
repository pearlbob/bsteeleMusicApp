/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.application.events;

import com.google.gwt.event.shared.EventHandler;

/**
 *
 * @author bob
 */
public interface SongReadEventHandler extends EventHandler {

  void onSongRead(SongReadEvent event);
}
