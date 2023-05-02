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

package com.vthmgnpipola.mcplot.ngui;

import com.vthmgnpipola.mcplot.nmath.PointsPlot;

import java.awt.*;
import java.text.MessageFormat;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class PointsPlotSettingsDialog extends MDialog {
    private final PointsPlot plot;

    public PointsPlotSettingsDialog(PointsPlot plot, int index, Window owner) {
        super(owner, MessageFormat.format(BUNDLE.getString("pointsPlotSettings.title"), index, plot.getLegend()),
                ModalityType.APPLICATION_MODAL);
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.plot = plot;
    }

    @Override
    public void init() {

    }
}
