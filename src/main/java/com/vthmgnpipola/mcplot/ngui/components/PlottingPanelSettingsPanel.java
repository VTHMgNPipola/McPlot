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

import com.vthmgnpipola.mcplot.GraphUnit;
import com.vthmgnpipola.mcplot.ngui.PlottingPanel;
import com.vthmgnpipola.mcplot.nmath.MathEventStreamer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DefaultFormatter;
import net.miginfocom.swing.MigLayout;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_CUSTOM_X_UNIT_DEFINITION;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_CUSTOM_X_UNIT_NAME;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_CUSTOM_Y_UNIT_DEFINITION;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_CUSTOM_Y_UNIT_NAME;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_GRAPH_UNIT_X;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_GRAPH_UNIT_Y;
import static com.vthmgnpipola.mcplot.PreferencesHelper.PREFERENCES;

public class PlottingPanelSettingsPanel extends JPanel {
    public PlottingPanelSettingsPanel(PlottingPanel plottingPanel) {
        setLayout(new MigLayout("insets 15", "[]15", "[]10"));

        // Enable antialias
        JCheckBox enableAntialias = new JCheckBox(BUNDLE.getString("settings.plottingPanel.enableAntialias"),
                plottingPanel.isAntialias());
        add(enableAntialias, "span");
        enableAntialias.setToolTipText(BUNDLE.getString("settings.plottingPanel.enableAntialias.tooltip"));
        enableAntialias.addActionListener(e -> plottingPanel.setAntialias(enableAntialias.isSelected()));

        // Enable function legends
        JCheckBox enableFuncLegends = new JCheckBox(BUNDLE.getString("settings.plottingPanel.enableFuncLegends"),
                plottingPanel.isFunctionLegends());
        add(enableFuncLegends, "span");
        enableFuncLegends.setToolTipText(BUNDLE.getString("settings.plottingPanel.enableFuncLegends.tooltip"));
        enableFuncLegends.addActionListener(e -> plottingPanel.setFunctionLegends(enableFuncLegends.isSelected()));

        // X Axis Unit
        JLabeledTextField axisXUnitName = new JLabeledTextField();
        JLabeledTextField axisXUnitDefinition = new JLabeledTextField();

        JLabel axisXUnitLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.axisXUnit"));
        add(axisXUnitLabel);
        JComboBox<GraphUnit> axisXUnit = new JComboBox<>(new GraphUnit[]{GraphUnit.DEFAULT, GraphUnit.PI,
                GraphUnit.EULER, GraphUnit.CUSTOM_X_UNIT});
        add(axisXUnit, "growx, span 2, wrap");
        axisXUnit.setSelectedItem(plottingPanel.getUnitX());
        axisXUnit.addActionListener(e -> {
            plottingPanel.setUnitX((GraphUnit) axisXUnit.getSelectedItem());
            PREFERENCES.put(KEY_GRAPH_UNIT_X, GraphUnit.getString((GraphUnit) axisXUnit.getSelectedItem()));
            MathEventStreamer.getInstance().functionUpdate(false);

            if (axisXUnit.getSelectedItem() != GraphUnit.CUSTOM_X_UNIT) {
                axisXUnitName.setEnabled(false);
                axisXUnitDefinition.setEnabled(false);
            } else {
                axisXUnitName.setEnabled(true);
                axisXUnitDefinition.setEnabled(true);
            }
        });

        add(new JPanel());

        add(axisXUnitName, "span 2, split 2");
        axisXUnitName.setPlaceholderText(BUNDLE.getString("settings.plottingPanel.axisUnitName"));
        axisXUnitName.setText(GraphUnit.CUSTOM_X_UNIT.getSymbol());
        axisXUnitName.setEnabled(axisXUnit.getSelectedItem() == GraphUnit.CUSTOM_X_UNIT);
        axisXUnitName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                GraphUnit.CUSTOM_X_UNIT.setSymbol(axisXUnitName.getText());
                PREFERENCES.put(KEY_CUSTOM_X_UNIT_NAME, axisXUnitName.getText());
                MathEventStreamer.getInstance().functionUpdate(false);
            }
        });

        add(axisXUnitDefinition, "growx, wrap");
        axisXUnitDefinition.setPlaceholderText(BUNDLE.getString("settings.plottingPanel.axisUnitDefinition"));
        axisXUnitDefinition.setText(GraphUnit.CUSTOM_X_UNIT.getUnitValueEvaluator().getConstant().getDefinition());
        axisXUnitDefinition.setEnabled(axisXUnit.getSelectedItem() == GraphUnit.CUSTOM_X_UNIT);
        axisXUnitDefinition.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                GraphUnit.CUSTOM_X_UNIT.getUnitValueEvaluator().setDefinition(axisXUnitDefinition.getText());
                PREFERENCES.put(KEY_CUSTOM_X_UNIT_DEFINITION, axisXUnitDefinition.getText());
                MathEventStreamer.getInstance().functionUpdate(false);
            }
        });

        // Y Axis Unit
        JLabeledTextField axisYUnitName = new JLabeledTextField();
        JLabeledTextField axisYUnitDefinition = new JLabeledTextField();

        JLabel axisYUnitLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.axisYUnit"));
        add(axisYUnitLabel);
        JComboBox<GraphUnit> axisYUnit = new JComboBox<>(new GraphUnit[]{GraphUnit.DEFAULT, GraphUnit.PI,
                GraphUnit.EULER, GraphUnit.CUSTOM_Y_UNIT});
        add(axisYUnit, "growx, span 2, wrap");
        axisYUnit.setSelectedItem(plottingPanel.getUnitY());
        axisYUnit.addActionListener(e -> {
            plottingPanel.setUnitY((GraphUnit) axisYUnit.getSelectedItem());
            PREFERENCES.put(KEY_GRAPH_UNIT_Y, GraphUnit.getString((GraphUnit) axisYUnit.getSelectedItem()));
            MathEventStreamer.getInstance().functionUpdate(false);

            if (axisYUnit.getSelectedItem() != GraphUnit.CUSTOM_Y_UNIT) {
                axisYUnitName.setEnabled(false);
                axisYUnitDefinition.setEnabled(false);
            } else {
                axisYUnitName.setEnabled(true);
                axisYUnitDefinition.setEnabled(true);
            }
        });

        add(new JPanel());

        add(axisYUnitName, "span 2, split 2");
        axisYUnitName.setPlaceholderText(BUNDLE.getString("settings.plottingPanel.axisUnitName"));
        axisYUnitName.setText(GraphUnit.CUSTOM_Y_UNIT.getSymbol());
        axisYUnitName.setEnabled(axisYUnit.getSelectedItem() == GraphUnit.CUSTOM_Y_UNIT);
        axisYUnitName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                GraphUnit.CUSTOM_Y_UNIT.setSymbol(axisYUnitName.getText());
                PREFERENCES.put(KEY_CUSTOM_Y_UNIT_NAME, axisYUnitName.getText());
                MathEventStreamer.getInstance().functionUpdate(false);
            }
        });

        add(axisYUnitDefinition, "growx, wrap");
        axisYUnitDefinition.setPlaceholderText(BUNDLE.getString("settings.plottingPanel.axisUnitDefinition"));
        axisYUnitDefinition.setText(GraphUnit.CUSTOM_Y_UNIT.getUnitValueEvaluator().getConstant().getDefinition());
        axisYUnitDefinition.setEnabled(axisYUnit.getSelectedItem() == GraphUnit.CUSTOM_Y_UNIT);
        axisYUnitDefinition.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                GraphUnit.CUSTOM_Y_UNIT.getUnitValueEvaluator().setDefinition(axisYUnitDefinition.getText());
                PREFERENCES.put(KEY_CUSTOM_Y_UNIT_DEFINITION, axisYUnitDefinition.getText());
                MathEventStreamer.getInstance().functionUpdate(false);
            }
        });

        // Samples per cell
        JLabel samplesPerCellLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.samplesPerCell"));
        add(samplesPerCellLabel);
        JSpinner samplesPerCell = new JSpinner(new SpinnerNumberModel(plottingPanel.getSamplesPerCell(), 1,
                1000000000, 1));
        add(samplesPerCell, "growx, span 2, wrap");
        samplesPerCell.setToolTipText(BUNDLE.getString("settings.plottingPanel.samplesPerCell.tooltip"));
        enableCommitsOnValidEdit(samplesPerCell);
        samplesPerCell.addChangeListener(e -> plottingPanel.setSamplesPerCell((int) samplesPerCell.getValue()));

        // Maximum step
        JLabel maxStepLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.maxStep"));
        add(maxStepLabel);
        JSpinner maxStep = new JSpinner(new SpinnerNumberModel(plottingPanel.getMaxStep(), 0.000000001,
                1000000000d, 0.01));
        add(maxStep, "growx, span 2, wrap");
        maxStep.setToolTipText(BUNDLE.getString("settings.plottingPanel.maxStep.tooltip"));
        enableCommitsOnValidEdit(maxStep);
        maxStep.addChangeListener(e -> plottingPanel.setMaxStep((double) maxStep.getValue()));

        // X Scale
        JLabel scaleXLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.scaleX"));
        add(scaleXLabel);
        JSpinner scaleX = new JSpinner(new SpinnerNumberModel(plottingPanel.getScaleX(), 0.000000001,
                1000000000d, 0.5));
        add(scaleX, "growx");
        enableCommitsOnValidEdit(scaleX);
        scaleX.addChangeListener(e -> plottingPanel.setScaleX((double) scaleX.getValue()));
        JLabel scaleXUnit = new JLabel("x");
        add(scaleXUnit, "alignx left, wrap");

        // Y Scale
        JLabel scaleYLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.scaleY"));
        add(scaleYLabel);
        JSpinner scaleY = new JSpinner(new SpinnerNumberModel(plottingPanel.getScaleY(), 0.000000001,
                1000000000d, 0.5));
        add(scaleY, "growx");
        enableCommitsOnValidEdit(scaleY);
        scaleY.addChangeListener(e -> plottingPanel.setScaleY((double) scaleY.getValue()));
        JLabel scaleYUnit = new JLabel("x");
        add(scaleYUnit, "alignx left, wrap");

        // Trace width
        JLabel traceWidthLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.traceWidth"));
        add(traceWidthLabel);
        JSpinner traceWidth = new JSpinner(new SpinnerNumberModel(plottingPanel.getTraceWidth(), 1, 10, 1));
        add(traceWidth, "growx");
        enableCommitsOnValidEdit(traceWidth);
        traceWidth.addChangeListener(e -> plottingPanel.setTraceWidth((int) traceWidth.getValue()));
        JLabel traceWidthUnit = new JLabel("px");
        add(traceWidthUnit, "alignx left, wrap");

        // Fill transparency
        JLabel fillTransparencyLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.fillTransparency"));
        add(fillTransparencyLabel);
        JSpinner fillTransparency = new JSpinner(new SpinnerNumberModel(plottingPanel.getFillTransparency(), 0,
                100, 0.5));
        add(fillTransparency, "growx");
        fillTransparency.setToolTipText(BUNDLE.getString("settings.plottingPanel.fillTransparency.tooltip"));
        enableCommitsOnValidEdit(fillTransparency);
        fillTransparency.addChangeListener(e -> plottingPanel.setFillTransparency((double) fillTransparency.getValue()));
        JLabel fillTransparencyUnit = new JLabel("%");
        add(fillTransparencyUnit, "alignx left, wrap");
    }

    private void enableCommitsOnValidEdit(JSpinner spinner) {
        JFormattedTextField field = (JFormattedTextField) spinner.getEditor().getComponent(0);
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);
    }
}
