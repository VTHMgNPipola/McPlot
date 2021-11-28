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

package com.vthmgnpipola.mcplot.ngui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JTextField;

public class JLabeledTextField extends JTextField {
    private String placeholderText;
    private Color placeholderColor = Color.gray;
    private FontMetrics fontMetrics;

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (getText().isEmpty() && placeholderText != null) {
            Graphics2D g = (Graphics2D) graphics;
            g.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(placeholderColor);
            if (fontMetrics == null) {
                fontMetrics = g.getFontMetrics();
            }
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

    @Override
    public void setFont(Font f) {
        super.setFont(f);
        fontMetrics = null;
    }
}
