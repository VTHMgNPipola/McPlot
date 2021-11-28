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

import com.vthmgnpipola.mcplot.ngui.components.ConstantCard;
import com.vthmgnpipola.mcplot.nmath.Constant;
import com.vthmgnpipola.mcplot.nmath.ConstantEvaluator;
import com.vthmgnpipola.mcplot.nmath.MathEventStreamer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class ConstantsPanel extends JPanel {
    private final List<ConstantCard> constantCards;

    private AtomicInteger index;

    public ConstantsPanel(MathEventStreamer eventStreamer) {
        super(new MigLayout());
        this.constantCards = new ArrayList<>();
        index = new AtomicInteger(1);

        if (false/*constants.size() != 0*/) {
//            constants.forEach(c -> {
//                ConstantCard constantCard = new ConstantCard(c, eventStreamer, this, index.getAndIncrement());
//                add(constantCard, "pushx, span, growx");
//                constantCards.add(constantCard);
//            });
        } else {
            ConstantEvaluator firstConstantEvaluator = new ConstantEvaluator(new Constant(), eventStreamer);
            ConstantCard firstConstantCard = new ConstantCard(firstConstantEvaluator, eventStreamer, this,
                    index.getAndIncrement());
            add(firstConstantCard, "pushx, span, growx");
            constantCards.add(firstConstantCard);
            eventStreamer.registerConstantEvaluator(firstConstantEvaluator);
        }

        JButton addConstantCard = new JButton(BUNDLE.getString("workspace.actions.createConstant"));
        add(addConstantCard, "pushx, span, growx");
        addConstantCard.addActionListener(e -> {
            ConstantEvaluator constantEvaluator = new ConstantEvaluator(new Constant(), eventStreamer);
            ConstantCard constantCard = new ConstantCard(constantEvaluator, eventStreamer, this,
                    index.getAndIncrement());
            add(constantCard, "pushx, span, growx", getComponentCount() - 1);
            constantCards.add(constantCard);
            eventStreamer.registerConstantEvaluator(constantEvaluator);
            updateUI();
        });
    }

    public void removeConstantCard(ConstantCard constantCard) {
        remove(constantCard);
        constantCards.remove(constantCard);

        index = new AtomicInteger(1);
        constantCards.forEach(fc -> fc.setIndex(index.getAndIncrement()));

        updateUI();
    }
}
