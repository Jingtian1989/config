package org.config.server.server;

import org.config.common.Constants;
import org.config.common.util.NetUtil;
import org.config.server.domain.Group;
import org.config.server.domain.Record;
import org.remote.common.service.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jingtian.zjt on 2014/12/12.
 */
public class ClientConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConnection.class);

    private Writer writer;
    private ConcurrentHashMap<Group, Publisher> publishers;
    private ConcurrentHashMap<Group, Subscriber> subscribers;
    private String hostId;

    public ClientConnection(Writer writer) {
        InetSocketAddress address = (InetSocketAddress) writer.getConnection().getRemoteAddress();
        this.writer = writer;
        this.hostId = address.getAddress().getHostAddress() +":"+ address.getPort();
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
        publishers.put(group, publisher);
        return true;
    }

    public void publish(Group group, String clientId, String data, int version) {
        Publisher publisher = publishers.get(group);
        if (publisher == null) {
            LOGGER.error("[CONFIG] not registered publisher clientId:" + clientId);
            return;
        }

        if (publisher.getVersion() >= version) {
            LOGGER.error("[CONFIG] publish low version data clientId:" + clientId);
            return;
        }
        publisher.update(data, version);
    }

    public Writer getWriter(){
        return writer;
    }

    public String getHostId() {
        return hostId;
    }


    public String hasSubscriber(Group group) {
        Subscriber subscriber = subscribers.get(group);
        String clientId = null;
        if (subscriber != null) {
            clientId = subscriber.getClientId();
        }
        return clientId;
    }


    public Record query(Group group) {
        Publisher publisher = publishers.get(group);
        Record record = null;
        if (publisher != null) {
            record = new Record();
            record.setClientId(publisher.getClientId());
            record.setData(publisher.getData());
            record.setDataId(group.getDataId());
            record.setGroup(group.getGroup());
            record.setVersion(publisher.getVersion());
        }
        return record;
    }

    private static class Publisher {
        private Group group;
        private String clientId;
        private String data;
        private AtomicInteger version;

        public Publisher(Group group, String clientId) {
            this.group = group;
            this.clientId = clientId;
            this.data = null;
            this.version = new AtomicInteger(Constants.UNINIT_VERSION);
        }

        public Group getGroup() {
            return group;
        }

        public String getClientId() {
            return clientId;
        }

        public String getData() {
            return data;
        }

        public int getVersion() {
            return version.get();
        }

        public void update(String data, int version) {
            this.version.set(version);
            this.data = data;
        }
    }

    private static class Subscriber {
        private Group group;
        private String clientId;

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
