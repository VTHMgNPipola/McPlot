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

package com.prinjsystems.mcplot.ngui;

import com.prinjsystems.mcplot.ngui.components.GeneralSettingsPanel;
import com.prinjsystems.mcplot.ngui.components.PlottingPanelSettingsPanel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class SettingsFrame extends JFrame {
    private final PlottingPanel plottingPanel;

    public SettingsFrame(PlottingPanel plottingPanel) {
        super(BUNDLE.getString("settings.title"));
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.plottingPanel = plottingPanel;
    }

    public void init() {
        initContentPane();
        pack();
        setLocationRelativeTo(null);
    }

    private void initContentPane() {
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        getContentPane().add(tabbedPane);

        PlottingPanelSettingsPanel plottingPanelSettingsPanel = new PlottingPanelSettingsPanel(plottingPanel);
        tabbedPane.addTab(BUNDLE.getString("settings.plottingPanel.title"),
                new JScrollPane(plottingPanelSettingsPanel));

        GeneralSettingsPanel generalSettingsPanel = new GeneralSettingsPanel();
        tabbedPane.add(BUNDLE.getString("settings.general.title"), new JScrollPane(generalSettingsPanel));
    }
}
