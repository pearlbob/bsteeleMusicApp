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
    public BpmTap(){
        
    }
    
    public void tap( double t ){
        double dt = t - lastT;
        GWT.log("BpmTap: t: "+t);
        lastT = t;
    }
    
    private double lastT = System.currentTimeMillis()/1000.0;
}
