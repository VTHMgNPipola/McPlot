/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2022  VTHMgNPipola
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

import com.vthmgnpipola.mcplot.nmath.Constant;
import com.vthmgnpipola.mcplot.nmath.ConstantEvaluator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class GraphUnit implements Serializable {
    public static final GraphUnit CUSTOM_X_UNIT = new GraphUnit(BUNDLE.getString(
            "settings.plottingPanel.axisUnit.custom"), "", "");

    public static final GraphUnit DEFAULT = new GraphUnit(BUNDLE.getString("settings.plottingPanel.axisUnit.default"),
            "", "1");
    public static final GraphUnit PI = new GraphUnit(BUNDLE.getString("settings.plottingPanel.axisUnit.pi"),
            "π", "pi");
    public static final GraphUnit EULER = new GraphUnit(BUNDLE.getString("settings.plottingPanel.axisUnit.euler"),
            "e", "e");
    public static final GraphUnit CUSTOM_Y_UNIT = new GraphUnit(BUNDLE.getString(
            "settings.plottingPanel.axisUnit.custom"), "", "");
    @Serial
    private static final long serialVersionUID = -7412684447239118802L;
    private transient final String name;
    private transient ConstantEvaluator unitValueEvaluator;
    private String symbol;

    public GraphUnit(String name, String symbol, String definition) {
        this.name = name;
        this.symbol = symbol;

        unitValueEvaluator = new ConstantEvaluator(new Constant());
        unitValueEvaluator.setDefinition(definition);
    }

    public static String getString(GraphUnit unit) {
        if (unit == PI) {
            return "pi";
        } else if (unit == EULER) {
            return "euler";
        } else if (unit == CUSTOM_X_UNIT) {
            return "customx";
        } else if (unit == CUSTOM_Y_UNIT) {
            return "customy";
        } else {
            return "";
        }
    }

    public static GraphUnit getUnit(String str) {
        return switch (str.toLowerCase()) {
            case "pi" -> PI;
            case "euler" -> EULER;
            case "customx" -> CUSTOM_X_UNIT;
            case "customy" -> CUSTOM_Y_UNIT;
            default -> DEFAULT;
        };
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public ConstantEvaluator getUnitValueEvaluator() {
        return unitValueEvaluator;
    }

    public double getScale() {
        Double value = unitValueEvaluator.getConstant().getActualValue();
        return 1 / (value != null ? value : 1);
    }

    public String getTransformedUnit(double value, String text) {
        if (symbol.isBlank() || unitValueEvaluator.getConstant().getActualValue() == null) {
            return text;
        }

        if (value == 1) {
            return symbol;
        } else if (value == -1) {
            return "-" + symbol;
        } else {
            return text + symbol;
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public String getDefinition() {
        return unitValueEvaluator.getConstant().getDefinition();
    }

    public void setDefinition(String definition) {
        unitValueEvaluator.setDefinition(definition);
    }

    @Serial
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        symbol = ois.readUTF();

        unitValueEvaluator = new ConstantEvaluator(new Constant());
        unitValueEvaluator.setDefinition(ois.readUTF());
    }

    @Serial
    private void writeObject(ObjectOutputStream ois) throws ClassNotFoundException, IOException {
        ois.writeUTF(symbol);
        ois.writeUTF(unitValueEvaluator.getConstant().getDefinition());
    }
}
