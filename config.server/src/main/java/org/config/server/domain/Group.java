package org.config.server.domain;

import org.config.common.Constants;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jingtian.zjt on 2014/12/12.
 */
public class Group {

    private static ConcurrentHashMap<Group, Group> groups = new ConcurrentHashMap<Group, Group>();

    private String group;
    private String dataId;
    private String signature;
    private AtomicInteger version;

    public Group(String group, String dataId) {
        this.group = group;
        this.dataId = dataId;
        this.signature = group + "#" + dataId;
        this.version = new AtomicInteger(Constants.UNINIT_VERSION);
    }

    public String getGroup() {
        return group;
    }

    public String getDataId() {
        return dataId;
    }

    public int getVersion() {
        return version.get();
    }

    public int increment() {
        return version.incrementAndGet();
    }

    @Override
    public boolean equals(Object object) {
        if ((object == null) || !(object instanceof Group)) {
            return false;
        }
        Group group = (Group) object;
        if (!group.getGroup().equals(group.getGroup()) || !group.getDataId().equals(dataId) ) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public int hashCode() {
        return signature.hashCode();
    }

    public static Group getGroup(String groupId, String dataId) {
        Group group = new Group(groupId, dataId);
        Group absent =  groups.putIfAbsent(group, group);
        return absent == null ? group : absent;
    }

}
