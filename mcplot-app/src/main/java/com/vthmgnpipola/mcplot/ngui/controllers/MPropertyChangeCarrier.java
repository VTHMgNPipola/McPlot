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

package com.vthmgnpipola.mcplot.ngui.controllers;

import com.vthmgnpipola.mcplot.ngui.models.AbstractModel;

import java.beans.PropertyChangeEvent;
import java.io.Serial;

public class MPropertyChangeCarrier extends PropertyChangeEvent {
    @Serial
    private static final long serialVersionUID = -6309844636747213020L;

    private final MPropertyChange propertyChange;

    /**
     * Creates an {@code MPropertyChangeCarrier}. This is used to carry the more descriptive {@link MPropertyChange}
     * through the standard JavaBeans API before being processed by the {@link AbstractController} implementation.
     *
     * @throws IllegalArgumentException If {@code source} is {@code null}
     */
    public MPropertyChangeCarrier(AbstractModel source, String propertyName, Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);
        propertyChange = new MPropertyChange(propertyName, oldValue, newValue);
    }

    public MPropertyChange getPropertyChange() {
        return propertyChange;
    }

    public AbstractModel getModel() {
        return (AbstractModel) getSource();
    }
}
