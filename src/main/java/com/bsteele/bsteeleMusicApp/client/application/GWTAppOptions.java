package com.bsteele.bsteeleMusicApp.client.application;

import com.bsteele.bsteeleMusicApp.shared.songs.AppOptions;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.regexp.shared.SplitResult;
import com.google.gwt.storage.client.Storage;

public class GWTAppOptions implements AppOptions.SaveCallback {

    //make the constructor private so that this class cannot be instantiated externally
    private GWTAppOptions() {
        if (localStorage != null) {
            appOptions.fromJson(localStorage.getItem(storageKey));
            appOptions.registerSaveCallback(this);
        }
    }

    //Get the only object available
    public static AppOptions getInstance() {
        return instance.appOptions;
    }

    public void save() {
        if (storageKey == null)
            return;
        localStorage.setItem(storageKey, appOptions.toJson());
    }

    private Storage localStorage = Storage.getLocalStorageIfSupported();
    private final String storageKey = AppOptions.class.getSimpleName();     //  use name from super

    private static AppOptions appOptions = AppOptions.getInstance();
    private static GWTAppOptions instance = new GWTAppOptions();    //  singleton

    //  logger doesn't seem appropriate here  private static final Logger logger = Logger.getLogger(AppOptions.class.getName());
}
