/*
 * Copyright 2018 Robert Steele, bsteele.com
 * All rights reserved.
 */
package com.bsteele.bsteeleMusicApp.client.songs;

/**
 *
 * @author bob
 */
public class Note
{
    public Note( Pitch pitch, double duration) {
        this.pitch = pitch;
        this.duration = duration;
        scaleNote = pitch.getScaleNote();
    }
    private Pitch pitch;
    private double duration; // units of beat
    private boolean isDotted;
    private boolean isTied;
    private boolean isDrum;
    private boolean isRest;
    private DrumType drumType;

    private transient ScaleNote scaleNote;//    computed from song's key
    private transient boolean isBeamed;
    private transient boolean isSwing;//fixme: 1, 2
    private transient boolean isTriplet; //fixme: 1, 2, 3
    private transient boolean isPreNotationRequired; //   #,b, natural
    private transient int trebleClefPosition;
    transient int bassClefPosition;


    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public Pitch getPitch() {
        return pitch;
    }

    public boolean isDotted() {
        return isDotted;
    }

    public boolean isTied() {
        return isTied;
    }

    public boolean isDrum() {
        return isDrum;
    }

    public boolean isRest() {
        return isRest;
    }

    public ScaleNote getScaleNote() {
        return scaleNote;
    }

    public boolean isBeamed() {
        return isBeamed;
    }

    public boolean isSwing() {
        return isSwing;
    }

    public boolean isTriplet() {
        return isTriplet;
    }

    public boolean isPreNotationRequired() {
        return isPreNotationRequired;
    }

    public int getTrebleClefPosition() {
        return trebleClefPosition;
    }

    public DrumType getDrumType() {
        return drumType;
    }

    public void setDrumType(DrumType drumType) {
        this.drumType = drumType;
    }
}
