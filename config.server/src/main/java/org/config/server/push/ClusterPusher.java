package org.config.server.push;

import org.config.common.domain.ClusterMessage;
import org.config.common.domain.MessageDigest;
import org.config.server.domain.Record;
import org.config.server.event.Event;
import org.config.server.event.EventListener;
import org.config.server.server.ClientConnection;
import org.config.server.service.ClusterConfig;
import org.remote.common.client.Client;
import org.remote.common.client.ClientCallBack;

import java.util.List;

/**
 * Created by jingtian.zjt on 2014/12/16.
 */
public class ClusterPusher implements EventListener{

    private static final ClusterPusher instance = new ClusterPusher();

    private ClusterPusher() {
    }

    public static ClusterPusher getInstance() {
        return instance;
    }

    @Override
    public void event(Event event) {
        switch (event.getType()) {
            case Event.PUBLISHER_PUBLISH_EVENT:
                ClientConnection client = (ClientConnection) event.get("client");
                if (!client.isClusterClient()) {
                    schedule(client);
                }
                break;
        }
    }

    private void schedule(ClientConnection client) {
        List<Client> clusterClients = ClusterConfig.getInstance().getClusterClients();
        List<Record> records = client.query();
        ClusterMessage message = new ClusterMessage();
        for (Record record : records) {
            MessageDigest digest = new MessageDigest(ClusterMessage.CLUSTER_SYNC_TYPE);
            digest.put("group", record.getGroup());
            digest.put("clientId", record.getClientId());
            digest.put("data", record.getData());
            digest.put("version", String.valueOf(record.getVersion()));
            message.addDigest(digest);
        }
        message.setHostId(client.getHostId());
        for (Client clusterClient : clusterClients) {
            if (clusterClient.getConnection().isConnected()) {
                clusterClient.invoke(message, new ClusterPushCallBack());
            }
        }
    }

    public static class ClusterPushCallBack implements ClientCallBack {

        @Override
        public void handleResponse(Object data) {

        }

        @Override
        public void handleException(Exception e) {

        }
    }

}
