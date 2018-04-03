/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.events.DefaultDrumSelectEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.DefaultDrumSelectEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSelectionEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSelectionEventHandler;
import com.bsteele.bsteeleMusicApp.client.legacy.LegacyDrumMeasure;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * @author bob
 */
public class DrumOptionsView
        extends ViewImpl
        implements DrumOptionsPresenterWidget.MyView,
        HasHandlers
{
    interface Binder extends UiBinder<Widget, DrumOptionsView> {
    }

    @UiField
    SelectElement highHatSelect;

    @UiField
    SelectElement snareSelect;

    @UiField
    SelectElement kickSelect;

    @Inject
    DrumOptionsView(Binder binder) {
        initWidget(binder.createAndBindUi(this));

        handlerManager = new HandlerManager(this);

        Event.sinkEvents(highHatSelect, Event.ONCHANGE);
        Event.setEventListener(highHatSelect, (Event event) -> {
            if (Event.ONCHANGE == event.getTypeInt()) {
                drumMeasure.setHighHat(highHatSelect.getValue());
                fireEvent(new DefaultDrumSelectEvent(drumMeasure));
                logger.fine("highHatSelect: " + highHatSelect.getValue());
            }
        });

        Event.sinkEvents(snareSelect, Event.ONCHANGE);
        Event.setEventListener(snareSelect, (Event event) -> {
            if (Event.ONCHANGE == event.getTypeInt()) {
                drumMeasure.setSnare(snareSelect.getValue());
                fireEvent(new DefaultDrumSelectEvent(drumMeasure));
                logger.fine("snareSelect: " + snareSelect.getValue());
            }
        });


        Event.sinkEvents(kickSelect, Event.ONCHANGE);
        Event.setEventListener(kickSelect, (Event event) -> {
            if (Event.ONCHANGE == event.getTypeInt()) {
                drumMeasure.setKick(kickSelect.getValue());
                fireEvent(new DefaultDrumSelectEvent(drumMeasure));
                logger.fine("kickSelect: " + kickSelect.getValue());
            }
        });

        drumMeasure.setHighHat(highHatSelect.getValue());
        drumMeasure.setSnare(snareSelect.getValue());
        drumMeasure.setKick(kickSelect.getValue());
        fireEvent(new DefaultDrumSelectEvent(drumMeasure));
    }

    @Override
    public HandlerRegistration addSongSelectionEventHandler(
            SongSelectionEventHandler handler) {
        return handlerManager.addHandler(SongSelectionEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addDefaultDrumSelectEventHandler(DefaultDrumSelectEventHandler handler) {
        return handlerManager.addHandler(DefaultDrumSelectEvent.TYPE, handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    private LegacyDrumMeasure drumMeasure = new LegacyDrumMeasure();
    private final HandlerManager handlerManager;
    private static final Logger logger = Logger.getLogger(DrumOptionsView.class.getName());
}
