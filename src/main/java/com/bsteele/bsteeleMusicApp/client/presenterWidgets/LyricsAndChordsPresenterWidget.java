/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.events.SongSelectionEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSelectionEventHandler;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

/**
 * @author bob
 */
public class LyricsAndChordsPresenterWidget extends PresenterWidget<LyricsAndChordsPresenterWidget.MyView>
        implements SongSelectionEventHandler {

    public interface MyView extends View {

        void setSong(Song song);
    }

    @Inject
    LyricsAndChordsPresenterWidget(final EventBus eventBus,
                                   final MyView view) {
        super(eventBus, view);

        this.eventBus = eventBus;
        this.view = view;
    }

    @Override
    protected void onBind() {
        eventBus.addHandler(SongSelectionEvent.TYPE, this);
    }

    @Override
    public void onSongSelection(SongSelectionEvent event) {
        view.setSong(event.getSong());
    }

    private final EventBus eventBus;
    private final MyView view;
}
