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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Map;

public class FunctionLinePlot implements FunctionPlot {
    public static final String KEY_FILLED_SHAPE = "key.filled.shape";
    public static final String VALUE_FILL_DISABLE = "value.disable.fill";
    public static final String VALUE_FILL_SOLID = "value.solid.fill";

    private final Map<String, String> parameters;
    private String legend;
    private boolean visible;
    private final Trace trace;

    private Path2D.Double path, pathBuffer;
    private double startX;
    private double endX;

    private Path2D.Double filledPath;

    private boolean moving, hasPoints;
    private double lastInput;

    public FunctionLinePlot() {
        parameters = new HashMap<>();

        legend = null;
        visible = true;
        trace = new Trace();
    }

    @Override
    public String getLegend() {
        return legend;
    }

    @Override
    public void setLegend(String legend) {
        this.legend = legend;
    }

    @Override
    public boolean isInvisible() {
        return !visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
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
    public void setPlottingParameter(String key, String value) {
        parameters.put(key, value);
    }

    @Override
    public String getPlottingParameter(String key) {
        return parameters.get(key);
    }

    @Override
    public boolean hasPlottingParameter(String key, String value) {
        String plottingParameter = getPlottingParameter(key);
        return plottingParameter != null && plottingParameter.equals(value);
    }

    @Override
    public void plot(Graphics2D g, AffineTransform tx, PlottingPanelContext context) {
        if (hasPlottingParameter(KEY_FILLED_SHAPE, VALUE_FILL_SOLID)) {
            Color traceColor = getTrace().getColor();
            Color fillColor = new Color(traceColor.getRed(), traceColor.getGreen(), traceColor.getBlue(),
                    (int) Math.min((context.fillTransparency * 2.55), 255));
            g.setColor(fillColor);
            g.fill(tx.createTransformedShape(filledPath));
        }
        g.setStroke(getTrace().getStroke());
        g.setColor(getTrace().getColor());
        g.draw(tx.createTransformedShape(path));
    }

    @Override
    public double getStartInput() {
        return startX;
    }

    @Override
    public double getEndInput() {
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

            if (path != null) {
                filledPath = (Path2D.Double) path.clone();
                filledPath.lineTo(getEndInput(), 0);
                filledPath.lineTo(getStartInput(), 0);
                filledPath.closePath();
            }
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
        filledPath = null;
        path = null;
        pathBuffer = null;
        lastInput = Double.NaN;
        startX = Double.NaN;
        endX = Double.NaN;
    }
}
