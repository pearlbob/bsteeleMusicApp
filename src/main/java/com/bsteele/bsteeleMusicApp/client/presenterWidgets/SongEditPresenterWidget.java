/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.songs.SongSelectionEvent;
import com.bsteele.bsteeleMusicApp.client.application.songs.SongSelectionEventHandler;
import com.bsteele.bsteeleMusicApp.shared.Song;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

/**
 *
 * @author bob
 */
public class SongEditPresenterWidget extends PresenterWidget<SongEditPresenterWidget.MyView>
        implements SongSelectionEventHandler {

  public interface MyView extends View
  {
    void setSongEdit(Song song);
  }

  @Inject
  SongEditPresenterWidget(final EventBus eventBus,
          final MyView view
  ) {
    super(eventBus, view);

    this.eventBus = eventBus;
    this.view = view;
  }

  @Override
  public void onBind() {
    eventBus.addHandler(SongSelectionEvent.TYPE, this);
  }
  
  @Override
  public void onSongSelection(SongSelectionEvent event) {
    view.setSongEdit(event.getSong());
  }

  private final EventBus eventBus;
  private final MyView view;
}
