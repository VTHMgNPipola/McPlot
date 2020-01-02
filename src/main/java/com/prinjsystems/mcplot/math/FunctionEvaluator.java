package com.prinjsystems.mcplot.math;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.StaticVariableSet;
import java.awt.geom.Path2D;
import java.util.List;

public class FunctionEvaluator {
    private static List<Variable> variableList;

    /**
     * Returns a polygon of the specified section of a function.
     *
     * @param function Function to plot.
     * @param start    Start X value
     * @param end      End X value
     * @param step     Will define the "resolution" of the polygon. A smaller step means a higher accuracy plot.
     * @return Polygon containing a section of the function
     */
    public static Path2D plotRange(PlottableFunction function, double start, double end, double step) {
        String definition = function.getDefinition();
        if (definition == null || definition.isBlank()) {
            return null;
        } else if (!definition.matches("[a-zA-Z]+[a-zA-Z0-9]*\\s*\\(x\\)\\s*=\\s*.*")) {
            throw new IllegalArgumentException("Not a valid function!");
        } else if (start + step > end) {
            throw new IllegalArgumentException("Step is too big for selected range!");
        }

        DoubleEvaluator evaluator = new DoubleEvaluator();
        Path2D result = new Path2D.Double();

        // Checks if start and end arguments are beyond function's domain
        System.out.printf("%f %f\n", start, function.getDomainStart());
        if (start < function.getDomainStart()) {
            start = function.getDomainStart();
            System.out.print("Switched: ");
        }
        System.out.printf("%f %f\n", start, function.getDomainStart());
        if (end > function.getDomainEnd()) {
            end = function.getDomainEnd();
        }

        // Initial point
        String actualFunction = definition.substring(definition.indexOf('=') + 1).trim();
        StaticVariableSet<Double> variables = new StaticVariableSet<>();
        variables.set("x", start);
        for (Variable variable : variableList) {
            variables.set(variable.getName(), variable.getValue());
        }
        result.moveTo(start, evaluator.evaluate(actualFunction, variables));

        // Calculates all points
        for (double i = start + step; i < end; i += step) {
            variables.set("x", i);
            result.lineTo(i, evaluator.evaluate(actualFunction, variables));
        }

        return result;
    }

    public static List<Variable> getVariableList() {
        return variableList;
    }

    public static void setVariableList(List<Variable> variableList) {
        FunctionEvaluator.variableList = variableList;
    }
}
