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

package com.vthmgnpipola.mcplot.ngui;

import com.vthmgnpipola.mcplot.PreferencesHelper;
import com.vthmgnpipola.mcplot.nmath.Constant;
import com.vthmgnpipola.mcplot.nmath.Function;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

public abstract class ExportFunctionsFrame extends JFrame {
    protected static final JFileChooser FILE_CHOOSER = new JFileChooser();

    protected final Collection<Function> functions;
    protected final Collection<Constant> constants;
    protected final Map<String, Double> constantValues;

    public ExportFunctionsFrame(String title, Collection<Function> functions, Collection<Constant> constants,
                                Map<String, Double> constantValues) {
        super(title);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.functions = functions;
        this.constants = constants;
        this.constantValues = constantValues;
    }

    public abstract void init();

    public abstract void export();

    protected int openSaveDialog(FileNameExtensionFilter extensionFilter) {
        FILE_CHOOSER.setFileFilter(extensionFilter);

        String saveDirectory = PreferencesHelper.PREFERENCES.get(PreferencesHelper.KEY_CURRENT_DIRECTORY_SAVE, null);
        if (saveDirectory != null) {
            FILE_CHOOSER.setCurrentDirectory(new File(saveDirectory));
        }

        int option = FILE_CHOOSER.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            PreferencesHelper.PREFERENCES.put(PreferencesHelper.KEY_CURRENT_DIRECTORY_SAVE,
                    FILE_CHOOSER.getCurrentDirectory().getAbsolutePath());
        }
        return option;
    }
}
