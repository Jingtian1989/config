package org.config.client;

import org.remote.common.util.UUIDGenerator;

/**
 * Created by jingtian.zjt on 2014/12/11.
 */
public class ClientRegistration {

    private String name;
    private String group;
    private String dataId;
    private String clientId;

    public ClientRegistration(String name, String dataId, String group) {
        this.name = name;
        this.dataId = dataId;
        this.group = group;
        this.clientId = name + "." + UUIDGenerator.get();
    }

    public String getName() {
        return name;
    }

    public String getDataId() {
        return dataId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getGroup() {
        return group;
    }
}
