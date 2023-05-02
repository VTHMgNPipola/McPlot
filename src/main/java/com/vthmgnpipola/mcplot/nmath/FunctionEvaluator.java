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

import com.vthmgnpipola.mcplot.ngui.PlottingPanel;
import com.vthmgnpipola.mcplot.ngui.PlottingPanelContext;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for the "glue logic" between the {@link Function} class, that only stores data about
 * a function, the {@link MathEvaluatorPool} class, which is a thread pool that actually solves the expressions, and
 * the {@link MathEventStreamer} class, which streams modifications done to any function or constant to every other
 * {@code FunctionEvaluator} and {@link ConstantEvaluator}.
 */
public class FunctionEvaluator {
    private static final Pattern FUNCTION_CALL_PATTERN = Pattern.compile(" *[a-zA-Z]+[a-zA-Z0-9]* *\\" +
            "( *[a-zA-Z]+ *\\)");

    private final Function function;
    private final MathEventStreamer parent;
    private final PlottingPanel owner;

    private final ConstantEvaluator domainStartEvaluator;
    private final ConstantEvaluator domainEndEvaluator;

    private final FunctionPlot plot;
    private Expression expression;

    /**
     * Creates a new FunctionEvaluator and binds it to a function, event streamer and plotting panel. Also registers
     * two constant evaluators in the parent event streamer for the domain start and domain end values.
     *
     * @param function Function this function evaluator "takes care" of.
     * @param owner    Plotting panel where the function will be plotted.
     */
    public FunctionEvaluator(Function function, PlottingPanel owner) {
        this.function = function;
        this.parent = MathEventStreamer.getInstance();
        this.owner = owner;

        parent.registerFunctionEvaluator(this);

        domainStartEvaluator = new ConstantEvaluator(function.getDomainStart());

        domainEndEvaluator = new ConstantEvaluator(function.getDomainEnd());

        plot = new FunctionPlot();
        plot.loadFunction(function);
        owner.getPlots().add(plot);
    }

    public Function getFunction() {
        return function;
    }

    public void setDefinition(String definition) {
        function.setDefinition(definition);
        plot.loadFunction(function);
        parent.functionUpdate(true, true);
    }

    /**
     * Defines the definition string for the domain start constant. Doing so will trigger a constant update event on
     * the event streamer bound to this function evaluator, which will recalculate every constant and function
     * registered on the event streamer, effectively applying the new domain start value.
     *
     * @param domainStart Definition string of the domain start of the function.
     */
    public void setDomainStart(String domainStart) {
        domainStartEvaluator.setDefinition(domainStart);
    }

    /**
     * Defines the definition string for the domain end constant. Doing so will trigger a constant update event on
     * the event streamer bound to this function evaluator, which will recalculate every constant and function
     * registered on the event streamer, effectively applying the new domain end value.
     *
     * @param domainEnd Definition string of the domain end of the function.
     */
    public void setDomainEnd(String domainEnd) {
        domainEndEvaluator.setDefinition(domainEnd);
    }

    public void setTraceType(Plot.TraceType traceType) {
        function.setTraceType(traceType);
        plot.loadFunction(function);
        owner.repaint();
    }

    /**
     * Sets a new trace color on the function bound to this function evaluator and repaints the plotting panel
     * (doesn't recalculate any functions or constants while doing so).
     *
     * @param traceColor New trace color.
     */
    public void setTraceColor(Color traceColor) {
        function.setTraceColor(traceColor);
        plot.loadFunction(function);
        owner.repaint();
    }

    /**
     * Defines if the function bound to this function evaluator will be filled by the plotting panel or not, and
     * repaints it to apply the modification.
     *
     * @param filled Set to true if the function should be filled while rendering the graph in the plotting panel,
     *               false otherwise.
     */
    public void setFilled(boolean filled) {
        function.setFilled(filled);
        plot.loadFunction(function);
        owner.repaint();
    }

    /**
     * Defines if the function bound to this function evaluator should be calculated and displayed on the plotting
     * panel. If set to false, the function will never be recalculated or displayed on the plotting panel.
     *
     * @param visible Set to true if the function should be calculated and displayed, false otherwise.
     */
    public void setVisible(boolean visible) {
        function.setVisible(visible);
        plot.loadFunction(function);
        if (visible) {
            evaluate(true);
        }
        owner.repaint();
    }

    public void removePlotFromOwner() {
        owner.getPlots().remove(plot);
    }

    /**
     * This method takes care of determining the exact domain start, domain end and step size values, and submitting
     * the function that is bound to this function evaluator to be calculated on the {@link MathEvaluatorPool} that
     * was also bound to this function evaluator.
     * <p>
     * Note that the domain start and end constants aren't calculated at this step, instead this method simply checks
     * if they are defined. If so, it uses their values, otherwise it calculates them based on the camera position,
     * graph width and zoom.
     */
    public void evaluate(boolean force) {
        PlottingPanelContext context = owner.getContext();
        double zoomX = context.axisX.scale * context.pixelsPerStep * context.zoom *
                context.axisX.unit.getScale();
        double cameraStartX = context.cameraX / zoomX;
        double cameraEndX = cameraStartX + (owner.getWidth() / zoomX);

        double step = Math.min(context.maxStep, (cameraEndX - cameraStartX) /
                ((double) (context.getBase().getWidth() / context.pixelsPerStep) *
                        context.samplesPerCell));

        double domainStart = cameraStartX - ((cameraEndX - cameraStartX) / 2) - step;
        double domainEnd = cameraEndX + ((cameraEndX - cameraStartX) / 2) + step;

        if (function.getDomainStart().getActualValue() != null) {
            domainStart = Math.max(domainStart, function.getDomainStart().getActualValue());
        }

        if (function.getDomainEnd().getActualValue() != null) {
            domainEnd = Math.min(domainEnd, function.getDomainEnd().getActualValue());
        }

        double plotStartXDistance = cameraStartX - plot.getStartX();
        double plotEndXDistance = plot.getEndX() - cameraEndX;
        if (force || (plotStartXDistance < step && cameraStartX >= domainStart) ||
                (plotEndXDistance < step && cameraEndX <= domainEnd)) {
            step = Math.min(context.maxStep, (domainEnd - domainStart) /
                    ((double) (context.getBase().getWidth() / context.pixelsPerStep) *
                            context.samplesPerCell)); // Recalculate step, for if domain start or end values changed
            MathEvaluatorPool.getInstance().evaluateFunction(function, expression, plot, domainStart, domainEnd, step,
                    parent.getConstantValues());
        }
    }

    public static Expression processExpression(Function function, Map<String, Function> functionMap,
                                               Map<String, Double> constants) {
        if (function == null || function.getDefinition() == null) {
            return null;
        }

        try {
            String formationLaw = processFormationLaw(function, functionMap);

            return new ExpressionBuilder(formationLaw).variable(function.getVariableName())
                    .variables(constants.keySet()).build();
        } catch (Exception e) {
            return null;
        }
    }

    public static String processFormationLaw(Function function, Map<String, Function> functionMap) {
        boolean processed;
        StringBuilder formationLaw = new StringBuilder(function.getFormationLaw());
        do {
            processed = false;

            Matcher matcher = FUNCTION_CALL_PATTERN.matcher(formationLaw);
            while (matcher.find()) {
                String match = matcher.group();
                Function subFunction = functionMap.get(match.substring(0, match.indexOf('(')).trim());
                if (subFunction != null && !Objects.equals(subFunction.getName(), function.getName())) {
                    String subFunctionFormationLaw = subFunction.getFormationLaw();
                    subFunctionFormationLaw = "(" + subFunctionFormationLaw.replaceAll(subFunction.getVariableName(),
                            match.substring(match.indexOf('(') + 1, match.indexOf(')')).trim()) + ")";
                    formationLaw.replace(matcher.start(), matcher.end(), subFunctionFormationLaw);

                    processed = true;
                }
            }
        } while (processed);

        return formationLaw.toString();
    }

    /**
     * Processes a formation law into an exp4j's {@link Expression} object, using the constants defined on parent
     * event streamer.
     * <p>
     * The formation law is determined using the method {@link #processFormationLaw(Function, Map)}, which will
     * basically remove any calls to other functions defined on the list of functions passed as an argument and replace
     * them with their formation laws, and do this until there are no more calls to a function defined on the list
     */
    public void processExpression() {
        expression = processExpression(function, parent.getFunctionMap(), parent.getConstantValues());
    }
}
