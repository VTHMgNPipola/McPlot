/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2023  VTHMgNPipola
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MathEventStreamer {
    private static final MathEventStreamer INSTANCE = new MathEventStreamer();

    private List<ConstantEvaluator> constantEvaluators;
    private List<FunctionEvaluator> functionEvaluators;

    private List<Constant> constants;
    private List<Function> functions;

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
        functionEvaluators = Collections.synchronizedList(new ArrayList<>());

        constants = Collections.synchronizedList(new ArrayList<>());
        functions = Collections.synchronizedList(new ArrayList<>());

        constantValues = Collections.synchronizedSortedMap(new TreeMap<>());
        functionMap = Collections.synchronizedSortedMap(new TreeMap<>());
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

        functionUpdate(true, true);
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
            constantEvaluators.forEach(ConstantEvaluator::evaluate);
            constantValues = constants.stream()
                    .filter(c -> c.getActualValue() != null && c.getName() != null)
                    .collect(Collectors.toMap(Constant::getName, Constant::getActualValue));

            functionMap = functions.stream()
                    .filter(f -> f.getDefinition() != null)
                    .collect(Collectors.toMap(Function::getName, f -> f));
            functionEvaluators.forEach(fe -> {
                fe.processExpression();
                fe.evaluate(true);
            });
        });
    }

    public void functionUpdate(boolean processExpressions, boolean force) {
        Main.EXECUTOR_THREAD.submit(() -> {
            functionMap = functions.stream()
                    .collect(Collectors.toMap(Function::getName, f -> f));
            functionEvaluators.forEach(fe -> {
                if (processExpressions) {
                    fe.processExpression();
                }

                fe.evaluate(force);
            });
        });
    }
}
