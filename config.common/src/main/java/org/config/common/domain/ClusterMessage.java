package org.config.common.domain;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jingtian.zjt on 2014/12/14.
 */
public class ClusterMessage implements Serializable {

    private static final long serialVersionUID = 7933909043381885561L;

    public static final int SYNCHRONIZE_TYPE = 0x01;
    public static final int DELETE_TYPE = 0x02;
    public static final int DELAY_TYPE = 0x03;

    private List<MessageDigest> digests;

    public ClusterMessage() {
        this.digests = new LinkedList<MessageDigest>();
    }

    public void addDigest(MessageDigest digest) {
        digests.add(digest);
    }
}
