/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.server;

import com.google.gwt.core.shared.GWT;

/**
 *
 * @author bob
 */
//@ServerEndpoint(value = "/chat", subprotocols = {"superchat", "chat"})
public class ChatServer {

    ChatServer() {
        GWT.log("ChatServer constructed");
    }
//
//    @OnOpen
//    public void onOpen(final Session session) {
//        GWT.log("onOpen(" + session.getId() + ")");
//        peers.add(session);
//    }
//
//    @OnClose
//    public void onClose(final Session session) {
//        GWT.log("onClose(" + session.getId() + ")");
//        peers.remove(session);
//    }
//
//    @OnMessage
//    public void onMessage(final String message, final Session session) {
//        GWT.log("onMessage(" + message + "," + session.getId() + ")");
//        final String id = session.getId();
//        for (final Session peer : peers) {
//            if (peer.getId().equals(session.getId())) {
//                peer.getAsyncRemote().sendText("You said " + message);
//            } else {
//                peer.getAsyncRemote().sendText(id + " says " + message);
//            }
//        }
//    }
//
//    @OnMessage
//    public void onBinaryMessage(final byte[] data, final Session session) {
//        final String message = new String(data, Charset.forName("US-ASCII"));
//        GWT.log("onBinaryMessage(" + message + "," + session.getId() + ")");
//        final String id = session.getId();
//        for (final Session peer : peers) {
//            if (peer.getId().equals(session.getId())) {
//                peer.getAsyncRemote().sendBinary(ByteBuffer.wrap(("You said " + message).getBytes()));
//            } else {
//                peer.getAsyncRemote().sendBinary(ByteBuffer.wrap((id + " says " + message).getBytes()));
//            }
//        }
//    }
//
//    private static final Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
}
