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

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MathEvaluatorPool {
    private static final MathEvaluatorPool INSTANCE = new MathEvaluatorPool();

    private final ExecutorService executor;
    private final List<Runnable> functionsDoneTasks;
    private int runningFunctions;

    private MathEvaluatorPool() {
        functionsDoneTasks = new ArrayList<>();
        executor = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
        runningFunctions = 0;
    }

    public static MathEvaluatorPool getInstance() {
        return INSTANCE;
    }

    public void addFunctionsDoneTask(Runnable functionsDoneTask) {
        functionsDoneTasks.add(functionsDoneTask);
    }

    public Future<Double> evaluateConstant(String expressionString, String constantName, List<Constant> constants) {
        return executor.submit(() -> {
            Map<String, Double> constantMap = constants.stream()
                    .filter(c -> c.getActualValue() != null && c.getName() != null && !Objects.equals(c.getName(),
                            constantName)).collect(Collectors.toMap(Constant::getName, Constant::getActualValue));
            Expression expression = new ExpressionBuilder(expressionString).variables(constantMap.keySet()).build();
            expression.setVariables(constantMap);
            if (expression.validate().isValid()) {
                return expression.evaluate();
            }
            return null;
        });
    }

    public Future<FunctionPlot> evaluateFunction(Function function, Expression expression, FunctionPlot plot,
                                                 double domainStart, double domainEnd, double step,
                                                 Map<String, Double> constants, Consumer<FunctionPlot> callback) {
        runningFunctions++;
        return executor.submit(() -> {
            try {
                if (function == null || domainEnd < domainStart) {
                    runningFunctions--;
                    return null;
                }

                String variableName = function.getVariableName();
                expression.setVariables(constants);
                expression.setVariable(variableName, domainStart);
                if (!expression.validate(true).isValid()) {
                    runningFunctions--;
                    return null;
                }

                plot.setStartX(domainStart);
                plot.setEndX(domainEnd);

                Path2D.Double path = new Path2D.Double(Path2D.WIND_NON_ZERO, (int) ((domainEnd - domainStart) / step));
                path.reset();
                path.moveTo(domainStart, expression.evaluate());

                for (double i = domainStart + step; i <= domainEnd; i += step) {
                    expression.setVariable(variableName, i);
                    path.lineTo(i, expression.evaluate());
                }
                plot.setPath(path);

                runningFunctions--;
                callback.accept(plot);
                if (runningFunctions == 0) {
                    functionsDoneTasks.forEach(Runnable::run);
                }

                return plot;
            } catch (Throwable e) {
                runningFunctions--;
                callback.accept(null);
                return null;
            }
        });
    }
}
