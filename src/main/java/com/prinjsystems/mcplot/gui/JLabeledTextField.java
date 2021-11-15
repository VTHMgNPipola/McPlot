package com.prinjsystems.mcplot.gui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JTextField;

public class JLabeledTextField extends JTextField {
    private String placeholderText;
    private Color placeholderColor = Color.gray;

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (getText().isEmpty() && placeholderText != null) {
            Graphics2D g = (Graphics2D) graphics;
            g.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(placeholderColor);
            FontMetrics fontMetrics = g.getFontMetrics();
            // This gigantic thing will basically get how much is missing and add it to the position. It will pick the
            // absolute value of how far it is from the vertical center of the text field, and then add it to the
            // vertical center of the text field to get the position.
            double placeholderY = (double) (getHeight() / 2)
                    + Math.abs((double) (getHeight() / 2) - (fontMetrics.getAscent() + getInsets().top));
            g.drawString(placeholderText, getInsets().left, (int) placeholderY);
        }
    }

    public String getPlaceholderText() {
        return placeholderText;
    }

    public void setPlaceholderText(String placeholderText) {
        this.placeholderText = placeholderText;
    }

    public Color getPlaceholderColor() {
        return placeholderColor;
    }

    public void setPlaceholderColor(Color placeholderColor) {
        this.placeholderColor = placeholderColor;
    }
}
