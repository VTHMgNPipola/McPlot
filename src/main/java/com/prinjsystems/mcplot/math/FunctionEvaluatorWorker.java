package com.prinjsystems.mcplot.math;

import com.prinjsystems.mcplot.gui.PlottingPanel;
import com.prinjsystems.mcplot.gui.PlottingSettings;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class FunctionEvaluatorWorker extends Thread {
    private final FunctionEvaluatorWorkerPool owner;
    private final PlottableFunction function;

    public FunctionEvaluatorWorker(FunctionEvaluatorWorkerPool owner, PlottableFunction function) {
        this.owner = owner;
        this.function = function;
    }

    /**
     * Will plot the function set to this worker and store it into the owner's "plot map", that will be a reference to
     * {@link PlottingPanel}'s map.
     */
    @Override
    public void run() {
        PlottingPanel instance = PlottingPanel.getInstance();
        try {
            owner.putPlot(function, FunctionEvaluator.plotRange(function, instance.getRangeStart(), instance.getRangeEnd(),
                    PlottingSettings.getStep()));
        } catch (Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                    BUNDLE.getString("generics.errorDialog").replace("{0}", e.getMessage())));
        }
        if (--owner.workingThreads == 0) {
            owner.finish();
        }
    }
}
