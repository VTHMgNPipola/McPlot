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

import com.github.jferard.fastods.AnonymousOdsFileWriter;
import com.github.jferard.fastods.OdsDocument;
import com.github.jferard.fastods.OdsFactory;
import com.github.jferard.fastods.Table;
import com.github.jferard.fastods.TableCellWalker;
import com.vthmgnpipola.mcplot.ngui.components.FunctionSelectionPanel;
import com.vthmgnpipola.mcplot.ngui.icons.FlatApplyIcon;
import com.vthmgnpipola.mcplot.ngui.icons.FlatSelectAllIcon;
import com.vthmgnpipola.mcplot.ngui.icons.FlatUnselectAllIcon;
import com.vthmgnpipola.mcplot.nmath.Constant;
import com.vthmgnpipola.mcplot.nmath.Function;
import de.siegmar.fastcsv.writer.CsvWriter;
import de.siegmar.fastcsv.writer.LineDelimiter;
import de.siegmar.fastcsv.writer.QuoteStrategy;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.miginfocom.swing.MigLayout;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class ExportSpreadsheetDialog extends ExportFunctionsDialog {
    private static final String EXTENSION_CSV = "csv";
    private static final String EXTENSION_ODS = "ods";
    private static final String EXTENSION_XLSX = "xlsx";

    private static final FileChooserExtension EXTENSION = new FileChooserExtension(
            BUNDLE.getString("export.spreadsheet.extensionFilter"), EXTENSION_ODS,
            EXTENSION_CSV, EXTENSION_ODS, EXTENSION_XLSX);

    private static final String TYPE_CSV = "csv";
    private static final String TYPE_COMPLEX = "complex";
    private static final String TYPE_INVALID = "invalid";

    private static String lastFilename;

    private JTextField filename;
    private JCheckBox exportConstants;
    private JCheckBox exportFunctionDefinition;
    private JCheckBox exportHeaders;
    private CSVPropertiesPanel csvPropertiesPanel;
    private ComplexPropertiesPanel complexPropertiesPanel;
    private String selectedType;
    private FunctionSelectionPanel exportedFunctions;

    public ExportSpreadsheetDialog(Map<String, Function> functionMap, Collection<Constant> constants,
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
        if (selectedType.equals(TYPE_INVALID)) {
            JOptionPane.showMessageDialog(this, BUNDLE.getString("export.spreadsheet.invalidType"),
                    BUNDLE.getString("generics.errorDialog"), JOptionPane.WARNING_MESSAGE);
            return;
        }

        dispose();
        lastFilename = filename.getText();

        try {
            Map<Function, Future<double[]>> results = evaluateFunctions(exportedFunctions.getSelectedFunctions());
            if (results == null) {
                return;
            }

            switch (EXTENSION.getFileType(filename.getText().toLowerCase())) {
                case EXTENSION_CSV -> exportCsv(results);
                case EXTENSION_ODS -> exportOds(results);
                case EXTENSION_XLSX -> exportXlsx(results);
            }

            JOptionPane.showMessageDialog(this, BUNDLE.getString("export.success"),
                    BUNDLE.getString("generics.successDialog"), JOptionPane.INFORMATION_MESSAGE);
        } catch (Throwable t) {
            JOptionPane.showMessageDialog(this, BUNDLE.getString("export.error"),
                    BUNDLE.getString("generics.errorDialog"), JOptionPane.ERROR_MESSAGE);
            t.printStackTrace();
        }
    }

    private void exportCsv(Map<Function, Future<double[]>> results) throws ExecutionException, InterruptedException,
            IOException {
        char cellSeparator = 0;
        if (csvPropertiesPanel.commaSeparator.isSelected()) {
            cellSeparator = ',';
        } else if (csvPropertiesPanel.semicolonSeparator.isSelected()) {
            cellSeparator = ';';
        } else if (csvPropertiesPanel.tabSeparator.isSelected()) {
            cellSeparator = '\t';
        } else if (csvPropertiesPanel.spaceSeparator.isSelected()) {
            cellSeparator = ' ';
        }

        LineDelimiter lineSeparator = LineDelimiter.PLATFORM;
        if (csvPropertiesPanel.unixSeparator.isSelected()) {
            lineSeparator = LineDelimiter.LF;
        } else if (csvPropertiesPanel.windowsSeparator.isSelected()) {
            lineSeparator = LineDelimiter.CRLF;
        } else if (csvPropertiesPanel.macosSeparator.isSelected()) {
            lineSeparator = LineDelimiter.CR;
        }

        try (CsvWriter writer = CsvWriter.builder().fieldSeparator(cellSeparator).lineDelimiter(lineSeparator)
                .quoteCharacter('"').quoteStrategy(QuoteStrategy.ALWAYS)
                .build(Files.newBufferedWriter(Path.of(filename.getText())))) {
            boolean hasExportableConstants = constants != null &&
                    constants.parallelStream().anyMatch(c -> c.getActualValue() != null && c.getName() != null &&
                            !c.getName().isBlank());
            if (exportConstants.isSelected() && hasExportableConstants) {
                if (exportHeaders.isSelected()) {
                    writer.writeRow(BUNDLE.getString("export.spreadsheet.constantName"),
                            BUNDLE.getString("export.spreadsheet.constantDefinition"),
                            BUNDLE.getString("export.spreadsheet.constantValue"));
                }

                for (Constant constant : constants) {
                    if (constant.getActualValue() != null && constant.getName() != null && !constant.getName().isBlank()) {
                        writer.writeRow(constant.getName(), constant.getDefinition(), constant.getActualValue().toString());
                    }
                }

                writer.writeRow();
            }

            if (exportHeaders.isSelected()) {
                String xLabel = BUNDLE.getString("export.spreadsheet.functionX");
                String resultLabel = BUNDLE.getString("export.spreadsheet.functionResult");
                if (exportFunctionDefinition.isSelected()) {
                    writer.writeRow(BUNDLE.getString("export.spreadsheet.functionDefinition"), xLabel, resultLabel);
                } else {
                    writer.writeRow(xLabel, resultLabel);
                }
            }
            for (Map.Entry<Function, Future<double[]>> functionEntry : results.entrySet()) {
                double[] values = functionEntry.getValue().get();
                for (int i = 0; i < values.length; i++) {
                    String firstColumn = i == 0 ? functionEntry.getKey().getDefinition() : "";
                    if (exportFunctionDefinition.isSelected()) {
                        writer.writeRow(firstColumn, String.valueOf(values[i++]), String.valueOf(values[i]));
                    } else {
                        writer.writeRow(String.valueOf(values[i++]), String.valueOf(values[i]));
                    }
                }
            }
        }
    }

    private void exportOds(Map<Function, Future<double[]>> results) throws IOException, ExecutionException,
            InterruptedException {
        OdsFactory odsFactory = OdsFactory.create();
        AnonymousOdsFileWriter writer = odsFactory.createWriter();
        OdsDocument document = writer.document();

        // Initial sheet
        String tableName;
        AtomicInteger functionSheetIndex = new AtomicInteger(1);
        boolean hasExportableConstants = constants != null &&
                constants.parallelStream().anyMatch(c -> c.getActualValue() != null && c.getName() != null &&
                        !c.getName().isBlank());
        boolean firstFunctionSheet = false;
        if (exportConstants.isSelected() && complexPropertiesPanel.separateConstants.isSelected() && hasExportableConstants) {
            tableName = BUNDLE.getString("export.spreadsheet.constantSheetName");
        } else if (complexPropertiesPanel.separateFunctions.isSelected()) {
            tableName = MessageFormat.format(BUNDLE.getString("export.spreadsheet.functionSheetName"),
                    functionSheetIndex.getAndIncrement());
            firstFunctionSheet = true;
        } else if ((!exportConstants.isSelected() || !hasExportableConstants) &&
                !complexPropertiesPanel.separateFunctions.isSelected()) {
            tableName = BUNDLE.getString("export.spreadsheet.generalFunctionsSheetName");
            firstFunctionSheet = true;
        } else {
            tableName = BUNDLE.getString("export.spreadsheet.generalSheetName");
            firstFunctionSheet = true;
        }

        Table currentTable = document.addTable(tableName);
        TableCellWalker walker = currentTable.getWalker();

        // Constants table
        if (exportConstants.isSelected() && hasExportableConstants) {
            if (exportHeaders.isSelected()) {
                walker.setStringValue(BUNDLE.getString("export.spreadsheet.constantName"));
                walker.next();
                walker.setStringValue(BUNDLE.getString("export.spreadsheet.constantDefinition"));
                walker.next();
                walker.setStringValue(BUNDLE.getString("export.spreadsheet.constantValue"));
                walker.nextRow();
            }

            for (Constant constant : constants) {
                if (constant.getActualValue() != null && constant.getName() != null && !constant.getName().isBlank()) {
                    walker.setStringValue(constant.getName());
                    walker.next();
                    walker.setStringValue(constant.getDefinition());
                    walker.next();
                    walker.setFloatValue(constant.getActualValue());
                    walker.nextRow();
                }
            }

            walker.nextRow();
        }

        // Function sheets
        String functionDefinitionLabel = BUNDLE.getString("export.spreadsheet.functionDefinition");
        String xLabel = BUNDLE.getString("export.spreadsheet.functionX");
        String resultLabel = BUNDLE.getString("export.spreadsheet.functionResult");
        String tableLabel = BUNDLE.getString("export.spreadsheet.functionSheetName");
        boolean exportFunctionDefinitionValue = exportFunctionDefinition.isSelected();
        for (Map.Entry<Function, Future<double[]>> functionEntry : results.entrySet()) {
            double[] result = functionEntry.getValue().get();

            // Create new sheet if necessary
            if (((hasExportableConstants || !firstFunctionSheet) ^ (!exportConstants.isSelected() &&
                    firstFunctionSheet))) {
                if (complexPropertiesPanel.separateFunctions.isSelected()) {
                    currentTable = document.addTable(MessageFormat.format(tableLabel,
                            functionSheetIndex.getAndIncrement()));
                    walker = currentTable.getWalker();
                } else if (complexPropertiesPanel.separateConstants.isSelected() && functionSheetIndex.get() == 1) {
                    currentTable = document.addTable(BUNDLE
                            .getString("export.spreadsheet.generalFunctionsSheetName"));
                    walker = currentTable.getWalker();
                }
            }

            // Add function definition
            if (exportFunctionDefinitionValue) {
                if (exportHeaders.isSelected() && (complexPropertiesPanel.separateFunctions.isSelected() ||
                        functionSheetIndex.get() == 1 || firstFunctionSheet)) {
                    walker.setStringValue(functionDefinitionLabel);
                    walker.nextRow();
                    walker.setStringValue(functionEntry.getKey().getDefinition().trim());
                    walker.setCellMerge(result.length / 2, 1);
                    walker.previousRow();
                    walker.next();
                } else {
                    walker.setStringValue(functionEntry.getKey().getDefinition().trim());
                    walker.setCellMerge(result.length / 2, 1);
                }
            }
            // Add header
            if (exportHeaders.isSelected() && (complexPropertiesPanel.separateFunctions.isSelected() ||
                    functionSheetIndex.get() == 1 || firstFunctionSheet)) {
                walker.setStringValue(xLabel);
                walker.next();
                walker.setStringValue(resultLabel);
                walker.nextRow();
            }

            if (functionSheetIndex.get() == 1) {
                functionSheetIndex.getAndIncrement();
            }
            firstFunctionSheet = false;

            // Exports calculated data
            for (int i = 0; i < result.length; i++) {
                if (exportFunctionDefinitionValue) {
                    walker.next();
                }

                walker.setFloatValue(result[i++]);
                walker.next();
                walker.setFloatValue(result[i]);
                walker.nextRow();
            }
        }

        // Saves file
        writer.save(Files.newOutputStream(Path.of(filename.getText())));
    }

    private void exportXlsx(Map<Function, Future<double[]>> results) throws ExecutionException, InterruptedException,
            IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook(50);

        // Initial sheet
        String sheetName;
        AtomicInteger functionSheetIndex = new AtomicInteger(1);
        boolean hasExportableConstants = constants != null &&
                constants.parallelStream().anyMatch(c -> c.getActualValue() != null && c.getName() != null &&
                        !c.getName().isBlank());
        boolean firstFunctionSheet = false;
        if (exportConstants.isSelected() && complexPropertiesPanel.separateConstants.isSelected() && hasExportableConstants) {
            sheetName = BUNDLE.getString("export.spreadsheet.constantSheetName");
        } else if (complexPropertiesPanel.separateFunctions.isSelected()) {
            sheetName = MessageFormat.format(BUNDLE.getString("export.spreadsheet.functionSheetName"),
                    functionSheetIndex.getAndIncrement());
            firstFunctionSheet = true;
        } else if ((!exportConstants.isSelected() || !hasExportableConstants) &&
                !complexPropertiesPanel.separateFunctions.isSelected()) {
            sheetName = BUNDLE.getString("export.spreadsheet.generalFunctionsSheetName");
            firstFunctionSheet = true;
        } else {
            sheetName = BUNDLE.getString("export.spreadsheet.generalSheetName");
            firstFunctionSheet = true;
        }

        Sheet currentSheet = workbook.createSheet(sheetName);
        int rowNumber = 0;

        // Constants table
        if (exportConstants.isSelected() && hasExportableConstants) {
            if (exportHeaders.isSelected()) {
                Row row = currentSheet.createRow(rowNumber++);

                Cell nameCell = row.createCell(0, CellType.STRING);
                nameCell.setCellValue(BUNDLE.getString("export.spreadsheet.constantName"));

                Cell definitionCell = row.createCell(1, CellType.STRING);
                definitionCell.setCellValue(BUNDLE.getString("export.spreadsheet.constantDefinition"));

                Cell valueCell = row.createCell(2, CellType.STRING);
                valueCell.setCellValue(BUNDLE.getString("export.spreadsheet.constantValue"));
            }

            for (Constant constant : constants) {
                if (constant.getActualValue() != null && constant.getName() != null && !constant.getName().isBlank()) {
                    Row row = currentSheet.createRow(rowNumber++);

                    Cell nameCell = row.createCell(0, CellType.STRING);
                    nameCell.setCellValue(constant.getName());

                    Cell definitionCell = row.createCell(1, CellType.STRING);
                    definitionCell.setCellValue(constant.getDefinition());

                    Cell valueCell = row.createCell(2, CellType.NUMERIC);
                    valueCell.setCellValue(constant.getActualValue());
                }
            }

            rowNumber++;
        }

        // Function sheets
        String functionDefinitionLabel = BUNDLE.getString("export.spreadsheet.functionDefinition");
        String xLabel = BUNDLE.getString("export.spreadsheet.functionX");
        String resultLabel = BUNDLE.getString("export.spreadsheet.functionResult");
        String sheetLabel = BUNDLE.getString("export.spreadsheet.functionSheetName");
        for (Map.Entry<Function, Future<double[]>> functionEntry : results.entrySet()) {
            double[] result = functionEntry.getValue().get();

            // Create new sheet if necessary
            if (((hasExportableConstants || !firstFunctionSheet) ^ (!exportConstants.isSelected() &&
                    firstFunctionSheet))) {
                if (complexPropertiesPanel.separateFunctions.isSelected()) {
                    currentSheet = workbook.createSheet(MessageFormat.format(sheetLabel,
                            functionSheetIndex.getAndIncrement()));
                    rowNumber = 0;
                } else if (complexPropertiesPanel.separateConstants.isSelected() && functionSheetIndex.get() == 1) {
                    currentSheet = workbook.createSheet(BUNDLE
                            .getString("export.spreadsheet.generalFunctionsSheetName"));
                    rowNumber = 0;
                }
            }

            // Add headers
            if (exportHeaders.isSelected() && (complexPropertiesPanel.separateFunctions.isSelected() ||
                    firstFunctionSheet)) {
                Row headerRow = currentSheet.createRow(rowNumber++);
                int currentCell = 0;
                // Add function definition header
                if (exportFunctionDefinition.isSelected() && (complexPropertiesPanel.separateFunctions.isSelected()
                        || functionSheetIndex.get() == 1 || firstFunctionSheet)) {
                    Cell definitionHeaderCell = headerRow.createCell(currentCell++, CellType.STRING);
                    definitionHeaderCell.setCellValue(functionDefinitionLabel);
                }
                if (complexPropertiesPanel.separateFunctions.isSelected() ||
                        functionSheetIndex.get() == 1 || firstFunctionSheet) {
                    Cell xHeaderCell = headerRow.createCell(currentCell++, CellType.STRING);
                    xHeaderCell.setCellValue(xLabel);

                    Cell resultHeaderCell = headerRow.createCell(currentCell, CellType.STRING);
                    resultHeaderCell.setCellValue(resultLabel);
                }
            }

            if (functionSheetIndex.get() == 1) {
                functionSheetIndex.getAndIncrement();
            }
            firstFunctionSheet = false;

            // Exports calculated data
            for (int i = 0; i < result.length; i++) {
                Row row = currentSheet.createRow(rowNumber++);
                int cellNumber = 0;
                if (exportFunctionDefinition.isSelected()) {
                    if (i == 0) {
                        Cell definitionCell = row.createCell(0, CellType.STRING);
                        definitionCell.setCellValue(functionEntry.getKey().getDefinition().trim());
                        currentSheet.addMergedRegion(new CellRangeAddress(rowNumber - 1,
                                rowNumber + result.length / 2 - 2, 0, 0));
                    }

                    cellNumber++;
                }

                Cell xCell = row.createCell(cellNumber++, CellType.NUMERIC);
                xCell.setCellValue(result[i++]);

                Cell resultCell = row.createCell(cellNumber, CellType.NUMERIC);
                resultCell.setCellValue(result[i]);
            }
        }

        try (OutputStream outputStream = Files.newOutputStream(Path.of(filename.getText()))) {
            workbook.write(outputStream);
        } finally {
            workbook.dispose();
        }
    }

    private void initContentPane() {
        JPanel contentPane = new JPanel(new MigLayout("insets 15", "[]15", "[]10"));
        setContentPane(contentPane);

        filename = new JTextField(lastFilename);
        addFilenameField(contentPane, filename, EXTENSION);

        exportConstants = new JCheckBox(BUNDLE.getString("export.spreadsheet.exportConstants"));
        add(exportConstants, "span");
        exportConstants.setSelected(true);

        exportFunctionDefinition = new JCheckBox(BUNDLE.getString("export.spreadsheet.exportFunctionDefinition"));
        add(exportFunctionDefinition, "span");
        exportFunctionDefinition.setSelected(true);

        exportHeaders = new JCheckBox(BUNDLE.getString("export.spreadsheet.exportHeaders"));
        add(exportHeaders, "span");
        exportHeaders.setSelected(true);

        CardLayout cardLayout = new CardLayout();
        JPanel propertiesPanel = new JPanel(cardLayout);
        contentPane.add(propertiesPanel, "span");

        JPanel invalidTypePanel = new JPanel();
        propertiesPanel.add(invalidTypePanel, TYPE_INVALID);
        invalidTypePanel.add(new JLabel(BUNDLE.getString("export.spreadsheet.invalidType")));

        csvPropertiesPanel = new CSVPropertiesPanel();
        propertiesPanel.add(csvPropertiesPanel, TYPE_CSV);
        csvPropertiesPanel.init();

        complexPropertiesPanel = new ComplexPropertiesPanel();
        propertiesPanel.add(complexPropertiesPanel, TYPE_COMPLEX);
        complexPropertiesPanel.init();

        updatePropertiesPanel(cardLayout, propertiesPanel);

        filename.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePropertiesPanel(cardLayout, propertiesPanel);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePropertiesPanel(cardLayout, propertiesPanel);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePropertiesPanel(cardLayout, propertiesPanel);
            }
        });

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

    private void updatePropertiesPanel(CardLayout cardLayout, JPanel panel) {
        selectedType = switch (EXTENSION.getFileType(filename.getText()).toLowerCase()) {
            case EXTENSION_CSV -> TYPE_CSV;
            case EXTENSION_XLSX, EXTENSION_ODS -> TYPE_COMPLEX;
            default -> TYPE_INVALID;
        };
        cardLayout.show(panel, selectedType);
    }

    private static class CSVPropertiesPanel extends JPanel {
        public JRadioButton commaSeparator;
        public JRadioButton semicolonSeparator;
        public JRadioButton tabSeparator;
        public JRadioButton spaceSeparator;
        public JRadioButton unixSeparator;
        public JRadioButton windowsSeparator;
        public JRadioButton macosSeparator;

        public CSVPropertiesPanel() {
            super(new MigLayout("insets 0", "[]15", "[]10"));
        }

        private void init() {
            JPanel cellSeparatorPanel = new JPanel(new MigLayout("insets 15", "[]15", "[]10"));
            add(cellSeparatorPanel, "grow");
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
            add(lineSeparatorPanel, "span, grow");
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
        }
    }

    private static class ComplexPropertiesPanel extends JPanel {
        public JCheckBox separateConstants;
        public JCheckBox separateFunctions;

        public ComplexPropertiesPanel() {
            super(new MigLayout("insets 0", "[]15", "[]10"));
        }

        public void init() {
            separateConstants = new JCheckBox(BUNDLE.getString("export.spreadsheet.separateConstants"));
            add(separateConstants, "span");
            separateConstants.setToolTipText(BUNDLE.getString("export.spreadsheet.separateConstants.tooltip"));
            separateConstants.setSelected(true);
            separateConstants.addActionListener(e -> {
                if (!separateConstants.isSelected()) {
                    separateFunctions.setSelected(false);
                    separateFunctions.setEnabled(false);
                } else {
                    separateFunctions.setEnabled(true);
                }
            });

            separateFunctions = new JCheckBox(BUNDLE.getString("export.spreadsheet.separateFunctions"));
            add(separateFunctions, "span");
            separateFunctions.setToolTipText(BUNDLE.getString("export.spreadsheet.separateFunctions.tooltip"));
            separateFunctions.setSelected(true);
        }
    }
}
