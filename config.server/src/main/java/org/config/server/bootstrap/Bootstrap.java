package org.config.server.bootstrap;

import org.config.server.event.EventDispatcher;
import org.config.server.push.ClientPusher;
import org.config.server.push.ClusterPusher;
import org.config.server.service.ClientServer;
import org.config.server.service.ClusterServer;

/**
 * Created by jingtian.zjt on 2014/12/17.
 */
public class Bootstrap {

    public static void main(String args[]) {
        EventDispatcher.getInstance().registerListener(ClientPusher.getInstance());
        EventDispatcher.getInstance().registerListener(ClusterPusher.getInstance());

        ClientServer clientServer = new ClientServer("127.0.0.1", 8009);
        clientServer.start();

        ClusterServer clusterServer = new ClusterServer("127.0.0.1", 8010);
        clusterServer.start();
    }
}
