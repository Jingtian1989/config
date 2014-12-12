package org.config.server.store;

import org.config.server.server.ClientConnection;
import org.config.server.domain.Group;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jingtian.zjt on 2014/12/12.
 */
public class MemoryKeeper implements DataKeeper {

    private Map<Group, List<ClientConnection>> contributers;

    public MemoryKeeper() {
        contributers = new ConcurrentHashMap<Group, List<ClientConnection>>();
    }

    private synchronized List<ClientConnection> getContributers(Group group) {
        List<ClientConnection> clients = contributers.get(group);
        if (clients == null) {
            clients = new LinkedList<ClientConnection>();
            contributers.put(group, clients);
        }
        return clients;
    }

    @Override
    public void addPublisher(ClientConnection client, Group group, String clientId) {
        List<ClientConnection> contributers = getContributers(group);
        if (!contributers.contains(client)) {
            contributers.add(client);
        }
        client.addPublisher(group, clientId);
    }

    @Override
    public void addSubscriber(ClientConnection client, Group group, String clientId) {
        client.addSubscriber(group, clientId);
    }

    @Override
    public void publish(ClientConnection client, Group group, String clientId, String data, int version) {
        client.publish(group, clientId, data, version);
    }

    @Override
    public Map<String, String> query(Group group) {
        Map<String, String> data = new HashMap<String, String>();
        List<ClientConnection> contributers = getContributers(group);
        for (ClientConnection client : contributers) {
            data.put(client.getHostId(), client.query(group).getData());
        }
        return data;
    }

}
