/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.events.HomeTabEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.HomeTabEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongRemoveEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongRemoveEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.home.AppTab;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

/**
 * @author bob
 */
public class SongEditPresenterWidget extends PresenterWidget<SongEditPresenterWidget.MyView>
        implements SongUpdateEventHandler,
        SongSubmissionEventHandler,
        SongRemoveEventHandler,
        HomeTabEventHandler {


    public interface MyView extends View {

        public HandlerRegistration SongUpdateEventHandler(
                SongUpdateEventHandler handler);

        public HandlerRegistration SongSubmissionEventHandler(
                SongSubmissionEventHandler handler);

        public HandlerRegistration SongRemoveEventHandler(
                SongRemoveEventHandler handler);

        void onSongUpdate(Song song);

        public void measureFocus();

        void setActive(boolean isActive);
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
        eventBus.addHandler(SongUpdateEvent.TYPE, this);

        view.SongUpdateEventHandler(this);
        view.SongSubmissionEventHandler(this);
        view.SongRemoveEventHandler(this);
        eventBus.addHandler(HomeTabEvent.TYPE, this);
    }


    @Override
    public void onSongSubmission(SongSubmissionEvent event) {
        eventBus.fireEvent(event);
    }

    @Override
    public void onSongUpdate(SongUpdateEvent event) {
        song = event.getSongUpdate().getSong();
        if (active)
            view.onSongUpdate(song);
    }


    @Override
    public void onSongRemove(SongRemoveEvent event) {
        eventBus.fireEvent(event);
    }


    @Override
    public void onHomeTab(HomeTabEvent event) {
        boolean wasActive = active;
        active = (event.getTab() == AppTab.edit);
        view.setActive(active);
        if (!wasActive && active)
            view.onSongUpdate(song);
    }


    private final EventBus eventBus;
    private final MyView view;
    private boolean active = false;
    private Song song;
}
