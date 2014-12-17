package org.config.client;

import org.config.common.domain.MessageDigest;
import org.config.common.domain.ServerMessage;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by jingtian.zjt on 2014/12/17.
 */
public class ClientHandler extends SimpleChannelUpstreamHandler {

    private BlockingQueue<ServerMessage> messages;
    private ClientUpdateThread worker;


    public ClientHandler() {
        this.messages = new LinkedBlockingQueue<ServerMessage>();
        this.worker = new ClientUpdateThread();
        this.worker.start();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Object object = e.getMessage();
        if (object instanceof ServerMessage) {
            ServerMessage message = (ServerMessage) object;
            messages.offer(message);
        }
    }

    private class ClientUpdateThread extends Thread {
        @Override
        public void run() {
            for(;;) {
                ServerMessage message = messages.poll();
                if (message != null) {
                    for (MessageDigest digest : message.getDigests()) {
                        switch (digest.getType()) {
                            case ServerMessage.SUBSCRIBER_REGISTER_TYPE:
                                Subscriber subscriber = SubscriberRegistrar.query(message.getClientId());
                                if (subscriber != null) {
                                    subscriber.setState(ConfigClient.CLIENT_REGISTERED);
                                }
                                break;
                            case ServerMessage.PUBLISHER_REGISTER_TYPE:
                                Publisher publisher = PublisherRegistrar.query(message.getClientId());
                                if (publisher != null) {
                                    publisher.setState(ConfigClient.CLIENT_REGISTERED);
                                }
                                break;
                            case ServerMessage.SUBSCRIBER_SYNCHRONIZE_TYPE:
                                subscriber = SubscriberRegistrar.query(message.getClientId());
                                if (subscriber != null) {
                                    subscriber.update(digest.get("group"), digest.get("dataId"), digest.get("data"));
                                }
                                break;
                            case ServerMessage.PUBLISHER_PUBLISH_TYPE:
                                publisher = PublisherRegistrar.query(message.getClientId());
                                if (publisher != null) {
                                    publisher.update(digest.get("group"), digest.get("dataId"), Integer.parseInt(digest.get("version")));
                                }
                                break;
                        }
                    }
                }
            }
        }
    }
}
