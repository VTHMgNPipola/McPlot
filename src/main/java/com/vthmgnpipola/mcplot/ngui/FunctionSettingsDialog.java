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

import com.vthmgnpipola.mcplot.nmath.Function;
import com.vthmgnpipola.mcplot.nmath.FunctionEvaluator;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.miginfocom.swing.MigLayout;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class FunctionSettingsDialog extends MDialog {
    private final FunctionEvaluator functionEvaluator;

    public FunctionSettingsDialog(FunctionEvaluator functionEvaluator, int index, Window owner) {
        super(owner, MessageFormat.format(BUNDLE.getString("functionSettings.title"), index),
                ModalityType.APPLICATION_MODAL);
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.functionEvaluator = functionEvaluator;
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("A function settings dialog can't be created without a plotting panel" +
                " bound to it!");
    }

    public void init(PlottingPanel plottingPanel) {
        initContentPane(plottingPanel);
        pack();
        setLocationRelativeTo(SwingUtilities.getWindowAncestor(plottingPanel));
    }

    private void initContentPane(PlottingPanel plottingPanel) {
        Function function = functionEvaluator.getFunction();

        JPanel contentPane = new JPanel(new MigLayout("insets 15", "[]15",
                "[]10"));
        setContentPane(contentPane);

        JLabel domainStartLabel = new JLabel(BUNDLE.getString("functionSettings.domainStart"));
        contentPane.add(domainStartLabel);
        JTextField domainStart = new JTextField(function.getDomainStart().getActualValue() != null ?
                function.getDomainStart().getDefinition() : "*");
        contentPane.add(domainStart, "growx, wrap");
        domainStart.setToolTipText(BUNDLE.getString("functionSettings.domainStart.tooltip"));
        domainStart.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = domainStart.getText().trim();
                functionEvaluator.setDomainStart(text.equals("*") ? null : text);
            }
        });

        JLabel domainEndLabel = new JLabel(BUNDLE.getString("functionSettings.domainEnd"));
        contentPane.add(domainEndLabel);
        JTextField domainEnd = new JTextField(function.getDomainEnd().getActualValue() != null ?
                function.getDomainEnd().getDefinition() : "*");
        contentPane.add(domainEnd, "growx, wrap");
        domainEnd.setToolTipText(BUNDLE.getString("functionSettings.domainEnd.tooltip"));
        domainEnd.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = domainEnd.getText().trim();
                functionEvaluator.setDomainEnd(text.equals("*") ? null : text);
            }
        });

        JCheckBox fillArea = new JCheckBox(BUNDLE.getString("functionSettings.fillArea"), function.isFilled());
        contentPane.add(fillArea, "span");
        fillArea.setToolTipText(BUNDLE.getString("functionSettings.fillArea.tooltip"));
        fillArea.addActionListener(e -> functionEvaluator.setFilled(fillArea.isSelected()));
    }
}
