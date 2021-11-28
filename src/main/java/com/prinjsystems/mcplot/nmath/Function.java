/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2021  VTHMgNPipola
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

package com.prinjsystems.mcplot.nmath;

import java.awt.Color;
import java.io.Serial;
import java.io.Serializable;

public class Function implements Serializable {
    @Serial
    private static final long serialVersionUID = -3144429444076124342L;

    private String definition;
    private transient String name, variableName, formationLaw;

    private Color traceColor;
    private Double domainStart;
    private Double domainEnd;
    private boolean filled;
    private boolean visible = true;

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
        doDecomposition();
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getName() {
        checkDecomposition();
        return name;
    }

    public String getVariableName() {
        checkDecomposition();
        return variableName;
    }

    public String getFormationLaw() {
        checkDecomposition();
        return formationLaw;
    }

    private void doDecomposition() {
        String[] parts = definition.split("=");
        if (parts.length != 2) {
            return;
        }

        formationLaw = parts[1].trim();

        name = parts[0].substring(0, parts[0].indexOf('(')).trim();
        variableName = parts[0].substring(parts[0].indexOf('(') + 1, parts[0].indexOf(')')).trim();
    }

    private void checkDecomposition() {
        if (name == null || variableName == null || formationLaw == null) {
            doDecomposition();
        }
    }
}
