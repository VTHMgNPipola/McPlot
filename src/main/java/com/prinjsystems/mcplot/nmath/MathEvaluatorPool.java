package com.prinjsystems.mcplot.nmath;

import java.awt.geom.Path2D;
import java.util.List;
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

    private final Pattern functionPattern;

    private final ExecutorService executor;

    private MathEvaluatorPool() {
        functionPattern = Pattern.compile("\s*[a-zA-Z]+[a-zA-Z0-9]*\s*\\([a-zA-Z]+\\)\s*=[^=]*");
        executor = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
    }

    public static MathEvaluatorPool getInstance() {
        return INSTANCE;
    }

    public Future<Double> evaluateExpression(String expressionString) {
        return executor.submit(() -> {
            Expression expression = new ExpressionBuilder(expressionString).build();
            if (expression.validate().isValid()) {
                return expression.evaluate();
            }
            return null;
        });
    }

    public Future<Path2D.Double> evaluateFunction(String function, double domainStart, double domainEnd, double step,
                                                  List<Constant> constants, Consumer<Path2D.Double> callback) {
        return executor.submit(() -> {
            if (function == null || !functionPattern.matcher(function).matches() || domainEnd < domainStart) {
                return null;
            }

            String[] functionParts = function.split("=");
            String functionDefinition = functionParts[1];
            String variableName = functionParts[0].substring(functionParts[0].indexOf('(') + 1,
                    functionParts[0].indexOf(')'));


            Expression expression = new ExpressionBuilder(functionDefinition).variable(variableName)
                    .variables(constants.stream().filter(c -> c.getActualValue() != null && c.getName() != null)
                            .map(Constant::getName).collect(Collectors.toSet())).build();
            expression.setVariables(constants.stream().filter(c -> c.getActualValue() != null && c.getName() != null)
                    .collect(Collectors.toMap(Constant::getName, Constant::getActualValue)));
            expression.setVariable(variableName, domainStart);
            if (!expression.validate(true).isValid()) {
                return null;
            }

            Path2D.Double path = new Path2D.Double();
            path.moveTo(domainStart, expression.evaluate());

            for (double i = domainStart + step; i <= domainEnd; i += step) {
                expression.setVariable(variableName, i);
                path.lineTo(i, expression.evaluate());
            }

            callback.accept(path);
            return path;
        });
    }
}
