package com.bsteele.bsteeleMusicApp.client.application;

import com.bsteele.bsteeleMusicApp.shared.songs.AppOptions;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.regexp.shared.SplitResult;
import com.google.gwt.storage.client.Storage;

public class GWTAppOptions extends AppOptions {

    //make the constructor private so that this class cannot be instantiated externally
    private GWTAppOptions() {
        //  if in GWT, force all to use the GWT version
        //  done in lieu of injection
        AppOptions.instance = this;

        if (localStorage != null)
            fromJson(localStorage.getItem(storageKey));
    }

    //Get the only object available
    public static GWTAppOptions getInstance() {
        return instance;
    }

    @Override
    protected final void save() {
        if (storageKey == null)
            return;
        localStorage.setItem(storageKey, toJson());
    }

    private static GWTAppOptions instance = new GWTAppOptions();

    private Storage localStorage = Storage.getLocalStorageIfSupported();
    private final String storageKey = AppOptions.class.getSimpleName();     //  use name from super

    //  logger doesn't seem appropriate here  private static final Logger logger = Logger.getLogger(AppOptions.class.getName());
}
