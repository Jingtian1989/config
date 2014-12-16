package org.config.test;

import org.config.server.server.ConfigServer;

/**
 * Created by jingtian.zjt on 2014/12/16.
 */
public class ServerBootstrap {

    public static void main(String args[]) {
        ConfigServer configServer = new ConfigServer();
        configServer.start();
    }
}
