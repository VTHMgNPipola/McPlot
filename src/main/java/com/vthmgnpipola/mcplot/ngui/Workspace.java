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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.MessageFormat;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;
import static com.vthmgnpipola.mcplot.Main.VERSION;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_OPEN_MAXIMIZED;
import static com.vthmgnpipola.mcplot.PreferencesHelper.PREFERENCES;

public class Workspace extends JFrame {
    private MathPanel mathPanel;
    private PlottingPanel plottingPanel;
    private JSplitPane splitPane;

    public Workspace() {
        super(MessageFormat.format(BUNDLE.getString("workspace.title"), VERSION));
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

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

        JMenuItem save = new JMenuItem(BUNDLE.getString("workspace.menu.file.save"));
        file.add(save);
        save.addActionListener(e -> mathPanel.save());

        JMenuItem open = new JMenuItem(BUNDLE.getString("workspace.menu.file.open"));
        file.add(open);
        open.addActionListener(e -> mathPanel.open(plottingPanel));

        file.addSeparator();

        // Export submenu
        JMenu export = new JMenu(BUNDLE.getString("workspace.menu.file.export"));
        file.add(export);

//        JMenuItem exportSpreadsheet = new JMenuItem(BUNDLE.getString("workspace.menu.file.export.spreadsheet"));
//        export.add(exportSpreadsheet);

        JMenuItem exportText = new JMenuItem(BUNDLE.getString("workspace.menu.file.export.text"));
        export.add(exportText);
        exportText.addActionListener(e -> {
            ExportTextFileFrame exportTextFileFrame = new ExportTextFileFrame(
                    mathPanel.getEventStreamer().getFunctionMap(), mathPanel.getEventStreamer().getConstants(),
                    mathPanel.getEventStreamer().getConstantValues(),
                    plottingPanel);
            exportTextFileFrame.init();
            exportTextFileFrame.setVisible(true);
        });

//        JMenuItem exportPgfplots = new JMenuItem(BUNDLE.getString("workspace.menu.file.export.pgfplots"));
//        export.add(exportPgfplots);

//        JMenuItem exportPicture = new JMenuItem(BUNDLE.getString("workspace.menu.file.export.picture"));
//        export.add(exportPicture);

        file.addSeparator();

        JMenuItem settings = new JMenuItem(BUNDLE.getString("generics.settings"));
        file.add(settings);
        settings.addActionListener(e -> {
            SettingsFrame settingsFrame = new SettingsFrame(plottingPanel);
            settingsFrame.init();
            settingsFrame.setVisible(true);
        });

        file.addSeparator();

        JMenuItem about = new JMenuItem(BUNDLE.getString("workspace.menu.file.about"));
        file.add(about);
        about.addActionListener(e -> {
            AboutFrame aboutFrame = new AboutFrame();
            aboutFrame.init();
            aboutFrame.setVisible(true);
        });

        file.addSeparator();

        JMenuItem exit = new JMenuItem(BUNDLE.getString("workspace.menu.file.exit"));
        file.add(exit);
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
}
