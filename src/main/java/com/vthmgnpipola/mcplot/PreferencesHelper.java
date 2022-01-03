/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2022  VTHMgNPipola
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
    public static final String KEY_LAF = "laf";
    public static final String KEY_OPEN_MAXIMIZED = "openMaximized";

    public static final String VALUE_LIGHT_LAF = "light";
    public static final String VALUE_DARK_LAF = "dark";

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
