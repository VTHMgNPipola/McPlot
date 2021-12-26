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

import com.vthmgnpipola.mcplot.ngui.components.FunctionSelectionPanel;
import com.vthmgnpipola.mcplot.ngui.icons.FlatApplyIcon;
import com.vthmgnpipola.mcplot.ngui.icons.FlatSelectAllIcon;
import com.vthmgnpipola.mcplot.ngui.icons.FlatUnselectAllIcon;
import com.vthmgnpipola.mcplot.nmath.Constant;
import com.vthmgnpipola.mcplot.nmath.Function;
import java.awt.Dimension;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.miginfocom.swing.MigLayout;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class ExportTextFileFrame extends ExportFunctionsFrame {
    private static final FileChooserExtension EXTENSION = new FileChooserExtension(
            BUNDLE.getString("export.text.extensionFilter"), "txt", "txt");

    private static String lastFilename;

    private JTextField filename;
    private JCheckBox exportConstants;
    private JCheckBox exportFunctionDefinition;
    private JRadioButton tabSeparator;
    private JRadioButton unixSeparator;
    private JRadioButton windowsSeparator;
    private JRadioButton macosSeparator;
    private FunctionSelectionPanel exportedFunctions;

    public ExportTextFileFrame(Map<String, Function> functionMap, Collection<Constant> constants,
                               Map<String, Double> constantValues, PlottingPanel plottingPanel) {
        super(BUNDLE.getString("export.text.title"), functionMap, constants, constantValues, plottingPanel);
    }

    @Override
    public void init() {
        initContentPane();
        pack();
        setLocationRelativeTo(SwingUtilities.getWindowAncestor(plottingPanel));
    }

    private void initContentPane() {
        JPanel contentPane = new JPanel(new MigLayout("insets 15", "[]15", "[]10"));
        setContentPane(new JScrollPane(contentPane));

        filename = new JTextField(lastFilename);
        addFilenameField(contentPane, filename, EXTENSION);

        exportConstants = new JCheckBox(BUNDLE.getString("export.text.exportConstants"));
        contentPane.add(exportConstants, "span");
        exportConstants.setSelected(true);

        exportFunctionDefinition = new JCheckBox(BUNDLE.getString("export.text.exportFunctionDefinition"));
        contentPane.add(exportFunctionDefinition, "span");
        exportFunctionDefinition.setSelected(true);

        JPanel valueSeparatorPanel = new JPanel(new MigLayout("insets 15", "[]15", "[]10"));
        contentPane.add(valueSeparatorPanel, "span, grow");
        valueSeparatorPanel.setBorder(BorderFactory.createTitledBorder(
                BUNDLE.getString("export.text.valueSeparator.title")));

        ButtonGroup valueSeparatorGroup = new ButtonGroup();

        tabSeparator = new JRadioButton(BUNDLE.getString("export.text.valueSeparator.tab"));
        valueSeparatorPanel.add(tabSeparator, "span");
        valueSeparatorGroup.add(tabSeparator);
        tabSeparator.setSelected(true);

        JRadioButton spaceSeparator = new JRadioButton(BUNDLE.getString("export.text.valueSeparator.space"));
        valueSeparatorPanel.add(spaceSeparator, "span");
        valueSeparatorGroup.add(spaceSeparator);

        JPanel lineSeparatorPanel = new JPanel(new MigLayout("insets 15", "[]15", "[]10"));
        contentPane.add(lineSeparatorPanel, "span, grow");
        lineSeparatorPanel.setBorder(BorderFactory.createTitledBorder(
                BUNDLE.getString("export.text.lineSeparator.title")));

        ButtonGroup lineSeparatorGroup = new ButtonGroup();

        JRadioButton defaultSeparator = new JRadioButton(BUNDLE.getString("export.text.lineSeparator.default"));
        lineSeparatorPanel.add(defaultSeparator, "span");
        lineSeparatorGroup.add(defaultSeparator);
        defaultSeparator.setSelected(true);

        unixSeparator = new JRadioButton(BUNDLE.getString("export.text.lineSeparator.unix"));
        lineSeparatorPanel.add(unixSeparator, "span");
        lineSeparatorGroup.add(unixSeparator);

        windowsSeparator = new JRadioButton(BUNDLE.getString("export.text.lineSeparator.windows"));
        lineSeparatorPanel.add(windowsSeparator, "span");
        lineSeparatorGroup.add(windowsSeparator);

        macosSeparator = new JRadioButton(BUNDLE.getString("export.text.lineSeparator.macos"));
        lineSeparatorPanel.add(macosSeparator, "span");
        lineSeparatorGroup.add(macosSeparator);

        JPanel exportedFunctionsPanel = new JPanel(new MigLayout("insets 15", "[]15", "[]10"));
        contentPane.add(exportedFunctionsPanel, "span, grow");
        exportedFunctionsPanel.setBorder(BorderFactory.createTitledBorder(
                BUNDLE.getString("export.text.functions.title")));

        exportedFunctions = new FunctionSelectionPanel(functionMap);
        JScrollPane exportedFunctionsScrollPane = new JScrollPane(exportedFunctions);
        exportedFunctionsScrollPane.setMinimumSize(new Dimension(0, 75));
        exportedFunctionsScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        exportedFunctionsPanel.add(exportedFunctionsScrollPane, "span, push, grow");

        JButton selectAllFunctions = new JButton(BUNDLE.getString("export.text.functions.selectAll"),
                new FlatSelectAllIcon());
        exportedFunctionsPanel.add(selectAllFunctions);
        selectAllFunctions.addActionListener(e -> exportedFunctions.selectAll());

        JButton selectNoneFunctions = new JButton(BUNDLE.getString("export.text.functions.selectNone"),
                new FlatUnselectAllIcon());
        exportedFunctionsPanel.add(selectNoneFunctions);
        selectNoneFunctions.addActionListener(e -> exportedFunctions.selectNone());

        JButton export = new JButton(BUNDLE.getString("export.text.apply"), new FlatApplyIcon());
        contentPane.add(export, "span, alignx right");
        export.addActionListener(e -> export());
    }

    @Override
    public void export() {
        dispose();
        lastFilename = filename.getText();

        try {
            String valueSeparator;
            if (tabSeparator.isSelected()) {
                valueSeparator = "\t";
            } else {
                valueSeparator = "\s";
            }

            String lineSeparator;
            if (unixSeparator.isSelected()) {
                lineSeparator = "\n";
            } else if (windowsSeparator.isSelected()) {
                lineSeparator = "\r\n";
            } else if (macosSeparator.isSelected()) {
                lineSeparator = "\r";
            } else {
                lineSeparator = System.lineSeparator();
            }

            StringBuilder exportText = new StringBuilder();

            if (exportConstants.isSelected()) {
                boolean exportedAnyConstants = false;
                for (Constant constant : constants) {
                    if (constant.getName() != null && !constant.getName().isBlank() && constant.getDefinition() != null
                            && constant.getActualValue() != null) {
                        exportedAnyConstants = true;
                        exportText.append(constant.getName()).append(valueSeparator).append(constant.getDefinition())
                                .append(valueSeparator).append(constant.getActualValue()).append(lineSeparator);
                    }
                }

                if (exportedAnyConstants) {
                    exportText.append(lineSeparator);
                }
            }

            Map<Function, Future<double[]>> results = evaluateFunctions(exportedFunctions.getSelectedFunctions());
            if (results == null) {
                return;
            }

            for (Map.Entry<Function, Future<double[]>> resultEntry : results.entrySet()) {
                double[] result = resultEntry.getValue().get();

                if (result != null) {
                    if (exportFunctionDefinition.isSelected()) {
                        exportText.append(resultEntry.getKey().getDefinition()).append(lineSeparator);
                    }

                    for (int i = 0; i < result.length; i++) {
                        exportText.append(result[i++]).append(valueSeparator).append(result[i]).append(lineSeparator);
                    }

                    exportText.append(lineSeparator);
                } else {
                    JOptionPane.showMessageDialog(this, BUNDLE.getString("export.error.calculationError"),
                            BUNDLE.getString("generics.errorDialog"), JOptionPane.ERROR_MESSAGE);
                    System.err.println("One or more of the functions couldn't be calculated!");
                    return;
                }
            }

            Files.writeString(Path.of(filename.getText()), exportText.toString());

            JOptionPane.showMessageDialog(this, BUNDLE.getString("export.success"),
                    BUNDLE.getString("generics.successDialog"), JOptionPane.INFORMATION_MESSAGE);
        } catch (Throwable t) {
            JOptionPane.showMessageDialog(this, BUNDLE.getString("export.error"),
                    BUNDLE.getString("generics.errorDialog"), JOptionPane.ERROR_MESSAGE);
            t.printStackTrace();
        }
    }
}
