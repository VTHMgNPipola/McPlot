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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class FunctionSettingsFrame extends JFrame {
    private final FunctionEvaluator functionEvaluator;
    private final FunctionCard functionCard;

    public FunctionSettingsFrame(FunctionEvaluator functionEvaluator, FunctionCard functionCard, int index) {
        super(MessageFormat.format(BUNDLE.getString("functionSettings.title"), index));
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.functionEvaluator = functionEvaluator;
        this.functionCard = functionCard;
    }

    public void init(PlottingPanel plottingPanel) {
        initContentPane(plottingPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private void initContentPane(PlottingPanel plottingPanel) {
        Function function = functionEvaluator.getFunction();

        JPanel contentPane = new JPanel(new MigLayout("insets 15", "[]15",
                "[]10"));
        setContentPane(contentPane);

        JLabel domainStartLabel = new JLabel(BUNDLE.getString("functionSettings.domainStart"));
        contentPane.add(domainStartLabel);
        JTextField domainStart = new JTextField(function.getDomainStart() != null ?
                function.getDomainStart().toString() : "*");
        contentPane.add(domainStart, "growx, wrap");
        domainStart.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = domainStart.getText().trim();
                functionEvaluator.setDomainStart(text.equals("*") ? null : Double.parseDouble(text));
            }
        });

        JLabel domainEndLabel = new JLabel(BUNDLE.getString("functionSettings.domainEnd"));
        contentPane.add(domainEndLabel);
        JTextField domainEnd = new JTextField(function.getDomainEnd() != null ?
                function.getDomainEnd().toString() : "*");
        contentPane.add(domainEnd, "growx, wrap");
        domainEnd.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = domainEnd.getText().trim();
                functionEvaluator.setDomainEnd(text.equals("*") ? null : Double.parseDouble(text));
            }
        });

        JCheckBox fillArea = new JCheckBox(BUNDLE.getString("functionSettings.fillArea"), function.isFilled());
        contentPane.add(fillArea, "span");
        fillArea.setToolTipText(BUNDLE.getString("functionSettings.fillAreaTooltip"));
        fillArea.addActionListener(e -> functionEvaluator.setFilled(fillArea.isSelected()));
    }
}
