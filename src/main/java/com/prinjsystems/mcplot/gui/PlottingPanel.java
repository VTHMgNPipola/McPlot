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
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SubmissionPublisher;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class PlottingPanel extends JPanel {
    private static final PlottingPanel INSTANCE;
    private static final double[] BEST_LOW_SCALE_STEPS = new double[]{0.5, 0.25, 0.1, 0.05, 0.01};
    private static final double[] BEST_HIGH_SCALE_STEPS = new double[]{2, 5, 10, 25, 50, 100, 250, 500, 1000};
    private static final Font SCALE_FONT;
    private static final FontMetrics SCALE_FONT_METRICS;
    private static final Color TEXT_BLUE_COLOR = new Color(0, 80, 255);

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

        publisher = new SubmissionPublisher<>();

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem settings = new JMenuItem(BUNDLE.getString("generics.settings"));
        settings.addActionListener(e ->
                PlottingSettings.showPlottingSettingsDialog((JFrame) SwingUtilities.getWindowAncestor(this)));
        popupMenu.add(settings);

        setComponentPopupMenu(popupMenu);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocus();
            }
        });
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
                boolean updatedRanges = false;
                double newRangeStart = -((double) getWidth() / 2);
                double newRangeEnd = (double) getWidth() / 2;
                double newRangeStartY = -((double) getHeight() / 2);
                double newRangeEndY = (double) getHeight() / 2;
                if (newRangeStart < oldRangeStart || newRangeEnd > oldRangeEnd) {
                    oldRangeStart = newRangeStart;
                    oldRangeEnd = newRangeEnd;
                    updatedRanges = true;
                }
                if (newRangeStartY < rangeStartY || newRangeEndY > rangeEndY) {
                    rangeStartY = newRangeStartY;
                    rangeEndY = newRangeEndY;
                    updatedRanges = true;
                }

                if (updatedRanges) {
                    plot();
                }
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");

        // Numpad control keys
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD4, 0),
                "left");
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD6, 0),
                "right");
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD8, 0),
                "up");
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2, 0),
                "down");
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD7, 0),
                "left-up");
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD9, 0),
                "right-up");
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD3, 0),
                "right-down");
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1, 0),
                "left-down");
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD5, 0), "center");
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0),
                "zoom-in");
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0),
                "zoom-out");

        double movementSpeed = 50 * getZoom();
        getActionMap().put("left", new AbstractAction() {
            private static final long serialVersionUID = 4629247154788996726L;

            @Override
            public void actionPerformed(ActionEvent e) {
                cameraX += movementSpeed;
                rangeStart -= movementSpeed;
                rangeEnd -= movementSpeed;
                updateRange();
            }
        });
        getActionMap().put("right", new AbstractAction() {
            private static final long serialVersionUID = -6519412175245515374L;

            @Override
            public void actionPerformed(ActionEvent e) {
                cameraX -= movementSpeed;
                rangeStart += movementSpeed;
                rangeEnd += movementSpeed;
                updateRange();
            }
        });
        getActionMap().put("up", new AbstractAction() {
            private static final long serialVersionUID = 7493982171959945656L;

            @Override
            public void actionPerformed(ActionEvent e) {
                cameraY += movementSpeed;
                rangeStartY -= movementSpeed;
                rangeEndY -= movementSpeed;
                repaint();
            }
        });
        getActionMap().put("down", new AbstractAction() {
            private static final long serialVersionUID = 6417945762098090572L;

            @Override
            public void actionPerformed(ActionEvent e) {
                cameraY -= movementSpeed;
                rangeStartY += movementSpeed;
                rangeEndY += movementSpeed;
                repaint();
            }
        });
        getActionMap().put("left-up", new AbstractAction() {
            private static final long serialVersionUID = -5654333907101214364L;

            @Override
            public void actionPerformed(ActionEvent e) {
                getActionMap().get("left").actionPerformed(e);
                getActionMap().get("up").actionPerformed(e);
            }
        });
        getActionMap().put("right-up", new AbstractAction() {
            private static final long serialVersionUID = -4788514885885809789L;

            @Override
            public void actionPerformed(ActionEvent e) {
                getActionMap().get("right").actionPerformed(e);
                getActionMap().get("up").actionPerformed(e);
            }
        });
        getActionMap().put("right-down", new AbstractAction() {
            private static final long serialVersionUID = 3479461640899981136L;

            @Override
            public void actionPerformed(ActionEvent e) {
                getActionMap().get("right").actionPerformed(e);
                getActionMap().get("down").actionPerformed(e);
            }
        });
        getActionMap().put("left-down", new AbstractAction() {
            private static final long serialVersionUID = -3591950614579752641L;

            @Override
            public void actionPerformed(ActionEvent e) {
                getActionMap().get("left").actionPerformed(e);
                getActionMap().get("down").actionPerformed(e);
            }
        });
        getActionMap().put("center", new AbstractAction() {
            private static final long serialVersionUID = -3591950614579752641L;

            @Override
            public void actionPerformed(ActionEvent e) {
                cameraX = 0;
                cameraY = 0;
                rangeStart = -(getWidth() / 2);
                rangeEnd = getWidth() / 2;
                rangeStartY = -(getHeight() / 2);
                rangeEndY = getHeight() / 2;
                updateRange();
            }
        });
        getActionMap().put("zoom-in", new AbstractAction() {
            private static final long serialVersionUID = -3663952628496064947L;

            @Override
            public void actionPerformed(ActionEvent e) {
                setZoom(getZoom() * 2);
            }
        });
        getActionMap().put("zoom-out", new AbstractAction() {
            private static final long serialVersionUID = 8198157863526451469L;

            @Override
            public void actionPerformed(ActionEvent e) {
                setZoom(getZoom() / 2);
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
        g.drawLine((int) (oldRangeStart * getZoom()), 0, (int) (oldRangeEnd * getZoom()), 0);
        g.setFont(SCALE_FONT);
        // TODO: Find a way to accurately draw the steps
//        double scaleStep = findBestScaleStep();
//        for (double i = scaleStep * Math.round(oldRangeStart / scaleStep);
//             i < scaleStep * Math.round(oldRangeEnd / scaleStep); i += scaleStep) {
//            g.drawLine((int) i, -5, (int) i, 5);
//            g.drawString(i + "", (int) i - (SCALE_FONT_METRICS.stringWidth(i + "") / 2), -7);
//        }

        g.scale(1, -1);

        g.setStroke(traceStroke);
        for (Map.Entry<PlottableFunction, Path2D> pf : functions.entrySet()) {
            if (pf.getKey().isVisible() && pf.getValue() != null) {
                g.setColor(pf.getKey().getTraceColor());
                g.draw(zoomTx.createTransformedShape(pf.getValue()));
            }
        }

        g.scale(1, -1);
        g.translate(-((float) getWidth() / 2 + cameraX), -((float) getHeight() / 2 + cameraY));
        g.setStroke(baseStroke);
        g.setColor(TEXT_BLUE_COLOR);
        // Have to use ternary operator so it doesn't show negative 0 when the camera is at the origin
        String cameraPositionString = "X: " + (cameraX == 0 ? 0 : -cameraX) + "  Y: " + cameraY;
        int cpsWidth = SCALE_FONT_METRICS.stringWidth(cameraPositionString);
        g.drawString(cameraPositionString, getWidth() - cpsWidth - 3, getHeight() - 3);
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

    public void resetRanges() {
        oldRangeStart = rangeStart = -((double) getWidth() / 2);
        oldRangeEnd = rangeEnd = (double) getWidth() / 2;
        rangeStartY = -((double) getHeight() / 2);
        rangeEndY = (double) getHeight() / 2;
    }

    private void updateRange() {
        if (rangeStart < oldRangeStart) {
            oldRangeStart = rangeStart;
            plot();
        } else if (rangeEnd > oldRangeEnd) {
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
                bestStep = getBestLowScaleStep(i);
            }
        } else if (zoom < 4) {
            for (int i = 0; bestStep * zoom < 4; i++) {
                bestStep = getBestHighScaleStep(i);
            }
        }
        return bestStep;
    }

    private double getBestLowScaleStep(int index) {
        if (index >= BEST_LOW_SCALE_STEPS.length) {
            return BEST_LOW_SCALE_STEPS[BEST_LOW_SCALE_STEPS.length - 1] * ((index - BEST_LOW_SCALE_STEPS.length + 1) / 10);
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
}
