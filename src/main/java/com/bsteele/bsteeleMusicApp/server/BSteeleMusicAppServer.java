package com.bsteele.bsteeleMusicApp.server;

/*
 * Copyright 2018 Robert Steele at bsteele.com
 */

/**
 *
 * @author bob
 */
//@ServerEndpoint(value = "/bsteeleMusic" )
public class BSteeleMusicAppServer
{
//
// public BSteeleMusicAppServer(){
//    logger.log(Level.INFO, "BSteeleMusicAppServer()");
//    System.out.println("BSteeleMusicAppServer()");
//    GWT.log( "GWT.log(BSteeleMusicAppServer())");
//  }
// 
//  @OnOpen
//  public void onOpen(final Session session) {
//    logger.log(Level.INFO, "onOpen({0})", session.getId());
//    peers.add(session);
//  }
//
//  @OnMessage
//  public void onMessage(final String message, final Session session) {
//    logger.log(Level.INFO, "onMessage({0},{1})", new Object[]{message, session.getId()});
//    final String id = session.getId();
//    for (final Session peer : peers) {
//      if (peer.getId().equals(session.getId())) {
//        peer.getAsyncRemote().sendText("You said " + message);
//      } else {
//        peer.getAsyncRemote().sendText(id + " says " + message);
//      }
//    }
//  }
//
//  @OnClose
//  public void onClose(final Session session) {
//    logger.log(Level.INFO, "onClose(" + session.getId() + "): ");
//    peers.remove(session);
//  }
//
//  @OnError
//  public void onError(final Session session, Throwable t) {
//    logger.log(Level.INFO, "onError({0})", session.getId());
//  }
//
//  private static final Logger logger = Logger.getLogger(BSteeleMusicAppServer.class.getName());
//  private static final Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
}
