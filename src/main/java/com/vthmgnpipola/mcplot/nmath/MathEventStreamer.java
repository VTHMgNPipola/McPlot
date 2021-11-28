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

import com.vthmgnpipola.mcplot.Main;
import com.vthmgnpipola.mcplot.ngui.PlottingPanel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MathEventStreamer {
    private final List<ConstantEvaluator> constantEvaluators;
    private final List<FunctionEvaluator> functionEvaluators;

    private final List<Constant> constants;
    private final List<Function> functions;
    private final PlottingPanel plottingPanel;
    private Map<String, Double> constantValues;

    public MathEventStreamer(PlottingPanel plottingPanel) {
        constantEvaluators = new ArrayList<>();
        functionEvaluators = new ArrayList<>();

        constants = new ArrayList<>();
        functions = new ArrayList<>();

        constantValues = new HashMap<>();

        this.plottingPanel = plottingPanel;
    }

    public void registerConstantEvaluator(ConstantEvaluator constantEvaluator) {
        constantEvaluators.add(constantEvaluator);
        constants.add(constantEvaluator.constant());
    }

    public void removeConstantEvaluator(ConstantEvaluator constantEvaluator) {
        constantEvaluators.remove(constantEvaluator);
        constants.remove(constantEvaluator.constant());

        constantUpdate();
    }

    public void registerFunctionEvaluator(FunctionEvaluator functionEvaluator) {
        functionEvaluators.add(functionEvaluator);
        functions.add(functionEvaluator.getFunction());
    }

    public void removeFunctionEvaluator(FunctionEvaluator functionEvaluator) {
        functionEvaluators.remove(functionEvaluator);
        functions.remove(functionEvaluator.getFunction());

        functionUpdate();
    }

    public List<Constant> getConstants() {
        return constants;
    }

    public Map<String, Double> getConstantValues() {
        return constantValues;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public void constantUpdate() {
        Main.EXECUTOR_THREAD.submit(() -> {
            constantEvaluators.forEach(ConstantEvaluator::evaluate);
            constantValues = constants.stream()
                    .filter(c -> c.getActualValue() != null && c.getName() != null)
                    .collect(Collectors.toMap(Constant::getName, Constant::getActualValue));

            functionEvaluators.forEach(FunctionEvaluator::evaluate);
            plottingPanel.repaint();
        });
    }

    public void functionUpdate() {
        try {
            functionEvaluators.forEach(fe -> {
                fe.processExpression();
                fe.evaluate();
            });
            plottingPanel.repaint();
        } catch (Throwable t) {
            // For now all the exceptions from the expression processing and evaluation are going to be ignored
        }
    }
}
