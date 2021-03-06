package org.config.client;


import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public class SubscriberRegistrar {

    private static final ConcurrentHashMap<String, Subscriber> subscribers = new ConcurrentHashMap<String, Subscriber>();

    public static Subscriber register(ClientRegistration registration) {
        Subscriber subscriber = query(registration);
        if (subscriber != null) {
            return subscriber;
        }
        subscriber = new Subscriber(registration);
        subscribers.put(registration.getClientId(), subscriber);
        ClientWorker.getInstance().signal();
        return subscriber;
    }

    public static void unregister(ClientRegistration registration) {
        Subscriber subscriber = query(registration);
        if (subscriber != null) {
            subscriber.setState(ConfigClient.CLIENT_UNREGISTERED);
            ClientWorker.getInstance().signal();
        }
    }

    public static Subscriber query(ClientRegistration registration) {
        return subscribers.get(registration.getClientId());
    }

    public static Subscriber query(String clientId) {
        return subscribers.get(clientId);
    }

    protected static Subscriber[] getSubscribers() {
        return subscribers.values().toArray(new Subscriber[0]);
    }
}
