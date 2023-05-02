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

package com.vthmgnpipola.mcplot.ngui.components;

import com.formdev.flatlaf.icons.FlatTabbedPaneCloseIcon;
import com.vthmgnpipola.mcplot.ngui.PlotsPanel;
import com.vthmgnpipola.mcplot.ngui.PlottingPanel;
import com.vthmgnpipola.mcplot.ngui.PointsPlotSettingsDialog;
import com.vthmgnpipola.mcplot.ngui.icons.FlatMoreSettingsIcon;
import com.vthmgnpipola.mcplot.nmath.PointsPlot;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.Random;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class PointsPlotCard extends JPanel {
    private static final Random RANDOM = new Random();

    private final JCheckBox visible;
    private final PointsPlot plot;

    public PointsPlotCard(PointsPlot plot, PlottingPanel plottingPanel, PlotsPanel parent,
                          int index) {
        setLayout(new MigLayout());

        setIndex(index);

        this.plot = plot;

        ColorChooserButton colorChooserButton = new ColorChooserButton();
        add(colorChooserButton, "growy, split 3");
        colorChooserButton.setToolTipText(BUNDLE.getString("pointsPlotCard.selectColor"));
        if (plot.getTraceColor() == null) {
            Color startingColor = new Color(RANDOM.nextInt(255), RANDOM.nextInt(255),
                    RANDOM.nextInt(255));
            plot.setTraceColor(startingColor);
            colorChooserButton.setSelectedColor(startingColor);
        } else {
            colorChooserButton.setSelectedColor(plot.getTraceColor());
        }
        colorChooserButton.setMaximumSize(new Dimension(40, 40));
        colorChooserButton.setColorChooserListener(plot::setTraceColor);

        JButton otherSettings = new JButton(new FlatMoreSettingsIcon());
        add(otherSettings, "growy");
        otherSettings.setToolTipText(BUNDLE.getString("pointsPlotCard.otherSettings.tooltip"));
        otherSettings.addActionListener(e -> {
            PointsPlotSettingsDialog pointsPlotSettingsDialog = new PointsPlotSettingsDialog(plot, index,
                    SwingUtilities.getWindowAncestor(this));
            pointsPlotSettingsDialog.init();
            pointsPlotSettingsDialog.setVisible(true);
        });

        visible = new JCheckBox(BUNDLE.getString("pointsPlotCard.settings.visible"),
                !plot.isInvisible());
        add(visible, "pushx, growx, wrap");
        visible.setToolTipText(BUNDLE.getString("pointsPlotCard.settings.visible.tooltip"));
        visible.addActionListener(e -> plot.setVisible(visible.isSelected()));

        JLabeledTextField legendField = new JLabeledTextField();
        add(legendField, "pushx, growx");
        legendField.setText(plot.getLegend());
        legendField.setPlaceholderText(BUNDLE.getString("pointsPlotCard.legend.placeholder"));
        legendField.setToolTipText(BUNDLE.getString("pointsPlotCard.legend.tooltip"));
        legendField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                plot.setLegend(legendField.getText());
                plottingPanel.repaint();
            }
        });
        legendField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                plot.setLegend(legendField.getText());
                plottingPanel.repaint();
            }
        });

        JButton remove = new JButton(new FlatTabbedPaneCloseIcon());
        add(remove);
        remove.setToolTipText(BUNDLE.getString("generics.remove"));
        remove.addActionListener(e -> {
            plottingPanel.getPlots().remove(plot);

            //parent.removeFunctionCard(this);
        });
    }

    public void setIndex(int index) {
        setBorder(BorderFactory.createTitledBorder(
                MessageFormat.format(BUNDLE.getString("pointsPlotCard.functionId"), index)));
    }
}
