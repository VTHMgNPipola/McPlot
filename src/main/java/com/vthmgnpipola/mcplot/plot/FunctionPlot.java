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
import com.vthmgnpipola.mcplot.nmath.EvaluationResultConsumer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class FunctionPlot implements Plot, EvaluationResultConsumer<Double, Double> {
    private final FunctionPlotParameters parameters;
    private Path2D.Double path, pathBuffer;
    private double startX;
    private double endX;

    private boolean moving, hasPoints;
    private double lastInput;

    public FunctionPlot() {
        parameters = new FunctionPlotParameters();
    }

    public FunctionPlotParameters getParameters() {
        return parameters;
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
    public boolean isInvalid() {
        return path == null;
    }

    @Override
    public Trace getTrace() {
        return parameters.getTrace();
    }

    @Override
    public void plot(Graphics2D g, AffineTransform tx, PlottingPanelContext context) {
        if (parameters.isFilled()) {
            Path2D.Double fill = (Path2D.Double) path.clone();
            fill.lineTo(getEndX(), 0);
            fill.lineTo(getStartX(), 0);
            fill.closePath();

            Color traceColor = parameters.getTrace().getColor();
            Color fillColor = new Color(traceColor.getRed(), traceColor.getGreen(), traceColor.getBlue(),
                    (int) Math.min((context.fillTransparency * 2.55), 255));
            g.setColor(fillColor);
            g.fill(tx.createTransformedShape(fill));
        }
        g.setStroke(parameters.getTrace().getStroke());
        g.setColor(parameters.getTrace().getColor());
        g.draw(tx.createTransformedShape(path));
    }

    public double getStartX() {
        return startX;
    }

    public double getEndX() {
        return endX;
    }

    @Override
    public void accept(Double input, Double result) throws IllegalStateException {
        if (pathBuffer != null) {
            if (!Double.isNaN(startX)) {
                startX = input;
            }

            if (Double.isNaN(result)) {
                moving = true;
            } else if (moving) {
                pathBuffer.moveTo(input, result);
                moving = false;
            } else {
                hasPoints = true;
                pathBuffer.lineTo(input, result);
            }

            lastInput = input;
        } else {
            throw new IllegalStateException("There is no ongoing consumer session to accept results!");
        }
    }

    @Override
    public void complete() throws IllegalStateException {
        if (pathBuffer != null) {
            path = hasPoints ? pathBuffer : null;
            pathBuffer = null;
            endX = lastInput;
        } else {
            throw new IllegalStateException("There is no ongoing consumer session to complete!");
        }
    }

    @Override
    public void start() {
        pathBuffer = new Path2D.Double();
        lastInput = Double.NaN;
        startX = Double.NaN;
        endX = Double.NaN;

        moving = true;
        hasPoints = false;
    }

    @Override
    public void invalidate() {
        path = null;
        pathBuffer = null;
        lastInput = Double.NaN;
        startX = Double.NaN;
        endX = Double.NaN;
    }
}
