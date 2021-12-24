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

public class FlatApplyIcon extends FlatAbstractIcon {
    private final Path2D checkmark;

    public FlatApplyIcon() {
        super(16, 16, UIManager.getColor("Objects.Grey"));

        checkmark = FlatUIUtils.createPath(13.789, 2.09, 15.535, 3.837, 6.292, 13.08, 1.95, 8.738, 3.698, 6.99,
                6.293, 9.585);
    }

    @Override
    protected void paintIcon(Component c, Graphics2D g) {
        g.fill(checkmark);
    }
}
