package com.bsteele.bsteeleMusicApp.client.application;

import com.bsteele.bsteeleMusicApp.client.Function;
import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.jsTypes.WebSocket;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import jsinterop.annotations.JsType;

import javax.inject.Inject;
import java.util.Date;
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
           logger.fine("websocket url: " + url);

            socket = new WebSocket(url);
            socket.onerror = new SocketErrorFunction();
            socket.onmessage = new SocketReceiveFunction();
        }

        songPlayMaster.setBSteeleMusicIO(this);

        songPlayMaster.initialize();
    }

    private String getWebSocketURL() {
        String url = GWT.getHostPageBaseURL();
        logger.fine("raw base url: " + url);
        if (url == null
          //  || url.startsWith("http://127.0.0.1:8888/")
                || url.startsWith("file://")
        )
            return null;

        logger.fine("GWT.getHostPageBaseURL(): " + url);
        url = url.replaceFirst("bsteeleMusicApp", "");  //  due to jetty startup
        url = url.replaceFirst("^http\\:", "ws:");
        url = url.replaceFirst("^https\\:", "wss:");
        url += "bsteeleMusicApp/bsteeleMusic";
        url = url.replaceFirst("8888", "8080");//  fixme: devmode uses tomcat at 127.0.0.1:8080
        logger.fine("final url: " + url);
        return url;
    }

    private final class SocketReceiveFunction implements Function {

        @Override
        public Object call(Object event) {
            long t = System.currentTimeMillis(); //  mark time immediately

            OnMessageEvent me = (OnMessageEvent) event;

            logger.fine("message recv: " + me.data +" at " + dateTimeFormat.format(new Date(t)));
            songPlayMaster.onMessage(t / 1000.0, me.data);
            return event;
        }
    }

    private final class SocketErrorFunction implements Function {

        @Override
        public Object call(Object event) {
            if (socket != null)
                logger.info("warning: socket readyState: " + socket.readyState);
            isSocketOpen = isSocketOpen();
            return event;
        }
    }

    public boolean isSocketOpen() {
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

        if (!isSocketOpen()) {
            logger.fine("socket is closed");
            return false;
        }

        logger.fine("socket send: " + message.substring(0,Math.min(30,message.length()))
                + " at " + dateTimeFormat.format(new Date()));

        socket.send(message);
        return true;
    }

    private final SongPlayMaster songPlayMaster;
    private static WebSocket socket;
    private static boolean isSocketOpen = true;
    private final DateTimeFormat dateTimeFormat =  DateTimeFormat.getFormat("HH:mm:ss.SSS\"");

    private static final Logger logger = Logger.getLogger(BSteeleMusicIO.class.getName());
}
