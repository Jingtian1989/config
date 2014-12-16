package org.config.server.store;

import org.config.server.domain.Group;
import org.config.server.event.Event;
import org.config.server.event.EventDispatcher;
import org.config.server.event.EventListener;
import org.config.server.server.ClientConnection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by jingtian.zjt on 2014/12/16.
 */
public class GroupQueue implements EventListener {

    private static final GroupQueue instance = new GroupQueue();

    private ConcurrentHashMap<Group, CopyOnWriteArrayList<ClientConnection>> contributors;
    private BlockingQueue<Event> eventQueue;
    private QueueWorker queueWorker;

    private GroupQueue() {
        contributors = new ConcurrentHashMap<Group, CopyOnWriteArrayList<ClientConnection>>();
        eventQueue = new LinkedBlockingQueue<Event>();
        queueWorker = new QueueWorker();
        queueWorker.start();
    }

    public static GroupQueue getInstance() {
        return instance;
    }

    @Override
    public void event(Event event) {
        switch (event.getType()) {
            case Event.PUBLISHER_PUBLISH_EVENT:
                eventQueue.offer(event);
                break;
        }
    }

    private void handlePublishEvent(Group group, ClientConnection client) {
        List<ClientConnection> contributors = getContributors(group);
        if (!contributors.contains(client)) {
            contributors.add(client);
        }
        Event event = new Event(Event.GROUPDATA_CHANGE_EVENT);
        event.put("group", group);
        event.put("client", client);
        EventDispatcher.getInstance().fire(event);
    }

    public List<ClientConnection> getContributors(Group group) {
        CopyOnWriteArrayList<ClientConnection> clients = contributors.get(group);
        if (clients == null) {
            clients = new CopyOnWriteArrayList<ClientConnection>();
            contributors.put(group, clients);
        }
        return clients;
    }

    private class QueueWorker extends Thread {

        public static final int TIMEOUT = 300;
        @Override
        public void run() {
            for (;;) {
                Event event = null;
                try {
                     event = eventQueue.poll(TIMEOUT, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                }
                if (event != null) {
                    switch (event.getType()) {
                        case Event.PUBLISHER_PUBLISH_EVENT:
                            handlePublishEvent((Group)event.get("group"), (ClientConnection)event.get("client"));
                            break;
                    }
                }
            }
        }
    }

}
