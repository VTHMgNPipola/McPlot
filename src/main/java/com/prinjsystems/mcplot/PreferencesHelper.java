package com.prinjsystems.mcplot;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static com.prinjsystems.mcplot.Main.PREFERENCES_PATH;

public class PreferencesHelper {
    public static final Preferences PREFERENCES = Preferences.userRoot().node(PREFERENCES_PATH);

    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_OPEN_MAXIMIZED = "openMaximized";

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                PREFERENCES.sync();
            } catch (BackingStoreException e) {
                e.printStackTrace();
            }
        }));
    }
}
