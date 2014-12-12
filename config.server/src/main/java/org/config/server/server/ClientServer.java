package org.config.server.server;


import org.config.server.service.ClientService;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public class ClientServer extends BaseServer {

    public ClientServer(String host, int port) {
        super(host, port);
        registerProcessor(new ClientService());
    }
}
