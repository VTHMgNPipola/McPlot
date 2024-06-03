/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2024  VTHMgNPipola
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.vthmgnpipola.mcplot.ngui.views;

import com.vthmgnpipola.mcplot.GraphUnit;
import com.vthmgnpipola.mcplot.StringNumberUtils;
import com.vthmgnpipola.mcplot.ngui.controllers.MPropertyChangeEvent;
import com.vthmgnpipola.mcplot.ngui.controllers.TransactionalController;
import com.vthmgnpipola.mcplot.ngui.models.PlottingModel;
import com.vthmgnpipola.mcplot.nmath.ScientificNotationNumber;
import com.vthmgnpipola.mcplot.plot.Plot;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.vthmgnpipola.mcplot.Main.EXECUTOR_THREAD;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_LAF;
import static com.vthmgnpipola.mcplot.PreferencesHelper.PREFERENCES;
import static com.vthmgnpipola.mcplot.PreferencesHelper.VALUE_DARK_LAF;
import static com.vthmgnpipola.mcplot.PreferencesHelper.VALUE_LIGHT_LAF;
import static com.vthmgnpipola.mcplot.ngui.PlottingPanelContext.INITIAL_PIXELS_PER_STEP;

public class PlottingView extends MPanel implements PropertyChangeAware {
    private final int[] zoomArray = new int[]{1, 2, 5};
    private final AffineTransform zoomTx;
    private final List<Plot> plots;
    private final Font rotatedFont; // Rotated in 45 degrees
    private int previousWidth, previousHeight;
    private int flWidth = 0;
    private int flHeight = 0;
    private PlottingModel model;
    private final TransactionalController controller;
    private Font font;
    private Color backgroundColor;
    private Color minorGridColor;
    private Color majorGridColor;
    private Color globalAxisColor;
    private FontMetrics fontMetrics;

    public PlottingView(PlottingModel model, TransactionalController controller) {
        setDoubleBuffered(true);
        plots = Collections.synchronizedList(new ArrayList<>());

        this.model = model;
        this.controller = controller;

        font = new Font("Monospaced", Font.PLAIN, 12);
        AffineTransform deg45Transform = new AffineTransform();
        deg45Transform.rotate(Math.toRadians(45));
        rotatedFont = font.deriveFont(deg45Transform);
        zoomTx = new AffineTransform();
        zoomTx.setToScale(model.getAxisX().scale * model.getPixelsPerStep() * model.getZoom(),
                -model.getAxisY().scale * model.getPixelsPerStep() * model.getZoom());

        final int DRAGGING_PLOTTING_PANEL = 1;
        final int DRAGGING_LEGEND_PANEL = 2;
        final int[] dragging = new int[1]; // 1: dragging plotting panel; 2: dragging function legend panel
        final int[] startPos = new int[2]; // 0: X position; 1: Y position
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPos[0] = e.getXOnScreen();
                startPos[1] = e.getYOnScreen();

                if ((e.getX() >= model.getFlPositionX() && e.getX() <= model.getFlPositionX() + flWidth) &&
                        (e.getY() >= model.getFlPositionY() && e.getY() <= model.getFlPositionY() + flHeight)) {
                    dragging[0] = DRAGGING_LEGEND_PANEL;
                } else if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3) {
                    dragging[0] = DRAGGING_PLOTTING_PANEL;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if ((e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3 && dragging[0] == 1) ||
                        dragging[0] == 2) {
                    dragging[0] = 0;
                }
            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                moveCamera(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                moveCamera(e);
            }

            private void moveCamera(MouseEvent e) {
                controller.startTransaction(model);
                if (dragging[0] == DRAGGING_PLOTTING_PANEL) {
                    int currentMouseX = e.getXOnScreen();
                    int currentMouseY = e.getYOnScreen();

                    model.setCameraX(model.getCameraX() + startPos[0] - currentMouseX);
                    model.setCameraY(model.getCameraY() + startPos[1] - currentMouseY);

                    startPos[0] = currentMouseX;
                    startPos[1] = currentMouseY;
                } else if (dragging[0] == DRAGGING_LEGEND_PANEL) {
                    int currentMouseX = e.getXOnScreen();
                    int currentMouseY = e.getYOnScreen();

                    model.setFlPositionX(model.getFlPositionX() + currentMouseX - startPos[0]);
                    model.setFlPositionY(model.getFlPositionY() + currentMouseY - startPos[1]);

                    startPos[0] = currentMouseX;
                    startPos[1] = currentMouseY;
                }
                controller.endTransaction(model);
            }
        });

        /*
         * This method changes the zoom based on the mouse wheel rotation.
         * Firstly, it increases or decreases the amount of pixels per step on the axis lines by some predefined
         * amount of pixels times the amount of wheel clicks registered. If the amount of pixels per step is smaller
         * or larger than predefined values it sets it back to the original starting value and decreases or increases
         * the zoom "position" (how many times the wheel rotated), respectively.
         * It then checks if the zoom position is a valid address in an array of zoom values I considered good, and
         * if so chooses one of those.
         * If it is a valid address it simply uses the zoom value that corresponds to that address.
         * If it is over the maximum address, it calculates how many times the value "circled" around the list of zoom
         * values and calculates what would be the index if the array of zoom values was circular. It then calculates
         * the zoom by grabbing the zoom value from the array using the index it calculated and multiplying the value
         * by 10 to the power of the times the zoom position circled the array.
         * If it is below the minimum address, it does the same thing but considering the zoom index as a positive
         * value and inverting the final zoom result (by doing 1 over whatever zoom value it got).
         */
        addMouseWheelListener(e -> EXECUTOR_THREAD.submit(() -> {
            // How many pixels are added or subtracted from pixelsPerStep with each wheel rotation
            final int ZOOM_PER_CLICK = 10;

            // Maximum amount of times pixels are added or subtracted from pixelsPerStep before changing the zoom level
            final int MAX_ZOOM = 3;

            int wheelRotation = -e.getWheelRotation();
            controller.startTransaction(model);
            model.setPixelsPerStep(model.getPixelsPerStep() + wheelRotation * ZOOM_PER_CLICK);
            if (model.getPixelsPerStep() < INITIAL_PIXELS_PER_STEP - (ZOOM_PER_CLICK * MAX_ZOOM)) {
                model.setPixelsPerStep(INITIAL_PIXELS_PER_STEP);
                model.setZoomPos(model.getZoomPos() - 1);
            } else if (model.getPixelsPerStep() > INITIAL_PIXELS_PER_STEP + (ZOOM_PER_CLICK * MAX_ZOOM)) {
                model.setPixelsPerStep(INITIAL_PIXELS_PER_STEP);
                model.setZoomPos(model.getZoomPos() + 1);
            }

            if (model.getZoomPos() >= 0 && model.getZoomPos() < zoomArray.length) {
                model.setZoom(zoomArray[model.getZoomPos()]);
            } else if (model.getZoomPos() >= zoomArray.length) {
                int timesCircled = model.getZoomPos() / zoomArray.length;
                int arrayPos = model.getZoomPos() % zoomArray.length;
                model.setZoom(zoomArray[arrayPos] * Math.pow(10, timesCircled));
            } else {
                int timesCircled = -model.getZoomPos() / zoomArray.length;
                int arrayPos = (-model.getZoomPos() + zoomArray.length) % zoomArray.length;
                model.setZoom((double) 1 / (zoomArray[arrayPos] * Math.pow(10, timesCircled)));
            }

            zoomTx.setToScale(model.getAxisX().scale * model.getPixelsPerStep() * model.getZoom(),
                    -model.getAxisY().scale * model.getPixelsPerStep() * model.getZoom());
            controller.endTransaction(model);
        }));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                EXECUTOR_THREAD.submit(() -> {
                    controller.startTransaction(model);
                    model.setCameraX(model.getCameraX() - (e.getComponent().getWidth() - previousWidth) / 2);
                    model.setCameraY(model.getCameraY() - (e.getComponent().getHeight() - previousHeight) / 2);

                    previousWidth = e.getComponent().getWidth();
                    previousHeight = e.getComponent().getHeight();
                    controller.endTransaction(model);
                });
            }
        });
    }

    @Override
    public void init() {
        controller.startTransaction(model);
        model.setCameraX(-getWidth() / 2);
        model.setCameraY(-getHeight() / 2);

        previousWidth = getWidth();
        previousHeight = getHeight();

        resetColors();
        controller.endTransaction(model);
    }

    public PlottingModel getModel() {
        return model;
    }

    public void setModel(PlottingModel model) {
        this.model = model;
        repaint();
    }

    public void resetColors() {
        if (PREFERENCES.get(KEY_LAF, VALUE_LIGHT_LAF).equals(VALUE_DARK_LAF)) {
            backgroundColor = new Color(60, 60, 60);
            minorGridColor = new Color(96, 96, 96);
            majorGridColor = Color.gray;
            globalAxisColor = Color.lightGray;
        } else {
            backgroundColor = Color.white;
            minorGridColor = new Color(-1513240);
            majorGridColor = Color.lightGray;
            globalAxisColor = Color.black;
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        // Initialization
        Graphics2D g = (Graphics2D) graphics;
        g.setFont(font);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        Stroke baseStroke = g.getStroke();

        if (fontMetrics == null) {
            fontMetrics = g.getFontMetrics();
        }

        // Background
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Camera translation
        g.translate(-model.getCameraX(), -model.getCameraY());

        // Grid
        if (model.isDrawGrid()) {
            // Minor grid
            if (model.isDrawMinorGrid()) {
                drawMinorGrid(g);
            }

            // Major grid and steps
            drawMajorGrid(g);
            if (!model.isDrawAxisOverFunc()) {
                drawAxisValues(g);
            }
        }

        // Global axis
        if (!model.isDrawAxisOverFunc()) {
            drawGlobalAxis(g);
        }

        // Plots
        if (model.isAntialias()) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        zoomTx.setToScale(model.getAxisX().scale * model.getPixelsPerStep() * model.getZoom() * model.getAxisX().unit.getScale(),
                -model.getAxisY().scale * model.getPixelsPerStep() * model.getZoom() * model.getAxisY().unit.getScale());

        int visibleFunctions = 0;
        int longestPlot = 0;
        for (Plot plot : plots) {
            if (plot == null || plot.isInvalid() || plot.isInvisible()) {
                continue;
            }

            plot.plot(g, zoomTx, model);

            longestPlot = Math.max(longestPlot, fontMetrics.stringWidth(plot.getLegend().trim()));

            visibleFunctions++;
        }

        // Global axis and steps
        if (model.isDrawAxisOverFunc()) {
            g.setStroke(baseStroke);
            drawGlobalAxis(g);
            drawAxisValues(g);
        }

        // Legends
        if (model.isFunctionLegends() && visibleFunctions != 0) {
            g.translate(model.getCameraX(), model.getCameraY());
            g.setStroke(baseStroke);

            int panelHeight = flHeight = 20 + fontMetrics.getHeight() * visibleFunctions;
            int panelWidth = flWidth = 70 + longestPlot;
            g.setColor(backgroundColor);
            g.fillRect(model.getFlPositionX(), model.getFlPositionY(), panelWidth, panelHeight);
            g.setColor(globalAxisColor);
            g.drawRect(model.getFlPositionX(), model.getFlPositionY(), panelWidth, panelHeight);

            g.translate(model.getFlPositionX(), model.getFlPositionY());
            int i = 0;
            for (Plot plot : plots) {
                if (plot == null || plot.isInvalid() || plot.isInvisible()) {
                    continue;
                }

                g.setStroke(plot.getTrace().getStroke());
                g.setColor(plot.getTrace().getColor());
                int y = fontMetrics.getHeight() * i + fontMetrics.getHeight() / 2 + 10;
                g.drawLine(10, y, 50, y);

                g.setStroke(baseStroke);
                g.setColor(globalAxisColor);
                g.drawString(plot.getLegend().trim(), 60,
                        fontMetrics.getHeight() * i + fontMetrics.getAscent() + 10);
                i++;
            }
        } else {
            flWidth = 0;
            flHeight = 0;
        }
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public void setFont(Font font) {
        this.font = font;
        fontMetrics = null;
        repaint();
    }

    public List<Plot> getPlots() {
        return plots;
    }

    private void drawGlobalAxis(Graphics2D g) {
        g.setColor(globalAxisColor);
        g.drawLine(model.getCameraX(), 0, model.getCameraX() + getWidth(), 0);
        g.drawLine(0, model.getCameraY(), 0, model.getCameraY() + getHeight());
    }

    private void drawMinorGrid(Graphics2D g) {
        // X
        g.setColor(minorGridColor);
        double step = (double) model.getPixelsPerStep() / model.getMinorGridDivisions();

        double initialMinorX = model.getCameraX() -
                (model.getCameraX() % ((double) model.getPixelsPerStep() / model.getMinorGridDivisions()));
        double endMinorX = model.getCameraX() + getWidth();
        for (double i = initialMinorX; i < endMinorX; i += step) {
            g.drawLine((int) i, model.getCameraY(), (int) i, model.getCameraY() + getHeight());
        }

        // Y
        double initialMinorY = model.getCameraY() -
                (model.getCameraY() % ((double) model.getPixelsPerStep() / model.getMinorGridDivisions()));
        double endMinorY = model.getCameraY() + getHeight();
        for (double i = initialMinorY; i < endMinorY; i += step) {
            g.drawLine(model.getCameraX(), (int) i, model.getCameraX() + getWidth(), (int) i);
        }
    }

    private void drawMajorGrid(Graphics2D g) {
        // X
        // Constants
        int initialMajorX = model.getCameraX() - (model.getCameraX() % model.getPixelsPerStep()) - model.getPixelsPerStep();
        int endMajorX = model.getCameraX() + getWidth() + model.getPixelsPerStep();

        // Graphics
        for (int i = initialMajorX; i < endMajorX; i += model.getPixelsPerStep()) {
            g.setColor(majorGridColor);
            g.drawLine(i, model.getCameraY(), i, model.getCameraY() + getHeight());
        }

        // Y
        // Constants
        int initialMajorY = model.getCameraY() - (model.getCameraY() % model.getPixelsPerStep()) - model.getPixelsPerStep();
        int endMajorY = model.getCameraY() + getHeight() + model.getPixelsPerStep();

        // Graphics
        for (int i = initialMajorY; i < endMajorY; i += model.getPixelsPerStep()) {
            g.setColor(majorGridColor);
            g.drawLine(model.getCameraX(), i, model.getCameraX() + getWidth(), i);
        }
    }

    private void drawAxisValues(Graphics2D g) {
        // X
        // Constants
        int initialMajorX = model.getCameraX() - (model.getCameraX() % model.getPixelsPerStep()) - model.getPixelsPerStep();
        int endMajorX = model.getCameraX() + getWidth() + model.getPixelsPerStep();

        int stringHeight = fontMetrics.getAscent();
        int rectHeight = stringHeight + 2;
        int heightHalfHypotenuse = (int) (Math.sqrt(2 * (stringHeight * stringHeight)) / 2);
        double deg45Radians = Math.toRadians(45);

        AffineTransform rotatedRectangleTransform = new AffineTransform();
        rotatedRectangleTransform.translate(-heightHalfHypotenuse, 7 + heightHalfHypotenuse);

        // Graphics
        for (int i = initialMajorX; i < endMajorX; i += model.getPixelsPerStep()) {
            if (i != 0) {
                g.setColor(globalAxisColor);
                g.drawLine(i, -5, i, 5);
                double stepValue = (((double) i / model.getPixelsPerStep()) / (model.getAxisX().scale * model.getZoom()));
                String step = getStepString(stepValue, model.getAxisX().unit);

                int stringWidth = fontMetrics.stringWidth(step);
                final int y = 7;
                int x = i - stringWidth / 2;
                int rectWidth = stringWidth + 4;
                if (stringWidth >= model.getPixelsPerStep()) { // Draw rotated string
                    x = i;
                    g.setFont(rotatedFont);
                    g.setColor(backgroundColor);

                    rotatedRectangleTransform.setToRotation(deg45Radians, x - 2, y);
                    Rectangle stepRectangle = new Rectangle(x - 2, y, rectWidth, rectHeight);
                    g.fill(rotatedRectangleTransform.createTransformedShape(stepRectangle));

                    g.setColor(globalAxisColor);
                    g.drawString(step, x - heightHalfHypotenuse, y + heightHalfHypotenuse);
                    g.setFont(font);
                } else { // Draw straight string
                    g.setColor(backgroundColor);
                    g.fillRect(x - 2, y, rectWidth, rectHeight);

                    g.setColor(globalAxisColor);
                    g.drawString(step, x, y + stringHeight);
                }
            }
        }

        // Y
        // Constants
        int initialMajorY = model.getCameraY() - (model.getCameraY() % model.getPixelsPerStep()) - model.getPixelsPerStep();
        int endMajorY = model.getCameraY() + getHeight() + model.getPixelsPerStep();

        // Graphics
        for (int i = initialMajorY; i < endMajorY; i += model.getPixelsPerStep()) {
            if (i != 0) {
                g.setColor(globalAxisColor);
                g.drawLine(-5, i, 5, i);
                double stepValue = -(((double) i / model.getPixelsPerStep()) / (model.getAxisY().scale * model.getZoom()));
                String step = getStepString(stepValue, model.getAxisY().unit);
                int stringWidth = fontMetrics.stringWidth(step);
                g.setColor(backgroundColor);
                g.fillRect(-9 - stringWidth, i - stringHeight / 2, stringWidth + 4, stringHeight + 2);
                g.setColor(globalAxisColor);
                g.drawString(step, -7 - stringWidth, i + stringHeight / 2);
            }
        }
    }

    private String getStepString(double stepValue, GraphUnit unit) {
        String step;
        double absStepValue = Math.abs(stepValue);
        if (model.isShowScientificNotation() && (absStepValue >= 1000 || absStepValue < 0.01d)) {
            ScientificNotationNumber number = ScientificNotationNumber.fromDouble(stepValue,
                    model.isShowEngineeringNotation());
            step = unit.getScientificTransformedUnit(stepValue, StringNumberUtils.getFormattedDouble(number.base(),
                    model.isShowDecAsFractions()), number.getExponentString());
        } else {
            step = unit.getTransformedUnit(stepValue, StringNumberUtils.getFormattedDouble(stepValue,
                    model.isShowDecAsFractions()));
        }
        return step;
    }

    @Override
    public void modelPropertyChange(MPropertyChangeEvent event) {
        if (event.getModel() instanceof PlottingModel) {
            repaint();
        }
    }

    @Override
    public String vetoablePropertyChange(MPropertyChangeEvent event) {
        return null;
    }
}
