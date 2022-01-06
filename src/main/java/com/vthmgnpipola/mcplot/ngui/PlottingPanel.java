/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2022  VTHMgNPipola
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

package com.vthmgnpipola.mcplot.ngui;

import com.vthmgnpipola.mcplot.nmath.Function;
import com.vthmgnpipola.mcplot.nmath.FunctionPlot;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JPanel;

import static com.vthmgnpipola.mcplot.Main.EXECUTOR_THREAD;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_LAF;
import static com.vthmgnpipola.mcplot.PreferencesHelper.PREFERENCES;
import static com.vthmgnpipola.mcplot.PreferencesHelper.VALUE_DARK_LAF;
import static com.vthmgnpipola.mcplot.PreferencesHelper.VALUE_LIGHT_LAF;
import static com.vthmgnpipola.mcplot.ngui.PlottingPanelContext.INITIAL_PIXELS_PER_STEP;

public class PlottingPanel extends JPanel {
    private final int[] zoomArray = new int[]{1, 2, 5};
    private int previousWidth, previousHeight;
    private int flWidth = 0;
    private int flHeight = 0;
    private PlottingPanelContext context;
    private final AffineTransform zoomTx;
    private final Map<Function, FunctionPlot> functions;

    private Font font;
    private Color backgroundColor;
    private Color minorGridColor;
    private Color majorGridColor;
    private Color globalAxisColor;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.#####");
    private FontMetrics fontMetrics;

    public PlottingPanel() {
        setDoubleBuffered(true);
        functions = new TreeMap<>();

        context = new PlottingPanelContext(this);
        font = new Font("Monospaced", Font.PLAIN, 12);
        zoomTx = new AffineTransform();
        zoomTx.setToScale(context.scaleX * context.pixelsPerStep * context.zoom,
                -context.scaleY * context.pixelsPerStep * context.zoom);

        final int[] dragging = new int[1]; // 1: dragging plotting panel; 2: dragging function legend panel
        final int[] startPos = new int[2]; // 0: X position; 1: Y position
        // TODO: Start dragging when shift is pressed
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPos[0] = e.getXOnScreen();
                startPos[1] = e.getYOnScreen();

                if ((e.getX() >= context.flPositionX && e.getX() <= context.flPositionX + flWidth) &&
                        (e.getY() >= context.flPositionY && e.getY() <= context.flPositionY + flHeight)) {
                    dragging[0] = 2;
                } else if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3) {
                    dragging[0] = 1;
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
                if (dragging[0] == 1) {
                    int currentMouseX = e.getXOnScreen();
                    int currentMouseY = e.getYOnScreen();

                    context.cameraX += startPos[0] - currentMouseX;
                    context.cameraY += startPos[1] - currentMouseY;

                    startPos[0] = currentMouseX;
                    startPos[1] = currentMouseY;

                    context.recalculateAllFunctions();
                    repaint();
                } else if (dragging[0] == 2) {
                    int currentMouseX = e.getXOnScreen();
                    int currentMouseY = e.getYOnScreen();

                    context.flPositionX += currentMouseX - startPos[0];
                    context.flPositionY += currentMouseY - startPos[1];

                    startPos[0] = currentMouseX;
                    startPos[1] = currentMouseY;

                    repaint();
                }
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
            context.pixelsPerStep += wheelRotation * ZOOM_PER_CLICK;
            if (context.pixelsPerStep < INITIAL_PIXELS_PER_STEP - (ZOOM_PER_CLICK * MAX_ZOOM)) {
                context.pixelsPerStep = INITIAL_PIXELS_PER_STEP;
                context.zoomPos--;
            } else if (context.pixelsPerStep > INITIAL_PIXELS_PER_STEP + (ZOOM_PER_CLICK * MAX_ZOOM)) {
                context.pixelsPerStep = INITIAL_PIXELS_PER_STEP;
                context.zoomPos++;
            }

            if (context.zoomPos >= 0 && context.zoomPos < zoomArray.length) {
                context.zoom = zoomArray[context.zoomPos];
            } else if (context.zoomPos >= zoomArray.length) {
                int timesCircled = context.zoomPos / zoomArray.length;
                int arrayPos = context.zoomPos % zoomArray.length;
                context.zoom = zoomArray[arrayPos] * Math.pow(10, timesCircled);
            } else {
                int timesCircled = -context.zoomPos / zoomArray.length;
                int arrayPos = (-context.zoomPos + zoomArray.length) % zoomArray.length;
                context.zoom = (double) 1 / (zoomArray[arrayPos] * Math.pow(10, timesCircled));
            }

            zoomTx.setToScale(context.scaleX * context.pixelsPerStep * context.zoom,
                    -context.scaleY * context.pixelsPerStep * context.zoom);
            context.recalculateAllFunctions();
            repaint();
        }));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                EXECUTOR_THREAD.submit(() -> {
                    context.cameraX -= (e.getComponent().getWidth() - previousWidth) / 2;
                    context.cameraY -= (e.getComponent().getHeight() - previousHeight) / 2;

                    previousWidth = e.getComponent().getWidth();
                    previousHeight = e.getComponent().getHeight();

                    context.recalculateAllFunctions();
                    repaint();
                });
            }
        });
    }

    public void init() {
        context = new PlottingPanelContext(this);

        context.cameraX = -getWidth() / 2;
        context.cameraY = -getHeight() / 2;

        previousWidth = getWidth();
        previousHeight = getHeight();

        resetColors();
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
        g.translate(-context.cameraX, -context.cameraY);

        // Minor grid
        if (context.drawGrid) {
            if (context.drawMinorGrid) {
                // X
                g.setColor(minorGridColor);
                double step = (double) context.pixelsPerStep / context.minorGridDivisions;

                double initialMinorX = context.cameraX -
                        (context.cameraX % ((double) context.pixelsPerStep / context.minorGridDivisions));
                double endMinorX = context.cameraX + getWidth();
                for (double i = initialMinorX; i < endMinorX; i += step) {
                    g.drawLine((int) i, context.cameraY, (int) i, context.cameraY + getHeight());
                }

                // Y
                double initialMinorY = context.cameraY -
                        (context.cameraY % ((double) context.pixelsPerStep / context.minorGridDivisions));
                double endMinorY = context.cameraY + getHeight();
                for (double i = initialMinorY; i < endMinorY; i += step) {
                    g.drawLine(context.cameraX, (int) i, context.cameraX + getWidth(), (int) i);
                }
            }

            // Major grid and steps
            // X
            int initialMajorX = context.cameraX - (context.cameraX % context.pixelsPerStep);
            int endMajorX = context.cameraX + getWidth();
            for (int i = initialMajorX; i < endMajorX; i += context.pixelsPerStep) {
                // Major grid
                g.setColor(majorGridColor);
                g.drawLine(i, context.cameraY, i, context.cameraY + getHeight());

                // Step
                if (i != 0 && context.drawAxisValues) {
                    g.setColor(globalAxisColor);
                    g.drawLine(i, -5, i, 5);
                    double stepValue = (((double) i / context.pixelsPerStep) / (context.scaleX * context.zoom));
                    String step = context.unitX.getTransformedUnit(stepValue, decimalFormat.format(stepValue));
                    g.drawString(step, i - fontMetrics.stringWidth(step) / 2, 7 + fontMetrics.getAscent());
                }
            }

            // Y
            int initialMajorY = context.cameraY - (context.cameraY % context.pixelsPerStep);
            int endMajorY = context.cameraY + getHeight();
            for (int i = initialMajorY; i < endMajorY; i += context.pixelsPerStep) {
                // Major grid
                g.setColor(majorGridColor);
                g.drawLine(context.cameraX, i, context.cameraX + getWidth(), i);

                // Step
                if (i != 0 && context.drawAxisValues) {
                    g.setColor(globalAxisColor);
                    g.drawLine(-5, i, 5, i);
                    double stepValue = -(((double) i / context.pixelsPerStep) / (context.scaleY * context.zoom));
                    String step = context.unitY.getTransformedUnit(stepValue, decimalFormat.format(stepValue));
                    g.drawString(step, -7 - fontMetrics.stringWidth(step), i + fontMetrics.getAscent() / 2);
                }
            }
        }

        // Global axis
        g.setColor(globalAxisColor);
        g.drawLine(context.cameraX, 0, context.cameraX + getWidth(), 0);
        g.drawLine(0, context.cameraY, 0, context.cameraY + getHeight());

        // Functions
        if (context.antialias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        zoomTx.setToScale(context.scaleX * context.pixelsPerStep * context.zoom * context.unitX.getScale(),
                -context.scaleY * context.pixelsPerStep * context.zoom * context.unitY.getScale());

        int visibleFunctions = 0;
        int longestFunction = 0;
        for (Map.Entry<Function, FunctionPlot> functionEntry : functions.entrySet()) {
            Function function = functionEntry.getKey();
            FunctionPlot plot = functionEntry.getValue();
            Color traceColor = function.getTraceColor();
            if (!function.isVisible() || plot == null || plot.getPath() == null) {
                continue;
            }

            if (function.isFilled()) {
                Path2D.Double fill = (Path2D.Double) plot.getPath().clone();
                fill.lineTo(plot.getEndX(), 0);
                fill.lineTo(plot.getStartX(), 0);
                fill.closePath();

                Color fillColor = new Color(traceColor.getRed(), traceColor.getGreen(), traceColor.getBlue(),
                        (int) Math.min((context.fillTransparency * 2.55), 255));
                g.setColor(fillColor);
                g.fill(zoomTx.createTransformedShape(fill));
            }
            g.setStroke(getStroke(function.getTraceType()));
            g.setColor(traceColor);
            g.draw(zoomTx.createTransformedShape(plot.getPath()));

            longestFunction = Math.max(longestFunction, fontMetrics.stringWidth(function.getDefinition().trim()));

            visibleFunctions++;
        }

        // Function legends
        if (context.functionLegends && visibleFunctions != 0) {
            g.translate(context.cameraX, context.cameraY);
            g.setStroke(baseStroke);

            int panelHeight = flHeight = 20 + fontMetrics.getHeight() * visibleFunctions;
            int panelWidth = flWidth = 70 + longestFunction;
            g.setColor(backgroundColor);
            g.fillRect(context.flPositionX, context.flPositionY, panelWidth, panelHeight);
            g.setColor(globalAxisColor);
            g.drawRect(context.flPositionX, context.flPositionY, panelWidth, panelHeight);

            g.translate(context.flPositionX, context.flPositionY);
            int i = 0;
            for (Map.Entry<Function, FunctionPlot> functionEntry : functions.entrySet()) {
                Function function = functionEntry.getKey();
                FunctionPlot plot = functionEntry.getValue();
                Color traceColor = function.getTraceColor();
                if (!function.isVisible() || plot == null || plot.getPath() == null) {
                    continue;
                }

                g.setStroke(getStroke(functionEntry.getKey().getTraceType()));
                g.setColor(traceColor);
                int y = fontMetrics.getHeight() * i + fontMetrics.getHeight() / 2 + 10;
                g.drawLine(10, y, 50, y);

                g.setStroke(baseStroke);
                g.setColor(globalAxisColor);
                g.drawString(function.getDefinition().trim(), 60,
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

    public Map<Function, FunctionPlot> getFunctions() {
        return functions;
    }

    public PlottingPanelContext getContext() {
        return context;
    }

    void setContext(PlottingPanelContext context) {
        this.context = context;
        context.setBase(this);
        context.updateTraces();
    }

    private Stroke getStroke(String traceType) {
        if (traceType == null) {
            return context.defaultTraceStroke;
        }
        return switch (traceType) {
            case Function.TRACE_TYPE_DASHED -> context.dashedTraceStroke;
            case Function.TRACE_TYPE_DOTTED -> context.dottedTraceStroke;
            case Function.TRACE_TYPE_DASHED_DOTTED -> context.dashedDottedTraceStroke;
            default -> context.defaultTraceStroke;
        };
    }
}
