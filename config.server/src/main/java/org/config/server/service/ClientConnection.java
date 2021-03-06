package org.config.server.service;

import org.config.common.Constants;
import org.config.server.domain.Group;
import org.config.server.domain.Record;
import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jingtian.zjt on 2014/12/12.
 */
public class ClientConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConnection.class);

    private Channel channel;
    private ConcurrentHashMap<Group, Publisher> publishers;
    private ConcurrentHashMap<Group, Subscriber> subscribers;
    private String hostId;
    private boolean cluster;

    public ClientConnection(Channel channel) {
        InetSocketAddress address = (InetSocketAddress) channel.getRemoteAddress();
        this.channel = channel;
        this.publishers = new ConcurrentHashMap<Group, Publisher>();
        this.subscribers = new ConcurrentHashMap<Group, Subscriber>();
        this.hostId = address.getAddress().getHostAddress() +":"+ address.getPort();
        this.cluster = false;
    }

    public ClientConnection(Channel channel, String hostId) {
        this.channel = channel;
        this.publishers = new ConcurrentHashMap<Group, Publisher>();
        this.subscribers = new ConcurrentHashMap<Group, Subscriber>();
        this.hostId = hostId;
        this.cluster = true;
    }


    public boolean addSubscriber(Group group, String clientId) {
        subscribers.put(group, new Subscriber(group, clientId));
        return true;
    }

    public void deleteSubscriber(Group group, String clientId) {
        subscribers.remove(group);
    }

    public boolean addPublisher(Group group, String clientId) {
        Publisher publisher = publishers.get(group);

        if (publisher != null) {
            if (publisher.getClientId().equals(clientId)) {
                return true;
            } else {
                LOGGER.error("[CONFIG] conflict publisher register from "
                        + channel.getRemoteAddress() + " clientId:" + clientId);
                return false;
            }
        }
        publisher = new Publisher(group, clientId);
        publishers.put(group, publisher);
        return true;
    }

    public void deletePublisher(Group group, String clientId) {
        publishers.remove(group);
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

    public Channel getChannel(){
        return channel;
    }

    public String getHostId() {
        return hostId;
    }

    public boolean isClusterClient() {
        return cluster;
    }

    public String hasSubscriber(Group group) {
        Subscriber subscriber = subscribers.get(group);
        String clientId = null;
        if (subscriber != null) {
            clientId = subscriber.getClientId();
        }
        return clientId;
    }

    public String hasPublisher(Group group) {
        Publisher publisher = publishers.get(group);
        String clientId = null;
        if (publisher != null) {
            clientId = publisher.getClientId();
        }
        return clientId;
    }

    public Record query(Group group) {
        Publisher publisher = publishers.get(group);
        Record record = null;
        if (publisher != null) {
            record = new Record();
            record.setGroup(group.getGroup());
            record.setDataId(group.getDataId());
            record.setClientId(publisher.getClientId());
            record.setData(publisher.getData());
            record.setVersion(publisher.getVersion());
        }
        return record;
    }

    public List<Record> query() {
        List<Record> records = new LinkedList<Record>();
        for (Map.Entry<Group, Publisher> entry : publishers.entrySet()) {
            Group group = entry.getKey();
            Publisher publisher = entry.getValue();
            Record record = new Record();
            record.setGroup(group.getGroup());
            record.setDataId(group.getDataId());
            record.setClientId(publisher.getClientId());
            record.setData(publisher.getData());
            record.setVersion(publisher.getVersion());
            records.add(record);
        }
        return records;
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
