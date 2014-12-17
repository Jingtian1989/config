package org.config.test;

import org.config.client.ClientRegistration;
import org.config.client.Observer;
import org.config.client.Subscriber;
import org.config.client.SubscriberRegistrar;

/**
 * Created by jingtian.zjt on 2014/12/16.
 */
public class SubscriberBootstrap {

    public static void main(String args[]) {
        ClientRegistration subscriberRegistration = new ClientRegistration("subscriber", "testId-01", "group-01");
        Subscriber subscriber = SubscriberRegistrar.register(subscriberRegistration);
        subscriber.addObserver(new EchoObserver());
    }

    public static class EchoObserver implements Observer {
        @Override
        public void update(String data) {
            System.out.println(data);
        }
    }
}
