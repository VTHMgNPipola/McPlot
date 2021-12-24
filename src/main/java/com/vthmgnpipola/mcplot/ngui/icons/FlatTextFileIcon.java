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
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

public class FlatTextFileIcon extends FlatAbstractIcon {
    private final Path2D file;
    private final Path2D lines;

    private final Color secondary;

    public FlatTextFileIcon() {
        // This icon doesn't have a dark variant, so I used the colors directly
        super(16, 16, new Color(10135472));
        secondary = new Color(2301728);

        file = new Path2D.Double();
        file.moveTo(7, 1);
        file.lineTo(3, 5);
        file.lineTo(7, 5);
        file.closePath();

        file.moveTo(8, 1);
        file.lineTo(8, 6);
        file.lineTo(3, 6);
        file.lineTo(3, 15);
        file.lineTo(13, 15);
        file.lineTo(13, 1);
        file.closePath();

        lines = new Path2D.Double();
        lines.moveTo(5, 7);
        lines.lineTo(11, 7);
        lines.lineTo(11, 8);
        lines.lineTo(5, 8);
        lines.closePath();

        lines.moveTo(5, 9);
        lines.lineTo(11, 9);
        lines.lineTo(11, 10);
        lines.lineTo(5, 10);
        lines.closePath();

        lines.moveTo(5, 11);
        lines.lineTo(9, 11);
        lines.lineTo(9, 12);
        lines.lineTo(5, 12);
        lines.closePath();
    }

    @Override
    protected void paintIcon(Component c, Graphics2D g) {
        g.fill(file);

        g.setColor(secondary);
        g.fill(lines);
    }
}
