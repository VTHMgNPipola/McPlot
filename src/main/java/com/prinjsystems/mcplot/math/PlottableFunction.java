package com.prinjsystems.mcplot.math;

import java.awt.Color;

public class PlottableFunction {
    private String definition;
    private Color traceColor;

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Color getTraceColor() {
        return traceColor;
    }

    public void setTraceColor(Color traceColor) {
        this.traceColor = traceColor;
    }
}
