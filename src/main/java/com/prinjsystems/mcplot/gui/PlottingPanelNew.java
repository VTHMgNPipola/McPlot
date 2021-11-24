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
    private int cameraX, cameraY;
    private final int scaleX = 1;
    private final int scaleY = 1;
    private final int zoom = 1;
    private final int pixelsPerStep = 75;

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
                if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON1) {
                    dragging[0] = true;
                    startPos[0] = e.getXOnScreen();
                    startPos[1] = e.getYOnScreen();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON1) {
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
        g.setColor(minorGridColor);
        for (int i = cameraX - (cameraX % (pixelsPerStep / 5)); i < cameraX + getWidth(); i += pixelsPerStep / 5) {
            g.drawLine(i, cameraY, i, cameraY + getHeight());
        }

        for (int i = cameraY - (cameraY % (pixelsPerStep / 5)); i < cameraY + getHeight(); i += pixelsPerStep / 5) {
            g.drawLine(cameraX, i, cameraX + getWidth(), i);
        }

        // Major grid and steps
        for (int i = cameraX - (cameraX % pixelsPerStep); i < cameraX + getWidth(); i += pixelsPerStep) {
            // Major grid
            g.setColor(majorGridColor);
            g.drawLine(i, cameraY, i, cameraY + getHeight());

            // Step
            if (i != 0) {
                g.setColor(globalAxisColor);
                g.drawLine(i, -5, i, 5);
                String step = decimalFormat.format((((float) i / pixelsPerStep) / (scaleX * zoom)));
                g.drawString(step, i - fontMetrics.stringWidth(step) / 2, 7 + fontMetrics.getAscent());
            }
        }

        for (int i = cameraY - (cameraY % pixelsPerStep); i < cameraY + getHeight(); i += pixelsPerStep) {
            // Major grid
            g.setColor(majorGridColor);
            g.drawLine(cameraX, i, cameraX + getWidth(), i);

            // Step
            if (i != 0) {
                g.setColor(globalAxisColor);
                g.drawLine(-5, i, 5, i);
                String step = decimalFormat.format(-(((float) i / pixelsPerStep) / (scaleY * zoom)));
                g.drawString(step, -7 - fontMetrics.stringWidth(step), i + fontMetrics.getAscent() / 2);
            }
        }

        // Global axis
        g.setColor(globalAxisColor);
        g.drawLine(cameraX, 0, cameraX + getWidth(), 0);
        g.drawLine(0, cameraY, 0, cameraY + getHeight());
    }
}
