package org.config.server.server;

import org.config.server.service.ClusterService;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public class ClusterServer extends BaseServer{

    public ClusterServer(String host, int port) {
        super(host, port);
        registerProcessor(new ClusterService());
    }
}
