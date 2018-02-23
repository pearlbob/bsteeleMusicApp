package com.bsteele.bsteeleMusicApp.client.gin;

import com.bsteele.bsteeleMusicApp.client.application.ApplicationModule;
import com.bsteele.bsteeleMusicApp.client.application.CurrentUser;
import com.bsteele.bsteeleMusicApp.client.place.NameTokens;
import com.bsteele.bsteeleMusicApp.client.resources.ResourceLoader;
import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;

public class ClientModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new DefaultModule
                .Builder()
                .defaultPlace(NameTokens.LOGIN)
                .errorPlace(NameTokens.LOGIN)
                .unauthorizedPlace(NameTokens.LOGIN)
                .build());
        install(new ApplicationModule());

        bind(ResourceLoader.class).asEagerSingleton();
        bind(CurrentUser.class).in(Singleton.class);
    }
}
