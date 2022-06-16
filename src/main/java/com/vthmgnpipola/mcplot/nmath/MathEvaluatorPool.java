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

package com.vthmgnpipola.mcplot.nmath;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

    public Future<Double> evaluateConstant(String definition, Map<String, Double> constants) {
        return executor.submit(() -> {
            Expression expression = new ExpressionBuilder(definition).variables(constants.keySet()).build();
            expression.setVariables(constants);
            if (expression.validate(true).isValid()) {
                return expression.evaluate();
            }
            return null;
        });
    }

    public void evaluateFunction(Function function, Expression expression, FunctionPlot plot,
                                 double domainStart, double domainEnd, double step,
                                 Map<String, Double> constants) {
        runningFunctions++;
        executor.submit(() -> {
            try {
                if (function == null || expression == null || domainEnd < domainStart) {
                    plot.setPath(null);

                    runningFunctions--;
                    return;
                }

                String variableName = function.getVariableName();
                expression.setVariables(constants);
                expression.setVariable(variableName, domainStart);
                if (!expression.validate(true).isValid()) {
                    runningFunctions--;
                    return;
                }

                plot.setStartX(domainStart);
                plot.setEndX(domainEnd);

                Path2D.Double path = new Path2D.Double(Path2D.WIND_NON_ZERO, (int) ((domainEnd - domainStart) / step));
                path.reset();

                boolean moving = true;
                for (double i = domainStart; i <= domainEnd; i += step) {
                    expression.setVariable(variableName, i);
                    double value = expression.evaluate();
                    if (Double.isNaN(value)) {
                        moving = true;
                    } else if (moving) {
                        path.moveTo(i, value);
                        moving = false;
                    } else {
                        path.lineTo(i, value);
                    }
                }
                plot.setPath(path);

                runningFunctions--;
                if (runningFunctions == 0) {
                    functionsDoneTasks.forEach(Runnable::run);
                }
            } catch (Throwable t) {
                runningFunctions--;
                plot.setPath(null);
            }
        });
    }

    public Future<double[]> evaluateFunctionRaw(Function function, Expression expression, double domainStart,
                                                double domainEnd,
                                                double step, Map<String, Double> constants) {
        return executor.submit(() -> {
            try {
                if (function == null || expression == null || domainEnd < domainStart) {
                    return null;
                }

                double[] values = new double[(int) Math.ceil((domainEnd - domainStart) / step) * 2 + 2];

                String variableName = function.getVariableName();
                expression.setVariables(constants);
                expression.setVariable(variableName, domainStart);
                if (!expression.validate(true).isValid()) {
                    return null;
                }

                int valueIndex = 0;
                for (double i = domainStart; i < domainEnd; i += step) {
                    expression.setVariable(variableName, i);
                    values[valueIndex++] = i;
                    values[valueIndex++] = expression.evaluate();
                }

                return values;
            } catch (Throwable t) {
                return null;
            }
        });
    }
}
