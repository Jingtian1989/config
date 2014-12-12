package org.config.common;

import org.config.common.util.UUIDGenerator;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public class ClientRegistration {

    private String group;
    private String dataId;
    private String datumId;
    private String name;
    private int id;

    public ClientRegistration(String name, String group, String dataid, String datumId) {
        this.name = name;
        this.group = group;
        this.dataId = dataid;
        this.datumId = datumId;
        this.id = UUIDGenerator.get();
    }

    public String getClientId() {
        return name + "." + id;
    }
}
