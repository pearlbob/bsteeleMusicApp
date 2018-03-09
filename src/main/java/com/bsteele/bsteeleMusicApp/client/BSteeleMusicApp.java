package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.bsteele.bsteeleMusicApp.shared.SongPlayMaster;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import static jsinterop.annotations.JsPackage.GLOBAL;
import jsinterop.annotations.JsType;

/**
 *
 */
public class BSteeleMusicApp implements EntryPoint {

  /**
   *
   */
  @Override
  public void onModuleLoad() {

    AppResources.INSTANCE.style().ensureInjected();

    String url = getWebSocketURL();
    //GWT.log("url: " + url);
    socket = new WebSocket(url);
    socket.onmessage = new SocketReceiveFunction();

    songPlayMaster = new SongPlayMaster();
  }

  private String getWebSocketURL() {
    String url = GWT.getHostPageBaseURL();
    url = url.replaceFirst("bsteeleMusicApp", "");  //  due to jetty startup
    url = url.replaceFirst("^http\\:", "ws:") + "bsteeleMusicApp/bsteeleMusic";
    url = url.replaceFirst("8888", "8082");//  fixme
    return url;
  }

  private final class SocketReceiveFunction implements Function {

    @Override
    public Object call(Object event) {

      OnMessageEevent me = (OnMessageEevent) event;
      GWT.log("message recv: " + me.data);
      //Window.alert("message recv: " + me.data);
      return event;
    }
  }

  @JsType(isNative = true, name = "Object", namespace = GLOBAL)
  static class OnMessageEevent {

    public String data;

    public native final String getOrigin();
  }

  public static boolean sendMessage(String message) {
    if (socket == null) {
      GWT.log("socket is null");
      return false;
    }

    GWT.log("socket send: " + message);
    socket.send(message);
    return true;
  }

  private SongPlayMaster songPlayMaster;
  private static WebSocket socket;
}
