package com.prinjsystems.mcplot.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

public class PlottingPanelNew extends JPanel {
    private int cameraX, cameraY;
    private int scaleX, scaleY;
    private final int pixelsPerStep;

    public PlottingPanelNew() {
        setPreferredSize(new Dimension(800, 600));
        cameraX = -getPreferredSize().width / 2;
        cameraY = -getPreferredSize().height / 2;

        pixelsPerStep = 50;

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
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        // Background
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Camera translation
        g.translate(-cameraX, -cameraY);

        // Minor grid
        g.setColor(new Color(232, 232, 232));
        for (int i = cameraX - (cameraX % (pixelsPerStep / 5)); i < cameraX + getWidth(); i += pixelsPerStep / 5) {
            g.drawLine(i, cameraY, i, cameraY + getHeight());
        }

        for (int i = cameraY - (cameraY % (pixelsPerStep / 5)); i < cameraY + getHeight(); i += pixelsPerStep / 5) {
            g.drawLine(cameraX, i, cameraX + getWidth(), i);
        }

        // Major grid
        g.setColor(Color.lightGray);
        for (int i = cameraX - (cameraX % pixelsPerStep); i < cameraX + getWidth(); i += pixelsPerStep) {
            g.drawLine(i, cameraY, i, cameraY + getHeight());
        }

        for (int i = cameraY - (cameraY % pixelsPerStep); i < cameraY + getHeight(); i += pixelsPerStep) {
            g.drawLine(cameraX, i, cameraX + getWidth(), i);
        }

        // Global axis
        g.setColor(Color.black);
        g.drawLine(cameraX, 0, cameraX + getWidth(), 0);
        g.drawLine(0, cameraY, 0, cameraY + getHeight());
    }
}
