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

package com.vthmgnpipola.mcplot.nmath;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class is used to store information about constants, which are, in McPlot at least, named mathematical
 * expressions that you can use in your functions to reduce the clutter and to be able to easily change one value for
 * another in many functions at the same time.
 */
public class Constant implements Serializable {
    @Serial
    private static final long serialVersionUID = -8235664400532015308L;

    private String name;
    private String definition;
    Double actualValue;

    /**
     * The name of the function is how it should be invoked in a function. Constants work just like normal variables
     * in function (internally they are, actually), so you use any constant by just invoking its name.
     *
     * @return Returns the name of this constant.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The definition of a constant is the mathematical expression that dictates its value. It cannot have variables
     * in it, since it is not a function and not a variable itself, but it can invoke any other constant and works
     * like any other fixed mathematical expression.
     *
     * @return Returns the definition of this function.
     */
    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    /**
     * The "actual value" is the calculated value for this function.
     * <p>
     * Since this class is used only for storing values it is not calculated here, instead a
     * {@link ConstantEvaluator} is used with an {@link MathEventStreamer} to determine the value of a constant after
     * the definition has been altered.
     *
     * @return Returns the last calculated value for this constant.
     */
    public Double getActualValue() {
        return actualValue;
    }
}
