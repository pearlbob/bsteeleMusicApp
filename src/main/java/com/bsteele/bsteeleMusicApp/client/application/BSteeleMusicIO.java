package com.bsteele.bsteeleMusicApp.client.application;

import com.bsteele.bsteeleMusicApp.client.Function;
import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.jsTypes.WebSocket;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
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
        url = getWebSocketURL();
        if (url != null) {
            logger.fine("websocket url: " + url);

            confirmSocketConnection();
        } else
            logger.warning("Cannot create websocket URL!");

        songPlayMaster.setBSteeleMusicIO(this);

        songPlayMaster.initialize();

        scheduler.scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                confirmSocketConnection();
                return true;
            }
        }, 15 * 1000);
    }

    private static String getWebSocketURL() {
        String url = GWT.getHostPageBaseURL();
        logger.fine("raw base url: " + url);
        if (url == null
                //  || url.startsWith("http://127.0.0.1:8888/")
                || url.startsWith("file://")
                || url.contains("bsteele.com")  //  not expected from internet web server, only local
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

            logger.fine("message recv: " + me.data + " at " + dateTimeFormat.format(new Date(t)));
            songPlayMaster.onMessage(t / 1000.0, me.data);
            return event;
        }
    }

    private final class SocketErrorFunction implements Function {

        @Override
        public Object call(Object event) {
            if (socket != null)
                logger.info("SocketErrorFunction: socket readyState: " + WebSocketReadyStateNameAt(socket.readyState));
            return event;
        }
    }

    public boolean isSocketOpen() {
        return socket != null && socket.readyState == WebSocketReadyState.OPEN.ordinal();
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

        logger.fine("socket send: " + message.substring(0, Math.min(30, message.length()))
                + " at " + dateTimeFormat.format(new Date()));

        socket.send(message);
        return true;
    }

    private void confirmSocketConnection() {
        if (isSocketOpen() || url == null) {
            if (!wasConnected) {
                logger.info("confirmSocketConnection() connected");
                wasConnected = true;
            }
            return;
        }
        wasConnected = false;
        if (url == null) {
            return;
        }

        //  try to open a new one
        socket = new WebSocket(url);
        socket.onerror = new SocketErrorFunction();
        socket.onmessage = new SocketReceiveFunction();

        logger.info("confirmSocketConnection() reset");
    }

    enum WebSocketReadyState {
        CONNECTING, // Socket has been created.The connection  yet open.
        OPEN,       // The connection is open and ready to  communicate.
        CLOSING,    // The connection is in the process of closing.
        CLOSED      // The connection is closed or couldn't be opened.
    }

    String WebSocketReadyStateNameAt(int i) {
        for (WebSocketReadyState ws : WebSocketReadyState.values()) {
            if (i == ws.ordinal())
                return ws.toString();
        }
        return Integer.toString(i);
    }

    private final String url;
    private final SongPlayMaster songPlayMaster;
    private static WebSocket socket;
    private boolean wasConnected = false;
    private final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("HH:mm:ss.SSS\"");
    private static final Scheduler scheduler = Scheduler.get();

    private static final Logger logger = Logger.getLogger(BSteeleMusicIO.class.getName());
}
