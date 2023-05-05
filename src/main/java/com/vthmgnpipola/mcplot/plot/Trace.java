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

package com.vthmgnpipola.mcplot.plot;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;

public class Trace implements Serializable {
    @Serial
    private static final long serialVersionUID = -6529900873156715437L;

    private static final float[] DASHED_STROKE_DASH = new float[]{10f, 7f};
    private static final float[] DOTTED_STROKE_DASH = new float[]{3f, 7f};
    private static final float[] DASHED_DOTTED_STROKE_DASH = new float[]{10f, 7f, 3f, 7f};
    private static final float STROKE_MITER_LIMIT = 10f;

    /**
     * This is the trace width normally used by all functions. If the trace has the custom width flag set, however, it
     * will use its own independent trace width.
     */
    public static int DEFAULT_TRACE_WIDTH = 3;

    private TraceType type;
    private Color color;
    private int width;
    private boolean customWidth;

    private Stroke stroke;

    public Trace() {
        type = TraceType.TRACE_TYPE_DEFAULT;
        width = 3;
        customWidth = false;
        updateStroke();
    }

    public void updateStroke() {
        int width = customWidth ? this.width : DEFAULT_TRACE_WIDTH;
        if (type == TraceType.TRACE_TYPE_DEFAULT) {
            stroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        } else {
            float[] dash = switch (type) {
                case TRACE_TYPE_DASHED -> DASHED_STROKE_DASH;
                case TRACE_TYPE_DOTTED -> DOTTED_STROKE_DASH;
                case TRACE_TYPE_DASHED_DOTTED -> DASHED_DOTTED_STROKE_DASH;
                default -> null;
            };
            assert dash != null;
            stroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, STROKE_MITER_LIMIT,
                    dash, 0f);
        }
    }

    public TraceType getType() {
        return type;
    }

    public void setType(TraceType type) {
        this.type = type;
        updateStroke();
    }

    /**
     * The trace color is the color used when plotting the function into the screen.
     *
     * @return Return the current trace color.
     */
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * The trace width defines the "thickness" of the trace used in the graph. It is by default globally defined in the
     * graph settings for all functions, but setting the custom width flag the function can have an independent trace
     * width.
     *
     * @return Return the current trace width.
     */
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        updateStroke();
    }

    /**
     * Normally all functions will use the default trace width found in the graph settings. However, if the custom width
     * flag is set, the trace will use its own trace width when creating the trace's Stroke (obtainable from the
     * {@link #getStroke()} method).
     *
     * @return Return the custom width flag.
     */
    public boolean isCustomWidth() {
        return customWidth;
    }

    public void setCustomWidth(boolean customWidth) {
        this.customWidth = customWidth;
        updateStroke();
    }

    public Stroke getStroke() {
        return stroke;
    }
}
