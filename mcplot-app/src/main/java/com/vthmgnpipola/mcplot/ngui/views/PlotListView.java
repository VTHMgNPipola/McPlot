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

import com.vthmgnpipola.mcplot.ngui.components.FunctionCard;
import com.vthmgnpipola.mcplot.ngui.icons.FlatAddIcon;
import com.vthmgnpipola.mcplot.nmath.Function;
import com.vthmgnpipola.mcplot.plot.FunctionPlotter;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class PlotListView extends MPanel {
    private final List<FunctionCard> functionCards;
    private final List<Function> functions;

    private AtomicInteger index;

    public PlotListView(List<Function> functions) {
        this.functions = functions;

        this.functionCards = new ArrayList<>();
        index = new AtomicInteger(1);
    }

    public void removeFunctionCard(FunctionCard functionCard) {
        remove(functionCard);
        functionCards.remove(functionCard);

        functions.remove(functionCard.getFunction());

        index = new AtomicInteger(1);
        functionCards.forEach(fc -> fc.setIndex(index.getAndIncrement()));

        updateUI();
    }

    @Override
    public void init() {
        setLayout(new MigLayout());

        if (!functions.isEmpty()) {
            functions.forEach(f -> {
                FunctionPlotter functionPlotter = new FunctionPlotter(f, context);

                FunctionCard functionCard = new FunctionCard(functionPlotter, this, index.getAndIncrement());
                add(functionCard, "pushx, span, growx");
                functionCards.add(functionCard);
            });
        } else {
            Function firstFunction = new Function();
            FunctionPlotter firstFunctionPlotter = new FunctionPlotter(firstFunction, context);

            FunctionCard firstFunctionCard = new FunctionCard(firstFunctionPlotter, this,
                    index.getAndIncrement());
            add(firstFunctionCard, "pushx, span, growx");
            functions.add(firstFunction);
            functionCards.add(firstFunctionCard);
        }

        JButton addFunctionCard = new JButton(BUNDLE.getString("workspace.actions.createFunction"),
                new FlatAddIcon());
        add(addFunctionCard, "pushx, span, growx");
        addFunctionCard.addActionListener(e -> {
            Function function = new Function();
            FunctionPlotter functionPlotter = new FunctionPlotter(function, context);

            FunctionCard functionCard = new FunctionCard(functionPlotter, this, index.getAndIncrement());
            add(functionCard, "pushx, span, growx", getComponentCount() - 1);
            functions.add(function);
            functionCards.add(functionCard);
            updateUI();
        });
    }
}
