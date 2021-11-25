package com.prinjsystems.mcplot;

import com.formdev.flatlaf.FlatLightLaf;
import com.prinjsystems.mcplot.gui.WorkspaceSettings;
import com.prinjsystems.mcplot.ngui.Workspace;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static final ResourceBundle BUNDLE = ResourceBundle.getBundle("mcplot",
            Locale.forLanguageTag(WorkspaceSettings.getLanguage()));

    public static final String PREFERENCES_PATH = "com.prinjsystems.mcplot";

    public static void main(String[] args) {
        FlatLightLaf.setup();
        JFrame.setDefaultLookAndFeelDecorated(true);

        SwingUtilities.invokeLater(() -> {
            Workspace workspace = new Workspace();
            workspace.init();
            workspace.setVisible(true);
        });
    }
}
