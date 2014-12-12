package org.config.server.server;

import org.config.common.Constants;
import org.config.common.util.NetUtil;
import org.config.server.domain.Data;
import org.config.server.domain.Group;
import org.remote.common.service.ResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jingtian.zjt on 2014/12/12.
 */
public class ClientConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConnection.class);

    private ResponseWriter writer;
    private ConcurrentHashMap<Group, Publisher> publishers;
    private ConcurrentHashMap<Group, Subscriber> subscribers;
    private String hostId;
    private String ip;

    public ClientConnection(ResponseWriter writer) {
        this.writer = writer;
        this.hostId = writer.getConnection().getRemoteAddress() + ":" + "8001";
        this.ip = NetUtil.getLocalAddress().getHostAddress();
        this.publishers = new ConcurrentHashMap<Group, Publisher>();
        this.subscribers = new ConcurrentHashMap<Group, Subscriber>();
    }

    public void addSubscriber(Group group, String clientId) {
        subscribers.put(group, new Subscriber(group, clientId));
    }

    public boolean addPublisher(Group group, String clientId) {
        Publisher publisher = publishers.get(group);

        if (publisher != null) {
            if (publisher.getClientId().equals(clientId)) {
                return true;
            } else {
                LOGGER.error("[CONFIG] conflict publisher register from "
                        + writer.getConnection().getRemoteAddress() + " clientId:" + clientId);
                return false;
            }
        }
        publisher = new Publisher(group, clientId);
        Data data = new Data(null, Constants.UNINIT_VERSION);
        publisher.setData(data);
        publishers.put(group, publisher);
        return true;
    }

    public void publish(Group group, String clientId, String data, int version) {
        Publisher publisher = publishers.get(group);
        if (publisher == null) {
            LOGGER.error("[CONFIG] not registered publisher clientId:" + clientId);
            return;
        }

        if (publisher.getData().getVersion() >= version) {
            LOGGER.error("[CONFIG] publish low version data clientId:" + clientId);
            return;
        }
        publisher.setData(new Data(data, version));
    }

    public ResponseWriter getWriter(){
        return writer;
    }

    public String getHostId() {
        return hostId;
    }

    public Data query(Group group) {
        return publishers.get(group).getData();
    }

    private static class Publisher {
        final Group group;
        final String clientId;
        Data data;

        public Publisher(Group group, String clientId) {
            this.group = group;
            this.clientId = clientId;
        }

        public Group getGroup() {
            return group;
        }

        public String getClientId() {
            return clientId;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public Data getData() {
            return data;
        }
    }

    private static class Subscriber {
        private final Group group;
        private final String clientId;

        public Subscriber(Group group, String clientId) {
            this.group = group;
            this.clientId = clientId;
        }

        public Group getGroup() {
            return group;
        }

        public String getClientId() {
            return clientId;
        }
    }

}
