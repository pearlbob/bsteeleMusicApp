package com.bsteele.bsteeleMusicApp.client.application;

import com.bsteele.bsteeleMusicApp.shared.util.LocalStorage;
import com.google.gwt.storage.client.Storage;

import java.util.logging.Logger;

public class GwtLocalStorage extends LocalStorage {

    public static LocalStorage instance() {
        return instance;
    }

    public String genericRead(String key) {
        if (localStorage == null) {
            logger.warning("LocalStorage Not Supported");
            return null;
        }
        return localStorage.getItem(key);
    }

    public final void genericWrite(String key, String value) {
        if (localStorage == null) {
            logger.warning("LocalStorage Not Supported");
            return;
        }
        localStorage.setItem(key, value);
    }

    private Storage localStorage = Storage.getLocalStorageIfSupported();

    private static final LocalStorage instance = new GwtLocalStorage();
    private static Logger logger = Logger.getLogger(GwtLocalStorage.class.getName());
}
