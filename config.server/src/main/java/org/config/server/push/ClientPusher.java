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
            case Event.SUBSCRIBER_ADD_EVENT:
                tasks.offer(new ClientPushTask((Group) event.get("group"), (ClientConnection)event.get("client")));
                break;
            case Event.PUBLISHER_ADD_EVENT:
                break;
            case Event.GDATA_CHANGE_EVENT:
                tasks.offer(new ClientPushTask((Group) event.get("group"), null));
                break;
        }
    }

    private void fullPush(Group group) {
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
            for (final ClientConnection client : clients) {
                if (client.getChannel().isConnected()) {
                    String clientId = null;
                    if ((clientId = client.hasSubscriber(group)) != null) {
                        message.setClientId(clientId);
                        ChannelFuture future = client.getChannel().write(message);
                        future.addListener(new ClientPushListener(group, client));
                    }
                }
            }
        }
    }

    private void singlePush(final Group group, final ClientConnection client) {
        ServerMessage message = new ServerMessage();
        List<Record> records = MemoryStore.query(group);
        for (Record record : records) {
            MessageDigest digest = new MessageDigest(ServerMessage.SUBSCRIBER_SYNCHRONIZE_TYPE);
            digest.put("group", record.getGroup());
            digest.put("dataId", record.getDataId());
            digest.put("data", record.getData());
            message.addDigest(digest);
        }
        if (message.getDigests().size() > 0) {
            if (client.getChannel().isConnected()) {
                String clientId = null;
                if ((clientId = client.hasSubscriber(group)) != null) {
                    message.setClientId(clientId);
                    ChannelFuture future = client.getChannel().write(message);
                    future.addListener(new ClientPushListener(group, client));
                }
            }
        }
    }

    public class ClientPushListener implements ChannelFutureListener {

        private Group group;
        private ClientConnection client;

        public ClientPushListener(Group group, ClientConnection client) {
            this.group = group;
            this.client = client;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (!future.isSuccess()) {
                LOGGER.error("[CONFIG] push to client " + future.getChannel().getRemoteAddress() + " failed. cause:" +
                        future.getCause());
                if (!(future.getCause() instanceof ConnectException)) {
                    tasks.offer(new ClientPushTask(group, client));
                }
            }
        }
    }

    public class ClientPushTask implements Runnable {

        private Group group;
        private ClientConnection client;

        public ClientPushTask(Group group, ClientConnection client) {
            this.client = client;
            this.group = group;
        }

        @Override
        public void run() {
            if (client  == null) {
                fullPush(group);
            } else {
                singlePush(group, client);
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
