package org.config.common.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public class UUIDGenerator {

    public static AtomicInteger id = new AtomicInteger(0);

    public static int get() {
        return id.getAndIncrement();
    }
}
