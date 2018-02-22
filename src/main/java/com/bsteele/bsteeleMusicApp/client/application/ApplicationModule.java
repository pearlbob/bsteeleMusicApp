package com.bsteele.bsteeleMusicApp.client.application;

import com.bsteele.bsteeleMusicApp.client.application.home.HomeModule;
import com.bsteele.bsteeleMusicApp.client.application.login.LoginModule;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class ApplicationModule extends AbstractPresenterModule {

  @Override
  protected void configure() {
    install(new LoginModule());
    install(new HomeModule());

    bindPresenter(ApplicationPresenter.class, ApplicationPresenter.MyView.class, ApplicationView.class,
            ApplicationPresenter.MyProxy.class);
  }
}
