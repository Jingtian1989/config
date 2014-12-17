package org.config.server.service;

import org.config.common.domain.ClusterMessage;
import org.config.common.domain.MessageDigest;
import org.config.server.domain.Group;
import org.config.server.store.MemoryStore;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * Created by jingtian.zjt on 2014/12/17.
 */
public class ClusterHandler extends SimpleChannelUpstreamHandler {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Object message = e.getMessage();
        if (message instanceof ClusterMessage) {
            ClusterMessage clusterMessage = (ClusterMessage) message;
            handleMessage(clusterMessage, ctx.getChannel());
        }
    }

    public void handleMessage(Object o, Channel channel) {
        ClusterMessage message = (ClusterMessage) o;
        for (MessageDigest digest : message.getDigests()) {
            switch (digest.getType()) {
                case ClusterMessage.CLUSTER_SYNC_TYPE:
                    handlSyncRequest(digest, channel, message.getHostId());
                    break;
                case ClusterMessage.CLUSTER_DELAY_TYPE:
                    handleDelayRequest(digest, channel);
                    break;
                case ClusterMessage.CLUSTER_DELETE_TYPE:
                    handleDeleteRequest(digest, channel);
                    break;
            }
        }
    }

    private void handleDelayRequest(MessageDigest digest, Channel channel) {

    }

    private void handleDeleteRequest(MessageDigest digest, Channel channel) {

    }

    private void handlSyncRequest(MessageDigest digest, Channel channel, String hostId) {
        Group group = Group.getGroup(digest.get("group"), digest.get("dataId"));
        ClientConnection client = MemoryStore.getInstance().addClusterClient(channel, hostId);
        if (client.hasPublisher(group) == null) {
            client.addPublisher(group, digest.get("clientId"));
        }
        MemoryStore.getInstance().publish(client, group, digest.get("clientId"), digest.get("data"),
                Integer.parseInt(digest.get("version")));
    }

}
