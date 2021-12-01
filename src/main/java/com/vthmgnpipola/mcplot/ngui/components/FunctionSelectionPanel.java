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

import com.vthmgnpipola.mcplot.nmath.Function;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class FunctionSelectionPanel extends JPanel {
    private final Map<String, Function> functionMap;
    private final Set<Function> selectedFunctions;

    private final List<JCheckBox> selectionBoxes;

    public FunctionSelectionPanel(Map<String, Function> functionMap) {
        super(new MigLayout());

        this.functionMap = functionMap;
        selectedFunctions = new HashSet<>(this.functionMap.size() + 10);

        selectionBoxes = new ArrayList<>();

        for (Function function : functionMap.values()) {
            if (function.getDefinition() == null || function.getDefinition().isBlank()) {
                continue;
            }

            JCheckBox selectFunction = new JCheckBox();
            add(selectFunction);
            selectionBoxes.add(selectFunction);
            selectFunction.addChangeListener(e -> {
                if (selectFunction.isSelected()) {
                    selectedFunctions.add(function);
                } else {
                    selectedFunctions.remove(function);
                }
            });

            ColorPanel colorPanel = new ColorPanel();
            add(colorPanel);
            colorPanel.setBackground(function.getTraceColor());

            JLabel definition = new JLabel(function.getDefinition());
            add(definition, "grow, wrap");
        }

        if (getComponentCount() == 0) {
            add(new JLabel(BUNDLE.getString("export.text.functions.noValidFunction")));
        }
    }

    public Set<Function> getSelectedFunctions() {
        return selectedFunctions;
    }

    public void selectAll() {
        selectionBoxes.forEach(checkbox -> checkbox.setSelected(true));
        selectedFunctions.clear();
        selectedFunctions.addAll(functionMap.values());
    }

    public void selectNone() {
        selectionBoxes.forEach(checkbox -> checkbox.setSelected(false));
        selectedFunctions.clear();
    }

    private static class ColorPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

            g.setColor(Color.black);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }
}
