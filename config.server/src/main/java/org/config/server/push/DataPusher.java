package org.config.server.push;

import org.config.common.domain.ServerEvent;
import org.config.server.server.ClientConnection;

import java.util.Map;

/**
 * Created by jingtian.zjt on 2014/12/12.
 */
public class DataPusher {

    public void push(Map<String, String> data, ClientConnection client) {
        ServerEvent event = new ServerEvent(ServerEvent.SERVER_SUBSCRIBER_SUBSCRIBE_EVENT, data);
        client.getWriter().write(event);
    }

    public void push(Map<String, String> data, ClientConnection[] clients) {
        for (ClientConnection client : clients) {
            push(data, client);
        }
    }
}
