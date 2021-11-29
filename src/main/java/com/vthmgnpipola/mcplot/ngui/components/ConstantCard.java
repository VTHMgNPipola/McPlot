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

package com.vthmgnpipola.mcplot.ngui.components;

import com.vthmgnpipola.mcplot.ngui.ConstantsPanel;
import com.vthmgnpipola.mcplot.nmath.ConstantEvaluator;
import com.vthmgnpipola.mcplot.nmath.MathEventStreamer;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;
import static com.vthmgnpipola.mcplot.Main.EXECUTOR_THREAD;

public class ConstantCard extends JPanel {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#####");

    private final ConstantEvaluator constantEvaluator;

    private final JSecondaryTextField value;

    public ConstantCard(ConstantEvaluator constantEvaluator, MathEventStreamer eventStreamer, ConstantsPanel parent,
                        int index) {
        super(new MigLayout());
        this.constantEvaluator = constantEvaluator;
        constantEvaluator.setUpdateAction(this::updateValueTooltip);

        setIndex(index);

        JLabeledTextField name = new JLabeledTextField();
        add(name, "pushx, growx");
        name.setText(constantEvaluator.getConstant().getName());
        name.setPlaceholderText(BUNDLE.getString("constantCard.name"));
        name.setToolTipText(BUNDLE.getString("constantCard.nameTooltip"));
        name.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                constantEvaluator.setName(name.getText());
            }
        });
        name.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    constantEvaluator.setName(name.getText());
                }
            }
        });

        JButton remove = new JButton("X");
        add(remove, "wrap");
        remove.setToolTipText(BUNDLE.getString("generics.remove"));
        remove.addActionListener(e -> {
            eventStreamer.removeConstantEvaluator(constantEvaluator);

            parent.removeConstantCard(this);
        });

        value = new JSecondaryTextField();
        add(value, "pushx, span, growx");
        value.setText(constantEvaluator.getConstant().getDefinition());
        value.setPlaceholderText(BUNDLE.getString("constantCard.value"));
        value.setToolTipText(BUNDLE.getString("constantCard.valueTooltip"));
        updateValueTooltip();
        value.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateValue(parent);
            }
        });
        value.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateValue(parent);
            }
        });
    }

    public void updateValueTooltip() {
        value.setSecondaryText(null);
        if (constantEvaluator.getConstant().getActualValue() != null) {
            value.setSecondaryText(" = " + DECIMAL_FORMAT.format(constantEvaluator.getConstant().getActualValue()));
        }
        value.repaint();
    }

    private void updateValue(ConstantsPanel parent) {
        EXECUTOR_THREAD.submit(() -> {
            constantEvaluator.setDefinition(value.getText());
            parent.updateTooltips();
        });
    }

    public void setIndex(int index) {
        setBorder(BorderFactory.createTitledBorder(
                MessageFormat.format(BUNDLE.getString("constantCard.constantId"), index)));
    }
}
