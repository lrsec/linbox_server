package com.medtree.im.server.monitor.impl;

import com.medtree.im.server.monitor.IConnectorMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by lrsec on 7/26/15.
 */
public class ConnectorMonitor implements IConnectorMonitor {
    private static Logger logger = LoggerFactory.getLogger(ConnectorMonitor.class);

    private static final int DURATION = 30;
    private static final TimeUnit DURATION_UNIT = TimeUnit.SECONDS;

    private AtomicLong totalConnections;
    private ScheduledExecutorService executorService;

    public ConnectorMonitor() {
        totalConnections = new AtomicLong(0);
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void incrConnCount() {
        long count = totalConnections.incrementAndGet();
        logger.debug("Connection increase to {}", count);
    }

    @Override
    public void decConnCount() {
        long count = totalConnections.decrementAndGet();
        logger.debug("Connection decrease to {}", count);
    }

    @Override
    public void start() {
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    long connections = totalConnections.get();
                    sendConnCount(connections);
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }, DURATION, DURATION, DURATION_UNIT);
    }

    private void sendConnCount(long count) {
        logger.info("Current Total Connection Count: {}", count);
    }
}
