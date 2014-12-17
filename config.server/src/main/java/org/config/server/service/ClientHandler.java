package org.config.server.service;

import org.config.common.domain.ClientMessage;
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
public class ClientHandler extends SimpleChannelUpstreamHandler {

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e){
        Object message = e.getMessage();
        if (message instanceof ClientMessage) {
            ClientMessage clientMessage = (ClientMessage) message;
            handleMessage(clientMessage, ctx.getChannel());
        }
    }

    public void handleMessage(ClientMessage message, Channel channel) {
        for (MessageDigest digest : message.getDigests()) {
            switch (digest.getType()) {
                case ClientMessage.PUBLISHER_REGISTER_TYPE:
                    handlePublisherRegisterRequest(digest, channel);
                    break;
                case ClientMessage.PUBLISHER_PUBLISH_TYPE:
                    handlePublisherPublishRequest(digest, channel);
                    break;
                case ClientMessage.SUBSCRIBER_REGISTER_TYPE:
                    handleSubscriberRegisterRequest(digest, channel);
                    break;
            }
        }
    }

    private void handleSubscriberRegisterRequest(MessageDigest digest, Channel channel) {
        Group group = Group.getGroup(digest.get("group"), digest.get("dataId"));
        ClientConnection client = MemoryStore.getInstance().addNativeClient(channel);
        MemoryStore.getInstance().addSubscriber(client, group, digest.get("clientId"));
    }


    private void handlePublisherRegisterRequest(MessageDigest digest, Channel channel) {
        Group group = Group.getGroup(digest.get("group"), digest.get("dataId"));
        ClientConnection client = MemoryStore.getInstance().addNativeClient(channel);
        MemoryStore.getInstance().addPublisher(client, group, digest.get("clientId"));
    }

    private void handlePublisherPublishRequest(MessageDigest digest, Channel channel) {
        Group group = Group.getGroup(digest.get("group"), digest.get("dataId"));
        ClientConnection client = MemoryStore.getInstance().addNativeClient(channel);
        MemoryStore.getInstance().publish(client, group, digest.get("clientId"), digest.get("data"),
                Integer.parseInt(digest.get("version")));
    }
}
