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

package com.vthmgnpipola.mcplot.ngui;

import com.vthmgnpipola.mcplot.ngui.components.FunctionCard;
import com.vthmgnpipola.mcplot.nmath.Function;
import com.vthmgnpipola.mcplot.nmath.FunctionEvaluator;
import com.vthmgnpipola.mcplot.nmath.MathEventStreamer;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class FunctionPanel extends JPanel {
    private final List<FunctionCard> functionCards;

    private AtomicInteger index;

    public FunctionPanel(List<FunctionCard> functionCards, MathEventStreamer eventStreamer,
                         PlottingPanel plottingPanel) {
        setLayout(new MigLayout());
        this.functionCards = functionCards;
        index = new AtomicInteger(1);

        if (false/*functions.size() != 0*/) {
//            functions.forEach(f -> {
//                FunctionCard functionCard = new FunctionCard(f, eventStreamer, plottingPanel, this,
//                        index.getAndIncrement());
//                add(functionCard, "pushx, span, growx");
//                functionCards.add(functionCard);
//            });
        } else {
            FunctionEvaluator firstFunctionEvaluator = new FunctionEvaluator(new Function(), eventStreamer,
                    plottingPanel);
            FunctionCard firstFunctionCard = new FunctionCard(firstFunctionEvaluator, eventStreamer, plottingPanel,
                    this, index.getAndIncrement());
            add(firstFunctionCard, "pushx, span, growx");
            functionCards.add(firstFunctionCard);
            eventStreamer.registerFunctionEvaluator(firstFunctionEvaluator);
        }

        JButton addFunctionCard = new JButton(BUNDLE.getString("workspace.actions.createFunction"));
        add(addFunctionCard, "pushx, span, growx");
        addFunctionCard.addActionListener(e -> {
            FunctionEvaluator functionEvaluator = new FunctionEvaluator(new Function(), eventStreamer, plottingPanel);
            FunctionCard functionCard = new FunctionCard(functionEvaluator, eventStreamer, plottingPanel, this,
                    index.getAndIncrement());
            add(functionCard, "pushx, span, growx", getComponentCount() - 1);
            functionCards.add(functionCard);
            eventStreamer.registerFunctionEvaluator(functionEvaluator);
            updateUI();
        });
    }

    public void removeFunctionCard(FunctionCard functionCard) {
        remove(functionCard);
        functionCards.remove(functionCard);

        index = new AtomicInteger(1);
        functionCards.forEach(fc -> fc.setIndex(index.getAndIncrement()));

        updateUI();
    }
}
