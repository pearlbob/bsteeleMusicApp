package com.bsteele.bsteeleMusicApp.client.application;

import com.bsteele.bsteeleMusicApp.client.Function;
import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.jsTypes.WebSocket;
import com.google.gwt.core.client.GWT;
import jsinterop.annotations.JsType;

import javax.inject.Inject;
import java.util.logging.Logger;

import static jsinterop.annotations.JsPackage.GLOBAL;


/**
 *
 */
public class BSteeleMusicIO {
    /**
     * @param songPlayMaster the song play master to transport
     */
    @Inject
    public BSteeleMusicIO(SongPlayMaster songPlayMaster) {

        this.songPlayMaster = songPlayMaster;
        String url = getWebSocketURL();
        if (url != null) {
            GWT.log("url: " + url);

            socket = new WebSocket(url);
            socket.onerror = new SocketErrorFunction();
            socket.onmessage = new SocketReceiveFunction();
        }

        songPlayMaster.setBSteeleMusicIO(this);

        songPlayMaster.initialize();
    }

    private String getWebSocketURL() {
        String url = GWT.getHostPageBaseURL();
        if (url == null || url.startsWith("http://127.0.0.1:8888/"))
            return null;

        logger.fine("GWT.getHostPageBaseURL(): " + url);
        url = url.replaceFirst("bsteeleMusicApp", "");  //  due to jetty startup
        url = url.replaceFirst("^http\\:", "ws:");
        url = url.replaceFirst("^https\\:", "wss:");
        url += "bsteeleMusicApp/bsteeleMusic";
        url = url.replaceFirst("8888", "8082");//  fixme
        logger.fine("url: " + url);
        return url;
    }

    private final class SocketReceiveFunction implements Function {

        @Override
        public Object call(Object event) {
            long t = System.currentTimeMillis(); //  mark time immediately

            OnMessageEvent me = (OnMessageEvent) event;

            //GWT.log("message recv: " + me.data + " at my " + t);
            logger.fine("message recv: " + me.data + " at my " + t);
            songPlayMaster.onMessage(t / 1000.0, me.data);
            return event;
        }
    }

    private final class SocketErrorFunction implements Function {

        @Override
        public Object call(Object event) {
            if (socket != null)
                logger.info("error: socket readyState: " + socket.readyState);
            isSocketOpen = isSocketOpen();
            return event;
        }
    }

    private boolean isSocketOpen() {
        return socket != null && socket.readyState == 1;
    }

    @JsType(isNative = true, name = "Object", namespace = GLOBAL)
    static class OnMessageEvent {

        public String data;

        public native final String getOrigin();
    }

    public boolean sendMessage(String message) {
        if (message == null || message.length() <= 0)
            return false;

        if (socket == null) {
            logger.fine("socket is null");
            return false;
        }
//        if ( !isSocketOpen) {
//            //  socket down, run locally only
//            GWT.log("socket readyState: " + socket.readyState);
//            songPlayMaster.onMessage(System.currentTimeMillis() / 1000.0, message);
//            return true;
//        }

//        GWT.log("socket send: " + message.substring(0,Math.min(30,message.length()))
//                + " at my " + System.currentTimeMillis());
        if (!isSocketOpen())
            return false;

        socket.send(message);
        return true;
    }

    private final SongPlayMaster songPlayMaster;
    private static WebSocket socket;
    private static boolean isSocketOpen = true;
    private static final Logger logger = Logger.getLogger(BSteeleMusicIO.class.getName());
}
