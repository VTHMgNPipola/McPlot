/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2023  VTHMgNPipola
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

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.vthmgnpipola.mcplot.ngui.Workspace;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import java.awt.Frame;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_LAF;
import static com.vthmgnpipola.mcplot.PreferencesHelper.PREFERENCES;
import static com.vthmgnpipola.mcplot.PreferencesHelper.VALUE_DARK_LAF;
import static com.vthmgnpipola.mcplot.PreferencesHelper.VALUE_LIGHT_LAF;

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
            Locale.forLanguageTag(PREFERENCES.get(PreferencesHelper.KEY_LANGUAGE,
                    Language.LANGUAGES[0].getTag())));

    /**
     * Path used in the Preferences object, on {@link PreferencesHelper}, to load and save settings
     */
    public static final String PREFERENCES_PATH = "com.vthmgnpipola.mcplot";

    /**
     * McPlot version, displayed in the Workspace window title and About window.
     */
    public static final String VERSION = "0.4-SNAPSHOT";

    /**
     * Single threaded executor for general tasks done by the application. Normally used when a task might be too
     * long to run on the AWT EventDispatcher
     */
    public static final ExecutorService EXECUTOR_THREAD = Executors.newSingleThreadExecutor();

    public static BufferedImage FRAME_ICON = null;

    static {
        try {
            FRAME_ICON = ImageIO
                    .read(Objects.requireNonNull(Main.class.getResourceAsStream("/mcplot-logo.png")));
        } catch (IOException e) {
            System.err.println("Unable to find the McPlot icon!");
            e.printStackTrace();
        }
    }

    /**
     * First method called by the JVM. Used to initialize the LookAndFeel and the AWT EventDispatcher to start the GUI.
     * The AWT EventDispatcher then starts other services, such as the
     * {@link com.vthmgnpipola.mcplot.nmath.MathEvaluatorPool}.
     *
     * @param args Command line arguments, which are ignored.
     */
    public static void main(String[] args) {
        updateLookAndFeel(PREFERENCES.get(KEY_LAF, VALUE_LIGHT_LAF));
        JFrame.setDefaultLookAndFeelDecorated(true);

        SwingUtilities.invokeLater(() -> {
            Workspace workspace = new Workspace();
            workspace.init();
            for (String arg : args) {
                Path sessionPath = null;
                try {
                    sessionPath = Path.of(arg);
                } catch (Exception ignored) {
                }

                if (sessionPath != null && Files.exists(sessionPath)) {
                    workspace.openSession(sessionPath);
                    break;
                }
            }
            workspace.setVisible(true);
        });
    }

    /**
     * Changes the Look and Feel of the application during runtime, and then updates all created {@link Frame}s.
     *
     * @param tag Tag of the Look and Feel. This is a value constant in {@link PreferencesHelper}.
     */
    public static void updateLookAndFeel(String tag) {
        LookAndFeel newLaf = null;
        switch (tag) {
            case VALUE_LIGHT_LAF -> newLaf = new FlatLightLaf();
            case VALUE_DARK_LAF -> newLaf = new FlatDarkLaf();
        }
        FlatLaf.setup(newLaf);

        for (Frame frame : JFrame.getFrames()) {
            SwingUtilities.updateComponentTreeUI(frame);
        }
        for (Window dialog : JDialog.getWindows()) {
            SwingUtilities.updateComponentTreeUI(dialog);
        }
    }
}
