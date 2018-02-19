package com.bsteele.bsteeleMusicApp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.vaadin.polymer.paper.widget.PaperButton;
import static jsinterop.annotations.JsPackage.GLOBAL;
import jsinterop.annotations.JsType;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BSteeleMusicApp implements EntryPoint {

  /**
   *
   */
  @Override
  public void onModuleLoad() {

    BSteeleMusicAppClientUtil.onModuleLoadJS();

    socket = new WebSocket(getWebSocketURL());

    socket.onmessage = new SocketReceiveFunction();

    // Use Widget API to Create a <paper-button>
    PaperButton button = new PaperButton("Press me!");
    button.setRaised(true);

    {
      ClickHandler handler = (ClickEvent event) -> {
        String msg = "hello bob";
        socket.send("hello bob");
        GWT.log("message sent:" + msg);
      };
      button.addClickHandler(handler);
    }

    RootPanel.get().add(button);
  }

  private String getWebSocketURL() {
    final String moduleBaseURL = GWT.getHostPageBaseURL();
    return moduleBaseURL.replaceFirst("^http\\:", "ws:") + "bsteeleMusic";
  }

  private final class SocketReceiveFunction implements Function {

    @Override
    public Object call(Object event) {

      OnMessageEevent me = (OnMessageEevent) event;
      GWT.log("message recv:");
      Window.alert("message me:" + me.data);
      return event;
    }
  }

  @JsType(isNative = true, name = "Object", namespace = GLOBAL)
  static class OnMessageEevent {

    public String data;
  }

  WebSocket socket;
}
