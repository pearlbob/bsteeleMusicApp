package com.bsteele.bsteeleMusicApp.client.jsTypes;

import com.google.gwt.user.client.Window;
import jsinterop.annotations.*;
import net.sourceforge.htmlunit.corejs.javascript.annotations.JSGetter;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class AudioContext {
    
    private int currentTime;

    //public static native void start();

    @JsOverlay
    public final int getCurrentTime() {
        return currentTime;
    }
}

