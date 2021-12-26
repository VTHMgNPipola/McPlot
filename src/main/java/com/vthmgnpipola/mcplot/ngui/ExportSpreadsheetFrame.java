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
import java.util.Collection;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.miginfocom.swing.MigLayout;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class ExportSpreadsheetFrame extends ExportFunctionsFrame {
    private static final String EXTENSION_CSV = "csv";
    private static final String EXTENSION_ODS = "ods";
    private static final String EXTENSION_XLSX = "xlsx";

    private static final FileChooserExtension EXTENSION = new FileChooserExtension(
            BUNDLE.getString("export.spreadsheet.extensionFilter"), "xlsx",
            EXTENSION_CSV, EXTENSION_ODS, EXTENSION_XLSX);

    private static String lastFilename;

    private JTextField filename;
    private JCheckBox exportConstants;
    private JCheckBox exportFunctionDefinition;
    private JRadioButton commaSeparator;
    private JRadioButton semicolonSeparator;
    private JRadioButton tabSeparator;
    private JRadioButton spaceSeparator;
    private JRadioButton unixSeparator;
    private JRadioButton windowsSeparator;
    private JRadioButton macosSeparator;
    private FunctionSelectionPanel exportedFunctions;

    public ExportSpreadsheetFrame(Map<String, Function> functionMap, Collection<Constant> constants,
                                  Map<String, Double> constantValues, PlottingPanel plottingPanel) {
        super(BUNDLE.getString("export.spreadsheet.title"), functionMap, constants, constantValues, plottingPanel);
    }

    @Override
    public void init() {
        initContentPane();
        pack();
        setLocationRelativeTo(SwingUtilities.getWindowAncestor(plottingPanel));
    }

    @Override
    public void export() {
        dispose();
        lastFilename = filename.getText();
    }

    private void initContentPane() {
        JPanel contentPane = new JPanel(new MigLayout("insets 15", "[]15", "[]10"));
        setContentPane(contentPane);

        filename = new JTextField(lastFilename);
        addFilenameField(contentPane, filename, EXTENSION);

        JPanel csvPanel = new JPanel(new MigLayout("insets 0"));
        contentPane.add(csvPanel, "span");

        exportConstants = new JCheckBox(BUNDLE.getString("export.spreadsheet.exportConstants"));
        csvPanel.add(exportConstants, "span");
        exportConstants.setSelected(true);

        exportFunctionDefinition = new JCheckBox(BUNDLE.getString("export.spreadsheet.exportFunctionDefinition"));
        csvPanel.add(exportFunctionDefinition, "span");
        exportFunctionDefinition.setSelected(true);

        JPanel cellSeparatorPanel = new JPanel(new MigLayout("insets 15", "[]15", "[]10"));
        csvPanel.add(cellSeparatorPanel, "grow");
        cellSeparatorPanel.setBorder(BorderFactory.createTitledBorder(
                BUNDLE.getString("export.spreadsheet.cellSeparator.title")));

        ButtonGroup cellSeparatorGroup = new ButtonGroup();

        commaSeparator = new JRadioButton(BUNDLE.getString("export.spreadsheet.cellSeparator.comma"));
        cellSeparatorPanel.add(commaSeparator, "span");
        cellSeparatorGroup.add(commaSeparator);
        commaSeparator.setSelected(true);

        semicolonSeparator = new JRadioButton(BUNDLE.getString("export.spreadsheet.cellSeparator.semicolon"));
        cellSeparatorPanel.add(semicolonSeparator, "span");
        cellSeparatorGroup.add(semicolonSeparator);

        tabSeparator = new JRadioButton(BUNDLE.getString("export.spreadsheet.cellSeparator.tab"));
        cellSeparatorPanel.add(tabSeparator, "span");
        cellSeparatorGroup.add(tabSeparator);

        spaceSeparator = new JRadioButton(BUNDLE.getString("export.spreadsheet.cellSeparator.space"));
        cellSeparatorPanel.add(spaceSeparator, "span");
        cellSeparatorGroup.add(spaceSeparator);

        JPanel lineSeparatorPanel = new JPanel(new MigLayout("insets 15", "[]15", "[]10"));
        csvPanel.add(lineSeparatorPanel, "span, grow");
        lineSeparatorPanel.setBorder(BorderFactory.createTitledBorder(
                BUNDLE.getString("export.spreadsheet.lineSeparator.title")));

        ButtonGroup lineSeparatorGroup = new ButtonGroup();

        JRadioButton defaultSeparator = new JRadioButton(BUNDLE.getString("export.spreadsheet.lineSeparator.default"));
        lineSeparatorPanel.add(defaultSeparator, "span");
        lineSeparatorGroup.add(defaultSeparator);
        defaultSeparator.setSelected(true);

        unixSeparator = new JRadioButton(BUNDLE.getString("export.spreadsheet.lineSeparator.unix"));
        lineSeparatorPanel.add(unixSeparator, "span");
        lineSeparatorGroup.add(unixSeparator);

        windowsSeparator = new JRadioButton(BUNDLE.getString("export.spreadsheet.lineSeparator.windows"));
        lineSeparatorPanel.add(windowsSeparator, "span");
        lineSeparatorGroup.add(windowsSeparator);

        macosSeparator = new JRadioButton(BUNDLE.getString("export.spreadsheet.lineSeparator.macos"));
        lineSeparatorPanel.add(macosSeparator, "span");
        lineSeparatorGroup.add(macosSeparator);

        JPanel exportedFunctionsPanel = new JPanel(new MigLayout("insets 15", "[]15", "[]10"));
        contentPane.add(exportedFunctionsPanel, "span, grow");
        exportedFunctionsPanel.setBorder(BorderFactory.createTitledBorder(
                BUNDLE.getString("export.spreadsheet.functions.title")));

        exportedFunctions = new FunctionSelectionPanel(functionMap);
        JScrollPane exportedFunctionsScrollPane = new JScrollPane(exportedFunctions);
        exportedFunctionsScrollPane.setMinimumSize(new Dimension(0, 75));
        exportedFunctionsScrollPane.setMaximumSize(new Dimension(5120, 150));
        exportedFunctionsPanel.add(exportedFunctionsScrollPane, "span, push, grow");

        JButton selectAllFunctions = new JButton(BUNDLE.getString("export.spreadsheet.functions.selectAll"),
                new FlatSelectAllIcon());
        exportedFunctionsPanel.add(selectAllFunctions);
        selectAllFunctions.addActionListener(e -> exportedFunctions.selectAll());

        JButton selectNoneFunctions = new JButton(BUNDLE.getString("export.spreadsheet.functions.selectNone"),
                new FlatUnselectAllIcon());
        exportedFunctionsPanel.add(selectNoneFunctions);
        selectNoneFunctions.addActionListener(e -> exportedFunctions.selectNone());

        JButton export = new JButton(BUNDLE.getString("export.spreadsheet.apply"), new FlatApplyIcon());
        contentPane.add(export, "span, alignx right");
        export.addActionListener(e -> export());
    }
}
