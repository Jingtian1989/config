package org.config.server.domain;

/**
 * Created by jingtian.zjt on 2014/12/12.
 */
public class Group {

    private String groupId;
    private String dataId;

    public Group(String groupId, String dataId) {
        this.groupId = groupId;
        this.dataId = dataId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getDataId() {
        return dataId;
    }

    public static Group getGroup(String groupId, String dataId) {
        return null;
    }
}
