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

import com.formdev.flatlaf.icons.FlatFileViewFileIcon;
import com.formdev.flatlaf.icons.FlatFileViewFloppyDriveIcon;
import com.formdev.flatlaf.icons.FlatTabbedPaneCloseIcon;
import com.formdev.flatlaf.icons.FlatTreeOpenIcon;
import com.vthmgnpipola.mcplot.ngui.icons.FlatAddFileIcon;
import com.vthmgnpipola.mcplot.ngui.icons.FlatHelpIcon;
import com.vthmgnpipola.mcplot.ngui.icons.FlatPictureIcon;
import com.vthmgnpipola.mcplot.ngui.icons.FlatSettingsIcon;
import com.vthmgnpipola.mcplot.ngui.icons.FlatTextFileIcon;
import com.vthmgnpipola.mcplot.nmath.MathEventStreamer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.MessageFormat;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;
import static com.vthmgnpipola.mcplot.Main.VERSION;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_OPEN_MAXIMIZED;
import static com.vthmgnpipola.mcplot.PreferencesHelper.PREFERENCES;

public class Workspace extends MFrame {
    private MathPanel mathPanel;
    private PlottingPanel plottingPanel;
    private JSplitPane splitPane;

    public Workspace() {
        super(MessageFormat.format(BUNDLE.getString("workspace.title"), VERSION));
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void init() {
        initContentPane();
        initMenu();
        pack();
        setLocationRelativeTo(null);

        splitPane.setDividerLocation(0.3);
        plottingPanel.init();

        if (PREFERENCES.getBoolean(KEY_OPEN_MAXIMIZED, false)) {
            setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
        }
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu file = new JMenu(BUNDLE.getString("workspace.menu.file"));
        menuBar.add(file);

        JMenuItem newWorkspace = new JMenuItem(BUNDLE.getString("workspace.menu.file.new"), new FlatAddFileIcon());
        file.add(newWorkspace);
        newWorkspace.setAccelerator(KeyStroke.getKeyStroke("control shift n"));
        newWorkspace.addActionListener(e -> {
            MathEventStreamer.getInstance().reset();
            this.init();
        });

        JMenuItem save = new JMenuItem(BUNDLE.getString("workspace.menu.file.save"),
                new FlatFileViewFloppyDriveIcon());
        file.add(save);
        save.setAccelerator(KeyStroke.getKeyStroke("control S"));
        save.addActionListener(e -> mathPanel.save());

        JMenuItem saveAs = new JMenuItem(BUNDLE.getString("workspace.menu.file.saveAs"));
        file.add(saveAs);
        saveAs.setAccelerator(KeyStroke.getKeyStroke("control shift S"));
        saveAs.addActionListener(e -> mathPanel.saveAs());

        JMenuItem open = new JMenuItem(BUNDLE.getString("workspace.menu.file.open"),
                new FlatTreeOpenIcon());
        file.add(open);
        open.setAccelerator(KeyStroke.getKeyStroke("control O"));
        open.addActionListener(e -> mathPanel.open(plottingPanel));

        file.addSeparator();

        // Export submenu
        JMenu export = new JMenu(BUNDLE.getString("workspace.menu.file.export"));
        file.add(export);

        JMenuItem exportSpreadsheet = new JMenuItem(BUNDLE.getString("workspace.menu.file.export.spreadsheet"),
                new FlatFileViewFileIcon());
        export.add(exportSpreadsheet);
        exportSpreadsheet.setAccelerator(KeyStroke.getKeyStroke("control P"));
        exportSpreadsheet.addActionListener(e -> {
            ExportSpreadsheetDialog exportSpreadsheetFrame = new ExportSpreadsheetDialog(
                    MathEventStreamer.getInstance().getFunctionMap(), MathEventStreamer.getInstance().getConstants(),
                    MathEventStreamer.getInstance().getConstantValues(),
                    plottingPanel);
            exportSpreadsheetFrame.init();
            exportSpreadsheetFrame.setVisible(true);
        });

        JMenuItem exportText = new JMenuItem(BUNDLE.getString("workspace.menu.file.export.text"),
                new FlatTextFileIcon());
        export.add(exportText);
        exportText.setAccelerator(KeyStroke.getKeyStroke("control T"));
        exportText.addActionListener(e -> {
            ExportTextFileDialog exportTextFileFrame = new ExportTextFileDialog(
                    MathEventStreamer.getInstance().getFunctionMap(), MathEventStreamer.getInstance().getConstants(),
                    MathEventStreamer.getInstance().getConstantValues(),
                    plottingPanel);
            exportTextFileFrame.init();
            exportTextFileFrame.setVisible(true);
        });

//        JMenuItem exportPgfplots = new JMenuItem(BUNDLE.getString("workspace.menu.file.export.pgfplots"));
//        export.add(exportPgfplots);

        JMenuItem exportPicture = new JMenuItem(BUNDLE.getString("workspace.menu.file.export.picture"),
                new FlatPictureIcon());
        export.add(exportPicture);
        exportPicture.setAccelerator(KeyStroke.getKeyStroke("control I"));
        exportPicture.addActionListener(e -> {
            ExportImageFileDialog exportImageFileFrame = new ExportImageFileDialog(
                    MathEventStreamer.getInstance().getFunctionMap(), MathEventStreamer.getInstance().getConstants(),
                    MathEventStreamer.getInstance().getConstantValues(),
                    plottingPanel);
            exportImageFileFrame.init();
            exportImageFileFrame.setVisible(true);
        });

        file.addSeparator();

        JMenuItem settings = new JMenuItem(BUNDLE.getString("generics.settings"), new FlatSettingsIcon());
        file.add(settings);
        settings.setAccelerator(KeyStroke.getKeyStroke("control alt S"));
        settings.addActionListener(e -> {
            SettingsDialog settingsDialog = new SettingsDialog(plottingPanel, this);
            settingsDialog.init();
            settingsDialog.setVisible(true);
        });

        file.addSeparator();

        JMenuItem about = new JMenuItem(BUNDLE.getString("workspace.menu.file.about"), new FlatHelpIcon());
        file.add(about);
        about.setAccelerator(KeyStroke.getKeyStroke("control shift ?"));
        about.addActionListener(e -> {
            AboutDialog aboutDialog = new AboutDialog(this);
            aboutDialog.init();
            aboutDialog.setVisible(true);
        });

        file.addSeparator();

        JMenuItem exit = new JMenuItem(BUNDLE.getString("workspace.menu.file.exit"),
                new FlatTabbedPaneCloseIcon());
        file.add(exit);
        exit.setAccelerator(KeyStroke.getKeyStroke("control Q"));
        exit.addActionListener(e -> System.exit(0));
    }

    private void initContentPane() {
        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        contentPane.setPreferredSize(new Dimension(1024, 576));

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        contentPane.add(splitPane, BorderLayout.CENTER);
        splitPane.setContinuousLayout(false);
        splitPane.setDividerLocation(0.3);

        plottingPanel = new PlottingPanel();
        splitPane.setRightComponent(plottingPanel);

        mathPanel = new MathPanel();
        splitPane.setLeftComponent(mathPanel);
        mathPanel.init(plottingPanel);
    }

    @Override
    public void validate() {
        super.validate();
        plottingPanel.resetColors();
    }
}
