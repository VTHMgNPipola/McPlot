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
