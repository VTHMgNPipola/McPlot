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

package com.vthmgnpipola.mcplot.nmath;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class ConstantEvaluator {
    private final Constant constant;
    private final MathEventStreamer parent;

    private Runnable updateAction;

    public ConstantEvaluator(Constant constant,
                             MathEventStreamer parent) {
        this.constant = constant;
        this.parent = parent;
    }

    public Constant getConstant() {
        return constant;
    }

    public void setUpdateAction(Runnable updateAction) {
        this.updateAction = updateAction;
    }

    public void setName(String name) {
        constant.setName(name);
        parent.constantUpdate();
    }

    public void setDefinition(String definition) {
        constant.setDefinition(definition);
        parent.constantUpdate();
    }

    public void evaluate() {
        String definition = processDefinition();

        Future<Double> calculatedValueFuture = MathEvaluatorPool.getInstance()
                .evaluateConstant(definition, parent.getConstantValues());

        try {
            constant.actualValue = calculatedValueFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            constant.actualValue = null;
        }

        if (updateAction != null) {
            updateAction.run();
        }
    }

    private String processDefinition() {
        boolean processed;
        List<Constant> constants = parent.getConstants();
        String definition = constant.getDefinition();
        if (definition == null) {
            return null;
        }

        do {
            processed = false;

            for (Constant c : constants) {
                if (c.getName() != null && !c.getName().isBlank() && !c.getName().equals(constant.getName()) &&
                        definition.contains(c.getName())) {
                    definition = definition.replace(c.getName(), "(" + c.getDefinition() + ")");
                    processed = true;
                }
            }
        } while (processed);

        return definition;
    }
}
