package com.bsteele.bsteeleMusicApp.client.application;

public class CurrentUser {

  public void setLoggedIn(boolean loggedIn) {
    this.loggedIn = loggedIn;
  }

  /**
   * @return the loggedIn
   */
  public boolean isLoggedIn() {
    return loggedIn;
  }

  private boolean loggedIn = false;
}
