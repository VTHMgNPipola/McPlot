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

package com.vthmgnpipola.mcplot.nmath;

import com.vthmgnpipola.mcplot.ngui.PlottingPanelContext;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class SimplePlot implements Plot {
    private Path2D.Double path;
    private String legend;
    private boolean visible;
    private TraceType traceType;
    private Color traceColor;

    @Override
    public Path2D.Double getPath() {
        return path;
    }

    @Override
    public void setPath(Path2D.Double path) {
        this.path = path;
    }

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
    public TraceType getTraceType() {
        return traceType;
    }

    public void setTraceType(TraceType traceType) {
        this.traceType = traceType;
    }

    @Override
    public Color getTraceColor() {
        return traceColor;
    }

    public void setTraceColor(Color traceColor) {
        this.traceColor = traceColor;
    }

    @Override
    public void plot(Graphics2D g, AffineTransform tx, PlottingPanelContext context) {
        g.setStroke(context.getStroke(traceType));
        g.setColor(traceColor);
        g.draw(tx.createTransformedShape(path));
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
