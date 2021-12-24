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

import com.formdev.flatlaf.icons.FlatTabbedPaneCloseIcon;
import com.vthmgnpipola.mcplot.ngui.FunctionSettingsFrame;
import com.vthmgnpipola.mcplot.ngui.FunctionsPanel;
import com.vthmgnpipola.mcplot.ngui.PlottingPanel;
import com.vthmgnpipola.mcplot.ngui.icons.FlatMoreSettingsIcon;
import com.vthmgnpipola.mcplot.nmath.Function;
import com.vthmgnpipola.mcplot.nmath.FunctionEvaluator;
import com.vthmgnpipola.mcplot.nmath.MathEventStreamer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class FunctionCard extends JPanel {
    private static final Random RANDOM = new Random();

    private final JCheckBox visible;

    public FunctionCard(FunctionEvaluator functionEvaluator, PlottingPanel plottingPanel, FunctionsPanel parent,
                        int index) {
        setLayout(new MigLayout());

        setIndex(index);

        Function function = functionEvaluator.getFunction();

        ColorChooserButton colorChooserButton = new ColorChooserButton();
        add(colorChooserButton, "growy, split 3");
        colorChooserButton.setToolTipText(BUNDLE.getString("functionCard.selectColor"));
        if (function.getTraceColor() == null) {
            Color startingColor = new Color(RANDOM.nextInt(255), RANDOM.nextInt(255),
                    RANDOM.nextInt(255));
            functionEvaluator.setTraceColor(startingColor);
            colorChooserButton.setSelectedColor(startingColor);
        } else {
            colorChooserButton.setSelectedColor(function.getTraceColor());
        }
        colorChooserButton.setMaximumSize(new Dimension(40, 40));
        colorChooserButton.setColorChooserListener(functionEvaluator::setTraceColor);

        JButton otherSettings = new JButton(new FlatMoreSettingsIcon());
        add(otherSettings, "growy");
        otherSettings.setToolTipText(BUNDLE.getString("functionCard.otherSettings.tooltip"));
        otherSettings.addActionListener(e -> {
            FunctionSettingsFrame functionSettingsFrame = new FunctionSettingsFrame(functionEvaluator, index);
            functionSettingsFrame.init(plottingPanel);
            functionSettingsFrame.setVisible(true);
        });

        visible = new JCheckBox(BUNDLE.getString("functionCard.settings.functionVisible"),
                function.isVisible());
        add(visible, "pushx, growx, wrap");
        visible.setToolTipText(BUNDLE.getString("functionCard.settings.functionVisible.tooltip"));
        visible.addActionListener(e -> functionEvaluator.setVisible(visible.isSelected()));

        JLabeledTextField functionField = new JLabeledTextField();
        add(functionField, "pushx, growx");
        functionField.setText(function.getDefinition());
        functionField.setPlaceholderText(BUNDLE.getString("functionCard.functionDefinition.placeholder"));
        functionField.setToolTipText(BUNDLE.getString("functionCard.functionDefinition.tooltip"));
        functionField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!Objects.equals(function.getDefinition(), functionField.getText())) {
                    functionEvaluator.setDefinition(functionField.getText());
                    plottingPanel.repaint();
                }
            }
        });
        functionField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!Objects.equals(function.getDefinition(), functionField.getText())) {
                    functionEvaluator.setDefinition(functionField.getText());
                    plottingPanel.repaint();
                }
            }
        });

        JButton remove = new JButton(new FlatTabbedPaneCloseIcon());
        add(remove);
        remove.setToolTipText(BUNDLE.getString("generics.remove"));
        remove.addActionListener(e -> {
            plottingPanel.getFunctions().remove(function);
            MathEventStreamer.getInstance().removeFunctionEvaluator(functionEvaluator);

            parent.removeFunctionCard(this);
        });
    }

    public void setIndex(int index) {
        setBorder(BorderFactory.createTitledBorder(
                MessageFormat.format(BUNDLE.getString("functionCard.functionId"), index)));
    }
}
