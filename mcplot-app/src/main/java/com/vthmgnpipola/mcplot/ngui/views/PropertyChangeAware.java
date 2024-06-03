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

package com.vthmgnpipola.mcplot.ngui.views;

import com.vthmgnpipola.mcplot.ngui.controllers.MPropertyChangeEvent;

public interface PropertyChangeAware {
    void modelPropertyChange(MPropertyChangeEvent event);

    /**
     * Whenever a property is about to be changed in a model registered in the same controller that the property change
     * aware class is, this method is called, if the model so desires. If the change is allowed, this method should
     * return {@code null}. If the change is denied for any reason, the denial message should be returned. Any returned
     * value that is not {@code null} is considered a denial of the change.
     *
     * @param event Property change event that describes what property is about to change from which current value to
     *              what new value and in what model.
     * @return {@code null} if the change is accepted, the denial message otherwise.
     */
    String vetoablePropertyChange(MPropertyChangeEvent event);
}
