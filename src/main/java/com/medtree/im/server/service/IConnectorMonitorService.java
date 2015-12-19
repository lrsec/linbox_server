package com.medtree.im.server.service;

/**
 * Created by lrsec on 7/26/15.
 */
public interface IConnectorMonitorService {
    void incrConnCount();

    void decConnCount();
}
