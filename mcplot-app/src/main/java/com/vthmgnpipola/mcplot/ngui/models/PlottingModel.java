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

package com.vthmgnpipola.mcplot.ngui.models;

import com.vthmgnpipola.mcplot.GraphAxis;
import com.vthmgnpipola.mcplot.plot.Trace;

import java.io.Serial;
import java.io.Serializable;

public class PlottingModel extends AbstractModel implements Serializable {
    public static final String MODEL_NAME = "plottingModel";
    public static final int INITIAL_PIXELS_PER_STEP = 75;
    public static final String PROPERTY_CAMERA_X = "cameraX";
    public static final String PROPERTY_CAMERA_Y = "cameraY";
    public static final String PROPERTY_PIXELS_PER_STEP = "pixelsPerStep";
    public static final String PROPERTY_ZOOM = "zoom";
    public static final String PROPERTY_ZOOM_POS = "zoomPos";
    public static final String PROPERTY_FL_POSITION_X = "flPositionX";
    public static final String PROPERTY_FL_POSITION_Y = "flPositionY";
    public static final String PROPERTY_AXIS_X = "axisX";
    public static final String PROPERTY_AXIS_Y = "axisY";
    public static final String PROPERTY_SAMPLES_PER_CELL = "samplesPerCell";
    public static final String PROPERTY_TRACE_WIDTH = "traceWidth";
    public static final String PROPERTY_ANTIALIAS = "antialias";
    public static final String PROPERTY_FUNCTION_LEGENDS = "functionLegends";
    public static final String PROPERTY_SHOW_DEC_AS_FRACTIONS = "showDecAsFractions";
    public static final String PROPERTY_SHOW_SCIENTIFIC_NOTATION = "showScientificNotation";
    public static final String PROPERTY_SHOW_ENGINEERING_NOTATION = "showEngineeringNotation";
    public static final String PROPERTY_DRAW_AXIS_OVER_FUNC = "drawAxisOverFunc";
    public static final String PROPERTY_DRAW_MINOR_GRID = "drawMinorGrid";
    public static final String PROPERTY_DRAW_GRID = "drawGrid";
    public static final String PROPERTY_DRAW_AXIS_VALUES = "drawAxisValues";
    public static final String PROPERTY_MINOR_GRID_DIVISIONS = "minorGridDivisions";
    public static final String PROPERTY_FILL_TRANSPARENCY = "fillTransparency";
    @Serial
    private static final long serialVersionUID = 3061574792858931268L;
    private int cameraX;
    private int cameraY;
    private int pixelsPerStep = INITIAL_PIXELS_PER_STEP;
    private double zoom;
    private int zoomPos;
    private int flPositionX;
    private int flPositionY;
    private GraphAxis axisX;
    private GraphAxis axisY;
    private int samplesPerCell;
    private int traceWidth;
    private boolean antialias;
    private boolean functionLegends;
    private boolean showDecAsFractions;
    private boolean showScientificNotation;
    private boolean showEngineeringNotation;
    private boolean drawAxisOverFunc;
    private boolean drawMinorGrid;
    private boolean drawGrid;
    private boolean drawAxisValues;
    private int minorGridDivisions;
    private double fillTransparency;

    public PlottingModel() {
        super(MODEL_NAME);

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

    public int getCameraX() {
        return cameraX;
    }

    public void setCameraX(int cameraX) {
        int oldCameraX = this.cameraX;
        this.cameraX = cameraX;
        firePropertyChange(PROPERTY_CAMERA_X, oldCameraX, cameraX);
    }

    public int getCameraY() {
        return cameraY;
    }

    public void setCameraY(int cameraY) {
        int oldCameraY = this.cameraY;
        this.cameraY = cameraY;
        firePropertyChange(PROPERTY_CAMERA_Y, oldCameraY, cameraY);
    }

    public int getPixelsPerStep() {
        return pixelsPerStep;
    }

    public void setPixelsPerStep(int pixelsPerStep) {
        int oldPixelsPerStep = this.pixelsPerStep;
        this.pixelsPerStep = pixelsPerStep;
        firePropertyChange(PROPERTY_PIXELS_PER_STEP, oldPixelsPerStep, pixelsPerStep);
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        double oldZoom = this.zoom;
        this.zoom = zoom;
        firePropertyChange(PROPERTY_ZOOM, oldZoom, zoom);
    }

    public int getZoomPos() {
        return zoomPos;
    }

    public void setZoomPos(int zoomPos) {
        int oldZoomPos = this.zoomPos;
        this.zoomPos = zoomPos;
        firePropertyChange(PROPERTY_ZOOM_POS, oldZoomPos, zoomPos);
    }

    public int getFlPositionX() {
        return flPositionX;
    }

    public void setFlPositionX(int flPositionX) {
        int oldFlPositionX = this.flPositionX;
        this.flPositionX = flPositionX;
        firePropertyChange(PROPERTY_FL_POSITION_X, oldFlPositionX, flPositionX);
    }

    public int getFlPositionY() {
        return flPositionY;
    }

    public void setFlPositionY(int flPositionY) {
        int oldFlPositionY = this.flPositionY;
        this.flPositionY = flPositionY;
        firePropertyChange(PROPERTY_FL_POSITION_Y, oldFlPositionY, flPositionY);
    }

    public GraphAxis getAxisX() {
        return axisX;
    }

    public void setAxisX(GraphAxis axisX) {
        GraphAxis oldAxisX = this.axisX;
        this.axisX = axisX;
        firePropertyChange(PROPERTY_AXIS_X, oldAxisX, axisX);
    }

    public GraphAxis getAxisY() {
        return axisY;
    }

    public void setAxisY(GraphAxis axisY) {
        GraphAxis oldAxisY = this.axisY;
        this.axisY = axisY;
        firePropertyChange(PROPERTY_AXIS_Y, oldAxisY, axisY);
    }

    public int getSamplesPerCell() {
        return samplesPerCell;
    }

    public void setSamplesPerCell(int samplesPerCell) {
        int oldSamplesPerCell = this.samplesPerCell;
        this.samplesPerCell = samplesPerCell;
        firePropertyChange(PROPERTY_SAMPLES_PER_CELL, oldSamplesPerCell, samplesPerCell);
    }

    public int getTraceWidth() {
        return traceWidth;
    }

    public void setTraceWidth(int traceWidth) {
        int oldTraceWidth = this.traceWidth;
        this.traceWidth = traceWidth;
        firePropertyChange(PROPERTY_TRACE_WIDTH, oldTraceWidth, traceWidth);
    }

    public boolean isAntialias() {
        return antialias;
    }

    public void setAntialias(boolean antialias) {
        boolean oldAntialias = this.antialias;
        this.antialias = antialias;
        firePropertyChange(PROPERTY_ANTIALIAS, oldAntialias, antialias);
    }

    public boolean isFunctionLegends() {
        return functionLegends;
    }

    public void setFunctionLegends(boolean functionLegends) {
        boolean oldFunctionLegends = this.functionLegends;
        this.functionLegends = functionLegends;
        firePropertyChange(PROPERTY_FUNCTION_LEGENDS, oldFunctionLegends, functionLegends);
    }

    public boolean isShowDecAsFractions() {
        return showDecAsFractions;
    }

    public void setShowDecAsFractions(boolean showDecAsFractions) {
        boolean oldShowDecAsFractions = this.showDecAsFractions;
        this.showDecAsFractions = showDecAsFractions;
        firePropertyChange(PROPERTY_SHOW_DEC_AS_FRACTIONS, oldShowDecAsFractions, showDecAsFractions);
    }

    public boolean isShowScientificNotation() {
        return showScientificNotation;
    }

    public void setShowScientificNotation(boolean showScientificNotation) {
        boolean oldShowScientificNotation = this.showScientificNotation;
        this.showScientificNotation = showScientificNotation;
        firePropertyChange(PROPERTY_SHOW_SCIENTIFIC_NOTATION, oldShowScientificNotation, showScientificNotation);
    }

    public boolean isShowEngineeringNotation() {
        return showEngineeringNotation;
    }

    public void setShowEngineeringNotation(boolean showEngineeringNotation) {
        boolean oldShowEngineeringNotation = this.showEngineeringNotation;
        this.showEngineeringNotation = showEngineeringNotation;
        firePropertyChange(PROPERTY_SHOW_ENGINEERING_NOTATION, oldShowEngineeringNotation, showEngineeringNotation);
    }

    public boolean isDrawAxisOverFunc() {
        return drawAxisOverFunc;
    }

    public void setDrawAxisOverFunc(boolean drawAxisOverFunc) {
        boolean oldDrawAxisOverFunc = this.drawAxisOverFunc;
        this.drawAxisOverFunc = drawAxisOverFunc;
        firePropertyChange(PROPERTY_DRAW_AXIS_OVER_FUNC, oldDrawAxisOverFunc, drawAxisOverFunc);
    }

    public boolean isDrawMinorGrid() {
        return drawMinorGrid;
    }

    public void setDrawMinorGrid(boolean drawMinorGrid) {
        boolean oldDrawMinorGrid = this.drawMinorGrid;
        this.drawMinorGrid = drawMinorGrid;
        firePropertyChange(PROPERTY_DRAW_MINOR_GRID, oldDrawMinorGrid, drawMinorGrid);
    }

    public boolean isDrawGrid() {
        return drawGrid;
    }

    public void setDrawGrid(boolean drawGrid) {
        boolean oldDrawGrid = this.drawGrid;
        this.drawGrid = drawGrid;
        firePropertyChange(PROPERTY_DRAW_GRID, oldDrawGrid, drawGrid);
    }

    public boolean isDrawAxisValues() {
        return drawAxisValues;
    }

    public void setDrawAxisValues(boolean drawAxisValues) {
        boolean oldDrawAxisValues = this.drawAxisValues;
        this.drawAxisValues = drawAxisValues;
        firePropertyChange(PROPERTY_DRAW_AXIS_VALUES, oldDrawAxisValues, drawAxisValues);
    }

    public int getMinorGridDivisions() {
        return minorGridDivisions;
    }

    public void setMinorGridDivisions(int minorGridDivisions) {
        int oldMinorGridDivisions = this.minorGridDivisions;
        this.minorGridDivisions = minorGridDivisions;
        firePropertyChange(PROPERTY_MINOR_GRID_DIVISIONS, oldMinorGridDivisions, minorGridDivisions);
    }

    public double getFillTransparency() {
        return fillTransparency;
    }

    public void setFillTransparency(double fillTransparency) {
        double oldFillTransparency = this.fillTransparency;
        this.fillTransparency = fillTransparency;
        firePropertyChange(PROPERTY_FILL_TRANSPARENCY, oldFillTransparency, fillTransparency);
    }
}
