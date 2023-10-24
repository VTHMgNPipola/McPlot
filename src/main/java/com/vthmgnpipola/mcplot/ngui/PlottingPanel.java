/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2023  VTHMgNPipola
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
import com.vthmgnpipola.mcplot.nmath.EvaluationContext;
import com.vthmgnpipola.mcplot.nmath.ScientificNotationNumber;
import com.vthmgnpipola.mcplot.plot.Plot;

import javax.swing.JPanel;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private final EvaluationContext evaluationContext;
    private PlottingPanelContext context;
    private final AffineTransform zoomTx;
    private final List<Plot> plots;

    private Font font;
    private final Font rotatedFont; // Rotated in 45 degrees
    private Color backgroundColor;
    private Color minorGridColor;
    private Color majorGridColor;
    private Color globalAxisColor;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.#####");
    private FontMetrics fontMetrics;

    public PlottingPanel() {
        setDoubleBuffered(true);
        plots = Collections.synchronizedList(new ArrayList<>());

        evaluationContext = new EvaluationContext();
        context = new PlottingPanelContext(this);
        context.setEvaluationContext(evaluationContext);
        font = new Font("Monospaced", Font.PLAIN, 12);
        AffineTransform deg45Transform = new AffineTransform();
        deg45Transform.rotate(Math.toRadians(45));
        rotatedFont = font.deriveFont(deg45Transform);
        zoomTx = new AffineTransform();
        zoomTx.setToScale(context.axisX.scale * context.pixelsPerStep * context.zoom,
                -context.axisY.scale * context.pixelsPerStep * context.zoom);

        final int DRAGGING_PLOTTING_PANEL = 1;
        final int DRAGGING_LEGEND_PANEL = 2;
        final int[] dragging = new int[1]; // 1: dragging plotting panel; 2: dragging function legend panel
        final int[] startPos = new int[2]; // 0: X position; 1: Y position
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPos[0] = e.getXOnScreen();
                startPos[1] = e.getYOnScreen();

                if ((e.getX() >= context.flPositionX && e.getX() <= context.flPositionX + flWidth) &&
                        (e.getY() >= context.flPositionY && e.getY() <= context.flPositionY + flHeight)) {
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
                if (dragging[0] == DRAGGING_PLOTTING_PANEL) {
                    int currentMouseX = e.getXOnScreen();
                    int currentMouseY = e.getYOnScreen();

                    context.cameraX += startPos[0] - currentMouseX;
                    context.cameraY += startPos[1] - currentMouseY;

                    startPos[0] = currentMouseX;
                    startPos[1] = currentMouseY;

                    context.updateEvaluationContext();
                    context.recalculateAllFunctions(false);
                    repaint();
                } else if (dragging[0] == DRAGGING_LEGEND_PANEL) {
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

            zoomTx.setToScale(context.axisX.scale * context.pixelsPerStep * context.zoom,
                    -context.axisY.scale * context.pixelsPerStep * context.zoom);
            context.updateEvaluationContext();
            context.recalculateAllFunctions(true);
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

                    context.updateEvaluationContext();
                    context.recalculateAllFunctions(false);
                    repaint();
                });
            }
        });
    }

    public void init() {
        context.reset(this);

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
        g.translate(-context.cameraX, -context.cameraY);

        // Grid
        if (context.drawGrid) {
            // Minor grid
            if (context.drawMinorGrid) {
                drawMinorGrid(g);
            }

            // Major grid and steps
            drawMajorGrid(g);
            if (!context.drawAxisOverFunc) {
                drawAxisValues(g);
            }
        }

        // Global axis
        if (!context.drawAxisOverFunc) {
            drawGlobalAxis(g);
        }

        // Plots
        if (context.antialias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        zoomTx.setToScale(context.axisX.scale * context.pixelsPerStep * context.zoom * context.axisX.unit.getScale(),
                -context.axisY.scale * context.pixelsPerStep * context.zoom * context.axisY.unit.getScale());

        int visibleFunctions = 0;
        int longestPlot = 0;
        for (Plot plot : plots) {
            if (plot == null || plot.getPath() == null || plot.isInvisible()) {
                continue;
            }

            plot.plot(g, zoomTx, context);

            longestPlot = Math.max(longestPlot, fontMetrics.stringWidth(plot.getLegend().trim()));

            visibleFunctions++;
        }

        // Global axis and steps
        if (context.drawAxisOverFunc) {
            g.setStroke(baseStroke);
            drawGlobalAxis(g);
            drawAxisValues(g);
        }

        // Legends
        if (context.functionLegends && visibleFunctions != 0) {
            g.translate(context.cameraX, context.cameraY);
            g.setStroke(baseStroke);

            int panelHeight = flHeight = 20 + fontMetrics.getHeight() * visibleFunctions;
            int panelWidth = flWidth = 70 + longestPlot;
            g.setColor(backgroundColor);
            g.fillRect(context.flPositionX, context.flPositionY, panelWidth, panelHeight);
            g.setColor(globalAxisColor);
            g.drawRect(context.flPositionX, context.flPositionY, panelWidth, panelHeight);

            g.translate(context.flPositionX, context.flPositionY);
            int i = 0;
            for (Plot plot : plots) {
                if (plot == null || plot.getPath() == null || plot.isInvisible()) {
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

    public EvaluationContext getEvaluationContext() {
        return evaluationContext;
    }

    public PlottingPanelContext getContext() {
        return context;
    }

    void setContext(PlottingPanelContext context) {
        this.context = context;
        context.setBase(this);
        context.setEvaluationContext(evaluationContext);
    }

    private void drawGlobalAxis(Graphics2D g) {
        g.setColor(globalAxisColor);
        g.drawLine(context.cameraX, 0, context.cameraX + getWidth(), 0);
        g.drawLine(0, context.cameraY, 0, context.cameraY + getHeight());
    }

    private void drawMinorGrid(Graphics2D g) {
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

    private void drawMajorGrid(Graphics2D g) {
        // X
        // Constants
        int initialMajorX = context.cameraX - (context.cameraX % context.pixelsPerStep) - context.pixelsPerStep;
        int endMajorX = context.cameraX + getWidth() + context.pixelsPerStep;

        // Graphics
        for (int i = initialMajorX; i < endMajorX; i += context.pixelsPerStep) {
            g.setColor(majorGridColor);
            g.drawLine(i, context.cameraY, i, context.cameraY + getHeight());
        }

        // Y
        // Constants
        int initialMajorY = context.cameraY - (context.cameraY % context.pixelsPerStep) - context.pixelsPerStep;
        int endMajorY = context.cameraY + getHeight() + context.pixelsPerStep;

        // Graphics
        for (int i = initialMajorY; i < endMajorY; i += context.pixelsPerStep) {
            g.setColor(majorGridColor);
            g.drawLine(context.cameraX, i, context.cameraX + getWidth(), i);
        }
    }

    private void drawAxisValues(Graphics2D g) {
        // X
        // Constants
        int initialMajorX = context.cameraX - (context.cameraX % context.pixelsPerStep) - context.pixelsPerStep;
        int endMajorX = context.cameraX + getWidth() + context.pixelsPerStep;

        int stringHeight = fontMetrics.getAscent();
        int rectHeight = stringHeight + 2;
        int heightHalfHypotenuse = (int) (Math.sqrt(2 * (stringHeight * stringHeight)) / 2);
        double deg45Radians = Math.toRadians(45);

        AffineTransform rotatedRectangleTransform = new AffineTransform();
        rotatedRectangleTransform.translate(-heightHalfHypotenuse, 7 + heightHalfHypotenuse);

        // Graphics
        for (int i = initialMajorX; i < endMajorX; i += context.pixelsPerStep) {
            if (i != 0) {
                g.setColor(globalAxisColor);
                g.drawLine(i, -5, i, 5);
                double stepValue = (((double) i / context.pixelsPerStep) / (context.axisX.scale * context.zoom));
                String step = getStepString(stepValue, context.axisX.unit);

                int stringWidth = fontMetrics.stringWidth(step);
                final int y = 7;
                int x = i - stringWidth / 2;
                int rectWidth = stringWidth + 4;
                if (stringWidth >= context.pixelsPerStep) { // Draw rotated string
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
        int initialMajorY = context.cameraY - (context.cameraY % context.pixelsPerStep) - context.pixelsPerStep;
        int endMajorY = context.cameraY + getHeight() + context.pixelsPerStep;

        // Graphics
        for (int i = initialMajorY; i < endMajorY; i += context.pixelsPerStep) {
            if (i != 0) {
                g.setColor(globalAxisColor);
                g.drawLine(-5, i, 5, i);
                double stepValue = -(((double) i / context.pixelsPerStep) / (context.axisY.scale * context.zoom));
                String step = getStepString(stepValue, context.axisY.unit);
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
        if (context.showScientificNotation && (absStepValue >= 1000 || absStepValue < 0.01d)) {
            ScientificNotationNumber number = ScientificNotationNumber.fromDouble(stepValue, context.showEngineeringNotation);
            String exponent = "×10" + toSuperscript(String.valueOf(number.exponent()));
            step = unit.getScientificTransformedUnit(stepValue, getFormattedDouble(number.base()),
                    exponent);
        } else {
            step = unit.getTransformedUnit(stepValue, getFormattedDouble(stepValue));
        }
        return step;
    }

    private String getFormattedDouble(double value) {
        if (context.showDecAsFractions) {
            StringBuilder sb = new StringBuilder();
            int intValue = (int) (value > 0 ? Math.floor(value) : Math.ceil(value));
            if (intValue != value && intValue == -1) {
                sb.append("-");
            } else if (intValue != 0) {
                sb.append(decimalFormat.format(intValue));
            }

            intValue = Math.abs(intValue);
            BigDecimal difference = new BigDecimal(String.valueOf(Math.abs(value)));
            difference = difference.subtract(BigDecimal.valueOf(intValue));
            if (difference.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal denominator = BigDecimal.ONE.divide(difference, difference.scale() * 2,
                        RoundingMode.HALF_UP);
                if (denominator.intValue() == denominator.doubleValue()) {
                    sb.append("¹⁄").append(toSubscript(String.valueOf(denominator.intValue())));
                } else {
                    int divisor = 1;
                    BigDecimal result;
                    do {
                        divisor++;
                        BigDecimal scaledDenominator = difference.divide(BigDecimal.valueOf(divisor),
                                RoundingMode.HALF_UP);
                        result = BigDecimal.ONE.divide(scaledDenominator, scaledDenominator.scale() * 2,
                                RoundingMode.HALF_UP);
                    } while (result.intValue() != result.doubleValue());

                    sb.append(toSuperscript(String.valueOf(divisor))).append("⁄")
                            .append(toSubscript(String.valueOf(result.intValue())));
                }
            }

            return sb.toString();
        } else {
            return decimalFormat.format(value);
        }
    }

    private String toSuperscript(String str) {
        final char[] replaced = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'};
        final char[] replacement = new char[]{'⁰', '¹', '²', '³', '⁴', '⁵', '⁶',
                '⁷', '⁸', '⁹', '⁻'};
        return fastReplace(str, replaced, replacement);
    }

    private String toSubscript(String str) {
        final char[] replaced = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'};
        final char[] replacement = new char[]{'₀', '₁', '₂', '₃', '₄', '₅', '₆',
                '₇', '₈', '₉', '₋'};
        return fastReplace(str, replaced, replacement);
    }

    private String fastReplace(String input, char[] replaced, char[] replacement) {
        char[] cs = input.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            for (int j = 0; j < replaced.length; j++) {
                if (cs[i] == replaced[j]) {
                    cs[i] = replacement[j];
                }
            }
        }
        return new String(cs);
    }
}
