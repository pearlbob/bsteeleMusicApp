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
 *
 * @author bob
 */
@ServerEndpoint(value = "/bsteeleMusic" )
public class BSteeleMusicAppServer
{

 public BSteeleMusicAppServer(){
    logger.log(Level.INFO, "BSteeleMusicAppServer()");
    System.out.println("BSteeleMusicAppServer()");
    //GWT.log( "GWT.log(BSteeleMusicAppServer())");
  }
 
  @OnOpen
  public void onOpen(final Session session) throws IOException  {
    session.getBasicRemote().sendText("onOpen");
    logger.log(Level.INFO, "onOpen({0})", session.getId());
    peers.add(session);
  }

  @OnMessage
  public void onMessage(final String message, final Session session) {
    logger.log(Level.INFO, "onMessage({0},{1})", new Object[]{message, session.getId()});
    final String id = session.getId();
    for (final Session peer : peers) {
      if (peer.getId().equals(session.getId())) {
        peer.getAsyncRemote().sendText("You said " + message);
      } else {
        peer.getAsyncRemote().sendText(id + " says " + message);
      }
    }
  }

  @OnClose
  public void onClose(final Session session) {
    logger.log(Level.INFO, "onClose(" + session.getId() + "): ");
    peers.remove(session);
  }

  @OnError
  public void onError(final Session session, Throwable t) {
    logger.log(Level.INFO, "onError({0})", session.getId());
    t.printStackTrace();
  }

  private static final Logger logger = Logger.getLogger(BSteeleMusicAppServer.class.getName());
  private static final Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
}
