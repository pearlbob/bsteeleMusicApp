package com.bsteele.bsteeleMusicApp.client.application.songs;

import com.bsteele.bsteeleMusicApp.client.songs.DrumMeasure;
import com.google.gwt.event.shared.GwtEvent;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class DefaultDrumSelectEvent extends GwtEvent<DefaultDrumSelectEventHandler> {

    public static Type<DefaultDrumSelectEventHandler> TYPE = new Type<DefaultDrumSelectEventHandler>();

    private final DrumMeasure drumSelection;

    public DefaultDrumSelectEvent(DrumMeasure drumSelection) {
        this.drumSelection = drumSelection;
    }

    @Override
    public Type<DefaultDrumSelectEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DefaultDrumSelectEventHandler handler) {
        handler.onDefaultDrumSelection(this);
    }

    public DrumMeasure getDrumSelection() {
        return drumSelection;
    }
}
