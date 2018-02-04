/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.google.gwt.core.client.GWT;
import jsinterop.annotations.JsType;

/**
 *
 * @author bob
 */
@JsType
public class BpmTap {

    public BpmTap() {

    }

    public int tap(double t) {
        double dt = t - lastT;
        if (dt < 3) {
            if (hertz == 0) {
                hertz = 1 / dt;
            } else {
                hertz = hertz * (1 - pass) + pass * 1 / dt;
            }

            //GWT.log("BpmTap: bpm: " + getBPM());
        } else {
            hertz = 0;
        }
        lastT = t;
        return getBPM();
    }
    
    
    /**
     * @return the hertz
     */
    public final double getHertz() {
        return hertz;
    }
    
    /**
     * @return the BPM
     */
    public final int getBPM() {
        return (int) Math.round(60*hertz);
    }

    private static final double pass = 0.3;
    private double hertz = 0;
    private double lastT = 0;

}
