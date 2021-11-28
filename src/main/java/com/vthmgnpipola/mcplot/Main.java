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

import com.formdev.flatlaf.FlatLightLaf;
import com.vthmgnpipola.mcplot.ngui.Workspace;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static final ResourceBundle BUNDLE = ResourceBundle.getBundle("mcplot",
            Locale.forLanguageTag(PreferencesHelper.PREFERENCES.get(PreferencesHelper.KEY_LANGUAGE,
                    Language.LANGUAGES[0].getTag())));

    public static final String PREFERENCES_PATH = "com.vthmgnpipola.mcplot";

    public static final String VERSION = "0.1-SNAPSHOT";

    public static final ExecutorService EXECUTOR_THREAD = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        FlatLightLaf.setup();
        JFrame.setDefaultLookAndFeelDecorated(true);

        SwingUtilities.invokeLater(() -> {
            Workspace workspace = new Workspace();
            workspace.init();
            workspace.setVisible(true);
        });
    }
}
