package org.config.server.server;


import org.config.server.event.EventDispatcher;
import org.config.server.push.ClientPusher;
import org.config.server.push.ClusterPusher;
import org.config.server.service.ClusterService;
import org.config.server.service.ServerService;
import org.config.server.store.GroupQueue;
import org.remote.common.service.ProcessorRegistrar;
import org.remote.netty.server.NettyServer;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public class ConfigServer {

    private NettyServer clientServer;

    private NettyServer clusterServer;

    public ConfigServer() {
        EventDispatcher.getInstance().registerListener(ClusterPusher.getInstance());
        EventDispatcher.getInstance().registerListener(ClientPusher.getInstance());
        EventDispatcher.getInstance().registerListener(GroupQueue.getInstance());

        ProcessorRegistrar clientRegister = new ProcessorRegistrar();
        clientRegister.registerProcessor(new ServerService());
        clientServer = new NettyServer("127.0.0.1", 8009, clientRegister);

        ProcessorRegistrar clusterRegister = new ProcessorRegistrar();
        clusterRegister.registerProcessor(new ClusterService());
        clusterServer = new NettyServer("127.0.0.1", 8010, clusterRegister);
    }

    public void start() {
        clientServer.start();
        clusterServer.start();
    }

    public void stop() {
        clientServer.stop();
        clusterServer.stop();
    }
}
