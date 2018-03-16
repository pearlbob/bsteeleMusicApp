package com.bsteele.bsteeleMusicApp.client.jsTypes;

import jsinterop.annotations.*;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class AudioFilePlayer {

    @JsConstructor
    public AudioFilePlayer() {
    }

    public native boolean bufferFile(String filePath);

    public native boolean play(String filePath, double when, double duration);

    public native boolean stop();

    public native double getCurrentTime();

    public native double getBaseLatency();

    public native double getOutputLatency();
}
