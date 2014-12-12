package org.config.client;


/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public class Subscriber extends ConfigClient {

    private Observer observer;

    public Subscriber(ClientRegistration registration) {
        super(registration);
    }

    public void setObserver(Observer observer) {
        this.observer = observer;
    }

}
