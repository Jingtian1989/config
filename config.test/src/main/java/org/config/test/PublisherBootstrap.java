package org.config.test;

import org.config.client.ClientRegistration;
import org.config.client.Publisher;
import org.config.client.PublisherRegistrar;

/**
 * Created by jingtian.zjt on 2014/12/16.
 */
public class PublisherBootstrap {

    public static void main(String args[]) {
        ClientRegistration publisherRegistration = new ClientRegistration("publisher", "testId-01", "group-01");
        Publisher publisher = PublisherRegistrar.register(publisherRegistration);
        publisher.publish("hello, world");
        synchronized (PublisherBootstrap.class) {
            try {
                PublisherBootstrap.class.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("publisher bootstrap close.");
            }
        }
    }
}
