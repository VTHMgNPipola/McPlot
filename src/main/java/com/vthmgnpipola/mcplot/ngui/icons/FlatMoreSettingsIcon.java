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
import java.awt.geom.Ellipse2D;
import javax.swing.UIManager;

public class FlatMoreSettingsIcon extends FlatAbstractIcon {
    private final Ellipse2D.Float circle1;
    private final Ellipse2D.Float circle2;
    private final Ellipse2D.Float circle3;

    public FlatMoreSettingsIcon() {
        super(16, 16, UIManager.getColor("Objects.Grey"));

        circle1 = new Ellipse2D.Float(6.5f, 3, 3, 3);
        circle2 = new Ellipse2D.Float(6.5f, 7, 3, 3);
        circle3 = new Ellipse2D.Float(6.5f, 11, 3, 3);
    }

    @Override
    protected void paintIcon(Component component, Graphics2D g) {
        g.fill(circle1);
        g.fill(circle2);
        g.fill(circle3);
    }
}
