/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.application.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author bob
 */
public class HomeTabEvent extends GwtEvent<HomeTabEventHandler> {

    public static Type<HomeTabEventHandler> TYPE = new Type<HomeTabEventHandler>();

    public HomeTabEvent() {
    }

    public HomeTabEvent(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public Type<HomeTabEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(HomeTabEventHandler handler) {
        handler.onHomeTab(this);
    }

    private int number = 0;
}
