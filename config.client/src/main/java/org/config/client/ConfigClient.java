package org.config.client;



/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public abstract class ConfigClient {

    private ClientRegistration registration;

    public ConfigClient(ClientRegistration registration) {
        this.registration = registration;
    }

    public ClientRegistration getRegistration() {
        return registration;
    }
}
