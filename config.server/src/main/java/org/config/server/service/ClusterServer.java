package org.config.server.service;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;
import org.remote.common.thread.NamedThreadFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jingtian.zjt on 2014/12/17.
 */
public class ClusterServer {

    private ServerBootstrap bootstrap;
    private ClusterHandler handler;
    private AtomicBoolean started;
    private String host;
    private int port;

    public ClusterServer(String host, int port) {
        ThreadFactory master = new NamedThreadFactory("[CONFIG-CLUSTER-MASTER]");
        ThreadFactory worker = new NamedThreadFactory("[CONFIG-CLUSTER-WORKER]");
        this.host = host;
        this.port = port;
        this.started = new AtomicBoolean(false);
        this.bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(master),
                Executors.newCachedThreadPool(worker)));
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
    }

    public void start() {
        if (!started.compareAndSet(false, true)) {
            return;
        }
        handler = new ClusterHandler();
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
        bootstrap.bind(new InetSocketAddress(host, port));
    }

}
