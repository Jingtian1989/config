package org.config.server.service;

import org.config.common.domain.ClusterMessage;
import org.config.common.domain.MessageDigest;
import org.config.server.domain.Group;
import org.config.server.server.ClientConnection;
import org.config.server.store.MemoryStore;
import org.remote.common.annotation.TargetType;
import org.remote.common.service.Processor;
import org.remote.common.service.Writer;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
@TargetType(value = ClusterMessage.class)
public class ClusterService implements Processor{

    @Override
    public void handleMessage(Object o, Writer writer) {
        ClusterMessage message = (ClusterMessage) o;
        for (MessageDigest digest : message.getDigests()) {
            switch (digest.getType()) {
                case ClusterMessage.CLUSTER_SYNC_TYPE:
                    handlSyncRequest(digest, writer, message.getHostId());
                    break;
                case ClusterMessage.CLUSTER_DELAY_TYPE:
                    handleDelayRequest(digest, writer);
                    break;
                case ClusterMessage.CLUSTER_DELETE_TYPE:
                    handleDeleteRequest(digest, writer);
                    break;
            }
        }
    }

    private void handleDelayRequest(MessageDigest digest, Writer writer) {

    }

    private void handleDeleteRequest(MessageDigest digest, Writer writer) {

    }

    private void handlSyncRequest(MessageDigest digest, Writer writer, String hostId) {
        Group group = Group.getGroup(digest.get("group"), digest.get("dataId"));
        ClientConnection client = MemoryStore.getInstance().addClusterClient(writer, hostId);
        if (client.hasPublisher(group) == null) {
            client.addPublisher(group, digest.get("clientId"));
        }
        MemoryStore.getInstance().publish(client,group,digest.get("clientId"), digest.get("data"), Integer.parseInt(digest.get("version")));
    }
}
