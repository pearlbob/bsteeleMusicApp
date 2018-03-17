package com.bsteele.bsteeleMusicApp.client.application;

import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.SongPlayMasterImpl;
import com.bsteele.bsteeleMusicApp.client.application.home.HomeModule;
import com.bsteele.bsteeleMusicApp.client.application.login.LoginModule;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class ApplicationModule extends AbstractPresenterModule {

    @Override
    protected void configure() {

        bind(SongPlayMaster.class).to(SongPlayMasterImpl.class).asEagerSingleton();
        
        install(new LoginModule());
        install(new HomeModule());
        //install(new SongsModule());

        bindPresenter(ApplicationPresenter.class, ApplicationPresenter.MyView.class, ApplicationView.class,
                ApplicationPresenter.MyProxy.class);

    }
}
