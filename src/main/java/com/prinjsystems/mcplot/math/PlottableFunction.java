package com.prinjsystems.mcplot.math;

import java.awt.Color;

public class PlottableFunction {
    private String definition;
    private double domainStart, domainEnd;
    private Color traceColor;
    private boolean visible;

    public PlottableFunction() {
        visible = true;
        domainStart = -Double.MAX_VALUE;
        domainEnd = Double.MAX_VALUE;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public double getDomainStart() {
        return domainStart;
    }

    public void setDomainStart(double domainStart) {
        this.domainStart = domainStart;
    }

    public double getDomainEnd() {
        return domainEnd;
    }

    public void setDomainEnd(double domainEnd) {
        this.domainEnd = domainEnd;
    }

    public Color getTraceColor() {
        return traceColor;
    }

    public void setTraceColor(Color traceColor) {
        this.traceColor = traceColor;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
