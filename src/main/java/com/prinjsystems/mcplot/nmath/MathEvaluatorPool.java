package com.prinjsystems.mcplot.nmath;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MathEvaluatorPool {
    private static final MathEvaluatorPool INSTANCE = new MathEvaluatorPool();
    private static final Pattern FUNCTION_PATTERN = Pattern.compile("\s*[a-zA-Z]+[a-zA-Z0-9]*\s*\\([a-zA-Z]+\\)" +
            "\s*=[^=]*");

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

    public Future<FunctionPlot> evaluateFunction(String function, double domainStart, double domainEnd, double step,
                                                 List<Constant> constants, Consumer<FunctionPlot> callback) {
        runningFunctions++;
        return executor.submit(() -> {
            try {
                if (function == null || !FUNCTION_PATTERN.matcher(function).matches() || domainEnd < domainStart) {
                    runningFunctions--;
                    return null;
                }

                String[] functionParts = function.split("=");
                String functionDefinition = functionParts[1];
                String variableName = functionParts[0].substring(functionParts[0].indexOf('(') + 1,
                        functionParts[0].indexOf(')'));

                Map<String, Double> constantMap = constants.stream()
                        .filter(c -> c.getActualValue() != null && c.getName() != null)
                        .collect(Collectors.toMap(Constant::getName, Constant::getActualValue));
                Expression expression = new ExpressionBuilder(functionDefinition).variable(variableName)
                        .variables(constantMap.keySet()).build();
                expression.setVariables(constantMap);
                expression.setVariable(variableName, domainStart);
                if (!expression.validate(true).isValid()) {
                    runningFunctions--;
                    return null;
                }

                FunctionPlot plot = new FunctionPlot();
                plot.setStartX(domainStart);
                plot.setEndX(domainEnd);

                Path2D.Double path = new Path2D.Double();
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
                return null;
            }
        });
    }
}
