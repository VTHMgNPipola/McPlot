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
