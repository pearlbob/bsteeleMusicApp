/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.events.DefaultDrumSelectEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.DefaultDrumSelectEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class DrumOptionsPresenterWidget extends PresenterWidget<DrumOptionsPresenterWidget.MyView>
        implements SongUpdateEventHandler,
        DefaultDrumSelectEventHandler
{

    public interface MyView extends View {

        HandlerRegistration addSongUpdateEventHandler(
                SongUpdateEventHandler handler);
        HandlerRegistration addDefaultDrumSelectEventHandler(
                DefaultDrumSelectEventHandler handler);
    }

    @Inject
    DrumOptionsPresenterWidget(final EventBus eventBus,
                               final MyView view
    ) {
        super(eventBus, view);

        this.eventBus = eventBus;
        this.view = view;
    }

    @Override
    public void onBind() {
        view.addDefaultDrumSelectEventHandler(this);
    }


    @Override
    public void onSongUpdate(SongUpdateEvent event) {
        //  todo: adjust drum to song drum description
    }
    
    @Override
    public void onDefaultDrumSelection(DefaultDrumSelectEvent event) {
        eventBus.fireEvent(event);
    }

    private final EventBus eventBus;
    private final MyView view;
}
