package org.config.server.service;

import org.config.server.store.MemoryStore;
import org.remote.common.annotation.TargetType;
import org.remote.common.service.Processor;
import org.remote.common.service.Writer;
import org.config.common.domain.ClientMessage;
import org.config.common.domain.MessageDigest;
import org.config.server.server.ClientConnection;
import org.config.server.domain.Group;
import org.config.server.event.Event;
import org.config.server.event.EventDispatcher;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */

@TargetType(value = ClientMessage.class)
public class ServerService implements Processor{

    @Override
    public void handleMessage(Object o, Writer writer) {
        ClientMessage message = (ClientMessage) o;
        for (MessageDigest digest : message.getDigests()) {
            switch (digest.getType()) {
                case ClientMessage.PUBLISHER_REGISTER_TYPE:
                    handlePublisherRegisterRequest(digest, writer);
                    break;
                case ClientMessage.PUBLISHER_PUBLISH_TYPE:
                    handlePublisherPublishRequest(digest, writer);
                    break;
                case ClientMessage.SUBSCRIBER_REGISTER_TYPE:
                    handleSubscriberRegisterRequest(digest, writer);
                    break;
            }
        }
    }

    private void handleSubscriberRegisterRequest(MessageDigest digest, Writer writer) {
        Group group = Group.getGroup(digest.get("group"), digest.get("dataId"));
        ClientConnection client = MemoryStore.getInstance().addClient(writer);
        MemoryStore.getInstance().addSubscriber(client, group, digest.get("clientId"));
        Event event = new Event(Event.SUBSCRIBER_REGISTER_EVENT);
        event.put("group", group);
        event.put("client", client);
        EventDispatcher.getInstance().fire(event);
    }


    private void handlePublisherRegisterRequest(MessageDigest digest, Writer writer) {
        Group group = Group.getGroup(digest.get("group"), digest.get("dataId"));
        ClientConnection client = MemoryStore.getInstance().addClient(writer);
        MemoryStore.getInstance().addPublisher(client, group, digest.get("clientId"));
    }

    private void handlePublisherPublishRequest(MessageDigest digest, Writer writer) {
        Group group = Group.getGroup(digest.get("group"), digest.get("dataId"));
        ClientConnection client = MemoryStore.getInstance().addClient(writer);
        MemoryStore.getInstance().publish(client, group, digest.get("clientId"), digest.get("data"), Integer.parseInt(digest.get("version")));
        Event event = new Event(Event.PUBLISHER_PUBLISH_EVENT);
        event.put("group", group);
        event.put("client", client);
        EventDispatcher.getInstance().fire(event);
    }

}
