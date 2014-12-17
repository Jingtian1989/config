package org.config.client;


import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public class PublisherRegistrar{

    private static final ConcurrentHashMap<String, Publisher> publishers = new ConcurrentHashMap<String, Publisher>();

    public static Publisher query(ClientRegistration registration) {
        return publishers.get(registration.getClientId());
    }

    public static Publisher query(String clientId) {
        return publishers.get(clientId);
    }

    public static Publisher register(ClientRegistration registration) {
        Publisher publisher = query(registration);
        if (publisher != null) {
            return publisher;
        }
        publisher = new Publisher(registration);
        publishers.put(registration.getClientId(), publisher);
        return publisher;
    }


    public static void unregister(ClientRegistration registration) {
        Publisher publisher = query(registration);
        if (publisher != null) {
            publisher.setState(ConfigClient.CLIENT_UNREGISTERED);
            ClientWorker.getInstance().signal();
        }
    }

    protected static Publisher[] getPublishers() {
        return publishers.values().toArray(new Publisher[0]);
    }

}
