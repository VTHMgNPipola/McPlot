/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2024  VTHMgNPipola
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

import com.vthmgnpipola.mcplot.messaging.MessagePublisher;
import com.vthmgnpipola.mcplot.messaging.MessageRouter;
import com.vthmgnpipola.mcplot.nmath.Function;
import com.vthmgnpipola.mcplot.plot.FunctionPlot;
import com.vthmgnpipola.mcplot.plot.FunctionPlotter;
import com.vthmgnpipola.mcplot.plot.PlotUpdateMessage;
import com.vthmgnpipola.mcplot.plot.TraceType;
import net.miginfocom.swing.MigLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DefaultFormatter;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.Objects;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;
import static com.vthmgnpipola.mcplot.plot.SingleVariableFunctionPlot.KEY_DOT_SHAPE;
import static com.vthmgnpipola.mcplot.plot.SingleVariableFunctionPlot.KEY_DOT_SIZE;
import static com.vthmgnpipola.mcplot.plot.SingleVariableFunctionPlot.KEY_FILLED_SHAPE;
import static com.vthmgnpipola.mcplot.plot.SingleVariableFunctionPlot.KEY_TRACE_TYPE;
import static com.vthmgnpipola.mcplot.plot.SingleVariableFunctionPlot.VALUE_DOT_SHAPE_CIRCLE;
import static com.vthmgnpipola.mcplot.plot.SingleVariableFunctionPlot.VALUE_DOT_SHAPE_DIAMOND;
import static com.vthmgnpipola.mcplot.plot.SingleVariableFunctionPlot.VALUE_DOT_SHAPE_DISABLED;
import static com.vthmgnpipola.mcplot.plot.SingleVariableFunctionPlot.VALUE_DOT_SHAPE_SQUARE;
import static com.vthmgnpipola.mcplot.plot.SingleVariableFunctionPlot.VALUE_DOT_SHAPE_TRIANGLE;
import static com.vthmgnpipola.mcplot.plot.SingleVariableFunctionPlot.VALUE_DOT_SIZE_FOLLOWS_TRACE;
import static com.vthmgnpipola.mcplot.plot.SingleVariableFunctionPlot.VALUE_FILL_DISABLE;
import static com.vthmgnpipola.mcplot.plot.SingleVariableFunctionPlot.VALUE_FILL_SOLID;
import static com.vthmgnpipola.mcplot.plot.SingleVariableFunctionPlot.VALUE_TRACE_TYPE_DEFAULT;
import static com.vthmgnpipola.mcplot.plot.SingleVariableFunctionPlot.VALUE_TRACE_TYPE_DISABLED;

public class FunctionSettingsDialog extends MDialog {
    private static final FunctionTraceType TRACE_DEFAULT = new FunctionTraceType(TraceType.TRACE_TYPE_DEFAULT,
            BUNDLE.getString("functionSettings.traceType.default"));
    private static final FunctionTraceType TRACE_DASHED = new FunctionTraceType(TraceType.TRACE_TYPE_DASHED,
            BUNDLE.getString("functionSettings.traceType.dashed"));
    private static final FunctionTraceType TRACE_DOTTED = new FunctionTraceType(TraceType.TRACE_TYPE_DOTTED,
            BUNDLE.getString("functionSettings.traceType.dotted"));
    private static final FunctionTraceType TRACE_DASHED_DOTTED = new FunctionTraceType(
            TraceType.TRACE_TYPE_DASHED_DOTTED, BUNDLE.getString("functionSettings.traceType.dashedDotted"));
    private static final FunctionTraceType TRACE_DISABLED = new FunctionTraceType(
            null, BUNDLE.getString("functionSettings.traceType.disabled"));
    private static final FunctionTraceType[] TRACES = new FunctionTraceType[]{TRACE_DEFAULT, TRACE_DASHED,
            TRACE_DOTTED, TRACE_DASHED_DOTTED, TRACE_DISABLED};

    private static final StringDescriptionPair DOT_SHAPE_DISABLED = new StringDescriptionPair(VALUE_DOT_SHAPE_DISABLED,
            BUNDLE.getString("functionSettings.dotShape.disabled"));
    private static final StringDescriptionPair DOT_SHAPE_CIRCLE = new StringDescriptionPair(VALUE_DOT_SHAPE_CIRCLE,
            BUNDLE.getString("functionSettings.dotShape.circle"));
    private static final StringDescriptionPair DOT_SHAPE_SQUARE = new StringDescriptionPair(VALUE_DOT_SHAPE_SQUARE,
            BUNDLE.getString("functionSettings.dotShape.square"));
    private static final StringDescriptionPair DOT_SHAPE_TRIANGLE = new StringDescriptionPair(VALUE_DOT_SHAPE_TRIANGLE,
            BUNDLE.getString("functionSettings.dotShape.triangle"));
    private static final StringDescriptionPair DOT_SHAPE_DIAMOND = new StringDescriptionPair(VALUE_DOT_SHAPE_DIAMOND,
            BUNDLE.getString("functionSettings.dotShape.diamond"));
    private static final StringDescriptionPair[] DOT_SHAPES = {DOT_SHAPE_DISABLED, DOT_SHAPE_CIRCLE, DOT_SHAPE_SQUARE,
            DOT_SHAPE_TRIANGLE, DOT_SHAPE_DIAMOND};

    private final FunctionPlotter functionPlotter;

    private final MessagePublisher messagePublisher = MessageRouter.publisher();

    public FunctionSettingsDialog(FunctionPlotter functionPlotter, int index, Window owner) {
        super(owner, MessageFormat.format(BUNDLE.getString("functionSettings.title"), index),
                ModalityType.APPLICATION_MODAL);
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.functionPlotter = functionPlotter;
    }

    @Override
    public void init() {
        initContentPane();
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void initContentPane() {
        Function function = functionPlotter.getEvaluator().getFunction();

        JPanel contentPane = new JPanel(new MigLayout("insets 15", "[]15",
                "[]10"));
        setContentPane(contentPane);

        // Domain start
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
                functionPlotter.getEvaluator().setDomainStart(text.equals("*") ? null : text);
                messagePublisher.publish(new PlotUpdateMessage());
            }
        });

        // Domain end
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
                functionPlotter.getEvaluator().setDomainEnd(text.equals("*") ? null : text);
                messagePublisher.publish(new PlotUpdateMessage());
            }
        });

        // Trace type
        JLabel traceTypeLabel = new JLabel(BUNDLE.getString("functionSettings.traceType"));
        contentPane.add(traceTypeLabel);
        JComboBox<FunctionTraceType> traceType = new JComboBox<>(TRACES);
        contentPane.add(traceType, "growx, wrap");
        traceType.setSelectedItem(getSelectedTraceType(functionPlotter.getPlot().getTrace().getType()));
        traceType.addActionListener(e -> {
            FunctionTraceType functionTraceType = (FunctionTraceType) traceType.getSelectedItem();
            if (functionTraceType == TRACE_DISABLED) {
                functionPlotter.getPlot().setPlottingParameter(KEY_TRACE_TYPE, VALUE_TRACE_TYPE_DISABLED);
            } else {
                functionPlotter.getPlot().setPlottingParameter(KEY_TRACE_TYPE, VALUE_TRACE_TYPE_DEFAULT);
                functionPlotter.getPlot().getTrace()
                        .setType(((FunctionTraceType) Objects.requireNonNull(traceType.getSelectedItem())).traceType);
            }
            messagePublisher.publish(new PlotUpdateMessage());
        });

        // Dot shape
        JLabel dotShapeLabel = new JLabel(BUNDLE.getString("functionSettings.dotShape"));
        contentPane.add(dotShapeLabel);
        JComboBox<StringDescriptionPair> dotShape = new JComboBox<>(DOT_SHAPES);
        contentPane.add(dotShape, "growx, wrap");
        dotShape.setToolTipText(BUNDLE.getString("functionSettings.dotShape.tooltip"));
        dotShape.setSelectedItem(getSelectedDotShape(functionPlotter.getPlot()));
        dotShape.addActionListener(e -> {
            StringDescriptionPair item = (StringDescriptionPair) dotShape.getSelectedItem();
            if (item != null) {
                functionPlotter.getPlot().setPlottingParameter(KEY_DOT_SHAPE, item.value);
                messagePublisher.publish(new PlotUpdateMessage());
            }
        });

        boolean dotSizeFollowsTraceValue = functionPlotter.getPlot()
                .hasPlottingParameter(KEY_DOT_SIZE, VALUE_DOT_SIZE_FOLLOWS_TRACE, true);
        int dotSizeValue;
        final int DEFAULT_DOT_SIZE_VALUE = 10;
        if (dotSizeFollowsTraceValue) {
            dotSizeValue = DEFAULT_DOT_SIZE_VALUE;
        } else {
            try {
                dotSizeValue = Integer.parseInt(functionPlotter.getPlot().getPlottingParameter(KEY_DOT_SIZE));
            } catch (Throwable t) {
                dotSizeValue = DEFAULT_DOT_SIZE_VALUE;
            }
        }
        JSpinner dotSize = new JSpinner(new SpinnerNumberModel(dotSizeValue, 1, 50, 1));
        dotSize.setEnabled(!dotSizeFollowsTraceValue);

        // Dot size follows trace width
        JCheckBox dotSizeFollowsTrace = new JCheckBox(BUNDLE.getString("functionSettings.dotSizeFollowsTrace"),
                dotSizeFollowsTraceValue);
        contentPane.add(dotSizeFollowsTrace, "span");
        dotSizeFollowsTrace.addActionListener(e -> {
            if (dotSizeFollowsTrace.isSelected()) {
                functionPlotter.getPlot().setPlottingParameter(KEY_DOT_SIZE, VALUE_DOT_SIZE_FOLLOWS_TRACE);
                dotSize.setEnabled(false);
            } else {
                dotSize.setEnabled(true);
                functionPlotter.getPlot().setPlottingParameter(KEY_DOT_SIZE, String.valueOf(dotSize.getValue()));
            }
            messagePublisher.publish(new PlotUpdateMessage());
        });

        JLabel dotSizeLabel = new JLabel(BUNDLE.getString("functionSettings.dotSize"));
        contentPane.add(dotSizeLabel);
        contentPane.add(dotSize, "growx, wrap");
        dotSize.setToolTipText(BUNDLE.getString("functionSettings.dotSize.tooltip"));
        JFormattedTextField field = (JFormattedTextField) dotSize.getEditor().getComponent(0);
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);
        dotSize.addChangeListener(e -> {
            functionPlotter.getPlot().setPlottingParameter(KEY_DOT_SIZE, String.valueOf(dotSize.getValue()));
            messagePublisher.publish(new PlotUpdateMessage());
        });

        JCheckBox fillArea = new JCheckBox(BUNDLE.getString("functionSettings.fillArea"),
                functionPlotter.getPlot().hasPlottingParameter(KEY_FILLED_SHAPE, VALUE_FILL_SOLID, false));
        contentPane.add(fillArea, "span");
        fillArea.setToolTipText(BUNDLE.getString("functionSettings.fillArea.tooltip"));
        fillArea.addActionListener(e -> {
            String plottingParameter = fillArea.isSelected() ? VALUE_FILL_SOLID : VALUE_FILL_DISABLE;
            functionPlotter.setPlottingParameter(KEY_FILLED_SHAPE, plottingParameter);
            messagePublisher.publish(new PlotUpdateMessage());
        });
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

    private StringDescriptionPair getSelectedDotShape(FunctionPlot plot) {
        if (plot == null || plot.getPlottingParameter(KEY_DOT_SHAPE) == null) {
            return DOT_SHAPE_DISABLED;
        }
        return switch (plot.getPlottingParameter(KEY_DOT_SHAPE)) {
            case VALUE_DOT_SHAPE_CIRCLE -> DOT_SHAPE_CIRCLE;
            case VALUE_DOT_SHAPE_SQUARE -> DOT_SHAPE_SQUARE;
            case VALUE_DOT_SHAPE_TRIANGLE -> DOT_SHAPE_TRIANGLE;
            case VALUE_DOT_SHAPE_DIAMOND -> DOT_SHAPE_DIAMOND;
            default -> DOT_SHAPE_DISABLED;
        };
    }

    private record FunctionTraceType(TraceType traceType, String description) {
        @Override
        public String toString() {
            return description;
        }
    }

    private record StringDescriptionPair(String value, String description) {
        @Override
        public String toString() {
            return description;
        }
    }
}
