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
    private final List<FunctionEvaluatorWorker> workers;
    private Flow.Subscription subscription;

    public FunctionEvaluatorWorkerPool() {
        workers = new ArrayList<>();
    }

    /**
     * When the last worker finishes working it will call this method, that repaints {@link PlottingPanel} and request
     * another plotting job.
     */
    void finish() {
        PlottingPanel.getInstance().repaint();
        subscription.request(1);
    }

    /**
     * Put a function and it's plot into a map, that is a reference to {@link PlottingPanel} own map.
     *
     * @param function Function that was plotted.
     * @param path     2D plot of that function.
     */
    void putPlot(PlottableFunction function, Path2D path) {
        functions.put(function, path);
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    /**
     * Every time this pool receives a plotting job it will start a {@link FunctionEvaluatorWorker} for each function to
     * be plotted, each one having its own thread, and then wait until the last one to finish calls {@link #finish()}.
     *
     * @param item Reference to {@link PlottingPanel} function map.
     */
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
