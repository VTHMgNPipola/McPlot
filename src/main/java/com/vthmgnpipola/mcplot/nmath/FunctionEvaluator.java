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

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

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
    private final EvaluationContext context;

    private final ConstantEvaluator domainStartEvaluator;
    private final ConstantEvaluator domainEndEvaluator;

    private final EvaluationResultConsumer<Double, Double> resultConsumer;
    private Expression expression;

    /**
     * Creates a new FunctionEvaluator and binds it to a function, event streamer and plotting panel. Also registers
     * two constant evaluators in the parent event streamer for the domain start and domain end values.
     *
     * @param function Function this function evaluator "takes care" of.
     * @param context  Settings for evaluating the expression
     */
    public FunctionEvaluator(Function function, EvaluationContext context,
                             EvaluationResultConsumer<Double, Double> resultConsumer) {
        this.function = function;
        this.parent = MathEventStreamer.getInstance();
        this.context = context;

        parent.registerFunctionEvaluator(this);

        domainStartEvaluator = new ConstantEvaluator(function.getDomainStart());

        domainEndEvaluator = new ConstantEvaluator(function.getDomainEnd());

        this.resultConsumer = resultConsumer;
        if (function.getDefinition() != null) {
            setDefinition(function.getDefinition());
        }
    }

    public Function getFunction() {
        return function;
    }

    public EvaluationResultConsumer<Double, Double> getResultConsumer() {
        return resultConsumer;
    }

    public EvaluationContext getContext() {
        return context;
    }

    public void setDefinition(String definition) {
        function.setDefinition(definition);
        parent.functionUpdate(true, true);
    }

    public Double getDomainStart() {
        return function.getDomainStart().getActualValue();
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

    public Double getDomainEnd() {
        return function.getDomainEnd().getActualValue();
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

    /**
     * This method takes care of submitting the function that is bound to this function evaluator to be calculated on
     * the {@link MathEvaluatorPool} that was also bound to this function evaluator.
     * <p>
     * Note that the domain start and end constants aren't calculated at this step, instead this method simply checks
     * if they are defined. If so, it uses their values, otherwise it uses the default values from the
     * {@link EvaluationContext}.
     */
    public void evaluate(boolean forceDomain) {
        if (context == null || resultConsumer == null) {
            return;
        }

        double domainStart = context.getDomainStart();
        double domainEnd = context.getDomainEnd();

        Double definedDomainStart = function.getDomainStart().getActualValue();
        if (definedDomainStart != null && (domainStart < definedDomainStart || forceDomain)) {
            domainStart = Math.max(domainStart, definedDomainStart);
        }

        Double definedDomainEnd = function.getDomainEnd().getActualValue();
        if (definedDomainEnd != null && (domainEnd > definedDomainEnd || forceDomain)) {
            domainEnd = Math.min(domainEnd, definedDomainEnd);
        }

        MathEvaluatorPool.getInstance().evaluateFunction(function, expression, resultConsumer, domainStart,
                domainEnd, context.getStep(), parent.getConstantValues());
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
