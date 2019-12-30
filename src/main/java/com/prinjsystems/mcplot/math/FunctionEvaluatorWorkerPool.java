package com.prinjsystems.mcplot.math;

import com.prinjsystems.mcplot.gui.PlottingPanel;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class FunctionEvaluatorWorkerPool implements Flow.Subscriber<Map<PlottableFunction, Path2D>> {
    int workingThreads;
    private Map<PlottableFunction, Path2D> functions;
    private List<FunctionEvaluatorWorker> workers;
    private Flow.Subscription subscription;

    public FunctionEvaluatorWorkerPool() {
        workers = new ArrayList<>();
    }

    void finish() {
        PlottingPanel.getInstance().repaint();
        subscription.request(1);
    }

    void putPlot(PlottableFunction function, Path2D path) {
        functions.put(function, path);
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(Map<PlottableFunction, Path2D> item) {
        functions = item;

        workers.clear();
        for (PlottableFunction plottableFunction : item.keySet()) {
            workers.add(new FunctionEvaluatorWorker(this, plottableFunction));
        }

        workingThreads = workers.size();
        if (workingThreads > 0) {
            for (FunctionEvaluatorWorker worker : workers) {
                worker.start();
            }
        } else {
            finish();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        System.err.println(BUNDLE.getString("errors.plottingError"));
    }

    @Override
    public void onComplete() {
        subscription = null;
    }
}
