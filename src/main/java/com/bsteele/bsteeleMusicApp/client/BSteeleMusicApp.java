package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.google.gwt.core.client.EntryPoint;

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

        AppResources.INSTANCE.style().ensureInjected();
    }

    private static final Logger logger = Logger.getLogger(BSteeleMusicApp.class.getName());
}
