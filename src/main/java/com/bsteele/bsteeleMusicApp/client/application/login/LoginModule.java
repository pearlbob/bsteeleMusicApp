package com.bsteele.bsteeleMusicApp.client.application.login;

import com.bsteele.bsteeleMusicApp.client.application.CurrentUser;
import com.bsteele.bsteeleMusicApp.client.application.LoggedInGatekeeper;
import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class LoginModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        bindPresenter(LoginPresenter.class, LoginPresenter.MyView.class, LoginView.class,
                LoginPresenter.MyProxy.class);
        
        bind(LoggedInGatekeeper.class).in(Singleton.class);
        bind(CurrentUser.class).asEagerSingleton();

    }
}
