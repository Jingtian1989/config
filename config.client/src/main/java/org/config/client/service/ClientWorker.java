package org.config.client.service;

import org.config.client.*;
import org.config.common.domain.ClientMessage;
import org.config.common.thread.NamedThreadFactory;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.*;

/**
 * Created by jingtian.zjt on 2014/12/13.
 */
public class ClientWorker {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientWorker.class);
    private static final ClientWorker instance = new ClientWorker();

    private BlockingQueue<Object> signals;
    private ClientWorkerThread worker;

    private ClientBootstrap bootstrap;
    private ClientHandler handler;
    private Channel channel;

    public static ClientWorker getInstance() {
        return instance;
    }

    public ClientWorker() {
        initConnector();
        signals = new LinkedBlockingQueue<Object>();
        worker = new ClientWorkerThread();
        worker.start();
    }

    private void initConnector() {
        ThreadFactory master = new NamedThreadFactory("[CONFIG-CLIENT-MASTER]");
        ThreadFactory worker = new NamedThreadFactory("[CONFIG-CLIENT-WORKER]");
        this.handler = new ClientHandler();
        this.bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(master),
                Executors.newCachedThreadPool(worker)));
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("reuseAddress", true);
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = new DefaultChannelPipeline();
                pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                pipeline.addLast("encoder", new ObjectEncoder());
                pipeline.addLast("handler", handler);
                return pipeline;
            }
        });
    }

    private void ensureConnected() {
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8009);
        ChannelFuture future = bootstrap.connect(address);
        future.awaitUninterruptibly(300l);
        if (!future.isDone() || !future.isSuccess() || !future.getChannel().isConnected()) {
            throw new RuntimeException("unable to connect to config server.");
        }
        channel = future.getChannel();
    }


    public void signal() {
        signals.offer(new Object());
    }

    private class ClientWorkerThread extends Thread {

        private static final int TIMEOUT = 30000;
        @Override
        public void run() {
            run0();
            wait0();
        }

        public void wait0() {
            try {
                signals.poll(TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                LOGGER.error("[CONFIG] client worker thread was interrupted by:", e);
            }
        }

        public void run0() {
            ClientMessage message = new ClientMessage();
            for (Subscriber subscriber : SubscriberRegistrar.getSubscribers()) {
                if (!subscriber.isSynchronized()) {
                    subscriber.synchronize(message);
                }
            }

            for (Publisher publisher : PublisherRegistrar.getPublishers()) {
                if (!publisher.isSynchronized()) {
                    publisher.synchronize(message);
                }
            }
            if (message.getDigests().size() > 0) {
                ensureConnected();
                ChannelFuture future = channel.write(message);
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            LOGGER.error("[CONFIG] write to " + future.getChannel().getRemoteAddress() + " failed.");
                            signal();
                        }
                    }
                });
            }
        }
    }

}