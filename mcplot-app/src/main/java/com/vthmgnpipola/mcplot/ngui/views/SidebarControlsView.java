/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2024  VTHMgNPipola
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

package com.vthmgnpipola.mcplot.ngui.views;

import com.vthmgnpipola.mcplot.ngui.ConstantsPanel;
import com.vthmgnpipola.mcplot.ngui.controllers.AbstractController;
import com.vthmgnpipola.mcplot.ngui.models.PlottingModel;
import com.vthmgnpipola.mcplot.nmath.Constant;
import com.vthmgnpipola.mcplot.nmath.Function;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class SidebarControlsView extends MPanel {
    private List<Function> functions;
    private List<Constant> constants;

    private final PlottingModel model;
    private final AbstractController controller;

    public SidebarControlsView(PlottingModel model, AbstractController controller) {
        setLayout(new BorderLayout());

        this.model = model;
        this.controller = controller;
    }

    @Override
    public void init() {
        removeAll();

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        this.functions = new ArrayList<>();
        this.constants = new ArrayList<>();

        PlotListView plotListView = new PlotListView(this.functions);
        tabbedPane.addTab(BUNDLE.getString("workspace.panels.functions"), new JScrollPane(plotListView));

        ConstantsPanel constantsPanel = new ConstantsPanel(this.constants);
        tabbedPane.addTab(BUNDLE.getString("workspace.panels.constants"), new JScrollPane(constantsPanel));

        add(tabbedPane, BorderLayout.CENTER);
    }

//    public void save() {
//        MathSessionHelper.saveSession(functions, constants, context, false);
//    }
//
//    public void saveAs() {
//        MathSessionHelper.saveSession(functions, constants, context, true);
//    }
//
//    public void open(Path path, PlottingPanel plottingPanel) {
//        MathSessionHelper.openSession(path, state -> applySession(state, plottingPanel));
//    }
//
//    public void open(PlottingPanel plottingPanel) {
//        MathSessionHelper.openSession(state -> applySession(state, plottingPanel));
//    }
//
//    private void applySession(MathSessionHelper.McPlotState state, PlottingPanel plottingPanel) {
//        plottingPanel.getPlots().clear();
//        MathEventStreamer.getInstance().reset();
//
//        GraphUnit.CUSTOM_X_UNIT.setSymbol(state.customUnitX.getSymbol());
//        GraphUnit.CUSTOM_X_UNIT.setDefinition(state.customUnitX.getDefinition());
//
//        GraphUnit.CUSTOM_Y_UNIT.setSymbol(state.customUnitY.getSymbol());
//        GraphUnit.CUSTOM_Y_UNIT.setDefinition(state.customUnitY.getDefinition());
//
//        MathEventStreamer.getInstance().registerConstantEvaluator(GraphUnit.CUSTOM_X_UNIT.getUnitValueEvaluator());
//        MathEventStreamer.getInstance().registerConstantEvaluator(GraphUnit.CUSTOM_Y_UNIT.getUnitValueEvaluator());
//
//        init(plottingPanel, state.functions, state.constants);
//        context.getBase().setContext(state.context);
//
//        context = state.context;
//        context.axisX.unit = GraphUnit.getUnit(state.unitX);
//        context.axisY.unit = GraphUnit.getUnit(state.unitY);
//
//        updateUI();
//        MathEventStreamer.getInstance().constantUpdate();
//    }
}
