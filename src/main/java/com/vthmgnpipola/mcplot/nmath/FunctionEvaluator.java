/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2021  VTHMgNPipola
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
import java.awt.Color;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class FunctionEvaluator {
    private static final Pattern FUNCTION_PATTERN = Pattern.compile("\s*[a-zA-Z]+[a-zA-Z0-9]*\s*\\(\s*[a-zA-Z]+\s*\\)" +
            "\s*=[^=]*");
    private static final Pattern FUNCTION_CALL_PATTERN = Pattern.compile("\s*[a-zA-Z]+[a-zA-Z0-9]*\s*\\" +
            "(\s*[a-zA-Z]+\s*\\)");

    private final Function function;
    private final MathEventStreamer parent;
    private final PlottingPanel plottingPanel;

    private final FunctionPlot plot;
    private Expression expression;

    public FunctionEvaluator(Function function, MathEventStreamer parent, PlottingPanel plottingPanel) {
        this.function = function;
        this.parent = parent;
        this.plottingPanel = plottingPanel;

        plot = new FunctionPlot();
        plottingPanel.getFunctions().put(function, plot);
    }

    public Function getFunction() {
        return function;
    }

    public void setDefinition(String definition) {
        function.setDefinition(definition);
        parent.functionUpdate(true);
    }

    public void setDomainStart(Double domainStart) {
        function.setDomainStart(domainStart);
        parent.functionUpdate(false);
    }

    public void setDomainEnd(Double domainEnd) {
        function.setDomainEnd(domainEnd);
        parent.functionUpdate(false);
    }

    public void setTraceColor(Color traceColor) {
        function.setTraceColor(traceColor);
        plottingPanel.repaint();
    }

    public void setFilled(boolean filled) {
        function.setFilled(filled);
        plottingPanel.repaint();
    }

    public void setVisible(boolean visible) {
        function.setVisible(visible);
        plottingPanel.repaint();
    }

    public void evaluate() {
        double zoomX = plottingPanel.getScaleX() * plottingPanel.getPixelsPerStep() * plottingPanel.getZoom();
        double domainStart = function.getDomainStart() != null ? function.getDomainStart() :
                plottingPanel.getCameraX() / zoomX;
        double domainEnd = function.getDomainEnd() != null ? function.getDomainEnd() :
                (plottingPanel.getCameraX() + plottingPanel.getWidth()) / zoomX;
        double step = Math.min(plottingPanel.getMaxStep(), (domainEnd - domainStart) /
                ((double) (plottingPanel.getWidth() / plottingPanel.getPixelsPerStep()) *
                        plottingPanel.getSamplesPerCell()));
        if (function.getDomainStart() == null) {
            domainStart -= step;
        }
        if (function.getDomainEnd() == null) {
            domainEnd += step;
        }
        MathEvaluatorPool.getInstance().evaluateFunction(function, expression, plot, domainStart, domainEnd, step,
                parent.getConstantValues(), plot -> {
                });
    }

    public void processExpression() {
        if (function == null || function.getDefinition() == null ||
                !FUNCTION_PATTERN.matcher(function.getDefinition()).matches()) {
            expression = null;
            return;
        }

        try {
            String formationLaw = processFormationLaw();

            expression = new ExpressionBuilder(formationLaw).variable(function.getVariableName())
                    .variables(parent.getConstantValues().keySet()).build();
        } catch (Exception e) {
            expression = null;
        }
    }

    private String processFormationLaw() {
        boolean processed;
        StringBuilder formationLaw = new StringBuilder(function.getFormationLaw());
        Map<String, Function> functionMap = parent.getFunctions().stream()
                .filter(f -> !Objects.equals(f.getName(), function.getName()))
                .collect(Collectors.toMap(Function::getName, f -> f));
        do {
            processed = false;

            Matcher matcher = FUNCTION_CALL_PATTERN.matcher(formationLaw);
            while (matcher.find()) {
                String match = matcher.group();
                Function subFunction = functionMap.get(match.substring(0, match.indexOf('(')).trim());
                if (subFunction != null) {
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
}
