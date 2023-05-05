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

package com.vthmgnpipola.mcplot.ngui;

import com.vthmgnpipola.mcplot.nmath.Function;
import com.vthmgnpipola.mcplot.nmath.FunctionEvaluator;
import com.vthmgnpipola.mcplot.plot.FunctionPlotParameters;
import com.vthmgnpipola.mcplot.plot.TraceType;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.Objects;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class FunctionSettingsDialog extends MDialog {
    private static final FunctionTraceType TRACE_DEFAULT = new FunctionTraceType(TraceType.TRACE_TYPE_DEFAULT,
            BUNDLE.getString("functionSettings.traceType.default"));
    private static final FunctionTraceType TRACE_DASHED = new FunctionTraceType(TraceType.TRACE_TYPE_DASHED,
            BUNDLE.getString("functionSettings.traceType.dashed"));
    private static final FunctionTraceType TRACE_DOTTED = new FunctionTraceType(TraceType.TRACE_TYPE_DOTTED,
            BUNDLE.getString("functionSettings.traceType.dotted"));
    private static final FunctionTraceType TRACE_DASHED_DOTTED = new FunctionTraceType(
            TraceType.TRACE_TYPE_DASHED_DOTTED, BUNDLE.getString("functionSettings.traceType.dashedDotted"));
    private static final FunctionTraceType[] TRACES = new FunctionTraceType[]{TRACE_DEFAULT, TRACE_DASHED,
            TRACE_DOTTED, TRACE_DASHED_DOTTED};

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
        initContentPane();
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void initContentPane() {
        Function function = functionEvaluator.getFunction();
        FunctionPlotParameters plotParameters = functionEvaluator.getParameters();

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

        JLabel traceTypeLabel = new JLabel(BUNDLE.getString("functionSettings.traceType"));
        contentPane.add(traceTypeLabel);
        JComboBox<FunctionTraceType> traceType = new JComboBox<>(TRACES);
        contentPane.add(traceType, "growx, wrap");
        traceType.setSelectedItem(getSelectedTraceType(plotParameters.getTrace().getType()));
        traceType.addActionListener(e -> plotParameters.getTrace()
                .setType(((FunctionTraceType) Objects.requireNonNull(traceType.getSelectedItem())).traceType));

        JCheckBox fillArea = new JCheckBox(BUNDLE.getString("functionSettings.fillArea"), plotParameters.isFilled());
        contentPane.add(fillArea, "span");
        fillArea.setToolTipText(BUNDLE.getString("functionSettings.fillArea.tooltip"));
        fillArea.addActionListener(e -> functionEvaluator.setFilled(fillArea.isSelected()));
    }

    private FunctionTraceType getSelectedTraceType(TraceType traceType) {
        if (traceType == null) {
            return TRACE_DEFAULT;
        }
        return switch (traceType) {
            case TRACE_TYPE_DASHED -> TRACE_DASHED;
            case TRACE_TYPE_DOTTED -> TRACE_DOTTED;
            case TRACE_TYPE_DASHED_DOTTED -> TRACE_DASHED_DOTTED;
            default -> TRACE_DEFAULT;
        };
    }

    private record FunctionTraceType(TraceType traceType, String description) {
        @Override
        public String toString() {
            return description;
        }
    }
}
