package com.prinjsystems.mcplot.ngui;

import java.awt.event.ActionEvent;

public class WorkspaceController {
    public void save(ActionEvent event) {
    }

    public void open(ActionEvent event) {
    }

    public void openSettings(ActionEvent event) {
    }

    public void openAbout(ActionEvent event) {
        AboutFrame aboutFrame = new AboutFrame();
        aboutFrame.init();
        aboutFrame.setVisible(true);
    }
}
