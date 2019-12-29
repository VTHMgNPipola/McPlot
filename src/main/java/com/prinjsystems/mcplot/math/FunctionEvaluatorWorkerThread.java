package com.prinjsystems.mcplot.math;

import com.prinjsystems.mcplot.gui.PlottingPanel;
import com.prinjsystems.mcplot.gui.PlottingSettings;

public class FunctionEvaluatorWorkerThread extends Thread {
    private FunctionEvaluatorWorkerPool owner;
    private PlottableFunction function;

    public FunctionEvaluatorWorkerThread(FunctionEvaluatorWorkerPool owner, PlottableFunction function) {
        this.owner = owner;
        this.function = function;
    }

    @Override
    public void run() {
        PlottingPanel instance = PlottingPanel.getInstance();
        System.out.println("Plotting function...");
        owner.putPlot(function, FunctionEvaluator.plotRange(function, instance.getRangeStart(), instance.getRangeEnd(),
                PlottingSettings.getStep()));
        if (--owner.workingThreads == 0) {
            owner.finish();
        }
    }
}
