package org.config.server.domain;

/**
 * Created by jingtian.zjt on 2014/12/12.
 */
public class Data {
    private String data;
    private int version;
    public Data (String data, int version) {
        this.data = data;
        this.version = version;
    }

    public String getData() {
        return data;
    }

    public int getVersion() {
        return version;
    }
}
