/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
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
        //GWT.log("Lyrics.onModuleLoad()");
        onModuleLoadJS();
    }

}
