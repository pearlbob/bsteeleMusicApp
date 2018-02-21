package com.bsteele.bsteeleMusicApp.client.gin;

import com.bsteele.bsteeleMusicApp.client.application.ApplicationModule;
import com.bsteele.bsteeleMusicApp.client.place.NameTokens;
import com.bsteele.bsteeleMusicApp.client.resources.ResourceLoader;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;

public class ClientModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new DefaultModule
                .Builder()
                .defaultPlace(NameTokens.HOME)
                .errorPlace(NameTokens.HOME)
                .unauthorizedPlace(NameTokens.HOME)
                .build());
        install(new ApplicationModule());

        bind(ResourceLoader.class).asEagerSingleton();
    }
}
