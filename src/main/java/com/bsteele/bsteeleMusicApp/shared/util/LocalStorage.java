package com.bsteele.bsteeleMusicApp.shared.util;

import com.bsteele.bsteeleMusicApp.shared.songs.AppOptions;

public abstract class LocalStorage {

    public final String getUserName() {
        return genericRead("username");
    }

    public  final void setUserName(String userName) {
        genericWrite("username", userName);
    }

    public final  String getAppOptions() {
        return genericRead(AppOptions.class.getSimpleName());
    }

    public  final void setAppOptions(String appOptions) {
        genericWrite(AppOptions.class.getSimpleName(), appOptions);
    }

    public abstract String genericRead(String key);
    public abstract void genericWrite(String key, String value);
}
