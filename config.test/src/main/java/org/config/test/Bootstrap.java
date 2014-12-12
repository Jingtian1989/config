package org.config.test;

import org.config.client.ClientRegistration;
import org.config.client.Observer;
import org.config.client.Publisher;
import org.config.client.Subscriber;
import org.config.client.register.PublisherRegistrar;
import org.config.client.register.SubscriberRegistrar;
import org.config.server.server.ConfigServer;

/**
 * Created by jingtian.zjt on 2014/12/12.
 */
public class Bootstrap {

    public static void main(String args[]) {
        ConfigServer configServer = new ConfigServer();
        configServer.start();

        ClientRegistration publisherRegistration = new ClientRegistration("publisher", "testId-01", "group-01");
        Publisher publisher = PublisherRegistrar.register(publisherRegistration);

        ClientRegistration subscriberRegistration = new ClientRegistration("subscriber", "testId-02", "group-01");
        Subscriber subscriber = SubscriberRegistrar.register(subscriberRegistration);
        subscriber.setObserver(new EchoObserver());

        publisher.publish("hello, world");

    }


    public static class EchoObserver implements Observer {
        @Override
        public void update(String data) {
            System.out.println(data);
        }
    }
}
