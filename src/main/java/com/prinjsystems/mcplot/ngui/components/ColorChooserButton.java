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

package com.prinjsystems.mcplot.ngui.components;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JColorChooser;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class ColorChooserButton extends JButton {
    private Color selectedColor;
    private ColorChooserListener colorChooserListener;

    public ColorChooserButton() {
        addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, BUNDLE.getString("functionCard.selectColor"),
                    selectedColor);
            if (color != null) {
                selectedColor = color;
                if (colorChooserListener != null) {
                    colorChooserListener.colorChanged(color);
                }
            }
            repaint();
        });
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
        if (colorChooserListener != null) {
            colorChooserListener.colorChanged(selectedColor);
        }
    }

    public void setColorChooserListener(ColorChooserListener colorChooserListener) {
        this.colorChooserListener = colorChooserListener;
    }

    @Override
    protected void paintComponent(Graphics g) {
        paintComponents(g);

        int squareSize;
        if (getHeight() <= getWidth()) {
            squareSize = getHeight() / 2;
        } else {
            squareSize = getWidth() / 2;
        }
        g.setColor(selectedColor);
        g.fillRect((getWidth() / 2) - (squareSize / 2), (getHeight() / 2) - (squareSize / 2), squareSize,
                squareSize);
        g.setColor(Color.black);
        g.drawRect((getWidth() / 2) - (squareSize / 2), (getHeight() / 2) - (squareSize / 2), squareSize,
                squareSize);
    }
}
