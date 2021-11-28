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

package com.prinjsystems.mcplot.ngui;

import com.prinjsystems.mcplot.MathSessionHelper;
import com.prinjsystems.mcplot.ngui.components.FunctionCard;
import com.prinjsystems.mcplot.nmath.Constant;
import com.prinjsystems.mcplot.nmath.Function;
import com.prinjsystems.mcplot.nmath.MathEvaluatorPool;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class MathPanel extends JPanel {
    private List<Function> functions;
    private List<Constant> constants;

    private List<FunctionCard> functionCards;

    public MathPanel() {
        setLayout(new BorderLayout());
    }

    public void init(PlottingPanel plottingPanel) {
        init(plottingPanel, new ArrayList<>(), new ArrayList<>());
    }

    public void init(PlottingPanel plottingPanel, List<Function> functions, List<Constant> constants) {
        removeAll();

        plottingPanel.setMathPanel(this);
        MathEvaluatorPool.getInstance().addFunctionsDoneTask(plottingPanel::repaint);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        this.functions = functions;
        this.constants = constants;

        functionCards = new ArrayList<>();

        FunctionPanel functionPanel = new FunctionPanel(functionCards, functions, constants, plottingPanel);
        tabbedPane.addTab(BUNDLE.getString("workspace.panels.functions"), new JScrollPane(functionPanel));

        ConstantsPanel constantsPanel = new ConstantsPanel(constants);
        tabbedPane.addTab(BUNDLE.getString("workspace.panels.constants"), new JScrollPane(constantsPanel));

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void recalculateAllFunctions() {
        functionCards.forEach(FunctionCard::recalculateFunction);
    }

    public void save() {
        MathSessionHelper.saveSession(functions, constants);
    }

    public void open(PlottingPanel plottingPanel) {
        MathSessionHelper.openSession((f, c) -> {
            plottingPanel.getFunctions().clear();
            init(plottingPanel, f, c);
            updateUI();
            recalculateAllFunctions();
        });
    }
}
