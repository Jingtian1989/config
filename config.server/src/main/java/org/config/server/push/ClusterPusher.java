package org.config.server.push;

import org.config.common.domain.ClusterMessage;
import org.config.common.domain.MessageDigest;
import org.config.server.domain.Record;
import org.config.server.event.Event;
import org.config.server.event.EventListener;
import org.config.server.service.ClientConnection;
import org.config.server.service.ClusterConfig;
import org.jboss.netty.channel.Channel;
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
 * Created by jingtian.zjt on 2014/12/16.
 */
public class ClusterPusher implements EventListener{

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterPusher.class);
    private static final ClusterPusher instance = new ClusterPusher();
    private static final int TIMEOUT = 300;
    private BlockingQueue<Runnable> tasks;
    private ClusterPushThread worker;

    private ClusterPusher() {
        this.tasks = new LinkedBlockingQueue<Runnable>();
        this.worker = new ClusterPushThread();
        worker.start();
    }

    public static ClusterPusher getInstance() {
        return instance;
    }

    @Override
    public void handleEvent(Event event) {
        switch (event.getType()) {
            case Event.PUBLISHER_PUBLISH_EVENT:
            case Event.PUBLISHER_UNREGISTER_EVENT:
                ClientConnection client = (ClientConnection) event.get("client");
                if (!client.isClusterClient()) {
                    tasks.offer(new ClusterFullPushTask(client));
                }
                break;
        }
    }

    public class ClusterSinglePushTask implements Runnable {

        private ClientConnection client;
        private Channel cluster;

        public ClusterSinglePushTask(ClientConnection client, Channel channel) {
            this.client = client;
            this.cluster = channel;

        }
        @Override
        public void run() {
            List<Record> records = client.query();
            ClusterMessage message = new ClusterMessage();
            for (Record record : records) {
                MessageDigest digest = new MessageDigest(ClusterMessage.CLUSTER_SYNC_TYPE);
                digest.put("group", record.getGroup());
                digest.put("clientId", record.getClientId());
                digest.put("data", record.getData());
                digest.put("version", String.valueOf(record.getVersion()));
                message.addDigest(digest);
            }
            if (message.getDigests().size() > 0) {
                message.setHostId(client.getHostId());
                ChannelFuture future = cluster.write(message);
                future.addListener(new ClusterPushListener(client, cluster));
            }
        }
    }

    public class ClusterFullPushTask implements Runnable {

        ClientConnection client;

        public ClusterFullPushTask(ClientConnection client) {
            this.client = client;
        }
        @Override
        public void run() {
            List<Channel> clusters = ClusterConfig.getInstance().getClusterChannels();
            List<Record> records = client.query();
            ClusterMessage message = new ClusterMessage();
            for (Record record : records) {
                MessageDigest digest = new MessageDigest(ClusterMessage.CLUSTER_SYNC_TYPE);
                digest.put("group", record.getGroup());
                digest.put("clientId", record.getClientId());
                digest.put("data", record.getData());
                digest.put("dataId", record.getDataId());
                digest.put("version", String.valueOf(record.getVersion()));
                message.addDigest(digest);
            }
            if (message.getDigests().size() > 0) {
                message.setHostId(client.getHostId());
//            for (Channel cluster : clusters) {
//                ChannelFuture future = cluster.write(message);
//                future.addListener(new ClusterPushListener(client, cluster));
//            }
            }
        }
    }

    public class ClusterPushListener implements ChannelFutureListener {

        private ClientConnection client;
        private Channel cluster;

        public ClusterPushListener(ClientConnection client, Channel cluster) {
            this.client = client;
            this.cluster = cluster;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (!future.isSuccess()) {
                LOGGER.error("[CONFIG] push to cluster " + future.getChannel().getRemoteAddress() + " failed. cause:" +
                        future.getCause());
                if (!(future.getCause() instanceof ConnectException)) {
                    tasks.offer(new ClusterSinglePushTask(client, cluster));
                }
            }
        }
    }

    public class ClusterPushThread extends Thread {

        @Override
        public void run() {
            for (;;) {
                Runnable task = null;
                try {
                    task = tasks.poll(TIMEOUT, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                }
                if (task != null) {
                    task.run();
                }
            }
        }
    }

}
