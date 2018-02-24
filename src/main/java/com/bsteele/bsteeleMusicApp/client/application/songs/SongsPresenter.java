package com.bsteele.bsteeleMusicApp.client.application.songs;

import com.bsteele.bsteeleMusicApp.client.application.ApplicationPresenter;
import com.bsteele.bsteeleMusicApp.client.application.LoggedInGatekeeper;
import com.bsteele.bsteeleMusicApp.client.place.NameTokens;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

public class SongsPresenter extends Presenter<SongsPresenter.MyView, SongsPresenter.MyProxy> {

  interface MyView extends View {
  }

  @ProxyStandard
  @NameToken(NameTokens.SONGS)
  @UseGatekeeper(LoggedInGatekeeper.class)
  interface MyProxy extends ProxyPlace<SongsPresenter> {
  }

  @Inject
  SongsPresenter(
          EventBus eventBus,
          MyView view,
          MyProxy proxy) {
    super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);
  }
}
