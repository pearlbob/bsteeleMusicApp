package com.bsteele.bsteeleMusicApp.client.application.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class StatusEvent extends GwtEvent<StatusEventHandler> {

    public static Type<StatusEventHandler> TYPE = new Type<StatusEventHandler>();

    private final String name;
    private final String value;

    public StatusEvent(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public Type<StatusEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(StatusEventHandler handler) {
        handler.onStatusEvent(this);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
