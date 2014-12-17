package org.config.server.event;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by jingtian.zjt on 2014/12/15.
 */
public class EventDispatcher {

    private static CopyOnWriteArrayList<EventListener> listeners = new CopyOnWriteArrayList<EventListener>();

    public static void fire(Event event) {
        for (EventListener listener : listeners) {
            listener.handleEvent(event);
        }
    }

    public static void registerListener(EventListener listener) {
        listeners.add(listener);
    }
}
