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

package com.vthmgnpipola.mcplot.ngui.models;

import com.vthmgnpipola.mcplot.ngui.controllers.AbstractController;
import com.vthmgnpipola.mcplot.ngui.controllers.MPropertyChangeCarrier;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeSupport;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractModel {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
    private final String name;
    private final String fullyQualifiedName;
    private final int id;
    protected PropertyChangeSupport propertyChangeSupport;
    protected VetoableChangeSupport vetoableChangeSupport;

    protected AbstractModel(String name) {
        this.name = name;
        propertyChangeSupport = new PropertyChangeSupport(this);
        vetoableChangeSupport = new VetoableChangeSupport(this);

        id = ID_GENERATOR.incrementAndGet();
        fullyQualifiedName = name + "." + id;
    }

    public void addPropertyChangeListener(AbstractController listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
        vetoableChangeSupport.addVetoableChangeListener(listener);
    }

    public void removePropertyChangeListener(AbstractController listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(new MPropertyChangeCarrier(this, propertyName, oldValue, newValue));
    }

    public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException {
        vetoableChangeSupport.fireVetoableChange(new MPropertyChangeCarrier(this, propertyName, oldValue, newValue));
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }
}
