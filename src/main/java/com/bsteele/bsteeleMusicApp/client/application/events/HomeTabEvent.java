/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.application.events;

import com.bsteele.bsteeleMusicApp.client.application.home.AppTab;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author bob
 */
public class HomeTabEvent extends GwtEvent<HomeTabEventHandler> {

    public static Type<HomeTabEventHandler> TYPE = new Type<HomeTabEventHandler>();

    public HomeTabEvent() {
    }

    public HomeTabEvent(AppTab tab) {
        this.tab = tab;
    }

    public AppTab getTab() {
        return tab;
    }

    @Override
    public Type<HomeTabEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(HomeTabEventHandler handler) {
        handler.onHomeTab(this);
    }

    private AppTab tab = AppTab.songs;
}
