package org.config.server.service;

import org.jboss.netty.channel.Channel;

import java.util.List;

/**
 * Created by jingtian.zjt on 2014/12/16.
 */
public class ClusterConfig {

    private static final ClusterConfig instance = new ClusterConfig();

    public static ClusterConfig getInstance() {
        return instance;
    }

    public List<Channel> getClusterChannels() {
        return null;
    }
}
