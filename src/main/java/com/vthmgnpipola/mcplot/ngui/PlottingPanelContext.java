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

package com.vthmgnpipola.mcplot.ngui;

import com.vthmgnpipola.mcplot.GraphAxis;
import com.vthmgnpipola.mcplot.nmath.MathEventStreamer;
import com.vthmgnpipola.mcplot.nmath.Plot;
import java.awt.BasicStroke;
import java.awt.Stroke;
import java.io.Serial;
import java.io.Serializable;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class PlottingPanelContext implements Serializable {
    public static final int INITIAL_PIXELS_PER_STEP = 75;
    @Serial
    private static final long serialVersionUID = 3754069167198513615L;
    public int cameraX;
    public int cameraY;
    public int pixelsPerStep = INITIAL_PIXELS_PER_STEP;
    public double zoom;
    public int zoomPos;
    public int flPositionX;
    public int flPositionY;
    public GraphAxis axisX;
    public GraphAxis axisY;
    public int samplesPerCell;
    public double maxStep;
    public int traceWidth;
    public boolean antialias;
    public boolean functionLegends;
    public boolean showDecAsFractions;
    public boolean drawMinorGrid;
    public boolean drawGrid;
    public boolean drawAxisValues;
    public int minorGridDivisions;
    public double fillTransparency;
    public transient Stroke defaultTraceStroke, dashedTraceStroke, dottedTraceStroke, dashedDottedTraceStroke;
    private transient PlottingPanel base;

    public PlottingPanelContext(PlottingPanel base) {
        this.base = base;

        zoom = 1;
        zoomPos = 0;
        flPositionX = 20;
        flPositionY = 20;
        axisX = new GraphAxis();
        axisY = new GraphAxis();
        samplesPerCell = 25;
        maxStep = 0.5;
        traceWidth = 3;
        antialias = false;
        functionLegends = true;
        drawMinorGrid = true;
        drawGrid = true;
        drawAxisValues = true;
        minorGridDivisions = 5;
        fillTransparency = 25;

        updateTraces();
    }

    public void setSamplesPerCell(int samplesPerCell) {
        this.samplesPerCell = samplesPerCell;
        recalculateAllFunctions(true);
    }

    public void setMaxStep(double maxStep) {
        this.maxStep = maxStep;
        recalculateAllFunctions(true);
    }

    public void setAntialias(boolean antialias) {
        this.antialias = antialias;
        base.repaint();
    }

    public void updateTraces() {
        defaultTraceStroke = new BasicStroke(traceWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        dashedTraceStroke = new BasicStroke(traceWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f,
                new float[]{10f, 7f}, 0);
        dottedTraceStroke = new BasicStroke(traceWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f,
                new float[]{3f, 7f}, 0);
        dashedDottedTraceStroke = new BasicStroke(traceWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f,
                new float[]{10f, 7f, 3f, 7f}, 0);
    }

    public void setTraceWidth(int traceWidth) {
        this.traceWidth = traceWidth;
        updateTraces();
        base.repaint();
    }

    public void setFillTransparency(double fillTransparency) {
        this.fillTransparency = fillTransparency;
        base.repaint();
    }

    public void setFunctionLegends(boolean functionLegends) {
        this.functionLegends = functionLegends;
        base.repaint();
    }

    public void setShowDecAsFractions(boolean showDecAsFractions) {
        this.showDecAsFractions = showDecAsFractions;
        base.repaint();
    }

    public void setDrawMinorGrid(boolean drawMinorGrid) {
        this.drawMinorGrid = drawMinorGrid;
        base.repaint();
    }

    public void setDrawGrid(boolean drawGrid) {
        this.drawGrid = drawGrid;
        base.repaint();
    }

    public void setDrawAxisValues(boolean drawAxisValues) {
        this.drawAxisValues = drawAxisValues;
        base.repaint();
    }

    public void setMinorGridDivisions(int minorGridDivisions) {
        this.minorGridDivisions = minorGridDivisions;
        base.repaint();
    }

    public JFrame getBaseFrame() {
        return (JFrame) SwingUtilities.getWindowAncestor(base);
    }

    /**
     * The base of this context is the {@link PlottingPanel} it is bound to. Do not call this method unless you
     * absolutely need to.
     *
     * @return PlottingPanel bound to this context.
     */
    public PlottingPanel getBase() {
        return base;
    }

    void setBase(PlottingPanel base) {
        this.base = base;
    }

    public void recalculateAllFunctions(boolean force) {
        MathEventStreamer.getInstance().functionUpdate(true, force);
    }

    public Stroke getStroke(Plot.TraceType traceType) {
        if (traceType == null) {
            return defaultTraceStroke;
        }
        return switch (traceType) {
            case TRACE_TYPE_DOTTED -> dottedTraceStroke;
            case TRACE_TYPE_DASHED -> dashedTraceStroke;
            case TRACE_TYPE_DASHED_DOTTED -> dashedDottedTraceStroke;
            default -> defaultTraceStroke;
        };
    }
}
