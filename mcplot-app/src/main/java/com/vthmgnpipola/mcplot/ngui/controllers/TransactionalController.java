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

import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

public class TransactionalController extends AbstractController {
    private final Map<Integer, MPropertyChangeTransactionEvent> openTransactions;

    public TransactionalController() {
        super();
        openTransactions = new HashMap<>();
    }

    public void startTransaction(AbstractModel model) {
        openTransactions.put(model.getId(), new MPropertyChangeTransactionEvent(model));
    }

    public void endTransaction(AbstractModel model) {
        MPropertyChangeTransactionEvent transaction = openTransactions.remove(model.getId());
        for (PropertyChangeAware view : views) {
            view.modelPropertyChange(transaction);
        }
    }

    @Override
    public void propertyChange(MPropertyChangeCarrier event) {
        AbstractModel model = event.getModel();
        if (openTransactions.containsKey(model.getId())) {
            // If there is an open transaction for that model, adds the property change to the transaction
            openTransactions.get(model.getId()).addPropertyChange(event.getPropertyChange());
        } else {
            // If there isn't any open transaction for that model, propagates the property change to all views
            // immediately
            MSinglePropertyChangeEvent mEvent = new MSinglePropertyChangeEvent(model, event.getPropertyChange());

            for (PropertyChangeAware view : views) {
                view.modelPropertyChange(mEvent);
            }
        }
    }

    @Override
    public void vetoableChange(MPropertyChangeCarrier event) throws PropertyVetoException {
        MSinglePropertyChangeEvent mEvent = new MSinglePropertyChangeEvent(event.getModel(), event.getPropertyChange());

        for (PropertyChangeAware view : views) {
            String message = view.vetoablePropertyChange(mEvent);
            if (message != null) {
                throw new PropertyVetoException(message, event);
            }
        }
    }
}
