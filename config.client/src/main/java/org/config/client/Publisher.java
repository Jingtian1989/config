package org.config.client;


import java.io.Serializable;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public class Publisher<T extends Serializable> extends ConfigClient {

    public Publisher(ClientRegistration registration) {
        super(registration);
    }

    public void publish(T data) {

    }
}
