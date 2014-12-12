package org.config.common.domain;

import org.remote.common.annotation.TargetType;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */

/**
 * 发布者注册消息结构
 * String, groupId
 * String, dataId
 * String, clientId
 *
 * 订阅者注册消息结构
 * String, groupId
 * String, dataId
 * String, clientId
 *
 * 发布者发布消息结构
 * String, groupId
 * String, dataId
 * String, clientId
 * String, data
 * int,    version
 */
@TargetType(value = ClientEvent.class)
public class ClientEvent implements Serializable {
    private static final long serialVersionUID = 2427671336713082629L;

    public static final int CLIENT_PUBLISHER_REGISTER_EVENT = 0x01;
    public static final int CLIENT_SUBSCRIBER_REGISTER_EVENT = 0x02;
    public static final int CLIENT_PUBLISHER_PUBLISH_EVENT = 0x03;


    private int type;
    private Map<String, Object> attributes;

    public ClientEvent(int type, Map<String, Object> attributes) {
        this.type = type;
        this.attributes = attributes;
    }

    public int getType() {
        return type;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

}
