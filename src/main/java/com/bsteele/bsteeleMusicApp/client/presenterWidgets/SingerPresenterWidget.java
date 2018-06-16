/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.songs.SongUpdate;
import com.bsteele.bsteeleMusicApp.client.application.events.MusicAnimationEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.MusicAnimationEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEventHandler;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

/**
 * @author bob
 */
public class SingerPresenterWidget extends PresenterWidget<SingerPresenterWidget.MyView>
        implements
        SongUpdateEventHandler,
        MusicAnimationEventHandler {


    public interface MyView extends View {

        void onSongUpdate(SongUpdate songUpdate);

        void onMusicAnimationEvent(MusicAnimationEvent event);
    }

    @Inject
    SingerPresenterWidget(final EventBus eventBus,
                          final MyView view) {
        super(eventBus, view);

        this.eventBus = eventBus;
        this.view = view;
    }

    @Override
    protected void onBind() {
        eventBus.addHandler(SongUpdateEvent.TYPE, this);
        eventBus.addHandler(MusicAnimationEvent.TYPE, this);
    }

    @Override
    public void onSongUpdate(SongUpdateEvent event) {
        view.onSongUpdate(event.getSongUpdate());
    }

    @Override
    public void onMusicAnimationEvent(MusicAnimationEvent event) {
        if (isVisible())
            view.onMusicAnimationEvent(event);
    }

    private final EventBus eventBus;
    private final MyView view;
}
