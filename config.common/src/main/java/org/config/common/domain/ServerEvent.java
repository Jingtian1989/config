package org.config.common.domain;

import java.io.Serializable;

/**
 * Created by jingtian.zjt on 2014/12/12.
 */
public class ServerEvent implements Serializable{

    private static final long serialVersionUID = 6348450016337299099L;

    public static final int SERVER_PUBLISHER_REGISTER_EVENT = 0x01;
    public static final int SERVER_SUBSCRIBER_REGISTER_EVENT = 0x02;
    public static final int SERVER_SUBSCRIBER_SUBSCRIBE_EVENT = 0x03;

    private int type;
    private Object data;

    public ServerEvent(int type, Object data) {
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

}
