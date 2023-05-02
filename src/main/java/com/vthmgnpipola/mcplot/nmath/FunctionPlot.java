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

package com.vthmgnpipola.mcplot.nmath;

import com.vthmgnpipola.mcplot.ngui.PlottingPanelContext;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class FunctionPlot implements Plot {
    private final FunctionParameters parameters;
    private Path2D.Double path;
    private double startX;
    private double endX;

    public FunctionPlot() {
        parameters = new FunctionParameters();
    }

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
        return parameters.getLegend();
    }

    @Override
    public boolean isInvisible() {
        return !parameters.isVisible();
    }

    @Override
    public TraceType getTraceType() {
        return parameters.getTraceType();
    }

    @Override
    public Color getTraceColor() {
        return parameters.getTraceColor();
    }

    @Override
    public void plot(Graphics2D g, AffineTransform tx, PlottingPanelContext context) {
        if (parameters.isFilled()) {
            Path2D.Double fill = (Path2D.Double) path.clone();
            fill.lineTo(getEndX(), 0);
            fill.lineTo(getStartX(), 0);
            fill.closePath();

            Color traceColor = parameters.getTraceColor();
            Color fillColor = new Color(traceColor.getRed(), traceColor.getGreen(), traceColor.getBlue(),
                    (int) Math.min((context.fillTransparency * 2.55), 255));
            g.setColor(fillColor);
            g.fill(tx.createTransformedShape(fill));
        }
        g.setStroke(context.getStroke(parameters.getTraceType()));
        g.setColor(parameters.getTraceColor());
        g.draw(tx.createTransformedShape(path));
    }

    public void loadFunction(Function function) {
        parameters.setLegend(function.getDefinition());
        parameters.setVisible(function.isVisible());
        parameters.setFilled(function.isFilled());
        parameters.setTraceType(function.getTraceType());
        parameters.setTraceColor(function.getTraceColor());
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }
}
