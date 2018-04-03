package com.bsteele.bsteeleMusicApp.client.application.events;

import com.bsteele.bsteeleMusicApp.client.legacy.LegacyDrumMeasure;
import com.google.gwt.event.shared.GwtEvent;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class DefaultDrumSelectEvent extends GwtEvent<DefaultDrumSelectEventHandler> {

    public static Type<DefaultDrumSelectEventHandler> TYPE = new Type<DefaultDrumSelectEventHandler>();

    private final LegacyDrumMeasure drumSelection;

    public DefaultDrumSelectEvent(LegacyDrumMeasure drumSelection) {
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

    public LegacyDrumMeasure getDrumSelection() {
        return drumSelection;
    }
}
