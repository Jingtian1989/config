package org.config.server.event;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by jingtian.zjt on 2014/12/15.
 */
public class EventDispatcher {

    private static EventDispatcher instance = new EventDispatcher();
    private CopyOnWriteArrayList<EventListener> listeners;

    private EventDispatcher() {
        listeners = new CopyOnWriteArrayList<EventListener>();
    }

    public static EventDispatcher getInstance() {
        return instance;
    }

    public void fire(Event event) {
        for (EventListener listener : listeners) {
            listener.event(event);
        }
    }

    public void registerListener(EventListener listener) {
        listeners.add(listener);
    }
}
