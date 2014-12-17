package org.config.server.event;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jingtian.zjt on 2014/12/15.
 */
public class Event {

    public static final int SUBSCRIBER_ADD_EVENT = 0x01;
    public static final int PUBLISHER_ADD_EVENT = 0x02;
    public static final int DATA_PUBLISH_EVENT = 0x03;
    public static final int GDATA_CHANGE_EVENT = 0x04;

    private int type;
    private Map<String, Object> attributes;

    public Event(int type) {
        this.type = type;
        this.attributes = new HashMap<String, Object>();
    }

    public void put(String key, Object value) {
        attributes.put(key, value);
    }

    public Object get(String key) {
        return attributes.get(key);
    }

    public int getType() {
        return type;
    }
}
