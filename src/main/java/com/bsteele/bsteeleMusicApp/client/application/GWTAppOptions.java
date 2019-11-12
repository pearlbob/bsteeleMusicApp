package com.bsteele.bsteeleMusicApp.client.application;

import com.bsteele.bsteeleMusicApp.shared.songs.AppOptions;
import com.bsteele.bsteeleMusicApp.shared.util.LocalStorage;

public class GWTAppOptions implements AppOptions.SaveCallback {

    //make the constructor private so that this class cannot be instantiated externally
    private GWTAppOptions() {
        if (localStorage != null) {
            appOptions.fromJson(localStorage.getAppOptions());
            appOptions.registerSaveCallback(this);
        }
    }

    //Get the only object available
    public static AppOptions getInstance() {
        return instance.appOptions;
    }

    public void save() {
        localStorage.setAppOptions(appOptions.toJson());
    }

    private LocalStorage localStorage = GwtLocalStorage.instance();

    private static AppOptions appOptions = AppOptions.getInstance();
    private static GWTAppOptions instance = new GWTAppOptions();    //  singleton


    //  logger doesn't seem appropriate here  private static final Logger logger = Logger.getLogger(AppOptions.class.getName());
}
