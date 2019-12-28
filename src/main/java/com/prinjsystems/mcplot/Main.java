package com.prinjsystems.mcplot;

import com.prinjsystems.mcplot.gui.Workspace;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;

public class Main {
    public static final ResourceBundle BUNDLE = ResourceBundle.getBundle("mcplot");

    public static void main(String[] args) {
        // Startup Swing
        SwingUtilities.invokeLater(() -> {
            Workspace workspace = new Workspace();
            workspace.setVisible(true);
            workspace.configure();
        });
    }
}
