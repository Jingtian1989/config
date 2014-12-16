package org.config.test;

import org.config.client.ClientRegistration;
import org.config.client.Publisher;
import org.config.client.PublisherRegistrar;

/**
 * Created by jingtian.zjt on 2014/12/16.
 */
public class PublisherBoot {

    public static void main(String args[]) {
        ClientRegistration publisherRegistration = new ClientRegistration("publisher", "testId-01", "group-01");
        Publisher publisher = PublisherRegistrar.register(publisherRegistration);
        publisher.publish("hello, world");
        publisher.publish("hello, world2");
        publisher.publish("hello, world3");
        for (;;);
    }
}
