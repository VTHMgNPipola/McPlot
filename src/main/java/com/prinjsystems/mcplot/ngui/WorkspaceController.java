package com.prinjsystems.mcplot.ngui;

import java.awt.event.ActionEvent;

public class WorkspaceController {
    private final PlottingPanel plottingPanel;

    public WorkspaceController(PlottingPanel plottingPanel) {
        this.plottingPanel = plottingPanel;
    }

    public void save(ActionEvent event) {
    }

    public void open(ActionEvent event) {
    }

    public void openSettings(ActionEvent event) {
        SettingsFrame settingsFrame = new SettingsFrame(plottingPanel);
        settingsFrame.init();
        settingsFrame.setVisible(true);
    }

    public void openAbout(ActionEvent event) {
        AboutFrame aboutFrame = new AboutFrame();
        aboutFrame.init();
        aboutFrame.setVisible(true);
    }
}
