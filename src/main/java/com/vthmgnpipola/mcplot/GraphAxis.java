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

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class GraphAxis {
    public double scale;
    public AxisType type;
    public GraphUnit unit;

    public GraphAxis() {
        scale = 1;
        type = AxisType.LINEAR;
        unit = GraphUnit.DEFAULT;
    }

    public enum AxisType {
        LINEAR(BUNDLE.getString("settings.plottingPanel.axisType.linear")),
        LOG_NATURAL(BUNDLE.getString("settings.plottingPanel.axisType.logNatural")),
        LOG10(BUNDLE.getString("settings.plottingPanel.axisType.log10"));

        public static final AxisType[] TYPES = new AxisType[]{LINEAR, LOG_NATURAL, LOG10};

        private final String name;

        AxisType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
