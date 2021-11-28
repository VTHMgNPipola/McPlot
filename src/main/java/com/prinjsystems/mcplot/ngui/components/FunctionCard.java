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

package com.prinjsystems.mcplot.ngui.components;

import com.prinjsystems.mcplot.ngui.FunctionPanel;
import com.prinjsystems.mcplot.ngui.FunctionSettingsFrame;
import com.prinjsystems.mcplot.ngui.PlottingPanel;
import com.prinjsystems.mcplot.nmath.Constant;
import com.prinjsystems.mcplot.nmath.Function;
import com.prinjsystems.mcplot.nmath.FunctionPlot;
import com.prinjsystems.mcplot.nmath.MathEvaluatorPool;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class FunctionCard extends JPanel {
    private static final Random RANDOM = new Random();

    private final JCheckBox visible;

    private final Function function;
    private final List<Function> functions;
    private final List<Constant> constants;
    private final PlottingPanel plottingPanel;

    public FunctionCard(Function function, List<Function> functions, List<Constant> constants,
                        PlottingPanel plottingPanel, FunctionPanel parent, int index) {
        setLayout(new MigLayout());
        this.function = function;
        this.functions = functions;
        this.constants = constants;
        this.plottingPanel = plottingPanel;

        setIndex(index);

        ColorChooserButton colorChooserButton = new ColorChooserButton();
        add(colorChooserButton, "growy, split 3");
        colorChooserButton.setToolTipText(BUNDLE.getString("functionCard.selectColor"));
        if (function.getTraceColor() == null) {
            Color startingColor = new Color(RANDOM.nextInt(255), RANDOM.nextInt(255),
                    RANDOM.nextInt(255));
            function.setTraceColor(startingColor);
            colorChooserButton.setSelectedColor(startingColor);
        } else {
            colorChooserButton.setSelectedColor(function.getTraceColor());
        }
        colorChooserButton.setMaximumSize(new Dimension(40, 40));
        colorChooserButton.setColorChooserListener(color -> {
            function.setTraceColor(color);
            plottingPanel.repaint();
        });

        JButton otherSettings = new JButton("...");
        add(otherSettings, "growy");
        otherSettings.setToolTipText(BUNDLE.getString("functionCard.otherSettingsTooltip"));
        otherSettings.addActionListener(e -> {
            FunctionSettingsFrame functionSettingsFrame = new FunctionSettingsFrame(function, this, index);
            functionSettingsFrame.init(plottingPanel);
            functionSettingsFrame.setVisible(true);
        });

        visible = new JCheckBox(BUNDLE.getString("functionCard.settings.functionVisible"),
                function.isVisible());
        add(visible, "pushx, growx, wrap");
        visible.setToolTipText(BUNDLE.getString("functionCard.settings.functionVisibleTooltip"));
        visible.addActionListener(e -> {
            FunctionPlot plot = plottingPanel.getFunctions().get(function);
            if (plot != null) {
                function.setVisible(visible.isSelected());
                plottingPanel.getFunctions().put(function, plot);
                plottingPanel.repaint();
            }
        });

        JLabeledTextField functionField = new JLabeledTextField();
        add(functionField, "pushx, growx");
        functionField.setText(function.getDefinition());
        functionField.setPlaceholderText(BUNDLE.getString("functionCard.functionDefinition.placeholder"));
        functionField.setToolTipText(BUNDLE.getString("functionCard.functionDefinition.tooltip"));
        functionField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!Objects.equals(function.getDefinition(), functionField.getText())) {
                    function.setDefinition(functionField.getText());
                    recalculateFunction();
                    plottingPanel.repaint();
                }
            }
        });
        functionField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!Objects.equals(function.getDefinition(), functionField.getText())) {
                    function.setDefinition(functionField.getText());
                    recalculateFunction();
                    plottingPanel.repaint();
                }
            }
        });

        JButton remove = new JButton("X");
        add(remove);
        remove.setToolTipText(BUNDLE.getString("generics.remove"));
        remove.addActionListener(e -> {
            plottingPanel.getFunctions().remove(function);
            plottingPanel.repaint();

            parent.removeFunctionCard(this);
        });
    }

    public void recalculateFunction() {
        double zoomX = plottingPanel.getScaleX() * plottingPanel.getPixelsPerStep() * plottingPanel.getZoom();
        double domainStart = function.getDomainStart() != null ? function.getDomainStart() :
                plottingPanel.getCameraX() / zoomX;
        double domainEnd = function.getDomainEnd() != null ? function.getDomainEnd() :
                (plottingPanel.getCameraX() + plottingPanel.getWidth()) / zoomX;
        double step =
                (domainEnd - domainStart) / (plottingPanel.getWidth() / zoomX * plottingPanel.getSamplesPerCell());
        if (function.getDomainStart() == null) {
            domainStart -= step;
        }
        if (function.getDomainEnd() == null) {
            domainEnd += step;
        }
        MathEvaluatorPool.getInstance().evaluateFunction(function, domainStart, domainEnd, step, functions, constants,
                plot -> plottingPanel.getFunctions().put(function, plot));
    }

    public void setIndex(int index) {
        setBorder(BorderFactory.createTitledBorder(
                MessageFormat.format(BUNDLE.getString("functionCard.functionId"), index)));
    }
}
