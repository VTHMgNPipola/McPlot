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

package com.vthmgnpipola.mcplot.messaging;

import java.util.ArrayList;
import java.util.List;

public final class MessageRouter {
    private static final MessageRouter INSTANCE = new MessageRouter();

    private final List<MessageConsumer> consumers;

    private MessageRouter() {
        consumers = new ArrayList<>();
    }

    public synchronized static MessagePublisher publisher() {
        return new MessagePublisher(INSTANCE);
    }

    public synchronized static void registerConsumer(MessageConsumer consumer) {
        INSTANCE.consumers.add(consumer);
    }

    public synchronized static void unregisterConsumer(MessageConsumer consumer) {
        INSTANCE.consumers.remove(consumer);
    }

    synchronized void publish(Message message) {
        consumers.forEach(c -> c.receive(message));
    }
}
