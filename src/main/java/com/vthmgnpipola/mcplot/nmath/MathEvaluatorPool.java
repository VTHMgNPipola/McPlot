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
import net.objecthunter.exp4j.operator.Operator;
import net.objecthunter.exp4j.operator.Operators;

import javax.swing.*;
import java.awt.geom.Path2D;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class MathEvaluatorPool {
    private static final MathEvaluatorPool INSTANCE = new MathEvaluatorPool();

    private final ExecutorService executor;
    private final List<Runnable> functionsDoneTasks;
    private int runningFunctions;

    private MathEvaluatorPool() {
        try {
            // Extremely hacky solution to the problem of traces disappearing when division or modulo by 0 happens
            Field builtinOperators = Operators.class.getDeclaredField("builtinOperators");
            builtinOperators.setAccessible(true);

            Field divisionIndexField = Operators.class.getDeclaredField("INDEX_DIVISION");
            divisionIndexField.setAccessible(true);

            Array.set(builtinOperators.get(null), divisionIndexField.getInt(null),
                    new Operator("/", 2, true, Operator.PRECEDENCE_DIVISION) {
                        @Override
                        public double apply(double... args) {
                            if (args[1] == 0d) {
                                return Double.NaN;
                            }
                            return args[0] / args[1];
                        }
                    });

            Field moduloIndexField = Operators.class.getDeclaredField("INDEX_MODULO");
            moduloIndexField.setAccessible(true);

            Array.set(builtinOperators.get(null), moduloIndexField.getInt(null),
                    new Operator("%", 2, true, Operator.PRECEDENCE_MODULO) {
                        @Override
                        public double apply(double... args) {
                            if (args[1] == 0d) {
                                return Double.NaN;
                            }
                            return args[0] % args[1];
                        }
                    });
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Unable to override exp4j builtin operators! Some functions might not be displayed " +
                    "correctly.");
            JOptionPane.showMessageDialog(null, BUNDLE.getString("internal.exp4jFieldError"),
                    BUNDLE.getString("generics.errorDialog"), JOptionPane.ERROR_MESSAGE);
        }

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

    private static double getT(double tbefore, double x0, double x1, double y0, double y1) {
        final double alpha = 0.5d;
        double xterm = x1 + x0;
        double yterm = y1 + y0;
        return Math.pow(Math.sqrt((xterm * xterm) + (yterm * yterm)), alpha) + tbefore;
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
                double lastI = domainStart;
//                double[] ypoints = new double[4];
//                double[] xpoints = new double[4];
//                int count = 0;
                for (double i = domainStart; i <= domainEnd; i += step) {
                    if (lastI < 0 && i > 0) {
                        i = 0;
                    }
                    expression.setVariable(variableName, i);
                    double value = expression.evaluate();
                    if (Double.isNaN(value)) {
//                        for (int j = 3; j > 3 - count; j--) {
//                            path.lineTo(xpoints[j], ypoints[j]);
//                        } FIXME: Uncomment when catmnull-rom implementation is done
                        moving = true;
//                        count = 0;
                    } else if (moving) {
                        path.moveTo(i, value);
                        moving = false;
//                        count = 1;
                    } else {
                        path.lineTo(i, value);

                        // TODO: Generate Catmull-Rom spline
                        // Shift points
//                        ypoints[0] = ypoints[1];
//                        ypoints[1] = ypoints[2];
//                        ypoints[2] = ypoints[3];
//                        ypoints[3] = value;
//
//                        xpoints[0] = xpoints[1];
//                        xpoints[1] = xpoints[2];
//                        xpoints[2] = xpoints[3];
//                        xpoints[3] = i;
//
//                        count++;
//
//                        if (count == 4) {
//                            double t0 = 0d;
//                            double t1 = getT(t0, xpoints[0], xpoints[1], ypoints[0], ypoints[1]);
//                            double t2 = getT(t1, xpoints[1], xpoints[2], ypoints[1], ypoints[2]);
//                            double t3 = getT(t2, xpoints[2], xpoints[3], ypoints[2], ypoints[3]);
//                            count--;
//                        }
                    }
                    lastI = i;
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
}
