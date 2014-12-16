package org.config.client.service;

import org.config.client.*;
import org.config.common.domain.ClientMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by jingtian.zjt on 2014/12/13.
 */
public class ClientWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientWorker.class);
    private static final ClientWorker instance = new ClientWorker();

    private BlockingQueue<Object> signals;
    private ClientWorkerThread worker;
    public static ClientWorker getInstance() {
        return instance;
    }

    public ClientWorker() {
        signals = new LinkedBlockingQueue<Object>();
        worker = new ClientWorkerThread();
        worker.start();
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
                ServerConnector.getInstance().send(message);
            }
        }
    }
}