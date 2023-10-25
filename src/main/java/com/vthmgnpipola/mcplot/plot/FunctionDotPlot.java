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
import java.util.Arrays;

public class FunctionDotPlot implements FunctionPlot {
    private String legend;
    private boolean visible;
    private boolean valid;

    private final Trace trace;
    private final double startInput;
    private final double endInput;

    private double[] xpoints;
    private double[] ypoints;
    private int points;

    private boolean sessionActive;
    private double[] sessionXPoints;
    private double[] sessionYPoints;
    private int sessionPoints;

    public FunctionDotPlot() {
        legend = null;
        visible = true;
        valid = false;
        trace = new Trace();
        startInput = Double.NaN;
        endInput = Double.NaN;

        xpoints = new double[5];
        ypoints = new double[5];
        points = 0;

        sessionActive = false;
        sessionXPoints = new double[5];
        sessionYPoints = new double[5];
        sessionPoints = 0;
    }

    @Override
    public void accept(Double input, Double result) throws IllegalStateException {
        if (sessionActive) {
            if (Double.isNaN(input) || Double.isNaN(result)) {
                return;
            }

            if (sessionPoints >= sessionXPoints.length) {
                sessionXPoints = Arrays.copyOf(sessionXPoints, sessionPoints + 5);
                sessionYPoints = Arrays.copyOf(sessionYPoints, sessionPoints + 5);
            }

            sessionXPoints[sessionPoints] = input;
            sessionYPoints[sessionPoints] = result;
            sessionPoints++;
        } else {
            throw new IllegalStateException("There is no ongoing consumer session to accept results!");
        }
    }

    @Override
    public void complete() throws IllegalStateException {
        if (sessionActive) {
            if (sessionPoints > xpoints.length) {
                xpoints = new double[sessionPoints];
                ypoints = new double[sessionPoints];
            }

            for (int i = 0; i < sessionPoints; i++) {
                xpoints[i] = sessionXPoints[i];
                ypoints[i] = sessionYPoints[i];
            }

            points = sessionPoints;
            valid = true;
        } else {
            throw new IllegalStateException("There is no ongoing consumer session to complete!");
        }
    }

    @Override
    public void start() {
        sessionPoints = 0;
        sessionActive = true;
    }

    @Override
    public void invalidate() {
        valid = false;
        sessionActive = false;
    }

    @Override
    public double getStartInput() {
        return startInput;
    }

    @Override
    public double getEndInput() {
        return endInput;
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
        return !valid;
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
        g.setStroke(getTrace().getStroke());
        g.setColor(getTrace().getColor());

        int size = context.traceWidth;
        for (int i = 0; i < points; i++) {
            g.fillOval((int) ((xpoints[i] * tx.getScaleX()) - (size / 2)), (int) ((ypoints[i] * tx.getScaleY()) - (size / 2)), size, size);
        }
    }
}
