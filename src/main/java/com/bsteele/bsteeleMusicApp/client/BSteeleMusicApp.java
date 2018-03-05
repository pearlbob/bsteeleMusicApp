package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
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
    socket = new WebSocket(url);
    socket.onmessage = new SocketReceiveFunction();

  }

  private String getWebSocketURL() {
    String url = GWT.getHostPageBaseURL();
    url = url.replaceFirst("^http\\:", "ws:") + "bsteeleMusic";
    url = url.replaceFirst("8888", "8082");//  fixme
    return url;

  }

  private final class SocketReceiveFunction implements Function {

    @Override
    public Object call(Object event) {

      OnMessageEevent me = (OnMessageEevent) event;
      GWT.log("message recv: " + me.data);
      Window.alert("message recv: " + me.data);
      System.out.println("sout: message recv: " + me.data);
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

  private static WebSocket socket;
}
