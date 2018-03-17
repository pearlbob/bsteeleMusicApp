package com.bsteele.bsteeleMusicApp.client.application;

import com.bsteele.bsteeleMusicApp.client.Function;
import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.application.songs.DefaultDrumSelectEvent;
import com.bsteele.bsteeleMusicApp.client.application.songs.DefaultDrumSelectEventHandler;
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
     *
     */
    @Inject
    public BSteeleMusicIO(SongPlayMaster songPlayMaster) {

        this.songPlayMaster = songPlayMaster;
        String url = getWebSocketURL();
        //GWT.log("url: " + url);
        socket = new WebSocket(url);
        socket.onmessage = new SocketReceiveFunction();
    }

    private String getWebSocketURL() {
        String url = GWT.getHostPageBaseURL();
        logger.fine("GWT.getHostPageBaseURL(): " + url);
        url = url.replaceFirst("bsteeleMusicApp", "");  //  due to jetty startup
        url = url.replaceFirst("^http\\:", "ws:") + "bsteeleMusicApp/bsteeleMusic";
        url = url.replaceFirst("8888", "8082");//  fixme
        url = url.replaceFirst("//", "/");
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

    @JsType(isNative = true, name = "Object", namespace = GLOBAL)
    static class OnMessageEvent {

        public String data;

        public native final String getOrigin();
    }

    public static boolean sendMessage(String message) {
        if (message == null || message.length() <= 0)
            return false;

        if (socket == null) {
            logger.info("socket is null");
            return false;
        }

//        GWT.log("socket send: " + message.substring(0,Math.min(30,message.length()))
//                + " at my " + System.currentTimeMillis());
        socket.send(message);
        return true;
    }

    private final SongPlayMaster songPlayMaster;
    private static WebSocket socket;
    private static final Logger logger = Logger.getLogger(BSteeleMusicIO.class.getName());
}
