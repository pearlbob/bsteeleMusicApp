package com.bsteele.bsteeleMusicApp.client.application.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MusicAnimationEvent extends GwtEvent<MusicAnimationEventHandler> {

    public static Type<MusicAnimationEventHandler> TYPE = new Type<MusicAnimationEventHandler>();

    private final double t; //  units: seconds
    private final int measureNumber;
    private final int beatNumber;

    public MusicAnimationEvent(double t, int beatNumber, int measureNumber) {
        this.t = t;
        this.beatNumber = beatNumber;
        this.measureNumber = measureNumber;
    }

    @Override
    public Type<MusicAnimationEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(MusicAnimationEventHandler handler) {
        handler.onMusicAnimationEvent(this);
    }

    public double getT() {
        return t;
    }

    public int getBeatNumber() {
        return beatNumber;
    }

    public int getMeasureNumber() {
        return measureNumber;
    }
}
