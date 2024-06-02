/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2024  VTHMgNPipola
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

package com.vthmgnpipola.mcplot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class StringNumberUtils {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#####");

    public static String getFormattedDouble(double value, boolean showDecAsFractions) {
        if (showDecAsFractions) {
            StringBuilder sb = new StringBuilder();
            int intValue = (int) (value > 0 ? Math.floor(value) : Math.ceil(value));
            if (intValue != value && intValue == -1) {
                sb.append("-");
            } else if (intValue != 0) {
                sb.append(DECIMAL_FORMAT.format(intValue));
            }

            intValue = Math.abs(intValue);
            BigDecimal difference = new BigDecimal(String.valueOf(Math.abs(value)));
            difference = difference.subtract(BigDecimal.valueOf(intValue));
            if (difference.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal denominator = BigDecimal.ONE.divide(difference, difference.scale() * 2,
                        RoundingMode.HALF_UP);
                if (denominator.intValue() == denominator.doubleValue()) {
                    sb.append("¹⁄").append(toSubscriptDigits(String.valueOf(denominator.intValue())));
                } else {
                    int divisor = 1;
                    BigDecimal result;
                    do {
                        divisor++;
                        BigDecimal scaledDenominator = difference.divide(BigDecimal.valueOf(divisor),
                                RoundingMode.HALF_UP);
                        result = BigDecimal.ONE.divide(scaledDenominator, scaledDenominator.scale() * 2,
                                RoundingMode.HALF_UP);
                    } while (result.intValue() != result.doubleValue());

                    sb.append(toSuperscriptDigits(String.valueOf(divisor))).append("⁄")
                            .append(toSubscriptDigits(String.valueOf(result.intValue())));
                }
            }

            return sb.toString();
        } else {
            return DECIMAL_FORMAT.format(value);
        }
    }

    public static String toSuperscriptDigits(String str) {
        final char[] replaced = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'};
        final char[] replacement = new char[]{'⁰', '¹', '²', '³', '⁴', '⁵', '⁶',
                '⁷', '⁸', '⁹', '⁻'};
        return fastReplace(str, replaced, replacement);
    }

    public static String toSubscriptDigits(String str) {
        final char[] replaced = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'};
        final char[] replacement = new char[]{'₀', '₁', '₂', '₃', '₄', '₅', '₆',
                '₇', '₈', '₉', '₋'};
        return fastReplace(str, replaced, replacement);
    }

    public static String fastReplace(String input, char[] replaced, char[] replacement) {
        assert replaced.length == replacement.length;
        char[] cs = input.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            for (int j = 0; j < replaced.length; j++) {
                if (cs[i] == replaced[j]) {
                    cs[i] = replacement[j];
                }
            }
        }
        return new String(cs);
    }
}
