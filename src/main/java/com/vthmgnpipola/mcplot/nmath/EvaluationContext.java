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

public class EvaluationContext {
    private double domainStart;
    private double domainEnd;
    private double step;

    private double viewportStart;
    private double viewportEnd;

    public void update(PlottingPanelContext plottingPanelContext) {
        double zoomX = plottingPanelContext.axisX.scale * plottingPanelContext.pixelsPerStep *
                plottingPanelContext.zoom * plottingPanelContext.axisX.unit.getScale();
        viewportStart = plottingPanelContext.cameraX / zoomX;
        viewportEnd = viewportStart + (plottingPanelContext.getBase().getWidth() / zoomX);

        domainStart = viewportStart - ((viewportEnd - viewportStart) / 2);
        domainEnd = viewportEnd + ((viewportEnd - viewportStart) / 2);

        double viewportWidth = viewportEnd - viewportStart;
        int viewportSteps = (plottingPanelContext.getBase().getWidth() / plottingPanelContext.pixelsPerStep) *
                plottingPanelContext.samplesPerCell;
        step = viewportWidth / viewportSteps;
    }

    public double getDomainStart() {
        return domainStart;
    }

    public double getDomainEnd() {
        return domainEnd;
    }

    public double getStep() {
        return step;
    }

    public double getViewportStart() {
        return viewportStart;
    }

    public double getViewportEnd() {
        return viewportEnd;
    }
}
