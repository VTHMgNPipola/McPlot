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

import com.vthmgnpipola.mcplot.nmath.Constant;
import com.vthmgnpipola.mcplot.nmath.Function;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

/**
 * Utility class for saving and loading serialized data to and from files.
 */
public class MathSessionHelper {
    private static final JFileChooser FILE_CHOOSER = new JFileChooser();
    private static final String EXTENSION = "mcp";

    static {
        FILE_CHOOSER.setFileFilter(new FileNameExtensionFilter(BUNDLE.getString("workspace.menu.file.fileFormat"),
                EXTENSION));
    }

    public static void saveSession(List<Function> functions, List<Constant> constants) {
        String saveDirectory = PreferencesHelper.PREFERENCES.get(PreferencesHelper.KEY_CURRENT_DIRECTORY_SAVE, null);
        if (saveDirectory != null) {
            FILE_CHOOSER.setCurrentDirectory(new File(saveDirectory));
        }

        int state = FILE_CHOOSER.showSaveDialog(null);
        if (state == JFileChooser.APPROVE_OPTION) {
            String chosenPath = FILE_CHOOSER.getSelectedFile().getAbsolutePath();
            if (!chosenPath.endsWith(EXTENSION)) {
                chosenPath += "." + EXTENSION;
            }

            PreferencesHelper.PREFERENCES.put(PreferencesHelper.KEY_CURRENT_DIRECTORY_SAVE, chosenPath);
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Path.of(chosenPath)))) {
                oos.writeObject(constants);
                oos.writeObject(functions);
            } catch (Throwable t) {
                JOptionPane.showMessageDialog(null, BUNDLE.getString("errors.save"),
                        BUNDLE.getString("generics.errorDialog"), JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void openSession(BiConsumer<List<Function>, List<Constant>> consumer) {
        String openDirectory = PreferencesHelper.PREFERENCES.get(PreferencesHelper.KEY_CURRENT_DIRECTORY_OPEN, null);
        if (openDirectory != null) {
            FILE_CHOOSER.setCurrentDirectory(new File(openDirectory));
        }

        int state = FILE_CHOOSER.showOpenDialog(null);
        if (state == JFileChooser.APPROVE_OPTION) {
            String chosenPath = FILE_CHOOSER.getSelectedFile().getAbsolutePath();
            PreferencesHelper.PREFERENCES.put(PreferencesHelper.KEY_CURRENT_DIRECTORY_OPEN, chosenPath);
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Path.of(chosenPath)))) {
                List<Constant> constants = (List<Constant>) ois.readObject();
                List<Function> functions = (List<Function>) ois.readObject();

                consumer.accept(functions, constants);
            } catch (Throwable t) {
                JOptionPane.showMessageDialog(null, BUNDLE.getString("errors.open"),
                        BUNDLE.getString("generics.errorDialog"), JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
