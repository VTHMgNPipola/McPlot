package com.prinjsystems.mcplot;

import com.prinjsystems.mcplot.gui.PlottingPanel;
import com.prinjsystems.mcplot.gui.Workspace;
import com.prinjsystems.mcplot.gui.WorkspaceSettings;
import com.prinjsystems.mcplot.math.FunctionEvaluatorWorkerPool;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
    public static final ResourceBundle BUNDLE = ResourceBundle.getBundle("mcplot",
            Locale.forLanguageTag(WorkspaceSettings.getLanguage()));

    public static final String PREFERENCES_PATH = "com.prinjsystems.mcplot";

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(WorkspaceSettings.getLookAndFeel());
        } catch (IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Create function evaluator pool
        FunctionEvaluatorWorkerPool pool = new FunctionEvaluatorWorkerPool();

        // Startup Swing
        SwingUtilities.invokeLater(() -> {
            Workspace workspace = Workspace.getInstance();
            workspace.setVisible(true);
            workspace.configure(args);
            PlottingPanel.getInstance().subscribeEvaluatorPool(pool);
        });
    }
}
