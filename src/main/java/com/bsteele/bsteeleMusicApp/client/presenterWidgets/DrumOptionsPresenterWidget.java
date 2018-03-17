/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.events.DefaultDrumSelectEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.DefaultDrumSelectEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSelectionEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSelectionEventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 * Date: 3/16/18
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class DrumOptionsPresenterWidget extends PresenterWidget<DrumOptionsPresenterWidget.MyView>
        implements SongSelectionEventHandler,
        DefaultDrumSelectEventHandler
{


    public interface MyView extends View {

        HandlerRegistration addSongSelectionEventHandler(
                SongSelectionEventHandler handler);
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
        view.addSongSelectionEventHandler(this);
        view.addDefaultDrumSelectEventHandler(this);
    }

    @Override
    public void onSongSelection(SongSelectionEvent event) {

    }
    
    @Override
    public void onDefaultDrumSelection(DefaultDrumSelectEvent event) {
        eventBus.fireEvent(event);
    }

    private final EventBus eventBus;
    private final MyView view;
}
