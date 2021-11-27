package com.prinjsystems.mcplot.nmath;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Constant implements Serializable {
    @Serial
    private static final long serialVersionUID = -8235664400532015308L;

    private String name;
    private String definition;
    private Double actualValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefinition() {
        return definition;
    }

    public boolean setDefinition(String definition) {
        if (definition == null || definition.isBlank()) {
            return false;
        }

        Future<Double> calculatedValueFuture = MathEvaluatorPool.getInstance().evaluateExpression(definition);
        try {
            Double calculatedValue = calculatedValueFuture.get();
            if (calculatedValue != null) {
                actualValue = calculatedValue;
                this.definition = definition;
            }
            return true;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Double getActualValue() {
        return actualValue;
    }
}
