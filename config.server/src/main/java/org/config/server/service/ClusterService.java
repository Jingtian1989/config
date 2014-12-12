package org.config.server.service;

import org.config.common.domain.ClusterEvent;
import org.remote.common.annotation.TargetType;
import org.remote.common.service.Processor;
import org.remote.common.service.ResponseWriter;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
@TargetType(value = ClusterEvent.class)
public class ClusterService implements Processor{

    @Override
    public void handleRequest(Object o, ResponseWriter writer) {
        ClusterEvent event = (ClusterEvent) o;
        switch (event.getType()) {
            case ClusterEvent.CLUSTER_SYNC_EVENT:
                handlSyncRequest(event.getData(), writer);
                break;
            case ClusterEvent.CLUSTER_DELETE_EVENT:
                handleDelRequest(event.getData(), writer);
                break;
            case ClusterEvent.CLUSTER_DELAY_EVENT:
                handleDlyRequest(event.getData(), writer);
                break;
        }
    }

    private void handleDlyRequest(Object data, ResponseWriter writer) {

    }

    private void handleDelRequest(Object data, ResponseWriter writer) {

    }

    private void handlSyncRequest(Object data, ResponseWriter writer) {

    }
}
