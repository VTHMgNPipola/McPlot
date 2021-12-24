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

public class FlatSettingsIcon extends FlatAbstractIcon {
    private final Path2D gear;

    public FlatSettingsIcon() {
        super(16, 16, UIManager.getColor("Objects.Grey"));

        gear = new Path2D.Double();
        gear.setWindingRule(Path2D.WIND_EVEN_ODD);

        gear.moveTo(12.7078144, 8.94092644);
        gear.lineTo(14.1860171, 10.0014962);
        gear.curveTo(13.9015285, 10.8814083, 13.4345167, 11.6792367, 12.8296171, 12.3503459);
        gear.lineTo(11.1720587, 11.6025852);
        gear.curveTo(10.7002906, 12.0182974, 10.1462196, 12.3427961, 9.53742767, 12.5484995);
        gear.lineTo(9.35682478, 14.3581758);
        gear.curveTo(8.91920787, 14.4511061, 8.46531382, 14.5, 8, 14.5);
        gear.curveTo(7.53468618, 14.5, 7.08079213, 14.4511061, 6.64317522, 14.3581758);
        gear.lineTo(6.46257231, 12.5484994);
        gear.curveTo(5.85378047, 12.3427959, 5.29970962, 12.0182972, 4.82794166, 11.6025851);
        gear.lineTo(3.1703829, 12.3503459);
        gear.curveTo(2.5654833, 11.6792367, 2.0984715, 10.8814083, 1.8139829, 10.0014962);
        gear.lineTo(3.29218604, 8.94092614);
        gear.curveTo(3.23171292, 8.63665953, 3.20000005, 8.32203335, 3.20000005, 8.00000024);
        gear.curveTo(3.20000005, 7.67796698, 3.23171295, 7.36334066, 3.29218612, 7.05907392);
        gear.lineTo(1.8139829, 5.99850385);
        gear.curveTo(2.0984715, 5.11859166, 2.5654833, 4.32076327, 3.1703829, 3.64965409);
        gear.lineTo(4.82794202, 4.39741509);
        gear.curveTo(5.29970989, 3.98170311, 5.85378059, 3.65720451, 6.46257226, 3.45150114);
        gear.lineTo(6.64317522, 1.64182422);
        gear.curveTo(7.08079213, 1.54889386, 7.53468618, 1.5, 8, 1.5);
        gear.curveTo(8.46531382, 1.5, 8.91920787, 1.54889386, 9.35682478, 1.64182422);
        gear.lineTo(9.53742772, 3.45150097);
        gear.curveTo(10.1462195, 3.65720431, 10.7002903, 3.98170292, 11.1720583, 4.39741495);
        gear.lineTo(12.8296171, 3.64965409);
        gear.curveTo(13.4345167, 4.32076327, 13.9015285, 5.11859166, 14.1860171, 5.99850385);
        gear.lineTo(12.7078143, 7.05907362);
        gear.curveTo(12.7682875, 7.36334046, 12.8000004, 7.67796687, 12.8000004, 8.00000024);
        gear.curveTo(12.8000004, 8.32203346, 12.7682875, 8.63665973, 12.7078144, 8.94092644);
        gear.closePath();

        gear.moveTo(7.99999976, 10.3003956);
        gear.curveTo(9.27025466, 10.3003956, 10.2999997, 9.27056196, 10.2999997, 8.00019773);
        gear.curveTo(10.2999997, 6.72983349, 9.27025466, 5.69999981, 7.99999976, 5.69999981);
        gear.curveTo(6.72974486, 5.69999981, 5.69999981, 6.72983349, 5.69999981, 8.00019773);
        gear.curveTo(5.69999981, 9.27056196, 6.72974486, 10.3003956, 7.99999976, 10.3003956);
        gear.closePath();
    }

    @Override
    protected void paintIcon(Component c, Graphics2D g) {
        g.fill(gear);
    }
}
