package com.bsteele.bsteeleMusicApp.client.application.songs;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class SongsModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        bindPresenter(SongsPresenter.class, SongsPresenter.MyView.class, SongsView.class,
                SongsPresenter.MyProxy.class);
    }
}
