package com.bsteele.bsteeleMusicApp.client.application;

public class CurrentUser {

  public void setLoggedIn(boolean loggedIn) {
    this.loggedIn = loggedIn;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void logout() {
        loggedIn = false;
    }
    
    private boolean loggedIn = false;
}
