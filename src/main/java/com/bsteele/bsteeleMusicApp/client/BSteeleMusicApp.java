package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.bsteele.bsteeleMusicApp.shared.SongPlayMaster;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import jsinterop.annotations.JsType;

import java.util.logging.Logger;

import static jsinterop.annotations.JsPackage.GLOBAL;

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
        logger.info("GWT.getHostPageBaseURL(): " + url);
        url = url.replaceFirst("bsteeleMusicApp", "");  //  due to jetty startup
        url = url.replaceFirst("^http\\:", "ws:") + "bsteeleMusicApp/bsteeleMusic";
        url = url.replaceFirst("8888", "8082");//  fixme
        logger.info("url: " + url);
        return url;
    }

    private final class SocketReceiveFunction implements Function {

        @Override
        public Object call(Object event) {

            OnMessageEevent me = (OnMessageEevent) event;
            long t = System.currentTimeMillis(); //  don't wait for string work to mark time
            GWT.log("message recv: " + me.data + " at my " + t);
            //Window.alert("message recv: " + me.data+ " at my " + t);
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

        GWT.log("socket send: " + message + " at my " + System.currentTimeMillis());
        socket.send(message);
        return true;
    }

    private SongPlayMaster songPlayMaster;
    private static WebSocket socket;
    private static final Logger logger = Logger.getLogger(BSteeleMusicApp.class.getName());
}
