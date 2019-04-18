/*
 * Copyright 2018 Robert Steele, bsteele.com
 * All rights reserved.
 */
package com.bsteele.bsteeleMusicApp.shared.songs;

/**
 * A pitch for a duration.
 *
 * @author bob
 */
public class Note {
    public Note(Pitch pitch, NoteDuration duration) {
        this.pitch = pitch;
        this.duration = duration;
        scaleNote = pitch.getScaleNote();
    }


    public final double getDuration() {
        return duration.getFractionOfBeat();
    }

    public final void setDuration(NoteDuration duration) {
        this.duration = duration;
    }

    public final Pitch getPitch() {
        return pitch;
    }

    public final boolean isDotted() {
        return isDotted;
    }

    public final boolean isTied() {
        return isTied;
    }

    public final boolean isDrum() {
        return isDrum;
    }

    public final boolean isRest() {
        return isRest;
    }

    public final ScaleNote getScaleNote() {
        return scaleNote;
    }

    public final boolean isBeamed() {
        return isBeamed;
    }

    public final boolean isSwing() {
        return isSwing;
    }

    public final boolean isTriplet() {
        return isTriplet;
    }

    public final boolean isPreNotationRequired() {
        return isPreNotationRequired;
    }

    public final int getTrebleClefPosition() {
        return trebleClefPosition;
    }

    public final DrumType getDrumType() {
        return drumType;
    }

    public final void setDrumType(DrumType drumType) {
        this.drumType = drumType;
    }

    private Pitch pitch;
    private NoteDuration duration;
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
}
