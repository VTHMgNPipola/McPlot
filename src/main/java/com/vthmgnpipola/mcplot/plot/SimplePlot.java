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
import java.awt.geom.Path2D;

public class SimplePlot implements Plot {
    private Path2D.Double path;
    private String legend;
    private boolean visible;
    private Trace trace;

    @Override
    public String getLegend() {
        return legend;
    }

    public void setLegend(String legend) {
        this.legend = legend;
    }

    @Override
    public boolean isInvisible() {
        return !visible;
    }

    @Override
    public boolean isInvalid() {
        return path == null;
    }

    @Override
    public Trace getTrace() {
        return trace;
    }

    public void setTrace(Trace trace) {
        this.trace = trace;
    }

    @Override
    public void plot(Graphics2D g, AffineTransform tx, PlottingPanelContext context) {
        g.setStroke(trace.getStroke());
        g.setColor(trace.getColor());
        g.draw(tx.createTransformedShape(path));
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
