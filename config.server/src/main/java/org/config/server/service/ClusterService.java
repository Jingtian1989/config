package org.config.server.service;

import org.config.common.domain.ClusterMessage;
import org.remote.common.annotation.TargetType;
import org.remote.common.service.Processor;
import org.remote.common.service.Writer;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
@TargetType(value = ClusterMessage.class)
public class ClusterService implements Processor{

    @Override
    public void handleMessage(Object o, Writer writer) {
        ClusterMessage message = (ClusterMessage) o;

    }

    private void handleDlyRequest(Object data, Writer writer) {

    }

    private void handleDelRequest(Object data, Writer writer) {

    }

    private void handlSyncRequest(Object data, Writer writer) {

    }
}
