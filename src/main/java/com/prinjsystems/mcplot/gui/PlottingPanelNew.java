package com.prinjsystems.mcplot.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import javax.swing.JPanel;

public class PlottingPanelNew extends JPanel {
    private static final int INITIAL_PIXELS_PER_STEP = 75;

    private int cameraX, cameraY;
    private final int scaleX = 1;
    private final int scaleY = 1;
    private final int[] zoomArray = new int[]{1, 2, 5, 10};
    private int pixelsPerStep = INITIAL_PIXELS_PER_STEP;
    private double zoom = 1;
    private int zoomPos = 0;

    private final Font font;
    private final Color backgroundColor = Color.white;
    private final Color minorGridColor = new Color(232, 232, 232);
    private final Color majorGridColor = Color.lightGray;
    private final Color globalAxisColor = Color.black;
    private FontMetrics fontMetrics;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.#####");

    public PlottingPanelNew() {
        setPreferredSize(new Dimension(800, 600));
        cameraX = -getPreferredSize().width / 2;
        cameraY = -getPreferredSize().height / 2;

        font = new Font("Monospaced", Font.PLAIN, 12);

        final boolean[] dragging = new boolean[1];
        final int[] startPos = new int[2];
        // TODO: Start dragging when shift is pressed
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3) {
                    dragging[0] = true;
                    startPos[0] = e.getXOnScreen();
                    startPos[1] = e.getYOnScreen();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3) {
                    dragging[0] = false;
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
                if (dragging[0]) {
                    int currentMouseX = e.getXOnScreen();
                    int currentMouseY = e.getYOnScreen();

                    cameraX += startPos[0] - currentMouseX;
                    cameraY += startPos[1] - currentMouseY;

                    startPos[0] = currentMouseX;
                    startPos[1] = currentMouseY;
                }

                repaint();
            }
        });

        /*
         * This method changes the zoom based on the mouse wheel rotation.
         * Firstly, it increases or decreases the amount of pixels per step on the axis lines by 10 pixels times the
         * amount of wheel clicks registered. If the amount of pixels per step is smaller or larger than predefined
         * values it sets it back to the original starting value and decreases or increases the zoom "position"(how
         * many times the wheel rotated), respectively.
         * It then checks if the zoom position is a valid address in an array of zoom values I considered good, and
         * if so chooses one of those.
         * If it is over the maximum address, it calculates how many times the value "circled" around the list of zoom
         * values and subtracts one from this value. It then calculates what would be the effective index if the
         * array of zoom values was circular, and skips the first item since it is 1. It then calculates the zoom by
         * grabbing the zoom value from the array using the effective index it calculated and multiplying the value
         * by 10 to the power of the times the zoom position circled the array.
         * If it is below the minimum address, it does the same thing but considering the zoom position as a positive
         *  value and inverting the final zoom result (by doing 1 over whatever zoom value it got).
         */
        addMouseWheelListener(e -> {
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
                if (arrayPos == 0) {
                    arrayPos++;
                }
                zoom = zoomArray[arrayPos] * Math.pow(10, timesCircled);
            } else {
                int timesCircled = -zoomPos / zoomArray.length;
                int arrayPos = (-zoomPos + zoomArray.length) % zoomArray.length;
                if (arrayPos == 0) {
                    arrayPos++;
                }
                zoom = (double) 1 / (zoomArray[arrayPos] * Math.pow(10, timesCircled));
            }

            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setFont(font);
        fontMetrics = g.getFontMetrics();

        // Background
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Camera translation
        g.translate(-cameraX, -cameraY);

        // Minor grid
        // X
        g.setColor(minorGridColor);
        for (int i = cameraX - (cameraX % (pixelsPerStep / 5)); i < cameraX + getWidth(); i += pixelsPerStep / 5) {
            g.drawLine(i, cameraY, i, cameraY + getHeight());
        }

        // Y
        for (int i = cameraY - (cameraY % (pixelsPerStep / 5)); i < cameraY + getHeight(); i += pixelsPerStep / 5) {
            g.drawLine(cameraX, i, cameraX + getWidth(), i);
        }

        // Major grid and steps
        // X
        for (int i = cameraX - (cameraX % pixelsPerStep); i < cameraX + getWidth(); i += pixelsPerStep) {
            // Major grid
            g.setColor(majorGridColor);
            g.drawLine(i, cameraY, i, cameraY + getHeight());

            // Step
            if (i != 0) {
                g.setColor(globalAxisColor);
                g.drawLine(i, -5, i, 5);
                String step = decimalFormat.format((((double) i / pixelsPerStep) / (scaleX * zoom)));
                g.drawString(step, i - fontMetrics.stringWidth(step) / 2, 7 + fontMetrics.getAscent());
            }
        }

        // Y
        for (int i = cameraY - (cameraY % pixelsPerStep); i < cameraY + getHeight(); i += pixelsPerStep) {
            // Major grid
            g.setColor(majorGridColor);
            g.drawLine(cameraX, i, cameraX + getWidth(), i);

            // Step
            if (i != 0) {
                g.setColor(globalAxisColor);
                g.drawLine(-5, i, 5, i);
                String step = decimalFormat.format(-(((double) i / pixelsPerStep) / (scaleY * zoom)));
                g.drawString(step, -7 - fontMetrics.stringWidth(step), i + fontMetrics.getAscent() / 2);
            }
        }

        // Global axis
        g.setColor(globalAxisColor);
        g.drawLine(cameraX, 0, cameraX + getWidth(), 0);
        g.drawLine(0, cameraY, 0, cameraY + getHeight());
    }
}
