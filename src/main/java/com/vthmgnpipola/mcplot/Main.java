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

/**
 * McPlot is a lightweight graphing calculator written in Java 17, free for anyone to use.
 * <p>
 * My goal with McPlot is to create a reliable, powerful and lightweight graphing calculator that is also free and
 * open-source, so that anyone can use it, even with the most basic of computers (provided it supports Java 17).
 * <p>
 * This class serves mainly as a bootstrapper for the AWT Event Dispatcher for the GUI and a single threaded executor
 * service for general tasks.
 */
public class Main {
    /**
     * Resource bundle used to set the text values of GUI components depending on the select language
     */
    public static final ResourceBundle BUNDLE = ResourceBundle.getBundle("mcplot",
            Locale.forLanguageTag(PreferencesHelper.PREFERENCES.get(PreferencesHelper.KEY_LANGUAGE,
                    Language.LANGUAGES[0].getTag())));

    /**
     * Path used in the Preferences object, on {@link PreferencesHelper}, to load and save settings
     */
    public static final String PREFERENCES_PATH = "com.vthmgnpipola.mcplot";

    /**
     * McPlot version, displayed in the Workspace window title and About window.
     */
    public static final String VERSION = "0.1";

    /**
     * Single threaded executor for general tasks done by the application. Normally used when a task might be too
     * long to run on the AWT EventDispatcher
     */
    public static final ExecutorService EXECUTOR_THREAD = Executors.newSingleThreadExecutor();

    /**
     * First method called by the JVM. Used to initialize the LookAndFeel and the AWT EventDispatcher to start the GUI.
     * The AWT EventDispatcher then starts other services, such as the
     * {@link com.vthmgnpipola.mcplot.nmath.MathEvaluatorPool}.
     *
     * @param args Command line arguments, which are ignored.
     */
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
