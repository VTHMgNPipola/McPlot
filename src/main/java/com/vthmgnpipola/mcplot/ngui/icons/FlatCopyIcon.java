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

public class FlatCopyIcon extends FlatAbstractIcon {
    private final Path2D copy;

    public FlatCopyIcon() {
        super(16, 16, UIManager.getColor("Objects.Grey"));

        copy = new Path2D.Double();
        copy.setWindingRule(Path2D.WIND_EVEN_ODD);

        copy.moveTo(2, 1);
        copy.lineTo(11, 1);
        copy.lineTo(11, 3);
        copy.lineTo(4, 3);
        copy.lineTo(4, 11);
        copy.lineTo(2, 11);
        copy.closePath();

        copy.moveTo(5, 4);
        copy.lineTo(14, 4);
        copy.lineTo(14, 14);
        copy.lineTo(5, 14);
        copy.closePath();

        copy.moveTo(7, 6);
        copy.lineTo(12, 6);
        copy.lineTo(12, 7);
        copy.lineTo(7, 7);
        copy.closePath();

        copy.moveTo(7, 8);
        copy.lineTo(12, 8);
        copy.lineTo(12, 9);
        copy.lineTo(7, 9);
        copy.closePath();

        copy.moveTo(7, 10);
        copy.lineTo(12, 10);
        copy.lineTo(12, 11);
        copy.lineTo(7, 11);
        copy.closePath();
    }

    @Override
    protected void paintIcon(Component c, Graphics2D g) {
        g.fill(copy);
    }
}
