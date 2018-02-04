/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.google.gwt.core.shared.GWT;
import jsinterop.annotations.JsType;

/**
 *
 * @author bob
 */
@JsType
public class Song {
    
    public String transcribe( String chords, int halfSteps ){
        GWT.log("Song.transcribe()  here" );
        return "transcription return";
    }
    
}
