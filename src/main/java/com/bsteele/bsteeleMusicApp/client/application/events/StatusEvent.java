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
    private final int intValue;
    private final double doubleValue;

    public enum StatusEventType {
        stringEvent,
        intEvent,
        doubleEvent;
    }

    public StatusEvent(String name, String value) {
        this.name = name;
        this.value = value;
        intValue = 0;
        doubleValue = 0;
        this.type = StatusEventType.stringEvent;
    }

    public StatusEvent(String name, int intValue) {
        this.name = name;
        value = "";
        this.intValue = intValue;
        doubleValue = 0;
        this.type = StatusEventType.intEvent;
    }

    public StatusEvent(String name, double doubleValue) {
        this.name = name;
        this.value = "";
        intValue = 0;
        this.doubleValue = doubleValue;
        this.type = StatusEventType.doubleEvent;
    }

    public int getStringValue() {
        return intValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public double getDoubleValue() {
        return intValue;
    }

    @Override
    public String toString() {
        switch (type) {
            default:
                return name + ": \"" + value + "\"";
            case intEvent:
            case doubleEvent:
                return name + ": " + getValue();
        }
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
        switch (type) {
            default:
                return value;
            case intEvent:
                return Integer.toString(intValue);
            case doubleEvent:
                return Double.toString(doubleValue);
        }
    }

    StatusEventType getType() {
        return type;
    }

    private final StatusEventType type;
}
