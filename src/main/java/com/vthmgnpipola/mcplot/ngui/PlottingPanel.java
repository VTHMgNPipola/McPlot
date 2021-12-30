/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2021  VTHMgNPipola
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

import com.vthmgnpipola.mcplot.GraphUnit;
import com.vthmgnpipola.mcplot.nmath.Function;
import com.vthmgnpipola.mcplot.nmath.FunctionPlot;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;

import static com.vthmgnpipola.mcplot.Main.EXECUTOR_THREAD;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_DRAW_AXIS_VALUES;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_DRAW_GRID;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_DRAW_MINOR_GRID;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_ENABLE_ANTIALIAS;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_ENABLE_FUNCTION_LEGENDS;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_FILL_TRANSPARENCY;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_GRAPH_UNIT_X;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_GRAPH_UNIT_Y;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_LAF;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_MAX_STEP;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_MINOR_GRID_DIVISIONS;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_SAMPLES_PER_CELL;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_SCALE_X;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_SCALE_Y;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_TRACE_WIDTH;
import static com.vthmgnpipola.mcplot.PreferencesHelper.PREFERENCES;
import static com.vthmgnpipola.mcplot.PreferencesHelper.VALUE_DARK_LAF;
import static com.vthmgnpipola.mcplot.PreferencesHelper.VALUE_LIGHT_LAF;

public class PlottingPanel extends JPanel {
    private static final int INITIAL_PIXELS_PER_STEP = 75;

    private int cameraX, cameraY;
    private double scaleX = PREFERENCES.getDouble(KEY_SCALE_X, 1);
    private double scaleY = PREFERENCES.getDouble(KEY_SCALE_Y, 1);
    private final int[] zoomArray = new int[]{1, 2, 5};
    private int pixelsPerStep = INITIAL_PIXELS_PER_STEP;
    private double zoom = 1;
    private int zoomPos = 0;
    private int previousWidth, previousHeight;
    private int samplesPerCell = PREFERENCES.getInt(KEY_SAMPLES_PER_CELL, 25);
    private double maxStep = PREFERENCES.getDouble(KEY_MAX_STEP, 0.5);
    private int traceWidth = PREFERENCES.getInt(KEY_TRACE_WIDTH, 3);
    private boolean antialias = PREFERENCES.getBoolean(KEY_ENABLE_ANTIALIAS, false);
    private int flPositionX = 20;
    private int flPositionY = 20;
    private int flWidth = 0;
    private int flHeight = 0;
    private boolean functionLegends = PREFERENCES.getBoolean(KEY_ENABLE_FUNCTION_LEGENDS, false);
    private boolean drawMinorGrid = PREFERENCES.getBoolean(KEY_DRAW_MINOR_GRID, true);
    private boolean drawGrid = PREFERENCES.getBoolean(KEY_DRAW_GRID, true);
    private boolean drawAxisValues = PREFERENCES.getBoolean(KEY_DRAW_AXIS_VALUES, true);
    private int minorGridDivisions = PREFERENCES.getInt(KEY_MINOR_GRID_DIVISIONS, 5);
    private final AffineTransform zoomTx;
    private final Map<Function, FunctionPlot> functions;

    private Font font;
    private double fillTransparency = PREFERENCES.getDouble(KEY_FILL_TRANSPARENCY, 25);
    private GraphUnit unitX = GraphUnit.getUnit(PREFERENCES.get(KEY_GRAPH_UNIT_X, ""));
    private GraphUnit unitY = GraphUnit.getUnit(PREFERENCES.get(KEY_GRAPH_UNIT_Y, ""));
    private Color backgroundColor;
    private Color minorGridColor;
    private Color majorGridColor;
    private Color globalAxisColor;
    private Stroke traceStroke;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.#####");
    private FontMetrics fontMetrics;
    private MathPanel mathPanel;

    public PlottingPanel() {
        setDoubleBuffered(true);
        functions = new HashMap<>();

        font = new Font("Monospaced", Font.PLAIN, 12);
        traceStroke = new BasicStroke(traceWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        zoomTx = new AffineTransform();
        zoomTx.setToScale(scaleX * pixelsPerStep * zoom, -scaleY * pixelsPerStep * zoom);

        final int[] dragging = new int[1]; // 1: dragging plotting panel; 2: dragging function legend panel
        final int[] startPos = new int[2]; // 0: X position; 1: Y position
        // TODO: Start dragging when shift is pressed
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPos[0] = e.getXOnScreen();
                startPos[1] = e.getYOnScreen();

                if ((e.getX() >= flPositionX && e.getX() <= flPositionX + flWidth) &&
                        (e.getY() >= flPositionY && e.getY() <= flPositionY + flHeight)) {
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

                    cameraX += startPos[0] - currentMouseX;
                    cameraY += startPos[1] - currentMouseY;

                    startPos[0] = currentMouseX;
                    startPos[1] = currentMouseY;

                    mathPanel.recalculateAllFunctions();
                    repaint();
                } else if (dragging[0] == 2) {
                    int currentMouseX = e.getXOnScreen();
                    int currentMouseY = e.getYOnScreen();

                    flPositionX += currentMouseX - startPos[0];
                    flPositionY += currentMouseY - startPos[1];

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
            pixelsPerStep += wheelRotation * ZOOM_PER_CLICK;
            if (pixelsPerStep < INITIAL_PIXELS_PER_STEP - (ZOOM_PER_CLICK * MAX_ZOOM)) {
                pixelsPerStep = INITIAL_PIXELS_PER_STEP;
                zoomPos--;
            } else if (pixelsPerStep > INITIAL_PIXELS_PER_STEP + (ZOOM_PER_CLICK * MAX_ZOOM)) {
                pixelsPerStep = INITIAL_PIXELS_PER_STEP;
                zoomPos++;
            }

            if (zoomPos >= 0 && zoomPos < zoomArray.length) {
                zoom = zoomArray[zoomPos];
            } else if (zoomPos >= zoomArray.length) {
                int timesCircled = zoomPos / zoomArray.length;
                int arrayPos = zoomPos % zoomArray.length;
                zoom = zoomArray[arrayPos] * Math.pow(10, timesCircled);
            } else {
                int timesCircled = -zoomPos / zoomArray.length;
                int arrayPos = (-zoomPos + zoomArray.length) % zoomArray.length;
                zoom = (double) 1 / (zoomArray[arrayPos] * Math.pow(10, timesCircled));
            }

            zoomTx.setToScale(scaleX * pixelsPerStep * zoom, -scaleY * pixelsPerStep * zoom);
            mathPanel.recalculateAllFunctions();
            repaint();
        }));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                EXECUTOR_THREAD.submit(() -> {
                    cameraX -= (e.getComponent().getWidth() - previousWidth) / 2;
                    cameraY -= (e.getComponent().getHeight() - previousHeight) / 2;

                    previousWidth = e.getComponent().getWidth();
                    previousHeight = e.getComponent().getHeight();

                    mathPanel.recalculateAllFunctions();
                    repaint();
                });
            }
        });
    }

    public void init() {
        cameraX = -getWidth() / 2;
        cameraY = -getHeight() / 2;

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
        g.translate(-cameraX, -cameraY);

        // Minor grid
        if (drawGrid) {
            if (drawMinorGrid) {
                // X
                g.setColor(minorGridColor);
                for (double i = cameraX - (cameraX % ((double) pixelsPerStep / minorGridDivisions));
                     i < cameraX + getWidth(); i += (double) pixelsPerStep / minorGridDivisions) {
                    g.drawLine((int) i, cameraY, (int) i, cameraY + getHeight());
                }

                // Y
                for (double i = cameraY - (cameraY % ((double) pixelsPerStep / minorGridDivisions));
                     i < cameraY + getHeight(); i += (double) pixelsPerStep / minorGridDivisions) {
                    g.drawLine(cameraX, (int) i, cameraX + getWidth(), (int) i);
                }
            }

            // Major grid and steps
            // X
            for (int i = cameraX - (cameraX % pixelsPerStep); i < cameraX + getWidth(); i += pixelsPerStep) {
                // Major grid
                g.setColor(majorGridColor);
                g.drawLine(i, cameraY, i, cameraY + getHeight());

                // Step
                if (i != 0 && drawAxisValues) {
                    g.setColor(globalAxisColor);
                    g.drawLine(i, -5, i, 5);
                    double stepValue = (((double) i / pixelsPerStep) / (scaleX * zoom));
                    String step = unitX.getTransformedUnit(stepValue, decimalFormat.format(stepValue));
                    g.drawString(step, i - fontMetrics.stringWidth(step) / 2, 7 + fontMetrics.getAscent());
                }
            }

            // Y
            for (int i = cameraY - (cameraY % pixelsPerStep); i < cameraY + getHeight(); i += pixelsPerStep) {
                // Major grid
                g.setColor(majorGridColor);
                g.drawLine(cameraX, i, cameraX + getWidth(), i);

                // Step
                if (i != 0 && drawAxisValues) {
                    g.setColor(globalAxisColor);
                    g.drawLine(-5, i, 5, i);
                    double stepValue = -(((double) i / pixelsPerStep) / (scaleY * zoom));
                    String step = unitY.getTransformedUnit(stepValue, decimalFormat.format(stepValue));
                    g.drawString(step, -7 - fontMetrics.stringWidth(step), i + fontMetrics.getAscent() / 2);
                }
            }
        }

        // Global axis
        g.setColor(globalAxisColor);
        g.drawLine(cameraX, 0, cameraX + getWidth(), 0);
        g.drawLine(0, cameraY, 0, cameraY + getHeight());

        // Functions
        if (antialias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        zoomTx.setToScale(scaleX * pixelsPerStep * zoom * unitX.getScale(),
                -scaleY * pixelsPerStep * zoom * unitY.getScale());

        g.setStroke(traceStroke);
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
                        (int) Math.min((fillTransparency * 2.55), 255));
                g.setColor(fillColor);
                g.fill(zoomTx.createTransformedShape(fill));
            }
            g.setColor(traceColor);
            g.draw(zoomTx.createTransformedShape(plot.getPath()));

            longestFunction = Math.max(longestFunction, fontMetrics.stringWidth(function.getDefinition().trim()));

            visibleFunctions++;
        }

        // Function legends
        if (functionLegends && visibleFunctions != 0) {
            g.translate(cameraX, cameraY);
            g.setStroke(baseStroke);

            int panelHeight = flHeight = 20 + fontMetrics.getHeight() * visibleFunctions;
            int panelWidth = flWidth = 70 + longestFunction;
            g.setColor(backgroundColor);
            g.fillRect(flPositionX, flPositionY, panelWidth, panelHeight);
            g.setColor(globalAxisColor);
            g.drawRect(flPositionX, flPositionY, panelWidth, panelHeight);

            g.translate(flPositionX, flPositionY);
            int i = 0;
            for (Map.Entry<Function, FunctionPlot> functionEntry : functions.entrySet()) {
                Function function = functionEntry.getKey();
                FunctionPlot plot = functionEntry.getValue();
                Color traceColor = function.getTraceColor();
                if (!function.isVisible() || plot == null || plot.getPath() == null) {
                    continue;
                }

                g.setStroke(traceStroke);
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

    public int getCameraX() {
        return cameraX;
    }

    public int getCameraY() {
        return cameraY;
    }

    public double getScaleX() {
        return scaleX;
    }

    public void setScaleX(double scaleX) {
        this.scaleX = scaleX;
        PREFERENCES.putDouble(KEY_SCALE_X, scaleX);
        mathPanel.recalculateAllFunctions();
    }

    public double getScaleY() {
        return scaleY;
    }

    public void setScaleY(double scaleY) {
        this.scaleY = scaleY;
        PREFERENCES.putDouble(KEY_SCALE_Y, scaleY);
        mathPanel.recalculateAllFunctions();
    }

    public int getPixelsPerStep() {
        return pixelsPerStep;
    }

    public double getZoom() {
        return zoom;
    }

    public int getSamplesPerCell() {
        return samplesPerCell;
    }

    public void setSamplesPerCell(int samplesPerCell) {
        this.samplesPerCell = samplesPerCell;
        PREFERENCES.putInt(KEY_SAMPLES_PER_CELL, samplesPerCell);
        mathPanel.recalculateAllFunctions();
    }

    public double getMaxStep() {
        return maxStep;
    }

    public void setMaxStep(double maxStep) {
        this.maxStep = maxStep;
        PREFERENCES.putDouble(KEY_MAX_STEP, maxStep);
        mathPanel.recalculateAllFunctions();
    }

    public boolean isAntialias() {
        return antialias;
    }

    public void setAntialias(boolean antialias) {
        this.antialias = antialias;
        PREFERENCES.putBoolean(KEY_ENABLE_ANTIALIAS, antialias);
        repaint();
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

    public int getTraceWidth() {
        return traceWidth;
    }

    public void setTraceWidth(int traceWidth) {
        this.traceWidth = traceWidth;
        PREFERENCES.putInt(KEY_TRACE_WIDTH, traceWidth);
        traceStroke = new BasicStroke(traceWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        repaint();
    }

    public double getFillTransparency() {
        return fillTransparency;
    }

    public void setFillTransparency(double fillTransparency) {
        this.fillTransparency = fillTransparency;
        PREFERENCES.putDouble(KEY_FILL_TRANSPARENCY, fillTransparency);
        repaint();
    }

    public GraphUnit getUnitX() {
        return unitX;
    }

    public void setUnitX(GraphUnit unitX) {
        this.unitX = unitX;
        repaint();
    }

    public GraphUnit getUnitY() {
        return unitY;
    }

    public void setUnitY(GraphUnit unitY) {
        this.unitY = unitY;
        repaint();
    }

    public boolean isFunctionLegends() {
        return functionLegends;
    }

    public void setFunctionLegends(boolean functionLegends) {
        this.functionLegends = functionLegends;
        PREFERENCES.putBoolean(KEY_ENABLE_FUNCTION_LEGENDS, functionLegends);
        repaint();
    }

    public boolean isDrawMinorGrid() {
        return drawMinorGrid;
    }

    public void setDrawMinorGrid(boolean drawMinorGrid) {
        this.drawMinorGrid = drawMinorGrid;
        PREFERENCES.putBoolean(KEY_DRAW_MINOR_GRID, drawMinorGrid);
        repaint();
    }

    public boolean isDrawGrid() {
        return drawGrid;
    }

    public void setDrawGrid(boolean drawGrid) {
        this.drawGrid = drawGrid;
        PREFERENCES.putBoolean(KEY_DRAW_GRID, drawGrid);
        repaint();
    }

    public boolean isDrawAxisValues() {
        return drawAxisValues;
    }

    public void setDrawAxisValues(boolean drawAxisValues) {
        this.drawAxisValues = drawAxisValues;
        PREFERENCES.putBoolean(KEY_DRAW_AXIS_VALUES, drawAxisValues);
        repaint();
    }

    public int getMinorGridDivisions() {
        return minorGridDivisions;
    }

    public void setMinorGridDivisions(int minorGridDivisions) {
        this.minorGridDivisions = minorGridDivisions;
        PREFERENCES.putInt(KEY_MINOR_GRID_DIVISIONS, minorGridDivisions);
        repaint();
    }

    public Map<Function, FunctionPlot> getFunctions() {
        return functions;
    }

    public void setMathPanel(MathPanel mathPanel) {
        this.mathPanel = mathPanel;
    }
}
