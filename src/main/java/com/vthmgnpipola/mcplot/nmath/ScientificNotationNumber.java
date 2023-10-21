/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2023  VTHMgNPipola
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

package com.vthmgnpipola.mcplot.nmath;

public record ScientificNotationNumber(double base, long exponent) {
    public static ScientificNotationNumber fromDouble(double value) {
        double absValue = Math.abs(value);
        long exponent = 0;
        if (absValue >= 1) {
            while (absValue >= 10) {
                absValue /= 10;
                exponent++;
            }
        } else {
            while (absValue < 1) {
                absValue *= 10;
                exponent--;
            }
        }
        double base = value < 0 ? -absValue : absValue;
        return new ScientificNotationNumber(base, exponent);
    }
}
