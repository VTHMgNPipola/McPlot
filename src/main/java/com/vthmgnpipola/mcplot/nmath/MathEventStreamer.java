/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2022  VTHMgNPipola
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MathEventStreamer {
    private static final MathEventStreamer INSTANCE = new MathEventStreamer();

    private List<ConstantEvaluator> constantEvaluators;
    private List<FunctionEvaluator> functionEvaluators;

    private List<Constant> constants;
    private List<Function> functions;
    private PlottingPanel plottingPanel;

    private Map<String, Double> constantValues;
    private Map<String, Function> functionMap;

    private MathEventStreamer() {
        reset();
    }

    public static MathEventStreamer getInstance() {
        return INSTANCE;
    }

    public void reset() {
        constantEvaluators = Collections.synchronizedList(new ArrayList<>());
        functionEvaluators = new ArrayList<>();

        constants = new ArrayList<>();
        functions = new ArrayList<>();

        constantValues = new HashMap<>();
        functionMap = new HashMap<>();
    }

    public void setPlottingPanel(PlottingPanel plottingPanel) {
        this.plottingPanel = plottingPanel;
    }

    public void registerConstantEvaluator(ConstantEvaluator constantEvaluator) {
        constantEvaluators.add(constantEvaluator);
        constants.add(constantEvaluator.getConstant());
    }

    public void removeConstantEvaluator(ConstantEvaluator constantEvaluator) {
        constantEvaluators.remove(constantEvaluator);
        constants.remove(constantEvaluator.getConstant());

        constantUpdate();
    }

    public void registerFunctionEvaluator(FunctionEvaluator functionEvaluator) {
        functionEvaluators.add(functionEvaluator);
        functions.add(functionEvaluator.getFunction());
    }

    public void removeFunctionEvaluator(FunctionEvaluator functionEvaluator) {
        functionEvaluators.remove(functionEvaluator);
        functions.remove(functionEvaluator.getFunction());

        functionUpdate(true);
    }

    public List<Constant> getConstants() {
        return constants;
    }

    public Map<String, Double> getConstantValues() {
        return constantValues;
    }

    public Map<String, Function> getFunctionMap() {
        return functionMap;
    }

    public void constantUpdate() {
        Main.EXECUTOR_THREAD.execute(() -> {
            try {
                constantEvaluators.forEach(ConstantEvaluator::evaluate);
                constantValues = constants.stream().sequential()
                        .filter(c -> c.getActualValue() != null && c.getName() != null)
                        .collect(Collectors.toMap(Constant::getName, Constant::getActualValue));

                functionMap = functions.stream().sequential()
                        .filter(f -> f.getDefinition() != null)
                        .collect(Collectors.toMap(Function::getName, f -> f));
                functionEvaluators.forEach(fe -> {
                    if (fe.getFunction().isVisible()) {
                        fe.processExpression();
                        fe.evaluate();
                    }
                });
            } finally {
                if (plottingPanel != null) {
                    plottingPanel.repaint();
                }
            }
        });
    }

    public void functionUpdate(boolean processExpressions) {
        Main.EXECUTOR_THREAD.submit(() -> {
            try {
                functionMap = functions.stream().sequential()
                        .collect(Collectors.toMap(Function::getName, f -> f));
                functionEvaluators.forEach(fe -> {
                    if (fe.getFunction().isVisible()) {
                        if (processExpressions) {
                            fe.processExpression();
                        }

                        fe.evaluate();
                    }
                });
            } finally {
                if (plottingPanel != null) {
                    plottingPanel.repaint();
                }
            }
        });
    }
}
