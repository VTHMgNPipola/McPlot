package com.prinjsystems.mcplot.ngui.components;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JColorChooser;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class ColorChooserButton extends JButton {
    private Color selectedColor;

    public ColorChooserButton() {
        addActionListener(e -> {
            selectedColor = JColorChooser.showDialog(null, BUNDLE.getString("functionCard.selectColor"),
                    selectedColor);
            repaint();
        });
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        paintComponents(g);

        int squareSize = getHeight() / 2;
        g.setColor(selectedColor);
        g.fillRect((getWidth() / 2) - (squareSize / 2), (getHeight() / 2) - (squareSize / 2), squareSize,
                squareSize);
        g.setColor(Color.black);
        g.drawRect((getWidth() / 2) - (squareSize / 2), (getHeight() / 2) - (squareSize / 2), squareSize,
                squareSize);
    }
}
