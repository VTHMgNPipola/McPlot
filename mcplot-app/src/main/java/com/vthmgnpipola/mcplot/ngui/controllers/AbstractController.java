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
import com.vthmgnpipola.mcplot.ngui.views.PropertyChangeAware;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractController implements PropertyChangeListener, VetoableChangeListener {
    protected final List<AbstractModel> models;
    protected final List<PropertyChangeAware> views;

    public AbstractController() {
        models = new ArrayList<>();
        views = new ArrayList<>();
    }

    public void addModel(AbstractModel model) {
        model.addPropertyChangeListener(this);
        models.add(model);
    }

    public void removeModel(AbstractModel model) {
        model.removePropertyChangeListener(this);
        models.remove(model);
    }

    public void addView(PropertyChangeAware view) {
        views.add(view);
    }

    public void removeView(PropertyChangeAware view) {
        views.remove(view);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event instanceof MPropertyChangeCarrier carrier) {
            propertyChange(carrier);
        } else {
            throw new IllegalArgumentException("The only supported property change event is MPropertyChangeCarrier! " +
                    "Unsupported property change received: " + event);
        }
    }

    @Override
    public void vetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
        if (event instanceof MPropertyChangeCarrier carrier) {
            vetoableChange(carrier);
        } else {
            throw new IllegalArgumentException("The only supported property change event is MPropertyChangeCarrier! " +
                    "Unsupported property change received: " + event);
        }
    }

    public abstract void propertyChange(MPropertyChangeCarrier event);

    public abstract void vetoableChange(MPropertyChangeCarrier event) throws PropertyVetoException;
}
