package com.bsteele.bsteeleMusicApp.client.application.home;

import com.bsteele.bsteeleMusicApp.client.presenterWidgets.BassPresenterWidget;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.BassViewImpl;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.DrumOptionsPresenterWidget;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.DrumOptionsView;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.LyricsAndChordsPresenterWidget;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.LyricsAndChordsViewImpl;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.PlayerPresenterWidget;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.PlayerViewImpl;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.SingerPresenterWidget;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.SingerView;
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
                LyricsAndChordsPresenterWidget.MyView.class, LyricsAndChordsViewImpl.class);

        bindPresenterWidget(PlayerPresenterWidget.class,
                PlayerPresenterWidget.MyView.class, PlayerViewImpl.class);

        bindPresenterWidget(BassPresenterWidget.class,
                BassPresenterWidget.MyView.class, BassViewImpl.class);

        bindPresenterWidget(SingerPresenterWidget.class,
                SingerPresenterWidget.MyView.class, SingerView.class);

        bindPresenterWidget(SongEditPresenterWidget.class,
                SongEditPresenterWidget.MyView.class, SongEditView.class);

        bindPresenterWidget(DrumOptionsPresenterWidget.class,
                DrumOptionsPresenterWidget.MyView.class, DrumOptionsView.class);
    }
}
