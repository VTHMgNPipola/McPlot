package com.prinjsystems.mcplot.nmath;

import java.io.Serial;
import java.io.Serializable;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

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

        Expression expression = new ExpressionBuilder(definition).build();
        if (expression.validate().isValid()) {
            this.definition = definition;
            actualValue = expression.evaluate();
            return true;
        }
        return false;
    }

    public Double getActualValue() {
        return actualValue;
    }
}
