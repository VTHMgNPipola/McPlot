/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2022  VTHMgNPipola
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

import com.vthmgnpipola.mcplot.GraphAxis;
import com.vthmgnpipola.mcplot.GraphUnit;
import com.vthmgnpipola.mcplot.ngui.PlottingPanelContext;
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

public class PlottingPanelSettingsPanel extends JPanel {
    public PlottingPanelSettingsPanel(PlottingPanelContext context) {
        setLayout(new MigLayout("insets 15", "[]15", "[]10"));

        // Enable antialias
        JCheckBox enableAntialias = new JCheckBox(BUNDLE.getString("settings.plottingPanel.enableAntialias"),
                context.antialias);
        add(enableAntialias, "span");
        enableAntialias.setToolTipText(BUNDLE.getString("settings.plottingPanel.enableAntialias.tooltip"));
        enableAntialias.addActionListener(e -> context.setAntialias(enableAntialias.isSelected()));

        // Enable function legends
        JCheckBox enableFuncLegends = new JCheckBox(BUNDLE.getString("settings.plottingPanel.enableFuncLegends"),
                context.functionLegends);
        add(enableFuncLegends, "span");
        enableFuncLegends.setToolTipText(BUNDLE.getString("settings.plottingPanel.enableFuncLegends.tooltip"));
        enableFuncLegends.addActionListener(e -> context.setFunctionLegends(enableFuncLegends.isSelected()));

        // Draw grid
        JCheckBox drawMinorGrid = new JCheckBox(BUNDLE.getString("settings.plottingPanel.drawMinorGrid"),
                context.drawMinorGrid);
        JCheckBox drawAxisValues = new JCheckBox(BUNDLE.getString("settings.plottingPanel.drawAxisValues"),
                context.drawAxisValues);
        JSpinner minorGridDivisions = new JSpinner(new SpinnerNumberModel(context.minorGridDivisions, 1,
                10, 1));

        JCheckBox drawGrid = new JCheckBox(BUNDLE.getString("settings.plottingPanel.drawGrid"),
                context.drawGrid);
        add(drawGrid, "span");
        drawGrid.setToolTipText(BUNDLE.getString("settings.plottingPanel.drawGrid.tooltip"));
        drawGrid.addActionListener(e -> {
            if (!drawGrid.isSelected()) {
                drawMinorGrid.setSelected(false);
                drawMinorGrid.setEnabled(false);

                drawAxisValues.setSelected(false);
                drawAxisValues.setEnabled(false);

                minorGridDivisions.setEnabled(false);
            } else {
                drawMinorGrid.setSelected(context.drawMinorGrid);
                drawMinorGrid.setEnabled(true);

                drawAxisValues.setSelected(context.drawAxisValues);
                drawAxisValues.setEnabled(true);

                minorGridDivisions.setEnabled(drawMinorGrid.isSelected());
            }
            context.setDrawGrid(drawGrid.isSelected());
        });

        // Draw minor grid
        add(drawMinorGrid, "span");
        drawMinorGrid.addActionListener(e -> {
            minorGridDivisions.setEnabled(drawMinorGrid.isSelected());
            context.setDrawMinorGrid(drawMinorGrid.isSelected());
        });

        // Draw axis values
        add(drawAxisValues, "span");
        drawAxisValues.addActionListener(e -> context.setDrawAxisValues(drawAxisValues.isSelected()));

        // Minor grid divisions
        JLabel minorGridDivisionsLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.minorGridDiv"));
        add(minorGridDivisionsLabel);
        add(minorGridDivisions, "growx, span 2, wrap");
        minorGridDivisions.setToolTipText(BUNDLE.getString("settings.plottingPanel.minorGridDiv.tooltip"));
        enableCommitsOnValidEdit(minorGridDivisions);
        minorGridDivisions.addChangeListener(e ->
                context.setMinorGridDivisions((int) minorGridDivisions.getValue()));

        // X Axis Unit
        JLabeledTextField axisXUnitName = new JLabeledTextField();
        JLabeledTextField axisXUnitDefinition = new JLabeledTextField();

        JLabel axisXUnitLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.axisXUnit"));
        add(axisXUnitLabel);
        JComboBox<GraphUnit> axisXUnit = new JComboBox<>(new GraphUnit[]{GraphUnit.DEFAULT, GraphUnit.PI,
                GraphUnit.EULER, GraphUnit.CUSTOM_X_UNIT});
        add(axisXUnit, "growx, span 2, wrap");
        axisXUnit.setSelectedItem(context.axisX.unit);
        axisXUnit.addActionListener(e -> {
            context.axisX.unit = (GraphUnit) axisXUnit.getSelectedItem();
            context.recalculateAllFunctions(true);

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
                MathEventStreamer.getInstance().functionUpdate(false, true);
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
                MathEventStreamer.getInstance().functionUpdate(false, true);
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
        axisYUnit.setSelectedItem(context.axisY.unit);
        axisYUnit.addActionListener(e -> {
            context.axisY.unit = (GraphUnit) axisYUnit.getSelectedItem();
            context.recalculateAllFunctions(true);

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
                MathEventStreamer.getInstance().functionUpdate(false, true);
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
                MathEventStreamer.getInstance().functionUpdate(false, true);
            }
        });

        // Samples per cell
        JLabel samplesPerCellLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.samplesPerCell"));
        add(samplesPerCellLabel);
        JSpinner samplesPerCell = new JSpinner(new SpinnerNumberModel(context.samplesPerCell, 1,
                1000000000, 1));
        add(samplesPerCell, "growx, span 2, wrap");
        samplesPerCell.setToolTipText(BUNDLE.getString("settings.plottingPanel.samplesPerCell.tooltip"));
        enableCommitsOnValidEdit(samplesPerCell);
        samplesPerCell.addChangeListener(e -> context.setSamplesPerCell((int) samplesPerCell.getValue()));

        // Maximum step
        JLabel maxStepLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.maxStep"));
        add(maxStepLabel);
        JSpinner maxStep = new JSpinner(new SpinnerNumberModel(context.maxStep, 0.000000001,
                1000000000d, 0.01));
        add(maxStep, "growx, span 2, wrap");
        maxStep.setToolTipText(BUNDLE.getString("settings.plottingPanel.maxStep.tooltip"));
        enableCommitsOnValidEdit(maxStep);
        maxStep.addChangeListener(e -> context.setMaxStep((double) maxStep.getValue()));

        // X Scale
        JLabel scaleXLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.scaleX"));
        add(scaleXLabel);
        JSpinner scaleX = new JSpinner(new SpinnerNumberModel(context.axisX.scale, 0.000000001,
                1000000000d, 0.5));
        add(scaleX, "growx");
        enableCommitsOnValidEdit(scaleX);
        scaleX.addChangeListener(e -> {
            context.axisX.scale = (double) scaleX.getValue();
            context.recalculateAllFunctions(true);
        });
        JLabel scaleXUnit = new JLabel("x");
        add(scaleXUnit, "alignx left, wrap");

        // X Type
        JLabel typeXLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.typeX"));
        add(typeXLabel);
        JComboBox<GraphAxis.AxisType> typeX = new JComboBox<>(GraphAxis.AxisType.TYPES);
        add(typeX, "growx, span 2, wrap");
        typeX.setSelectedItem(context.axisX.type);
        typeX.addActionListener(e -> {
            context.axisX.type = (GraphAxis.AxisType) typeX.getSelectedItem();
            context.recalculateAllFunctions(true);
        });

        // Y Scale
        JLabel scaleYLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.scaleY"));
        add(scaleYLabel);
        JSpinner scaleY = new JSpinner(new SpinnerNumberModel(context.axisY.scale, 0.000000001,
                1000000000d, 0.5));
        add(scaleY, "growx");
        enableCommitsOnValidEdit(scaleY);
        scaleY.addChangeListener(e -> {
            context.axisY.scale = (double) scaleY.getValue();
            context.recalculateAllFunctions(true);
        });
        JLabel scaleYUnit = new JLabel("x");
        add(scaleYUnit, "alignx left, wrap");

        // Y Type
        JLabel typeYLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.typeY"));
        add(typeYLabel);
        JComboBox<GraphAxis.AxisType> typeY = new JComboBox<>(GraphAxis.AxisType.TYPES);
        add(typeY, "growx, span 2, wrap");
        typeY.setSelectedItem(context.axisY.type);
        typeY.addActionListener(e -> {
            context.axisY.type = (GraphAxis.AxisType) typeY.getSelectedItem();
            context.recalculateAllFunctions(true);
        });

        // Trace width
        JLabel traceWidthLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.traceWidth"));
        add(traceWidthLabel);
        JSpinner traceWidth = new JSpinner(new SpinnerNumberModel(context.traceWidth, 1, 10, 1));
        add(traceWidth, "growx");
        enableCommitsOnValidEdit(traceWidth);
        traceWidth.addChangeListener(e -> context.setTraceWidth((int) traceWidth.getValue()));
        JLabel traceWidthUnit = new JLabel("px");
        add(traceWidthUnit, "alignx left, wrap");

        // Fill transparency
        JLabel fillTransparencyLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.fillTransparency"));
        add(fillTransparencyLabel);
        JSpinner fillTransparency = new JSpinner(new SpinnerNumberModel(context.fillTransparency, 0,
                100, 0.5));
        add(fillTransparency, "growx");
        fillTransparency.setToolTipText(BUNDLE.getString("settings.plottingPanel.fillTransparency.tooltip"));
        enableCommitsOnValidEdit(fillTransparency);
        fillTransparency.addChangeListener(e -> context.setFillTransparency((double) fillTransparency.getValue()));
        JLabel fillTransparencyUnit = new JLabel("%");
        add(fillTransparencyUnit, "alignx left, wrap");
    }

    private void enableCommitsOnValidEdit(JSpinner spinner) {
        JFormattedTextField field = (JFormattedTextField) spinner.getEditor().getComponent(0);
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);
    }
}
