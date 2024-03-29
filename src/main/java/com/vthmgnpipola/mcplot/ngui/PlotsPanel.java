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

package com.vthmgnpipola.mcplot.ngui;

import com.vthmgnpipola.mcplot.ngui.components.FunctionCard;
import com.vthmgnpipola.mcplot.ngui.icons.FlatAddIcon;
import com.vthmgnpipola.mcplot.nmath.EvaluationContext;
import com.vthmgnpipola.mcplot.nmath.Function;
import com.vthmgnpipola.mcplot.plot.FunctionLinePlot;
import com.vthmgnpipola.mcplot.plot.FunctionPlot;
import com.vthmgnpipola.mcplot.plot.FunctionPlotter;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class PlotsPanel extends JPanel {
    private final List<FunctionCard> functionCards;
    private final List<Function> functions;

    private AtomicInteger index;

    public PlotsPanel(List<Function> functions, PlottingPanel plottingPanel, EvaluationContext context) {
        this.functions = functions;

        setLayout(new MigLayout());
        this.functionCards = new ArrayList<>();
        index = new AtomicInteger(1);

        Runnable evaluationUpdateEvent = plottingPanel::repaint;
        if (functions.size() != 0) {
            functions.forEach(f -> {
                FunctionPlot plot = new FunctionLinePlot();
                FunctionPlotter functionPlotter = new FunctionPlotter(f, context, plot);
                functionPlotter.setUpdateEvent(evaluationUpdateEvent);
                plottingPanel.getPlots().add(plot);

                FunctionCard functionCard = new FunctionCard(functionPlotter, plottingPanel,
                        this, index.getAndIncrement());
                add(functionCard, "pushx, span, growx");
                functionCards.add(functionCard);
            });
        } else {
            Function firstFunction = new Function();
            FunctionPlot plot = new FunctionLinePlot();
            FunctionPlotter firstFunctionPlotter = new FunctionPlotter(firstFunction, context, plot);
            firstFunctionPlotter.setUpdateEvent(evaluationUpdateEvent);
            plottingPanel.getPlots().add(plot);

            FunctionCard firstFunctionCard = new FunctionCard(firstFunctionPlotter, plottingPanel,
                    this, index.getAndIncrement());
            add(firstFunctionCard, "pushx, span, growx");
            functions.add(firstFunction);
            functionCards.add(firstFunctionCard);
        }

        JButton addFunctionCard = new JButton(BUNDLE.getString("workspace.actions.createFunction"),
                new FlatAddIcon());
        add(addFunctionCard, "pushx, span, growx");
        addFunctionCard.addActionListener(e -> {
            Function function = new Function();
            FunctionPlot plot = new FunctionLinePlot();
            FunctionPlotter functionPlotter = new FunctionPlotter(function, context, plot);
            functionPlotter.setUpdateEvent(evaluationUpdateEvent);
            plottingPanel.getPlots().add(plot);

            FunctionCard functionCard = new FunctionCard(functionPlotter, plottingPanel, this,
                    index.getAndIncrement());
            add(functionCard, "pushx, span, growx", getComponentCount() - 1);
            functions.add(function);
            functionCards.add(functionCard);
            updateUI();
        });
    }

    public void removeFunctionCard(FunctionCard functionCard) {
        remove(functionCard);
        functionCards.remove(functionCard);

        functions.remove(functionCard.getFunction());

        index = new AtomicInteger(1);
        functionCards.forEach(fc -> fc.setIndex(index.getAndIncrement()));

        updateUI();
    }
}
