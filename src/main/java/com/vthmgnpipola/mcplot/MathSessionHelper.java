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

import com.vthmgnpipola.mcplot.ngui.PlottingPanelContext;
import com.vthmgnpipola.mcplot.nmath.Constant;
import com.vthmgnpipola.mcplot.nmath.Function;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
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

    private static String lastPath;

    static {
        FILE_CHOOSER.setFileFilter(new FileNameExtensionFilter(BUNDLE.getString("workspace.menu.file.fileFormat"),
                EXTENSION));
    }

    public static void saveSession(List<Function> functions, List<Constant> constants, PlottingPanelContext context,
                                   boolean forceDialog) {
        String chosenPath = lastPath;
        if (chosenPath == null || forceDialog) {
            String saveDirectory = PreferencesHelper.PREFERENCES.get(PreferencesHelper.KEY_CURRENT_DIRECTORY_SAVE, null);
            if (saveDirectory != null) {
                FILE_CHOOSER.setCurrentDirectory(new File(saveDirectory));
            }

            int state = FILE_CHOOSER.showSaveDialog(null);
            if (state != JFileChooser.APPROVE_OPTION) {
                return;
            }
            chosenPath = FILE_CHOOSER.getSelectedFile().getAbsolutePath();
        }

        if (!chosenPath.endsWith(EXTENSION)) {
            chosenPath += "." + EXTENSION;
        }

        PreferencesHelper.PREFERENCES.put(PreferencesHelper.KEY_CURRENT_DIRECTORY_SAVE, chosenPath);
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Path.of(chosenPath)))) {
            McPlotState state = new McPlotState();
            state.constants = constants;
            state.functions = functions;
            state.context = context;
            state.unitX = GraphUnit.getString(context.axisX.unit);
            state.unitY = GraphUnit.getString(context.axisY.unit);
            state.customUnitX = GraphUnit.CUSTOM_X_UNIT;
            state.customUnitY = GraphUnit.CUSTOM_Y_UNIT;

            oos.writeObject(state);
            lastPath = chosenPath;
        } catch (Throwable t) {
            t.printStackTrace();
            JOptionPane.showMessageDialog(null, BUNDLE.getString("errors.save"),
                    BUNDLE.getString("generics.errorDialog"), JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void openSession(Consumer<McPlotState> consumer) {
        String openDirectory = PreferencesHelper.PREFERENCES.get(PreferencesHelper.KEY_CURRENT_DIRECTORY_OPEN, null);
        if (openDirectory != null) {
            FILE_CHOOSER.setCurrentDirectory(new File(openDirectory));
        }

        int option = FILE_CHOOSER.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            lastPath = null;
            String chosenPath = FILE_CHOOSER.getSelectedFile().getAbsolutePath();
            PreferencesHelper.PREFERENCES.put(PreferencesHelper.KEY_CURRENT_DIRECTORY_OPEN, chosenPath);
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Path.of(chosenPath)))) {
                McPlotState state = (McPlotState) ois.readObject();
                lastPath = chosenPath;

                consumer.accept(state);
            } catch (Throwable t) {
                t.printStackTrace();
                JOptionPane.showMessageDialog(null, BUNDLE.getString("errors.open"),
                        BUNDLE.getString("generics.errorDialog"), JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    public static class McPlotState implements Serializable {
        @Serial
        private static final long serialVersionUID = -8335633288701597327L;

        public List<Constant> constants;
        public List<Function> functions;
        public PlottingPanelContext context;
        public String unitX, unitY;
        public GraphUnit customUnitX;
        public GraphUnit customUnitY;
    }
}
