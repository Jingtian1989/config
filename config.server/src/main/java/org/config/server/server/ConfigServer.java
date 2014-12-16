package org.config.server.server;


import org.config.server.event.EventDispatcher;
import org.config.server.push.ClientPusher;
import org.config.server.service.ServerService;
import org.config.server.store.GroupQueue;
import org.remote.common.service.ProcessorRegistrar;
import org.remote.netty.server.NettyServer;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public class ConfigServer {

    private NettyServer clientServer;

    public ConfigServer() {
        EventDispatcher.getInstance().registerListener(ClientPusher.getInstance());
        EventDispatcher.getInstance().registerListener(GroupQueue.getInstance());

        ProcessorRegistrar registrar = new ProcessorRegistrar();
        registrar.registerProcessor(new ServerService());
        clientServer = new NettyServer("127.0.0.1", 8009, registrar);
    }

    public void start() {
        clientServer.start();
    }

    public void stop() {
        clientServer.stop();
    }
}
