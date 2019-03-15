package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.UmbrellaException;

import javax.validation.constraints.NotNull;
import java.util.logging.Logger;


/**
 *
 */
public class BSteeleMusicApp implements EntryPoint {
    /**
     *
     */
    @Override
    public void onModuleLoad() {
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(@NotNull Throwable e) {
                ensureNotUmbrellaError(e);
            }
        });

        AppResources.INSTANCE.style().ensureInjected();
    }

    private static void ensureNotUmbrellaError(@NotNull Throwable e) {
        for (Throwable th : ((UmbrellaException) e).getCauses()) {
            if (th instanceof UmbrellaException) {
                ensureNotUmbrellaError(th);
            } else {
                th.printStackTrace();
            }
        }
    }
    private static final Logger logger = Logger.getLogger(BSteeleMusicApp.class.getName());
}
