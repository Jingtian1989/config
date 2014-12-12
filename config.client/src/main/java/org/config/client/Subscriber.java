package org.config.client;



import java.io.Serializable;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public class Subscriber<T extends Serializable> extends ConfigClient {

    private Observer observer;

    public Subscriber(ClientRegistration registration) {
        super(registration);
    }

    public void setObserver(Observer observer) {
        this.observer = observer;
    }

}
