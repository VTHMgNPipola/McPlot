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

public class FlatAddFileIcon extends FlatAbstractIcon {
    private final Path2D.Double path;

    public FlatAddFileIcon() {
        super(16, 16, UIManager.getColor("Objects.Grey"));

        path = new Path2D.Double();
        path.moveTo(3, 5);
        path.lineTo(7, 1);
        path.lineTo(7, 5);
        path.closePath();

        path.moveTo(3, 6);
        path.lineTo(8, 6);
        path.lineTo(8, 1);
        path.lineTo(13, 1);
        path.lineTo(13, 7);
        path.lineTo(10, 7);
        path.lineTo(10, 10);
        path.lineTo(7, 10);
        path.lineTo(7, 14);
        path.lineTo(10, 14);
        path.lineTo(10, 15);
        path.lineTo(3, 15);
        path.closePath();

        path.moveTo(8, 11);
        path.lineTo(11, 11);
        path.lineTo(11, 8);
        path.lineTo(13, 8);
        path.lineTo(13, 11);
        path.lineTo(16, 11);
        path.lineTo(16, 13);
        path.lineTo(13, 13);
        path.lineTo(13, 16);
        path.lineTo(11, 16);
        path.lineTo(11, 13);
        path.lineTo(8, 13);
        path.closePath();
    }

    @Override
    protected void paintIcon(Component c, Graphics2D g) {
        g.fill(path);
    }
}
