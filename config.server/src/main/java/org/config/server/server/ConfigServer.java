package org.config.server.server;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public class ConfigServer {

    private ClusterServer clusterServer;
    private ClientServer clientServer;

    public ConfigServer() {
        clusterServer = new ClusterServer("127.0.0.1", 8009);
        clientServer = new ClientServer("127.0.0.1", 8010);
    }

    public void start() {
        clusterServer.start();
        clientServer.start();
    }

    public void stop() {
        clusterServer.stop();
        clientServer.stop();
    }
}
