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

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
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

    public void setDefinition(String definition, List<Constant> constants) {
        if (definition == null || definition.isBlank()) {
            return;
        }

        this.definition = definition;
        updateValue(constants);
    }

    public Double getActualValue() {
        return actualValue;
    }

    public void updateValue(List<Constant> constants) {
        Future<Double> calculatedValueFuture = MathEvaluatorPool.getInstance().evaluateConstant(definition, name,
                constants);
        try {
            actualValue = calculatedValueFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            actualValue = null;
        }
    }
}
