package org.config.server.push;

import org.config.common.domain.MessageDigest;
import org.config.common.domain.ServerMessage;
import org.config.server.domain.Group;
import org.config.server.domain.Record;
import org.config.server.event.Event;
import org.config.server.event.EventListener;
import org.config.server.server.ClientConnection;
import org.config.server.store.MemoryStore;
import org.remote.common.client.ClientCallBack;
import org.remote.common.exception.CodecsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by jingtian.zjt on 2014/12/15.
 */
public class ClientPusher implements EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientPusher.class);

    private static final ClientPusher instance = new ClientPusher();

    public static ClientPusher getInstance() {
        return instance;
    }

    @Override
    public void event(Event event) {
        switch (event.getType()) {
            case Event.SUBSCRIBER_REGISTER_EVENT:
                schedule((Group) event.get("group"), (ClientConnection) event.get("client"));
                break;
            case Event.GROUPDATA_CHANGE_EVENT:
                schedule((Group) event.get("group"));
                break;
        }
    }

    private void schedule(Group group) {
        ServerMessage message = new ServerMessage();
        List<Record> records = MemoryStore.getInstance().query(group);
        ClientConnection[] clients = MemoryStore.getInstance().getClients();
        for (Record record : records) {
            MessageDigest digest = new MessageDigest(ServerMessage.SUBSCRIBER_SYNCHRONIZE_TYPE);
            digest.put("group", record.getGroup());
            digest.put("dataId", record.getDataId());
            digest.put("data", record.getData());
            message.addDigest(digest);
        }
        if (message.getDigests().size() > 0) {
            for (ClientConnection client : clients) {
                if (client.getWriter().getConnection().isConnected()) {
                    String clientId = null;
                    if ((clientId = client.hasSubscriber(group)) != null) {
                        message.setClientId(clientId);
                        try {
                            client.getWriter().request(message, new ClientPushCallBack());
                        } catch (CodecsException e) {
                            LOGGER.error("[CONFIG] encode request failed. exception:", e);
                        }
                    }
                }
            }
        }
    }

    private void schedule(Group group, ClientConnection client) {
        ServerMessage message = new ServerMessage();
        List<Record> records = MemoryStore.getInstance().query(group);
        for (Record record : records) {
            MessageDigest digest = new MessageDigest(ServerMessage.SUBSCRIBER_SYNCHRONIZE_TYPE);
            digest.put("group", record.getGroup());
            digest.put("dataId", record.getDataId());
            digest.put("data", record.getData());
            message.addDigest(digest);
        }
        if (message.getDigests().size() > 0) {
            if (client.getWriter().getConnection().isConnected()) {
                String clientId = null;
                if ((clientId = client.hasSubscriber(group)) != null) {
                    message.setClientId(clientId);
                    try {
                        client.getWriter().request(message, new ClientPushCallBack());
                    } catch (CodecsException e) {
                        LOGGER.error("[CONFIG] encode request failed. exception:", e);
                    }
                }
            }
        }
    }

    private class ClientPushCallBack implements ClientCallBack{

        @Override
        public void handleResponse(Object data) {

        }

        @Override
        public void handleException(Exception e) {

        }
    }
}
