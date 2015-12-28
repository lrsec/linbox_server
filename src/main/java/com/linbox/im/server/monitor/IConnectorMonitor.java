package com.linbox.im.server.monitor;

/**
 * Created by lrsec on 7/26/15.
 */
public interface IConnectorMonitor {
    void incrConnCount();

    void decConnCount();

    void start();
}
