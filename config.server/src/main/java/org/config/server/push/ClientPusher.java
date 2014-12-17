package org.config.server.push;

import org.config.common.domain.MessageDigest;
import org.config.common.domain.ServerMessage;
import org.config.server.domain.Group;
import org.config.server.domain.Record;
import org.config.server.event.Event;
import org.config.server.event.EventListener;
import org.config.server.service.ClientConnection;
import org.config.server.store.MemoryStore;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by jingtian.zjt on 2014/12/15.
 */
public class ClientPusher implements EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientPusher.class);
    private static final ClientPusher instance = new ClientPusher();
    private static final int TIMEOUT = 300;
    private BlockingQueue<Runnable> tasks;
    private ClientPushThread worker;

    public static ClientPusher getInstance() {
        return instance;
    }

    private ClientPusher(){
        this.tasks = new LinkedBlockingQueue<Runnable>();
        this.worker = new ClientPushThread();
        worker.start();
    }

    @Override
    public void handleEvent(Event event) {
        switch (event.getType()) {
            case Event.SUBSCRIBER_REGISTER_EVENT:
                tasks.offer(new ClientPushSubscriberRegisterTask((Group) event.get("group"), (ClientConnection)event.get("client")));
                break;
            case Event.PUBLISHER_REGISTER_EVENT:
                tasks.offer(new ClientPushPublisherRegisterTask((Group)event.get("group"), (ClientConnection)event.get("client")));
                break;
            case Event.GROUP_DATA_CHANGE_EVENT:
                tasks.offer(new ClientPushGroupDataChangeTask((Group) event.get("group")));
                break;
            case Event.SUBSCRIBER_UNREGISTER_EVENT:
                tasks.offer(new ClientPushSubscriberUnregisterTask((Group) event.get("group"), (ClientConnection) event.get("client"), (String)event.get("clientId")));
                break;
            case Event.PUBLISHER_UNREGISTER_EVENT:
                tasks.offer(new ClientPushPublisherUnregisterTask((Group) event.get("group"), (ClientConnection) event.get("client"), (String)event.get("clientId")));
                break;
        }
    }

    public class ClientPushPublisherUnregisterTask implements Runnable {

        private Group group;
        private ClientConnection client;
        private String clientId;

        public ClientPushPublisherUnregisterTask(Group group, ClientConnection client, String clientId) {
            this.group = group;
            this.client = client;
            this.clientId = clientId;
        }

        @Override
        public void run() {
            if (client.getChannel().isConnected()) {
                ServerMessage message = new ServerMessage();
                message.setClientId(clientId);
                MessageDigest digest = new MessageDigest(ServerMessage.PUBLISHER_UNREGISTER_TYPE);
                digest.put("group", group.getGroup());
                digest.put("dataId", group.getDataId());
                message.addDigest(digest);
                client.getChannel().write(message);
            }
        }
    }


    public class ClientPushSubscriberUnregisterTask implements Runnable {

        private Group group;
        private ClientConnection client;
        private String clientId;

        public ClientPushSubscriberUnregisterTask(Group group, ClientConnection client, String clientId) {
            this.group = group;
            this.client = client;
            this.clientId = clientId;
        }

        @Override
        public void run() {
            if (client.getChannel().isConnected()) {
                ServerMessage message = new ServerMessage();
                message.setClientId(clientId);
                MessageDigest digest = new MessageDigest(ServerMessage.SUBSCRIBER_UNREGISTER_TYPE);
                digest.put("group", group.getGroup());
                digest.put("dataId", group.getDataId());
                message.addDigest(digest);
                client.getChannel().write(message);
            }
        }
    }

    public class ClientPushGroupDataChangeTask implements Runnable {

        public Group group;

        public ClientPushGroupDataChangeTask(Group group) {
            this.group = group;
        }

        @Override
        public void run() {
            ServerMessage message = new ServerMessage();
            List<Record> records = MemoryStore.query(group);
            ClientConnection[] clients = MemoryStore.getNativeClients();
            for (Record record : records) {
                MessageDigest digest = new MessageDigest(ServerMessage.SUBSCRIBER_SYNCHRONIZE_TYPE);
                digest.put("group", record.getGroup());
                digest.put("dataId", record.getDataId());
                digest.put("data", record.getData());
                message.addDigest(digest);
            }
            if (message.getDigests().size() > 0) {
                for (ClientConnection client : clients) {
                    String clientId = client.hasSubscriber(group);
                    if (client.getChannel().isConnected() && clientId != null) {
                        message.setClientId(clientId);
                        ChannelFuture future = client.getChannel().write(message);
                        future.addListener(new ClientPushListener(this));
                    }
                }
            }
        }
    }

    public class ClientPushSubscriberRegisterTask implements Runnable {

        private Group group;
        private ClientConnection client;

        public ClientPushSubscriberRegisterTask(Group group, ClientConnection client) {
            this.client = client;
            this.group = group;
        }

        @Override
        public void run() {
            ServerMessage message = new ServerMessage();
            String clientId = client.hasSubscriber(group);
            if (client.getChannel().isConnected() && clientId != null) {
                message.setClientId(clientId);

                MessageDigest digest = new MessageDigest(ServerMessage.SUBSCRIBER_REGISTER_TYPE);
                digest.put("group", group.getGroup());
                digest.put("dataId", group.getDataId());
                message.addDigest(digest);

                List<Record> records = MemoryStore.query(group);
                for (Record record : records) {
                    digest = new MessageDigest(ServerMessage.SUBSCRIBER_SYNCHRONIZE_TYPE);
                    digest.put("group", record.getGroup());
                    digest.put("dataId", record.getDataId());
                    digest.put("data", record.getData());
                    message.addDigest(digest);
                }

                ChannelFuture future = client.getChannel().write(message);
                future.addListener(new ClientPushListener(this));
            }
        }
    }


    public class ClientPushPublisherRegisterTask implements Runnable {

        private Group group;
        private ClientConnection client;

        public ClientPushPublisherRegisterTask(Group group, ClientConnection client) {
            this.group = group;
            this.client = client;
        }

        @Override
        public void run() {
            String clientId = client.hasPublisher(group);
            ServerMessage message = new ServerMessage();
            if (client.getChannel().isConnected() && clientId != null) {
                MessageDigest digest = new MessageDigest(ServerMessage.PUBLISHER_REGISTER_TYPE);
                digest.put("group", group.getGroup());
                digest.put("dataId", group.getDataId());
                digest.put("clientId", clientId);
                message.addDigest(digest);
                ChannelFuture future = client.getChannel().write(message);
                future.addListener(new ClientPushListener(this));
            }
        }
    }

    public class ClientPushListener implements ChannelFutureListener {

        private Runnable task;

        public ClientPushListener(Runnable task) {
            this.task = task;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (!future.isSuccess()) {
                LOGGER.error("[CONFIG] push to client " + future.getChannel().getRemoteAddress() + " failed. cause:" +
                        future.getCause());
                if (!(future.getCause() instanceof ConnectException)) {
                    tasks.offer(task);
                }
            }
        }
    }

    public class ClientPushThread extends Thread {

        @Override
        public void run() {
            for (;;) {
                Runnable task = null;
                try {
                    task = tasks.poll(TIMEOUT, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e){
                }
                if (task != null) {
                    task.run();
                }
            }
        }
    }

}
