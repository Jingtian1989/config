package org.config.server.store;

import org.config.server.server.ClientConnection;
import org.config.server.domain.Group;

import java.util.Map;

/**
 * Created by jingtian.zjt on 2014/12/12.
 */
public class MemoryKeeper implements DataKeeper {


    @Override
    public void addPublisher(ClientConnection client, Group group, String clientId) {
        client.addPublisher(group, clientId);
    }

    @Override
    public void addSubscriber(ClientConnection client, Group group, String clientId) {
        client.addSubscriber(group, clientId);
    }

    @Override
    public void publish(ClientConnection client, Group group, String clientId, String data, int version) {

    }

    @Override
    public Map<String, String> query(Group group) {
        return null;
    }
}
