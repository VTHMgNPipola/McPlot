/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2021  VTHMgNPipola
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.vthmgnpipola.mcplot;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static com.vthmgnpipola.mcplot.Main.PREFERENCES_PATH;

/**
 * Utility class for retrieving and defining settings in a {@link Preferences} object. Constants for all of the used
 * settings are also defined in this class.
 */
public class PreferencesHelper {
    public static final Preferences PREFERENCES = Preferences.userRoot().node(PREFERENCES_PATH);

    public static final String KEY_CURRENT_DIRECTORY_SAVE = "currentDirSave";
    public static final String KEY_CURRENT_DIRECTORY_OPEN = "currentDirOpen";

    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_OPEN_MAXIMIZED = "openMaximized";

    public static final String KEY_SAMPLES_PER_CELL = "samplesPerCell";
    public static final String KEY_MAX_STEP = "maxStep";
    public static final String KEY_GRAPH_UNIT_X = "unitX";
    public static final String KEY_GRAPH_UNIT_Y = "unitY";
    public static final String KEY_SCALE_X = "scaleX";
    public static final String KEY_SCALE_Y = "scaleY";
    public static final String KEY_TRACE_WIDTH = "traceWidth";
    public static final String KEY_ENABLE_ANTIALIAS = "antialias";
    public static final String KEY_FILL_TRANSPARENCY = "fillTransparency";
    public static final String KEY_BACKGROUND_COLOR = "backgroundColor";
    public static final String KEY_MINOR_GRID_COLOR = "minorGridColor";
    public static final String KEY_MAJOR_GRID_COLOR = "majorGridColor";
    public static final String KEY_GLOBAL_AXIS_COLOR = "globalAxisColor";

    public static final String KEY_CUSTOM_X_UNIT_NAME = "unitXName";
    public static final String KEY_CUSTOM_X_UNIT_DEFINITION = "unitXDefinition";
    public static final String KEY_CUSTOM_Y_UNIT_NAME = "unitYName";
    public static final String KEY_CUSTOM_Y_UNIT_DEFINITION = "unitYDefinition";

    static {
        // This makes sure that when the application shuts down all the preferences are saved
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                PREFERENCES.sync();
            } catch (BackingStoreException e) {
                e.printStackTrace();
            }
        }));
    }
}
