/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2024  VTHMgNPipola
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
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SingleVariableFunctionPlot implements FunctionPlot {
    public static final String KEY_TRACE_TYPE = "key.trace.type";
    public static final String VALUE_TRACE_TYPE_DEFAULT = "value.trace.type.default";
    public static final String VALUE_TRACE_TYPE_DISABLED = "value.trace.type.disabled";

    public static final String KEY_FILLED_SHAPE = "key.filled.shape";
    public static final String VALUE_FILL_DISABLE = "value.disable.fill";
    public static final String VALUE_FILL_SOLID = "value.solid.fill";

    public static final String KEY_DOT_SHAPE = "key.dot.shape";
    public static final String VALUE_DOT_SHAPE_DISABLED = "key.dot.shape.disabled";
    public static final String VALUE_DOT_SHAPE_CIRCLE = "value.dot.shape.circle";
    public static final String VALUE_DOT_SHAPE_SQUARE = "value.dot.shape.square";
    public static final String VALUE_DOT_SHAPE_TRIANGLE = "value.dot.shape.triangle";
    public static final String VALUE_DOT_SHAPE_DIAMOND = "value.dot.shape.diamond";

    public static final String KEY_DOT_SIZE = "key.dot.size";
    public static final String VALUE_DOT_SIZE_FOLLOWS_TRACE = "value.dot.size.followsTrace";

    private final Map<String, String> parameters;
    private String legend;
    private boolean visible;
    private final Trace trace;

    private Path2D.Double path;
    private double[] xpoints;
    private double[] ypoints;
    private int numberOfPoints;
    private boolean sessionActive;

    private Path2D.Double filledPath;

    public SingleVariableFunctionPlot() {
        parameters = new HashMap<>();

        legend = null;
        visible = true;
        trace = new Trace();

        xpoints = new double[5];
        ypoints = new double[5];
        numberOfPoints = 0;
        sessionActive = false;
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
    public boolean hasPlottingParameter(String key, String value, boolean defaultValue) {
        String plottingParameter = getPlottingParameter(key);
        return plottingParameter != null ? plottingParameter.equals(value) : defaultValue;
    }

    @Override
    public void plot(Graphics2D g, AffineTransform tx, PlottingPanelContext context) {
        if (hasPlottingParameter(KEY_FILLED_SHAPE, VALUE_FILL_SOLID, false)) {
            Color traceColor = getTrace().getColor();
            Color fillColor = new Color(traceColor.getRed(), traceColor.getGreen(), traceColor.getBlue(),
                    (int) Math.min((context.fillTransparency * 2.55), 255));
            g.setColor(fillColor);
            g.fill(tx.createTransformedShape(filledPath));
        }

        g.setStroke(getTrace().getStroke());
        g.setColor(getTrace().getColor());
        if (hasPlottingParameter(KEY_TRACE_TYPE, VALUE_TRACE_TYPE_DEFAULT, true)) {
            g.draw(tx.createTransformedShape(path));
        }

        if (getPlottingParameter(KEY_DOT_SHAPE) != null) {
            int size;
            try {
                size = Integer.parseInt(getPlottingParameter(KEY_DOT_SIZE));
            } catch (Throwable t) {
                size = context.traceWidth * 3;
            }
            // Copied for loops so the switch wouldn't happen at every point, even though I don't know if this makes any
            // difference
            switch (getPlottingParameter(KEY_DOT_SHAPE)) {
                case VALUE_DOT_SHAPE_CIRCLE -> {
                    for (int i = 0; i < numberOfPoints; i++) {
                        if (!Double.isNaN(ypoints[i])) {
                            g.fillOval((int) ((xpoints[i] * tx.getScaleX()) - (size / 2)),
                                    (int) ((ypoints[i] * tx.getScaleY()) - (size / 2)), size, size);
                        }
                    }
                }
                case VALUE_DOT_SHAPE_SQUARE -> {
                    for (int i = 0; i < numberOfPoints; i++) {
                        if (!Double.isNaN(ypoints[i])) {
                            g.fillRect((int) ((xpoints[i] * tx.getScaleX()) - (size / 2)),
                                    (int) ((ypoints[i] * tx.getScaleY()) - (size / 2)), size, size);
                        }
                    }
                }
                case VALUE_DOT_SHAPE_TRIANGLE -> {
                    AffineTransform shapeTransform = new AffineTransform();
                    int[] triangleX = new int[]{-(size / 2), 0, (size / 2)};
                    int[] triangleY = new int[]{(size / 2), -(size / 2), (size / 2)};
                    Polygon triangle = new Polygon(triangleX, triangleY, 3);
                    for (int i = 0; i < numberOfPoints; i++) {
                        if (!Double.isNaN(ypoints[i])) {
                            shapeTransform.setToTranslation(xpoints[i] * tx.getScaleX(), ypoints[i] * tx.getScaleY());
                            g.fill(shapeTransform.createTransformedShape(triangle));
                        }
                    }
                }
                case VALUE_DOT_SHAPE_DIAMOND -> {
                    AffineTransform shapeTransform = new AffineTransform();
                    int[] triangleX = new int[]{-(size / 2), 0, (size / 2), 0};
                    int[] triangleY = new int[]{0, -(size / 2), 0, (size / 2)};
                    Polygon rect = new Polygon(triangleX, triangleY, 4);
                    for (int i = 0; i < numberOfPoints; i++) {
                        if (!Double.isNaN(ypoints[i])) {
                            shapeTransform.setToTranslation(xpoints[i] * tx.getScaleX(), ypoints[i] * tx.getScaleY());
                            g.fill(shapeTransform.createTransformedShape(rect));
                        }
                    }
                }
                default -> {
                }
            }
        }
    }

    @Override
    public double getStartInput() {
        if (numberOfPoints > 0) {
            return xpoints[0];
        } else {
            return Double.NaN;
        }
    }

    @Override
    public double getEndInput() {
        if (numberOfPoints > 0) {
            return xpoints[numberOfPoints - 1];
        } else {
            return Double.NaN;
        }
    }

    @Override
    public void accept(Double input, Double result) throws IllegalStateException {
        if (sessionActive) {
            if (Double.isNaN(input)) {
                return;
            }

            int i = numberOfPoints;
            while (i > 0 && xpoints[i - 1] > input) {
                i--;
            }

            if (i == 0 || (xpoints[i - 1] != input && !(Double.isNaN(result) && Double.isNaN(ypoints[i - 1])))) {
                if (numberOfPoints >= xpoints.length) {
                    xpoints = Arrays.copyOf(xpoints, numberOfPoints + 5);
                    ypoints = Arrays.copyOf(ypoints, numberOfPoints + 5);
                }

                for (int j = numberOfPoints; j > i; j--) {
                    xpoints[j] = xpoints[j - 1];
                    ypoints[j] = ypoints[j - 1];
                }
                xpoints[i] = input;
                ypoints[i] = result;
                numberOfPoints++;
            }
        } else {
            throw new IllegalStateException("There is no ongoing consumer session to accept results!");
        }
    }

    @Override
    public void complete() throws IllegalStateException {
        if (sessionActive) {
            if (numberOfPoints == 0) {
                path = null;
                sessionActive = false;
                return;
            }

            path = new Path2D.Double(Path2D.WIND_NON_ZERO, numberOfPoints);
            sessionActive = false;

            boolean moving = true;
            for (int i = 0; i < numberOfPoints; i++) {
                if (Double.isNaN(ypoints[i])) {
                    moving = true;
                } else if (moving) {
                    path.moveTo(xpoints[i], ypoints[i]);
                    moving = false;
                } else {
                    path.lineTo(xpoints[i], ypoints[i]);
                }
            }

            filledPath = (Path2D.Double) path.clone();
            filledPath.lineTo(getEndInput(), 0);
            filledPath.lineTo(getStartInput(), 0);
            filledPath.closePath();
        } else {
            throw new IllegalStateException("There is no ongoing consumer session to complete!");
        }
    }

    @Override
    public void start() {
        path = null;
        filledPath = null;
        numberOfPoints = 0;
        sessionActive = true;
    }

    @Override
    public void invalidate() {
        path = null;
        filledPath = null;
        numberOfPoints = 0;
        sessionActive = false;
    }
}
