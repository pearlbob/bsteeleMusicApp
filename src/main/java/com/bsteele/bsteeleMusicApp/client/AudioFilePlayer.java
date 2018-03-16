package com.bsteele.bsteeleMusicApp.client;

import jsinterop.annotations.*;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class AudioFilePlayer {

  @JsConstructor
  public AudioFilePlayer() {
  }

  @JsMethod
  public native boolean bufferFile(String filePath);

  public native boolean play(String filePath, double when, double duration);
}
