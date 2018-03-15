package com.bsteele.bsteeleMusicApp.server;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/*
 * Copyright 2018 Robert Steele at bsteele.com
 */

/**
 * @author bob
 */
@ServerEndpoint(value = "/bsteeleMusic")
public class BSteeleMusicAppServer {

    public BSteeleMusicAppServer() {
        logger.log(Level.INFO, "BSteeleMusicAppServer()");
        //System.out.println("BSteeleMusicAppServer()");
        //GWT.log( "GWT.log(BSteeleMusicAppServer())");
    }

    @OnOpen
    public void onOpen(final Session session) throws IOException {
        logger.log(Level.INFO, "onOpen({0})", session.getId());
        peers.add(session);
    }

    @OnMessage
    public void onMessage(final String message, final Session session) {
        if ( message == null || message.length() <= 0 )
            return;

        //     flip any message back to all registered peers
        for (final Session peer : peers) {
//            try {
                peer.getAsyncRemote().sendText(message);
//            } catch (Exception e){
//                logger.log(Level.WARNING, "peer "+peer.getId()+" closed early");
//                peers.remove(peer);
//            }
        }

        logger.log(Level.INFO, "onMessage(\"{0}...\") to {1} by {2}", new Object[]{
                message.substring(0,Math.min(message.length(),40)).replaceAll("\n"," "),
                peers.size(),
                System.currentTimeMillis()});
    }

    @OnClose
    public void onClose(final Session session) {
        logger.log(Level.INFO, "onClose(" + session.getId() + "): ");
        peers.remove(session);
    }

    @OnError
    public void onError(final Session session, Throwable t) {
        logger.log(Level.INFO, "onError({0})", session.getId());
        //t.printStackTrace();
    }

    private static final Logger logger = Logger.getLogger(BSteeleMusicAppServer.class.getName());
    private static final Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
}
