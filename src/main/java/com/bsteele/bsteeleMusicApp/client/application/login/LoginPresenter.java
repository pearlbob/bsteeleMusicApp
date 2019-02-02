/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.application.login;

import com.bsteele.bsteeleMusicApp.client.application.ApplicationPresenter;
import com.bsteele.bsteeleMusicApp.client.application.CurrentUser;
import com.bsteele.bsteeleMusicApp.client.place.NameTokens;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class LoginPresenter extends Presenter<LoginPresenter.MyView, LoginPresenter.MyProxy>
        implements LoginUiHandlers {

    @ProxyStandard
    @NameToken(NameTokens.LOGIN)
    @NoGatekeeper
    interface MyProxy extends ProxyPlace<LoginPresenter> {
    }

    interface MyView extends View, HasUiHandlers<LoginUiHandlers> {
    }

    // Credentials are stored here for demo purpose only.
    private static final String USERNAME = "bob";
    private static final String PASSWORD = "bob";

  private final CurrentUser currentUser;
  private final PlaceManager placeManager;

    @Inject
    LoginPresenter(
            EventBus eventBus,
            MyView view,
            MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser) {
        super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);

        this.currentUser = currentUser;
        this.placeManager = placeManager;

        final Map<String, List<String>> parameterMap = Window.Location.getParameterMap();
        if (parameterMap != null && parameterMap.get("open") != null) {
            requestHome();
        } else {
            if (!currentUser.isLoggedIn()) {
                placeManager.revealDefaultPlace();
            }
            getView().setUiHandlers(this);
        }
    }

    @Override
    public void confirm(String username, String password) {
        if (validateCredentials(username, password)) {
            requestHome();
        }
    }

    private void requestHome() {
        currentUser.setLoggedIn(true);

        PlaceRequest placeRequest = new PlaceRequest.Builder()
                .nameToken(NameTokens.HOME)
                .build();

        placeManager.revealPlace(placeRequest);
    }

    private boolean validateCredentials(String username, String password) {
        if (username == null || username.length() <= 0) {
            return false;
        }

        Storage localStorage = Storage.getLocalStorageIfSupported();
        if (localStorage == null)
            return false;
        localStorage.setItem("username", username);
        return true;  //  fixme!   username.equals(USERNAME) && password.equals(PASSWORD);
    }
}
