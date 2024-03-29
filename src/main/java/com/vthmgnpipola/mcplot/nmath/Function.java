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

import java.io.Serial;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * This class is used to store information about each function created by the user. It doesn't process any data,
 * except for the decomposition of the definition string into function name, variable name and formation law.
 */
public class Function implements Serializable, Comparable<Function> {
    @Serial
    private static final long serialVersionUID = -3144429444076124342L;

    private static final Pattern FUNCTION_PATTERN = Pattern.compile(" *[a-zA-Z]+[a-zA-Z0-9]* *\\( *[a-zA-Z]+ *\\)" +
            " *=[^=]+");

    private String definition;
    private transient int index;
    private transient String name, variableName, formationLaw;

    private final Constant domainStart;
    private final Constant domainEnd;

    public Function() {
        domainStart = new Constant();
        domainEnd = new Constant();
    }

    /**
     * The definition is the whole string entered by the user into a text field or from a file. It contains the
     * function name, the variable name and the formation law.
     * <p>
     * After the definition is altered it is decomposed into its parts. For example, the definition {@code f(x)=sin
     * (x)} would be decomposed into {@code name = "f"}, {@code variableName = "x"} and {@code formationLaw = "sin(x)"}.
     *
     * @return Returns the current function definition (not the formation law).
     */
    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
        doDecomposition();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * The domain start is the minimum value to be calculated for this function, if the range of the graph happens to
     * be lower than that. If you decide to plot the current curve of a charging inductor for example, any value
     * below {@code x < 0} are useless, so you can define the domain starting on 0 to save resources and make a
     * prettier visualization.
     * <p>
     * If necessary, the domain start and end values can be mathematical expressions using all the defined named
     * constants, since they themselves are just {@link Constant}s.
     *
     * @return Returns the domain start constant.
     */
    public Constant getDomainStart() {
        return domainStart;
    }

    /**
     * The domain end is the maximum value to be calculated for this function, if the range of the graph happens to
     * be higher than that. If you don't need to see anything after a certain value, for example you only need to see
     * {@code 2 * pi} of a sine wave or {@code 5 * tau} of the current curve of an inductor, you can set the
     * constant's value to that and nothing past it will be calculated or rendered.
     * <p>
     * If necessary, the domain start and end values can be mathematical expressions using all the defined named
     * constants, since they themselves are just {@link Constant}s.
     *
     * @return Return the domain end constant.
     */
    public Constant getDomainEnd() {
        return domainEnd;
    }

    /**
     * The name of the function are the characters before the first parenthesis. It is used when calling the function
     * from another function.
     *
     * @return Returns the name of the function. If the function definition has still not been decomposed, for
     * example after loading from a serialized file, it is decomposed now.
     */
    public String getName() {
        checkDecomposition();
        return name;
    }

    /**
     * The variable name is the, well, name of the variable used inside the function. So, for example, in the
     * function {@code f(x)=sin(x)}, {@code x} is the variable name, and is the value that will change with every
     * step in the graph when plotting the function.
     *
     * @return Returns the variable name of the function. If the function definition has still not been decomposed, for
     * example after loading from a serialized file, it is decomposed now.
     */
    public String getVariableName() {
        checkDecomposition();
        return variableName;
    }

    /**
     * The formation law is the "actual" function, that is, the mathematical expression that defines the function and
     * how it should be plotted. Every named constant and this function's variable can be used in the formation law.
     *
     * @return Returns the formation law of the function. If the function definition has still not been decomposed, for
     * example after loading from a serialized file, it is decomposed now.
     */
    public String getFormationLaw() {
        checkDecomposition();
        return formationLaw;
    }

    /**
     * Splits the definition string into the function name, variable name and formation law.
     */
    private void doDecomposition() {
        if (FUNCTION_PATTERN.matcher(definition).matches()) {
            String[] parts = definition.replace(" ", "").split("=");
            if (parts.length != 2) {
                return;
            }

            formationLaw = parts[1];

            name = parts[0].substring(0, parts[0].indexOf('('));
            variableName = parts[0].substring(parts[0].indexOf('(') + 1, parts[0].indexOf(')'));
        } else {
            name = null;
            variableName = "x";
            formationLaw = definition.replace(" ", "");
        }
    }

    /**
     * Decomposes the definition law only if the function name, variable name or formation law are null.
     */
    private void checkDecomposition() {
        if (name == null || variableName == null || formationLaw == null) {
            doDecomposition();
        }
    }

    @Override
    public int compareTo(Function o) {
        return Integer.compare(o.getIndex(), index);
    }
}
