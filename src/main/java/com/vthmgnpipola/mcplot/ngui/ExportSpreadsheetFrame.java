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

import com.vthmgnpipola.mcplot.nmath.Constant;
import com.vthmgnpipola.mcplot.nmath.Function;
import java.util.Collection;
import java.util.Map;
import javax.swing.SwingUtilities;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class ExportSpreadsheetFrame extends ExportFunctionsFrame {
    public ExportSpreadsheetFrame(Map<String, Function> functionMap, Collection<Constant> constants,
                                  Map<String, Double> constantValues, PlottingPanel plottingPanel) {
        super(BUNDLE.getString("export.spreadsheet.title"), functionMap, constants, constantValues, plottingPanel);
    }

    @Override
    public void init() {
        initContentPane();
        pack();
        setLocationRelativeTo(SwingUtilities.getWindowAncestor(plottingPanel));
    }

    @Override
    public void export() {

    }

    private void initContentPane() {
    }
}
