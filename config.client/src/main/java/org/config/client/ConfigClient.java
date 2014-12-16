package org.config.client;


import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public abstract class ConfigClient {

    private ClientRegistration registration;
    private AtomicBoolean registered;

    public ConfigClient(ClientRegistration registration) {
        this.registration = registration;
        this.registered = new AtomicBoolean(false);
    }

    public ClientRegistration getRegistration() {
        return registration;
    }

    public abstract boolean isSynchronized();


    public boolean getRegistered() {
        return registered.get();
    }

    public void setRegistered() {
        registered.set(true);
    }

}
