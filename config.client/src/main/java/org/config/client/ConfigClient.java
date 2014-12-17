package org.config.client;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public abstract class ConfigClient {

    public static final int CLIENT_UNINITED = 0x01;
    public static final int CLIENT_REGISTERED = 0x02;
    public static final int CLIENT_UNREGISTERED = 0X03;


    private ClientRegistration registration;
    private AtomicInteger state;

    public ConfigClient(ClientRegistration registration) {
        this.registration = registration;
        this.state = new AtomicInteger(CLIENT_UNINITED);
    }

    public ClientRegistration getRegistration() {
        return registration;
    }

    protected abstract boolean isSynchronized();

    protected int getState() {
        return state.get();
    }

    protected void setState(int state) {
        this.state.set(state);
    }

}
