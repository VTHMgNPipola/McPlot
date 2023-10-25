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

package com.vthmgnpipola.mcplot.plot;

import com.vthmgnpipola.mcplot.ngui.PlottingPanelContext;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public interface Plot {
    String getLegend();

    void setLegend(String legend);

    boolean isInvisible();

    void setVisible(boolean visible);

    boolean isInvalid();

    Trace getTrace();

    void setPlottingParameter(String key, boolean value);

    boolean getPlottingParameter(String key);

    void plot(Graphics2D g, AffineTransform tx, PlottingPanelContext context);
}
