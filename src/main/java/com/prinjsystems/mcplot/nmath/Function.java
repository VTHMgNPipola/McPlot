package com.prinjsystems.mcplot.nmath;

import java.awt.Color;
import java.io.Serial;
import java.io.Serializable;

public class Function implements Serializable {
    @Serial
    private static final long serialVersionUID = -3144429444076124342L;

    private String definition;
    private Color traceColor;

    private Double domainStart;
    private Double domainEnd;
    private boolean filled;

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

    public Double getDomainStart() {
        return domainStart;
    }

    public void setDomainStart(Double domainStart) {
        this.domainStart = domainStart;
    }

    public Double getDomainEnd() {
        return domainEnd;
    }

    public void setDomainEnd(Double domainEnd) {
        this.domainEnd = domainEnd;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }
}
