package com.prinjsystems.mcplot;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static com.prinjsystems.mcplot.Main.PREFERENCES_PATH;

public class PreferencesHelper {
    public static final Preferences PREFERENCES = Preferences.userRoot().node(PREFERENCES_PATH);

    public static final String KEY_CURRENT_DIRECTORY_SAVE = "currentDirSave";
    public static final String KEY_CURRENT_DIRECTORY_OPEN = "currentDirOpen";

    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_OPEN_MAXIMIZED = "openMaximized";

    public static final String KEY_SAMPLES_PER_CELL = "samplesPerCell";
    public static final String KEY_SCALE_X = "scaleX";
    public static final String KEY_SCALE_Y = "scaleY";
    public static final String KEY_TRACE_WIDTH = "traceWidth";
    public static final String KEY_FILL_TRANSPARENCY = "fillTransparency";
    public static final String KEY_BACKGROUND_COLOR = "backgroundColor";
    public static final String KEY_MINOR_GRID_COLOR = "minorGridColor";
    public static final String KEY_MAJOR_GRID_COLOR = "majorGridColor";
    public static final String KEY_GLOBAL_AXIS_COLOR = "globalAxisColor";

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
