package com.bsteele.bsteeleMusicApp.client.application.home;

import com.bsteele.bsteeleMusicApp.client.presenterWidgets.SongListPresenterWidget;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.SongListView;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class HomeModule extends AbstractPresenterModule {

  @Override
  protected void configure() {
    bindPresenter(HomePresenter.class, HomePresenter.MyView.class, HomeView.class,
            HomePresenter.MyProxy.class);

    bindPresenterWidget(SongListPresenterWidget.class,
            SongListPresenterWidget.MyView.class, SongListView.class);
  }
}
