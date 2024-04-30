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

package com.vthmgnpipola.mcplot.nmath;

import com.vthmgnpipola.mcplot.messaging.MessagePublisher;
import com.vthmgnpipola.mcplot.messaging.MessageRouter;
import com.vthmgnpipola.mcplot.plot.PlotUpdateMessage;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.operator.Operator;
import net.objecthunter.exp4j.operator.Operators;

import javax.swing.JOptionPane;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class MathEvaluatorPool {
    private static final MathEvaluatorPool INSTANCE = new MathEvaluatorPool();

    private final ExecutorService executor;
    private final Set<Function> runningFunctions;

    private final MessagePublisher messagePublisher;

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

        messagePublisher = MessageRouter.publisher();
        executor = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
        runningFunctions = ConcurrentHashMap.newKeySet();
    }

    public static MathEvaluatorPool getInstance() {
        return INSTANCE;
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

    public void evaluateFunction(Function function, Expression expression,
                                 EvaluationResultConsumer<Double, Double> resultConsumer,
                                 double domainStart, double domainEnd, double step,
                                 Map<String, Double> constants) {
        if (!runningFunctions.add(function)) {
            return;
        }

        executor.submit(() -> {
            try {
                if (function == null || expression == null || domainEnd < domainStart) {
                    resultConsumer.invalidate();

                    runningFunctions.remove(function);
                    if (runningFunctions.isEmpty()) {
                        messagePublisher.publish(new PlotUpdateMessage());
                    }
                    return;
                }

                String variableName = function.getVariableName();
                expression.setVariables(constants);
                expression.setVariable(variableName, domainStart);
                if (!expression.validate(true).isValid()) {
                    resultConsumer.invalidate();

                    runningFunctions.remove(function);
                    if (runningFunctions.isEmpty()) {
                        messagePublisher.publish(new PlotUpdateMessage());
                    }
                    return;
                }

                resultConsumer.start();

                double lastI = domainStart;
                for (double i = domainStart; i <= domainEnd; i += step) {
                    if (lastI < 0 && i > 0) {
                        i = 0;
                    }

                    if (i + step > domainEnd) {
                        i = domainEnd;
                    }

                    expression.setVariable(variableName, i);
                    double value = expression.evaluate();
                    resultConsumer.accept(i, value);
                    lastI = i;
                }

                resultConsumer.complete();

                runningFunctions.remove(function);
                if (runningFunctions.isEmpty()) {
                    messagePublisher.publish(new PlotUpdateMessage());
                }
            } catch (Throwable t) {
                runningFunctions.remove(function);
                resultConsumer.invalidate();
            }
        });
    }
}
