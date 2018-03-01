package com.bsteele.bsteeleMusicApp.client.application.home;

import com.bsteele.bsteeleMusicApp.client.presenterWidgets.LyricsAndChordsPresenterWidget;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.LyricsAndChordsView;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.SongEditPresenterWidget;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.SongEditView;
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
    
    bindPresenterWidget(LyricsAndChordsPresenterWidget.class,
            LyricsAndChordsPresenterWidget.MyView.class, LyricsAndChordsView.class);
    
     bindPresenterWidget(SongEditPresenterWidget.class,
            SongEditPresenterWidget.MyView.class, SongEditView.class);
  }
}
