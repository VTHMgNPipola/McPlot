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

package com.vthmgnpipola.mcplot.ngui;

import com.vthmgnpipola.mcplot.GraphUnit;
import com.vthmgnpipola.mcplot.MathSessionHelper;
import com.vthmgnpipola.mcplot.nmath.Constant;
import com.vthmgnpipola.mcplot.nmath.Function;
import com.vthmgnpipola.mcplot.nmath.MathEvaluatorPool;
import com.vthmgnpipola.mcplot.nmath.MathEventStreamer;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class MathPanel extends JPanel {
    private List<Function> functions;
    private List<Constant> constants;
    private PlottingPanelContext context;

    public MathPanel() {
        setLayout(new BorderLayout());
    }

    public void init(PlottingPanel plottingPanel) {
        init(plottingPanel, new ArrayList<>(), new ArrayList<>());
    }

    public void init(PlottingPanel plottingPanel, List<Function> functions, List<Constant> constants) {
        removeAll();

        MathEventStreamer.getInstance().setPlottingPanel(plottingPanel);
        MathEvaluatorPool.getInstance().addFunctionsDoneTask(plottingPanel::repaint);
        context = plottingPanel.getContext();

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        this.functions = new ArrayList<>();
        if (functions != null) {
            this.functions.addAll(functions);
        }

        this.constants = new ArrayList<>();
        if (constants != null) {
            this.constants.addAll(constants);
        }

        FunctionsPanel functionsPanel = new FunctionsPanel(this.functions, plottingPanel);
        tabbedPane.addTab(BUNDLE.getString("workspace.panels.functions"), new JScrollPane(functionsPanel));

        ConstantsPanel constantsPanel = new ConstantsPanel(this.constants);
        tabbedPane.addTab(BUNDLE.getString("workspace.panels.constants"), new JScrollPane(constantsPanel));

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void save() {
        MathSessionHelper.saveSession(functions, constants, context, false);
    }

    public void saveAs() {
        MathSessionHelper.saveSession(functions, constants, context, true);
    }

    public void open(PlottingPanel plottingPanel) {
        MathSessionHelper.openSession(state -> {
            plottingPanel.getPlots().clear();
            MathEventStreamer.getInstance().reset();

            GraphUnit.CUSTOM_X_UNIT.setSymbol(state.customUnitX.getSymbol());
            GraphUnit.CUSTOM_X_UNIT.setDefinition(state.customUnitX.getDefinition());

            GraphUnit.CUSTOM_Y_UNIT.setSymbol(state.customUnitY.getSymbol());
            GraphUnit.CUSTOM_Y_UNIT.setDefinition(state.customUnitY.getDefinition());

            MathEventStreamer.getInstance().registerConstantEvaluator(GraphUnit.CUSTOM_X_UNIT.getUnitValueEvaluator());
            MathEventStreamer.getInstance().registerConstantEvaluator(GraphUnit.CUSTOM_Y_UNIT.getUnitValueEvaluator());

            init(plottingPanel, state.functions, state.constants);
            context.getBase().setContext(state.context);

            context = state.context;
            context.axisX.unit = GraphUnit.getUnit(state.unitX);
            context.axisY.unit = GraphUnit.getUnit(state.unitY);

            updateUI();
            MathEventStreamer.getInstance().constantUpdate();
        });
    }
}
