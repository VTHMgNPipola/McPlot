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

public interface EvaluationResultConsumer<I, R> {
    /**
     * Process an evaluation result consisting of an input and a result.
     *
     * @param input  Input that was used to evaluate the expression and generate the result
     * @param result Result of the evaluation
     * @throws IllegalStateException If the {@link #start()} method hasn't been called since the last the
     *                               {@link #complete()} method has been called
     */
    void accept(I input, R result) throws IllegalStateException;

    /**
     * Signals that the evaluation has ended successfully, and thus the accepted results can be used.
     *
     * @throws IllegalStateException If the {@link #start()} method hasn't been called since the last time this method
     *                               has been called
     */
    void complete() throws IllegalStateException;

    /**
     * Starts a new consumer session.
     * <p>
     * The {@link #accept(Object, Object)} and {@link #complete()} methods can only be called after this method has been
     * called at least once. After the {@link #complete()} or the {@link #invalidate()} method has been called, this
     * method needs to be called again to accept new results.
     */
    void start();

    /**
     * Invalidates (makes unusable) the currently accepted results and the current consumer session.
     */
    void invalidate();
}
