package com.bsteele.bsteeleMusicApp.client.application.home;

import com.bsteele.bsteeleMusicApp.client.presenterWidgets.*;
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

        bindPresenterWidget(DrumOptionsPresenterWidget.class,
                DrumOptionsPresenterWidget.MyView.class, DrumOptionsView.class);
    }
}
