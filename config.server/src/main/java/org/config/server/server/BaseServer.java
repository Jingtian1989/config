package org.config.server.server;

import org.remote.common.server.Server;
import org.remote.common.service.Processor;
import org.remote.common.service.ProcessorService;
import org.remote.netty.server.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public class BaseServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseServer.class);

    private Server server;
    private String host;
    private int port;
    private ProcessorService service;

    public BaseServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.service = new ProcessorService();
        this.server = new NettyServer(host, port, service);
    }

    public void registerProcessor(Processor processor) {
        service.registerProcessor(processor);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop();
    }

}
