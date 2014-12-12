package org.config.server.push;

import org.config.common.domain.ServerEvent;
import org.config.server.domain.Group;
import org.config.server.server.ClientConnection;
import org.config.server.store.DataKeeper;

import java.util.Map;

/**
 * Created by jingtian.zjt on 2014/12/12.
 */
public class DataPusher {

    private DataKeeper keeper;

    public void push(Group group, ClientConnection client) {
        Map<String, String> data = keeper.query(group);
        ServerEvent event = new ServerEvent(ServerEvent.SERVER_SUBSCRIBER_SUBSCRIBE_EVENT, data);
        client.getWriter().write(event);
    }

    public void push(Group group) {

    }
}
