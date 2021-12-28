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

import com.vthmgnpipola.mcplot.ngui.components.GeneralSettingsPanel;
import com.vthmgnpipola.mcplot.ngui.components.PlottingPanelSettingsPanel;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class SettingsDialog extends JDialog {
    private final PlottingPanel plottingPanel;
    private final Workspace workspace;

    public SettingsDialog(PlottingPanel plottingPanel, Workspace workspace) {
        super(SwingUtilities.getWindowAncestor(workspace), BUNDLE.getString("settings.title"),
                ModalityType.APPLICATION_MODAL);
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.plottingPanel = plottingPanel;
        this.workspace = workspace;
    }

    public void init() {
        initContentPane();
        pack();
        setLocationRelativeTo(SwingUtilities.getWindowAncestor(plottingPanel));
    }

    private void initContentPane() {
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        getContentPane().add(tabbedPane);

        PlottingPanelSettingsPanel plottingPanelSettingsPanel = new PlottingPanelSettingsPanel(plottingPanel);
        tabbedPane.addTab(BUNDLE.getString("settings.plottingPanel.title"),
                new JScrollPane(plottingPanelSettingsPanel));

        GeneralSettingsPanel generalSettingsPanel = new GeneralSettingsPanel(workspace);
        tabbedPane.add(BUNDLE.getString("settings.general.title"), new JScrollPane(generalSettingsPanel));
    }
}
