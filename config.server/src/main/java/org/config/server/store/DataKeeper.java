package org.config.server.store;

import org.config.server.server.ClientConnection;
import org.config.server.domain.Group;

import java.util.Map;


/**
 * Created by jingtian.zjt on 2014/12/12.
 */
public interface DataKeeper {

    public void addPublisher(ClientConnection client, Group group, String clientId);

    public void addSubscriber(ClientConnection client, Group group, String clientId);

    public void publish(ClientConnection client, Group group, String clientId, String data, int version);

    public Map<String, String> query(Group group);

}
