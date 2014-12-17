package org.config.client;


import org.config.common.domain.ClientMessage;
import org.config.common.domain.MessageDigest;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public class Subscriber extends ConfigClient {

    private CopyOnWriteArrayList<Observer> observers;
    private String data;

    public Subscriber(ClientRegistration registration) {
        super(registration);
        observers = new CopyOnWriteArrayList<Observer>();
    }

    @Override
    protected boolean isSynchronized() {
        return getState() == ConfigClient.CLIENT_REGISTERED;
    }

    protected void synchronize(ClientMessage message) {
        if (getState() == ConfigClient.CLIENT_UNINITED) {
            MessageDigest digest = new MessageDigest(ClientMessage.SUBSCRIBER_REGISTER_TYPE);
            digest.put("clientId", getRegistration().getClientId());
            digest.put("dataId", getRegistration().getDataId());
            digest.put("group", getRegistration().getGroup());
            message.addDigest(digest);
        }

        if (getState() == ConfigClient.CLIENT_UNREGISTERED) {
            MessageDigest digest = new MessageDigest(ClientMessage.SUBSCRIBER_UNREGISTER_TYPE);
            digest.put("clientId", getRegistration().getClientId());
            digest.put("dataId", getRegistration().getDataId());
            digest.put("group", getRegistration().getGroup());
            message.addDigest(digest);
        }
    }

    public void addObserver(Observer observer) {
        observers.addIfAbsent(observer);
        if (data != null) {
            observer.update(data);
        }
    }

    protected void update(String group, String dataId, String data) {
        ClientRegistration registration = getRegistration();
        if (registration.getGroup().equals(group) && registration.getDataId().equals(dataId)) {
            this.data = data;
            for (Observer observer : observers) {
                observer.update(data);
            }
        }
    }


}
