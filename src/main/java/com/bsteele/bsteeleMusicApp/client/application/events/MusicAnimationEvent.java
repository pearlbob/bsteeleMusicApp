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
    private final int beatCount;
    private final int beatNumber;
    private final double beatFraction;

    public MusicAnimationEvent(double t, int beatCount, int beatNumber, double beatFraction, int measureNumber) {
        this.t = t;
        this.beatCount = beatCount;
        this.beatNumber = beatNumber;
        this.beatFraction = beatFraction;
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

    public int getBeatCount() {
        return beatCount;
    }

    public int getBeatNumber() {
        return beatNumber;
    }

    public double getBeatFraction() {
        return beatFraction;
    }

    public int getMeasureNumber() {
        return measureNumber;
    }
}
