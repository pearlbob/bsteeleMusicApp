/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.google.gwt.core.client.GWT;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsMethod;
import static jsinterop.annotations.JsPackage.GLOBAL;
import jsinterop.annotations.JsType;

/**
 *
 * @author bob
 */
@JsType(namespace = JsPackage.GLOBAL, isNative = true)
public class BSteeleMusicAppClientUtil {

    @JsMethod(namespace = GLOBAL)
    public static native void onModuleLoadJS();

    @JsOverlay
    public static final void onModuleLoad() {
//        GWT.log("Lyrics.onModuleLoad()");
        onModuleLoadJS();
    }

}
