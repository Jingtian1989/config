package org.config.client.service;

import org.config.common.domain.ClientMessage;
import org.remote.common.client.Client;
import org.remote.common.client.ClientCallBack;
import org.remote.common.client.ClientFactory;
import org.remote.common.service.ProcessorRegistrar;
import org.remote.netty.client.NettyClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;

/**
 * Created by jingtian.zjt on 2014/12/14.
 */
public class ServerConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConnector.class);
    private static ServerConnector instance = new ServerConnector();

    private ClientFactory clientFactory;
    private Client client;
    private ProcessorRegistrar registrar;
    private ClientService clientService;

    public static ServerConnector getInstance() {
        return instance;
    }

    public ServerConnector() {
        this.clientFactory = new NettyClientFactory();
        this.registrar = new ProcessorRegistrar();
        this.clientService = new ClientService();
        this.registrar.registerProcessor(clientService);
    }

    private void ensureConnected() {
        if (client == null) {
            try {
                client = clientFactory.build("127.0.0.1", 8009, registrar);
            } catch (ConnectException e) {
                LOGGER.error("[CONFIG] failed to connect to 127.0.0.1:8009. exception:", e);
            }
        }
    }

    public void send(ClientMessage message) {
        ensureConnected();
        client.invoke(message, new ServerPushCallBack());
    }

    public class ServerPushCallBack implements ClientCallBack {

        @Override
        public void handleResponse(Object data) {

        }

        @Override
        public void handleException(Exception e) {

        }
    }
}
