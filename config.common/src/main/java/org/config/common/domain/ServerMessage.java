package org.config.common.domain;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jingtian.zjt on 2014/12/14.
 */
public class ServerMessage implements Serializable{

    private static final long serialVersionUID = -674182451971395871L;

    public static final int PUBLISHER_REGISTER_TYPE     = 0x01;
    public static final int PUBLISHER_UNREGISTER_TYPE   = 0x02;
    public static final int SUBSCRIBER_REGISTER_TYPE    = 0X03;
    public static final int SUBSCRIBER_UNREGISTER_TYPE  = 0x04;
    public static final int SUBSCRIBER_SYNCHRONIZE_TYPE = 0x05;
    public static final int PUBLISHER_PUBLISH_TYPE      = 0x06;

    private List<MessageDigest> digests;

    private String clientId;

    public ServerMessage() {
        this.digests = new LinkedList<MessageDigest>();
    }

    public void addDigest(MessageDigest digest) {
        this.digests.add(digest);
    }

    public List<MessageDigest> getDigests() {
        return digests;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

}
