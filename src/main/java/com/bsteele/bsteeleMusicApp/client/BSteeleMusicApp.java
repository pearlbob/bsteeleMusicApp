package com.bsteele.bsteeleMusicApp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.user.client.Window;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.gwt.websockets.client.WebSocket;
import org.realityforge.gwt.websockets.client.WebSocketListener;

/**
 * App Entry point
 */
public class BSteeleMusicApp implements EntryPoint, WebSocketListener {

  public void send(final String message) {
    webSocket.send(message);
  }

  @Override
  public void onModuleLoad() {
    webSocket = WebSocket.newWebSocketIfSupported();
    if (null == webSocket) {
      Window.alert("WebSocket not available!");
    } else {
      webSocket.setListener(this);
      webSocket.connect( getWebSocketURL() );
      logStatus("onModuleLoad", webSocket);
      Window.alert("WebSocket ready? "+webSocket.getURL());
      send("final String message");
    }
  }

  @Override
  public void onOpen(@Nonnull final WebSocket webSocket) {
    logStatus("Open", webSocket);
  }

  @Override
  public void onClose(@Nonnull final WebSocket webSocket,
          final boolean wasClean,
          final int code,
          @Nullable final String reason) {
    logStatus("Close", webSocket);
  }

  @Override
  public void onMessage(@Nonnull final WebSocket webSocket, @Nonnull final ArrayBuffer data) {
    logStatus("DataMessage", webSocket);
  }

  @Override
  public void onMessage(@Nonnull final WebSocket webSocket, @Nonnull final String textData) {
    logStatus("Message", webSocket);
          Window.alert("message back: "+textData);
  }

  @Override
  public void onError(WebSocket webSocket) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  private String getWebSocketURL()
  {
    final String moduleBaseURL = GWT.getHostPageBaseURL();
    return moduleBaseURL.replaceFirst( "^http\\:", "ws:" ) + "bsteeleMusic";
  }

  private void logStatus(@Nonnull final String section,
          @Nonnull final WebSocket webSocket) {
    final String suffix = !webSocket.isConnected()
            ? ""
            : "URL:" + webSocket.getURL() + "\n"
            + "BinaryType:" + webSocket.getBinaryType() + "\n"
            + "BufferedAmount:" + webSocket.getBufferedAmount() + "\n"
            + "Extensions:" + webSocket.getExtensions() + "\n"
            + "Protocol:" + webSocket.getProtocol();
    GWT.log("WebSocket @ " + section + "\n" + "ReadyState:" + webSocket.getReadyState() + "\n" + suffix);
  }

  private WebSocket webSocket;
}
