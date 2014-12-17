package org.config.server.store;

import org.config.server.domain.Group;
import org.config.server.event.Event;
import org.config.server.event.EventDispatcher;
import org.config.server.event.EventListener;
import org.config.server.service.ClientConnection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by jingtian.zjt on 2014/12/16.
 */
public class GroupQueue implements EventListener {

    private static final GroupQueue instance = new GroupQueue();
    private ConcurrentHashMap<Group, CopyOnWriteArrayList<ClientConnection>> contributors;
    private BlockingQueue<Event> events;
    private GroupQueueThread worker;

    private GroupQueue() {
        contributors = new ConcurrentHashMap<Group, CopyOnWriteArrayList<ClientConnection>>();
        events = new LinkedBlockingQueue<Event>();
        worker = new GroupQueueThread();
        worker.start();
    }

    public static GroupQueue getInstance() {
        return instance;
    }

    @Override
    public void handleEvent(Event event) {
        switch (event.getType()) {
            case Event.PUBLISHER_PUBLISH_EVENT:
            case Event.PUBLISHER_UNREGISTER_EVENT:
                events.offer(event);
                break;
        }
    }

    private void handlePublisherPublishEvent(Group group, ClientConnection client) {
        List<ClientConnection> contributors = getContributors(group);
        if (!contributors.contains(client)) {
            contributors.add(client);
        }
        Event event = new Event(Event.GROUP_DATA_CHANGE_EVENT);
        event.put("group", group);
        event.put("client", client);
        EventDispatcher.fire(event);
    }

    private void handlePublisherUnregisterEvent(Group group, ClientConnection client) {
        List<ClientConnection> contributors = getContributors(group);
        contributors.remove(client);
        Event event = new Event(Event.GROUP_DATA_CHANGE_EVENT);
        event.put("group", group);
        event.put("client", client);
        EventDispatcher.fire(event);
    }

    public List<ClientConnection> getContributors(Group group) {
        CopyOnWriteArrayList<ClientConnection> clients = contributors.get(group);
        if (clients == null) {
            clients = new CopyOnWriteArrayList<ClientConnection>();
            contributors.put(group, clients);
        }
        return clients;
    }

    private class GroupQueueThread extends Thread {

        public static final int TIMEOUT = 300;
        @Override
        public void run() {
            for (;;) {
                Event event = null;
                try {
                     event = events.poll(TIMEOUT, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                }
                if (event != null) {
                    switch (event.getType()) {
                        case Event.PUBLISHER_PUBLISH_EVENT:
                            handlePublisherPublishEvent((Group) event.get("group"), (ClientConnection) event.get("client"));
                            break;
                        case Event.PUBLISHER_UNREGISTER_EVENT:
                            handlePublisherUnregisterEvent((Group)event.get("group"), (ClientConnection)event.get("client"));
                    }
                }
            }
        }
    }


}
