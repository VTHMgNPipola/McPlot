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
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import javax.swing.UIManager;

public class FlatHelpIcon extends FlatAbstractIcon {
    private final Path2D mark;

    public FlatHelpIcon() {
        super(16, 16, UIManager.getColor("Objects.Grey"));

        mark = new Path2D.Double();
        mark.moveTo(7, 14);
        mark.lineTo(9, 14);
        mark.lineTo(9, 12);
        mark.lineTo(7, 12);
        mark.lineTo(7, 14);
        mark.closePath();

        mark.moveTo(8, 2);
        mark.curveTo(5.79, 2, 4, 3.79, 4, 6);
        mark.lineTo(6, 6);
        mark.curveTo(6, 4.9, 6.9, 4, 8, 4);
        mark.curveTo(9.1, 4, 10, 4.9, 10, 6);
        mark.curveTo(10, 8, 7, 7.75, 7, 11);
        mark.lineTo(9, 11);
        mark.curveTo(9, 8.75, 12, 8.5, 12, 6);
        mark.curveTo(12, 3.79, 10.21, 2, 8, 2);
        mark.closePath();
    }

    @Override
    protected void paintIcon(Component c, Graphics2D g) {
        g.fill(mark);
    }
}
