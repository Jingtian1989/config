package org.config.server.service;

import org.remote.common.annotation.TargetType;
import org.config.server.push.DataPusher;
import org.config.server.server.ClientConnection;
import org.config.server.domain.Group;
import org.config.server.store.DataKeeper;
import org.config.common.domain.ClientEvent;
import org.remote.common.server.Connection;
import org.remote.common.service.Processor;
import org.remote.common.service.ResponseWriter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */

@TargetType(value = ClientEvent.class)
public class ClientService implements Processor{

    private DataKeeper keeper;
    private DataPusher pusher;
    private ConcurrentHashMap<Connection, ClientConnection> natives;

    @Override
    public void handleRequest(Object o, ResponseWriter writer) {
        ClientEvent event = (ClientEvent)o;
        switch (event.getType()) {
            case ClientEvent.CLIENT_PUBLISHER_REGISTER_EVENT:
                handlePublisherRegisterRequest(event.getAttributes(), writer);
                break;
            case ClientEvent.CLIENT_PUBLISHER_PUBLISH_EVENT:
                handlePublisherPublishRequest(event.getAttributes(), writer);
                break;
            case ClientEvent.CLIENT_SUBSCRIBER_REGISTER_EVENT:
                handleSubscriberRegisterRequest(event.getAttributes(), writer);
                break;
        }
    }

    private void handleSubscriberRegisterRequest(Map<String, Object> data, ResponseWriter writer) {
        Group group = Group.getGroup((String)data.get("groupId"),(String)data.get("dataId"));
        ClientConnection client = addNativeClient(writer);
        keeper.addSubscriber(client, group, (String)data.get("clientId"));
        pusher.push(group, client);
    }


    private void handlePublisherRegisterRequest(Map<String, Object> data, ResponseWriter writer) {
        Group group = Group.getGroup((String)data.get("groupId"), (String)data.get("dataId"));
        ClientConnection client = addNativeClient(writer);
        keeper.addPublisher(client, group, (String)data.get("clientId"));
    }



    private void handlePublisherPublishRequest(Map<String, Object> data, ResponseWriter writer) {
        Group group = Group.getGroup((String)data.get("groupId"), (String)data.get("dataId"));
        ClientConnection client = addNativeClient(writer);
        keeper.publish(client, group, (String)data.get("clientId"), (String)data.get("data"), (Integer)data.get("version"));
    }

    private ClientConnection addNativeClient(ResponseWriter writer) {
        ClientConnection client = natives.get(writer.getConnection());
        if (client == null) {
            client = new ClientConnection(writer);
            natives.put(writer.getConnection(), client);
        }
        return client;
    }
}
