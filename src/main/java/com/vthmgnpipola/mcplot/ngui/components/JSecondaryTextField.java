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

package com.vthmgnpipola.mcplot.ngui.components;

import java.awt.*;

/**
 * This text field is called "secondary" because it displays a secondary text in a different color in front of the
 * input.
 */
public class JSecondaryTextField extends JLabeledTextField {
    private String secondaryText;

    @Override
    protected synchronized void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (!getText().isEmpty() && secondaryText != null) {
            Graphics2D g = (Graphics2D) graphics;
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(placeholderColor);

            if (secondaryText != null) {
                double secondaryY = (double) fontMetrics.getAscent() + getInsets().top;
                double secondaryX = getInsets().left + fontMetrics.stringWidth(getText());
                double secondaryWidth = fontMetrics.stringWidth(secondaryText);
                if (secondaryX + secondaryWidth > getWidth() - getInsets().right) {
                    secondaryX = getWidth() - getInsets().left - secondaryWidth;
                }
                g.drawString(secondaryText, (int) secondaryX, (int) secondaryY);
            }
        }
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }
}
