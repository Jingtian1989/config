package org.config.server.store;

import org.config.server.domain.Record;
import org.config.server.event.Event;
import org.config.server.event.EventDispatcher;
import org.config.server.service.ClientConnection;
import org.config.server.domain.Group;
import org.jboss.netty.channel.Channel;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jingtian.zjt on 2014/12/12.
 */
public class MemoryStore {

    private static final MemoryStore instance = new MemoryStore();
    private ConcurrentHashMap<Channel, ClientConnection> natives;
    private ConcurrentHashMap<Channel, ClientConnection> clusters;

    public MemoryStore() {
        this.natives = new ConcurrentHashMap<Channel, ClientConnection>();
        this.clusters = new ConcurrentHashMap<Channel, ClientConnection>();
    }

    public static MemoryStore getInstance() {
        return instance;
    }

    public void addPublisher(ClientConnection client, Group group, String clientId) {
        client.addPublisher(group, clientId);
    }

    public void addSubscriber(ClientConnection client, Group group, String clientId) {
        client.addSubscriber(group, clientId);
        Event event = new Event(Event.SUBSCRIBER_ADD_EVENT);
        event.put("group", group);
        event.put("client", client);
        EventDispatcher.getInstance().fire(event);
    }

    public void publish(ClientConnection client, Group group, String clientId, String data, int version) {
        client.publish(group, clientId, data, version);
        Event event = new Event(Event.DATA_PUBLISH_EVENT);
        event.put("group", group);
        event.put("client", client);
        EventDispatcher.getInstance().fire(event);
    }

    public List<Record> query(Group group) {
        List<Record> records = new LinkedList<Record>();
        List<ClientConnection> contributors = GroupQueue.getInstance().getContributors(group);
        for (ClientConnection client : contributors) {
            Record record = client.query(group);
            if (record != null) {
                records.add(record);
            }
        }
        return records;
    }

    public ClientConnection addNativeClient(Channel channel) {
        ClientConnection client = natives.get(channel);
        if (client == null) {
            client = new ClientConnection(channel);
            natives.put(channel, client);
        }
        return client;
    }

    public ClientConnection addClusterClient(Channel channel, String hostId) {
        ClientConnection client = clusters.get(channel);
        if (client == null) {
            client = new ClientConnection(channel, hostId);
            clusters.put(channel, client);
        }
        return client;
    }

    public ClientConnection[] getNativeClients() {
        return natives.values().toArray(new ClientConnection[0]);
    }

    public ClientConnection[] getClusterClients() {
        return clusters.values().toArray(new ClientConnection[0]);
    }
}
