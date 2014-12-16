package org.config.common.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jingtian.zjt on 2014/12/14.
 */
public class MessageDigest implements Serializable{

    private static final long serialVersionUID = 2889936956861468592L;
    private int type;
    private Map<String, String> attributes;

    public MessageDigest(int type) {
        this.type = type;
        this.attributes = new HashMap<String, String>();
    }

    public void put(String key, String value) {
        attributes.put(key, value);
    }

    public String get(String key) {
        return attributes.get(key);
    }

    public int getType() {
        return type;
    }

}
