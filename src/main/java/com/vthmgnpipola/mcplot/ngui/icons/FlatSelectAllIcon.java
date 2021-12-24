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

package com.vthmgnpipola.mcplot.ngui.icons;

import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import javax.swing.UIManager;

public class FlatSelectAllIcon extends FlatAbstractIcon {
    private final Path2D box;
    private final Path2D checkmark;

    public FlatSelectAllIcon() {
        super(16, 16, UIManager.getColor("Objects.Grey"));

        box = FlatUIUtils.createPath(2, 2, 12.071, 2, 10, 4, 4, 4, 4, 12, 12, 12, 12, 9.931, 14, 8, 14, 14,
                2, 14);
        checkmark = FlatUIUtils.createPath(14.457, 2, 16, 3.528, 8.711, 10.736, 5, 7.066, 6.496, 5.585,
                8.684, 7.773);
    }

    @Override
    protected void paintIcon(Component component, Graphics2D g) {
        g.fill(box);
        g.fill(checkmark);
    }
}
