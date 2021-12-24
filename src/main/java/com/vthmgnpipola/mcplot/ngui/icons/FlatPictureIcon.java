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

public class FlatPictureIcon extends FlatAbstractIcon {
    private final Path2D p;

    public FlatPictureIcon() {
        super(12, 12, UIManager.getColor("Objects.Grey"));

        p = new Path2D.Double();
        p.setWindingRule(Path2D.WIND_EVEN_ODD);

        p.moveTo(1, 1);
        p.lineTo(15, 1);
        p.lineTo(15, 15);
        p.lineTo(1, 15);
        p.closePath();

        p.moveTo(2, 11.182);
        p.lineTo(5, 6.886);
        p.lineTo(7.143, 9.76);
        p.lineTo(10.143, 5.455);
        p.lineTo(14, 11.182);
        p.closePath();
    }

    @Override
    protected void paintIcon(Component c, Graphics2D g) {
        g.fill(p);
    }
}
