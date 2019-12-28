package com.prinjsystems.mcplot.math;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.StaticVariableSet;
import java.awt.geom.Path2D;

public class FunctionEvaluator {
    private static final DoubleEvaluator evaluator = new DoubleEvaluator();

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
        Path2D result = new Path2D.Double();

        String definition = function.getDefinition();
        if (definition == null || !definition.matches("[a-zA-Z]+\\(x\\)\\s*=\\s*.*")) {
            throw new IllegalArgumentException("Not a valid function!");
        } else if (start + step > end) {
            throw new IllegalArgumentException("Step is too big for selected range!");
        }

        // Initial point
        String actualFunction = definition.substring(definition.indexOf('=') + 1).trim();
        StaticVariableSet<Double> variables = new StaticVariableSet<>();
        variables.set("x", start);
        result.moveTo(start, evaluator.evaluate(actualFunction, variables));

        for (double i = start + step; i < end; i += step) {
            variables.set("x", i);
            result.lineTo(i, evaluator.evaluate(actualFunction, variables));
        }

        return result;
    }
}
