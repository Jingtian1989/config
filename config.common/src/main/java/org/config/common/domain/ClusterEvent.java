package org.config.common.domain;

import java.io.Serializable;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public class ClusterEvent implements Serializable{

    private static final long serialVersionUID = -3537967919119550926L;
    public static final int CLUSTER_SYNC_EVENT = 0x01;
    public static final int CLUSTER_DELETE_EVENT = 0x02;
    public static final int CLUSTER_DELAY_EVENT = 0x03;

    private int type;
    private Object data;

    public ClusterEvent(int type, Object data) {
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
