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

package com.vthmgnpipola.mcplot.ngui;

import com.vthmgnpipola.mcplot.GraphAxis;
import com.vthmgnpipola.mcplot.nmath.EvaluationContext;
import com.vthmgnpipola.mcplot.nmath.MathEventStreamer;
import com.vthmgnpipola.mcplot.plot.Trace;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.io.Serial;
import java.io.Serializable;

public class PlottingPanelContext implements Serializable {
    public static final int INITIAL_PIXELS_PER_STEP = 75;
    @Serial
    private static final long serialVersionUID = 8320578294132283362L;
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
    public int traceWidth;
    public boolean antialias;
    public boolean functionLegends;
    public boolean showDecAsFractions;
    public boolean showScientificNotation;
    public boolean showEngineeringNotation;
    public boolean drawAxisOverFunc;
    public boolean drawMinorGrid;
    public boolean drawGrid;
    public boolean drawAxisValues;
    public int minorGridDivisions;
    public double fillTransparency;
    private transient PlottingPanel base;
    private transient EvaluationContext evaluationContext;

    public PlottingPanelContext(PlottingPanel base) {
        reset(base);
    }

    public void reset(PlottingPanel base) {
        this.base = base;

        zoom = 1;
        zoomPos = 0;
        flPositionX = 20;
        flPositionY = 20;
        axisX = new GraphAxis();
        axisY = new GraphAxis();
        samplesPerCell = 25;
        traceWidth = Trace.DEFAULT_TRACE_WIDTH;
        antialias = false;
        functionLegends = true;
        showDecAsFractions = false;
        showScientificNotation = true;
        showEngineeringNotation = false;
        drawAxisOverFunc = false;
        drawMinorGrid = true;
        drawGrid = true;
        drawAxisValues = true;
        minorGridDivisions = 5;
        fillTransparency = 25;
    }

    public void setSamplesPerCell(int samplesPerCell) {
        this.samplesPerCell = samplesPerCell;
        updateEvaluationContext();
        recalculateAllFunctions(true);
    }

    public void setAntialias(boolean antialias) {
        this.antialias = antialias;
        base.repaint();
    }

    public void setTraceWidth(int traceWidth) {
        this.traceWidth = traceWidth;
        Trace.DEFAULT_TRACE_WIDTH = traceWidth;
        base.getPlots().parallelStream().forEach(p -> p.getTrace().updateStroke());
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

    public void setShowScientificNotation(boolean showScientificNotation) {
        this.showScientificNotation = showScientificNotation;
        base.repaint();
    }

    public void setShowEngineeringNotation(boolean showEngineeringNotation) {
        this.showEngineeringNotation = showEngineeringNotation;
        base.repaint();
    }

    public void setDrawAxisOverFunc(boolean drawAxisOverFunc) {
        this.drawAxisOverFunc = drawAxisOverFunc;
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
        updateEvaluationContext();
    }

    void setEvaluationContext(EvaluationContext evaluationContext) {
        this.evaluationContext = evaluationContext;
        updateEvaluationContext();
    }

    public void updateEvaluationContext() {
        evaluationContext.update(this);
    }

    public void recalculateAllFunctions(boolean force) {
        MathEventStreamer.getInstance().functionUpdate(true, force);
    }
}
