package org.config.client.service;

import org.config.client.*;
import org.remote.common.annotation.TargetType;
import org.config.common.domain.MessageDigest;
import org.config.common.domain.ServerMessage;
import org.remote.common.service.Processor;
import org.remote.common.service.Writer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by jingtian.zjt on 2014/12/13.
 */
@TargetType(value = ServerMessage.class)
public class ClientService implements Processor {

    private BlockingQueue<ServerMessage> messages;
    private ClientUpdateThread worker;

    public ClientService() {
        this.messages = new LinkedBlockingQueue<ServerMessage>();
        this.worker = new ClientUpdateThread();
        this.worker.start();
    }

    @Override
    public void handleMessage(Object o, Writer responseWriter) {
        ServerMessage message = (ServerMessage) o;
        messages.offer(message);
    }

    public class ClientUpdateThread extends Thread {
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
                                    subscriber.setRegistered();
                                }
                                break;
                            case ServerMessage.PUBLISHER_REGISTER_TYPE:
                                Publisher publisher = PublisherRegistrar.query(message.getClientId());
                                if (publisher != null) {
                                    publisher.setRegistered();
                                }
                                break;
                            case ServerMessage.SUBSCRIBER_SYNCHRONIZE_TYPE:
                                subscriber = SubscriberRegistrar.query(message.getClientId());
                                if (subscriber != null) {
                                    subscriber.update(digest.get("group"), digest.get("dataId"), digest.get("data"));
                                }
                                break;
                        }
                    }
                }
            }
        }
    }


}
