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

import com.vthmgnpipola.mcplot.messaging.MessagePublisher;
import com.vthmgnpipola.mcplot.messaging.MessageRouter;
import com.vthmgnpipola.mcplot.nmath.EvaluationContext;
import com.vthmgnpipola.mcplot.nmath.Function;
import com.vthmgnpipola.mcplot.nmath.FunctionEvaluator;

public class FunctionPlotter {
    private final FunctionEvaluator evaluator;
    private FunctionPlot plot;

    private final MessagePublisher messagePublisher;

    public FunctionPlotter(Function function, EvaluationContext context) {
        this.plot = new SingleVariableFunctionPlot();
        evaluator = new FunctionEvaluator(function, context, plot);

        messagePublisher = MessageRouter.publisher();
        messagePublisher.publish(new PlotRegistrationMessage(plot));
    }

    public void setPlot(FunctionPlot plot) {
        this.plot = plot;
        evaluator.setResultConsumer(plot);
        messagePublisher.publish(new PlotUpdateMessage());
    }

    public FunctionPlot getPlot() {
        return plot;
    }

    public FunctionEvaluator getEvaluator() {
        return evaluator;
    }

    public void setLegend(String legend) {
        plot.setLegend(legend);
        messagePublisher.publish(new PlotUpdateMessage());
    }

    public void setVisible(boolean visible) {
        plot.setVisible(visible);
        messagePublisher.publish(new PlotUpdateMessage());
    }

    public void setPlottingParameter(String key, String value) {
        plot.setPlottingParameter(key, value);
        messagePublisher.publish(new PlotUpdateMessage());
    }

    public void evaluate(boolean force) {
        EvaluationContext context = evaluator.getContext();

        if (!force &&
                !(context.getViewportStart() < plot.getStartInput() || context.getViewportEnd() > plot.getEndInput())) {
            return;
        }

        double domainStart = context.getDomainStart();
        double domainEnd = context.getDomainEnd();

        Double definedDomainStart = evaluator.getDomainStart();
        if (definedDomainStart != null && domainStart < definedDomainStart) {
            domainStart = Math.max(domainStart, definedDomainStart);
        }

        Double definedDomainEnd = evaluator.getDomainEnd();
        if (definedDomainEnd != null && domainEnd > definedDomainEnd) {
            domainEnd = Math.min(domainEnd, definedDomainEnd);
        }

        if (force || domainStart < plot.getStartInput() || domainEnd > plot.getEndInput()) {
            evaluator.evaluate(false);
        }
    }
}
