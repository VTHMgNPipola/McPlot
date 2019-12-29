package com.prinjsystems.mcplot.gui;

import com.prinjsystems.mcplot.math.FunctionEvaluatorWorkerPool;
import com.prinjsystems.mcplot.math.PlottableFunction;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SubmissionPublisher;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class PlottingPanel extends JPanel {
    private static final PlottingPanel INSTANCE;
    private static final double[] BEST_LOW_SCALE_STEPS = new double[]{0.5, 0.25, 0.1, 0.05, 0.01};
    private static final double[] BEST_HIGH_SCALE_STEPS = new double[]{2, 5, 10, 25, 50, 100, 250, 500, 1000};
    private static final Font SCALE_FONT;
    private static final FontMetrics SCALE_FONT_METRICS;

    static {
        INSTANCE = new PlottingPanel();
        SCALE_FONT = new Font("Monospaced", Font.PLAIN, 11);
        SCALE_FONT_METRICS = new JPanel().getFontMetrics(SCALE_FONT);
    }

    private Stroke baseStroke, traceStroke;
    private Map<PlottableFunction, Path2D> functions;
    private AffineTransform zoomTx;
    private double cameraX, cameraY;
    private double oldRangeStart, oldRangeEnd, rangeStart, rangeEnd;
    private double rangeStartY, rangeEndY;

    private SubmissionPublisher<Map<PlottableFunction, Path2D>> publisher;

    private PlottingPanel() {
        baseStroke = new BasicStroke(1);
        traceStroke = new BasicStroke(2);

        functions = new HashMap<>();

        zoomTx = AffineTransform.getScaleInstance(1, 1);
        cameraX = 0;
        cameraY = 0;
        oldRangeStart = rangeStart = -((double) getWidth() / 2);
        oldRangeEnd = rangeEnd = (double) getWidth() / 2;
        rangeStartY = -((double) getHeight() / 2);
        rangeEndY = (double) getHeight() / 2;

        publisher = new SubmissionPublisher<>();

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem settings = new JMenuItem(BUNDLE.getString("plotting.settings.title"));
        settings.addActionListener(event ->
                PlottingSettings.showPlottingSettingsDialog((JFrame) SwingUtilities.getWindowAncestor(this)));
        popupMenu.add(settings);

        setComponentPopupMenu(popupMenu);

        addMouseWheelListener(mouseWheelEvent -> {
            double preciseRotation = mouseWheelEvent.getPreciseWheelRotation();
            if (preciseRotation < 0) {
                preciseRotation = 1 / (-preciseRotation * 4);
            }
            double newZoom = getZoom() / (2 * preciseRotation);
            if (newZoom > 0) {
                setZoom(newZoom);
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (-((double) getWidth() / 2) < oldRangeStart || (double) getWidth() / 2 > oldRangeEnd) {
                    oldRangeStart = -((double) getWidth() / 2);
                    oldRangeEnd = (double) getWidth() / 2;
                    plot();
                }
            }
        });
    }

    public static PlottingPanel getInstance() {
        return INSTANCE;
    }

    public void setFunctions(Map<PlottableFunction, Path2D> functions) {
        this.functions = functions;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(250, 250, 250));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.translate((float) getWidth() / 2 + cameraX, (float) getHeight() / 2 + cameraY);

        g.setColor(Color.BLUE);
        g.setStroke(baseStroke);
        g.drawLine(0, (int) rangeStartY, 0, (int) rangeEndY);
        g.drawLine((int) oldRangeStart, 0, (int) oldRangeEnd, 0);
        g.setFont(SCALE_FONT);
        double scaleStep = findBestScaleStep();
        for (double i = scaleStep * Math.round(oldRangeStart / scaleStep);
             i < scaleStep * Math.round(oldRangeEnd / scaleStep); i += scaleStep) {
            g.drawLine((int) i, -5, (int) i, 5);
            g.drawString(i + "", (int) i - (SCALE_FONT_METRICS.stringWidth(i + "") / 2), -7);
        }

        g.scale(1, -1);

        g.setStroke(traceStroke);
        for (Map.Entry<PlottableFunction, Path2D> pf : functions.entrySet()) {
            if (pf.getKey().isVisible() && pf.getValue() != null) {
                g.setColor(pf.getKey().getTraceColor());
                g.draw(zoomTx.createTransformedShape(pf.getValue()));
            }
        }
    }

    public void plot() {
        publisher.submit(functions);
    }

    public void subscribeEvaluatorPool(FunctionEvaluatorWorkerPool pool) {
        if (publisher.getNumberOfSubscribers() == 0) {
            publisher.subscribe(pool);
        } else {
            throw new IllegalStateException(BUNDLE.getString("errors.invalidEvaluatorPool"));
        }
    }

    public double getZoom() {
        return zoomTx.getScaleX();
    }

    public void setZoom(double zoom) {
        rangeStart = -((double) getWidth() / 2) * (1 / Math.abs(zoomTx.getScaleX() - zoom));
        rangeEnd = ((double) getWidth() / 2) * (1 / Math.abs(zoomTx.getScaleX() - zoom));

        zoomTx.setToScale(zoom, zoom);

        updateRange();
    }

    public double getRangeStart() {
        return oldRangeStart;
    }

    public double getRangeEnd() {
        return oldRangeEnd;
    }

    private void updateRange() {
        if (rangeStart < oldRangeStart) {
            oldRangeStart = rangeStart;
            oldRangeEnd = rangeEnd;
            plot();
        } else {
            repaint();
        }
    }

    private double findBestScaleStep() {
        double bestStep = 1;
        double zoom = zoomTx.getScaleX();
        if (zoom > 32) {
            for (int i = 0; bestStep * zoom > 32; i++) {
                bestStep = getBestHighScaleStep(i);
            }
        } else if (zoom < 4) {
            for (int i = 0; bestStep * zoom < 4; i++) {
                bestStep = getBestLowScaleStep(i);
            }
        }
        return bestStep;
    }

    private double getBestLowScaleStep(int index) {
        if (index >= BEST_LOW_SCALE_STEPS.length) {
            return BEST_LOW_SCALE_STEPS[BEST_LOW_SCALE_STEPS.length - 1] * (10 * (index - BEST_LOW_SCALE_STEPS.length + 1));
        } else {
            return BEST_LOW_SCALE_STEPS[index];
        }
    }

    private double getBestHighScaleStep(int index) {
        if (index >= BEST_HIGH_SCALE_STEPS.length) {
            return BEST_HIGH_SCALE_STEPS[BEST_HIGH_SCALE_STEPS.length - 1]
                    * (10 * (index - BEST_HIGH_SCALE_STEPS.length + 1));
        } else {
            return BEST_HIGH_SCALE_STEPS[index];
        }
    }

    public static class PlottingPanelKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent keyEvent) {
            PlottingPanel instance = getInstance();
            if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
                instance.cameraX += 10;
                instance.rangeStart -= 10;
                instance.rangeEnd -= 10;
                instance.updateRange();
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
                instance.cameraY -= 10;
                if (instance.cameraY < instance.rangeStartY) {
                    instance.rangeStartY = instance.cameraY;
                }
                instance.repaint();
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
                instance.cameraX -= 10;
                instance.rangeStart -= 10;
                instance.rangeEnd -= 10;
                instance.updateRange();
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
                instance.cameraY += 10;
                if (instance.cameraY > instance.rangeEndY) {
                    instance.rangeEndY = instance.cameraY;
                }
                instance.repaint();
            }
        }
    }
}
