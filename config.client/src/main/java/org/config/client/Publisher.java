package org.config.client;


import org.config.common.Constants;
import org.config.common.domain.ClientMessage;
import org.config.common.domain.MessageDigest;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public class Publisher extends ConfigClient {

    private String data;
    private AtomicInteger version;
    private AtomicInteger last;

    public Publisher(ClientRegistration registration) {
        super(registration);
        this.version = new AtomicInteger(Constants.UNINIT_VERSION);
        this.last = new AtomicInteger(Constants.UNINIT_VERSION);
    }

    @Override
    protected boolean isSynchronized() {
        return isRegistered() && last.equals(version);
    }

    protected void synchronize(ClientMessage message) {
        if (!isRegistered()) {
            MessageDigest digest = new MessageDigest(ClientMessage.PUBLISHER_REGISTER_TYPE);
            digest.put("clientId", getRegistration().getClientId());
            digest.put("dataId", getRegistration().getDataId());
            digest.put("group", getRegistration().getGroup());
            message.addDigest(digest);
        }

        if (!last.equals(version)) {
            MessageDigest digest = new MessageDigest(ClientMessage.PUBLISHER_PUBLISH_TYPE);
            digest.put("clientId", getRegistration().getClientId());
            digest.put("dataId", getRegistration().getDataId());
            digest.put("group", getRegistration().getGroup());
            digest.put("data", data);
            digest.put("version", String.valueOf(version));
            message.addDigest(digest);
        }
    }

    public void publish(String data) {
        if (data == null) {
            throw new IllegalArgumentException("[CONFIG] publish data can't be null.");
        }

        this.data = data;
        this.version.incrementAndGet();
        ClientWorker.getInstance().signal();
    }
}
