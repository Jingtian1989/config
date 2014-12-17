package org.config.server.event;

/**
 * Created by jingtian.zjt on 2014/12/15.
 */
public interface EventListener extends java.util.EventListener{

    public void handleEvent(Event event);
}
