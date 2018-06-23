package com.bsteele.bsteeleMusicApp.client.application.events;

import com.google.gwt.event.dom.client.DomEvent;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class DomInputEvent extends DomEvent<DomInputEventHandler> {

    private static final Type<DomInputEventHandler> TYPE = new Type<>("compositionupdate", new DomInputEvent());

    public static Type<DomInputEventHandler> getType() {
        return TYPE;
    }

    protected DomInputEvent() {
    }

    @Override
    public Type<DomInputEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(DomInputEventHandler domInputEventHandler)
    {
        domInputEventHandler.onInput(this);
    }
}
