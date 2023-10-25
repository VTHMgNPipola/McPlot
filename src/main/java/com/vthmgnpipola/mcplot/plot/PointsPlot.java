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

public class PointsPlot implements Plot {
    private double[] xpoints;
    private double[] ypoints;
    private Path2D.Double path;
    private String legend;
    private boolean visible = true;
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

    @Override
    public void setPlottingParameter(String key, boolean value) {
    }

    @Override
    public boolean getPlottingParameter(String key) {
        return false;
    }

    @Override
    public void plot(Graphics2D g, AffineTransform tx, PlottingPanelContext context) {
        if (path != null) {
            g.setStroke(trace.getStroke());
            g.setColor(trace.getColor());
            g.draw(tx.createTransformedShape(path));
        }
    }

    public int getNumberOfPoints() {
        return xpoints.length;
    }

    public void setPoints(double[] xpoints, double[] ypoints) {
        if (xpoints.length != ypoints.length) {
            throw new IllegalArgumentException("The number of X points must match the number of Y points!");
        }

        this.xpoints = xpoints;
        this.ypoints = ypoints;

        path = new Path2D.Double(Path2D.WIND_NON_ZERO, xpoints.length);
        if (xpoints.length > 0) {
            path.moveTo(xpoints[0], ypoints[0]);
            for (int i = 1; i < xpoints.length; i++) {
                path.lineTo(xpoints[i], ypoints[i]);
            }
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
