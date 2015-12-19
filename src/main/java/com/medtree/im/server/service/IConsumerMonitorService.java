package com.medtree.im.server.service;

import com.medtree.im.message.RequestResponseType;
import com.medtree.im.server.monitor.MonitorMeta;

/**
 * Created by lrsec on 7/24/15.
 */
public interface IConsumerMonitorService {
    void addSuccessTreat(RequestResponseType type, MonitorMeta meta);

    void addFailCount(RequestResponseType type);
}
