package org.config.common.domain;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jingtian.zjt on 2014/12/14.
 */
public class ClientMessage implements Serializable {

    private static final long serialVersionUID = -2057424368270482764L;

    public static final int PUBLISHER_REGISTER_TYPE     = 0x01;
    public static final int SUBSCRIBER_REGISTER_TYPE    = 0x02;
    public static final int PUBLISHER_PUBLISH_TYPE      = 0x03;


    public static final int PUBLISHER_UNREGISTER_TYPE   = 0x04;
    public static final int SUBSCRIBER_UNREGISTER_TYPE  = 0x05;

    private List<MessageDigest> digests;

    public ClientMessage() {
        this.digests = new LinkedList<MessageDigest>();
    }

    public void addDigest(MessageDigest digest) {
        digests.add(digest);
    }

    public List<MessageDigest> getDigests() {
        return digests;
    }

}
