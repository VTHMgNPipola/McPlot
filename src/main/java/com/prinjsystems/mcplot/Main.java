package com.prinjsystems.mcplot;

import com.prinjsystems.mcplot.gui.PlottingPanel;
import com.prinjsystems.mcplot.gui.Workspace;
import com.prinjsystems.mcplot.math.FunctionEvaluatorWorkerPool;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;

public class Main {
    public static final ResourceBundle BUNDLE = ResourceBundle.getBundle("mcplot");

    public static void main(String[] args) {
        // Create function evaluator pool
        FunctionEvaluatorWorkerPool pool = new FunctionEvaluatorWorkerPool();

        // Startup Swing
        SwingUtilities.invokeLater(() -> {
            Workspace workspace = new Workspace();
            workspace.setVisible(true);
            workspace.configure(args);
            PlottingPanel.getInstance().subscribeEvaluatorPool(pool);
        });
    }
}
