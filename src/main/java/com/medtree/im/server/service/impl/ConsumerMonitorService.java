package com.medtree.im.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.medtree.im.message.RequestResponseType;
import com.medtree.im.server.service.IConsumerMonitorService;
import com.medtree.im.server.monitor.MonitorMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by lrsec on 7/24/15.
 */
@Service
public class ConsumerMonitorService implements IConsumerMonitorService {
    private static Logger logger = LoggerFactory.getLogger(ConsumerMonitorService.class);

    private static final int DURATION = 30;
    private static final TimeUnit DURATION_UNIT = TimeUnit.SECONDS;

    private ConcurrentHashMap<RequestResponseType, AtomicLong> failCount;
    private ConcurrentHashMap<RequestResponseType, ConcurrentLinkedQueue<MonitorMeta>> successTreat;

    private ScheduledExecutorService executorService;

    public ConsumerMonitorService() {
        successTreat = new ConcurrentHashMap<>();
        failCount = new ConcurrentHashMap<>();

        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {

                    ConcurrentHashMap<RequestResponseType, ConcurrentLinkedQueue<MonitorMeta>> success = new ConcurrentHashMap<>();
                    ConcurrentHashMap<RequestResponseType, AtomicLong> fail = new ConcurrentHashMap<>();

                    Set<RequestResponseType> keys = new HashSet<>();
                    keys.addAll(successTreat.keySet());
                    keys.addAll(failCount.keySet());

                    for (RequestResponseType type : keys) {
                        if (successTreat.containsKey(type)) {
                            success.put(type, successTreat.put(type, new ConcurrentLinkedQueue<MonitorMeta>()));
                        }

                        if (failCount.containsKey(type)) {
                            fail.put(type, failCount.put(type, new AtomicLong(0)));
                        }
                    }

                    for(RequestResponseType type : keys) {
                        long successNum = success.containsKey(type) ? success.get(type).size() : 0;
                        long failNum = fail.containsKey(type) ? fail.get(type).get() : 0;

                        if (RequestResponseType.isRequest(type)) {
                            long totalCost = 0;
                            long nsqCost = 0;
                            long aesDecryptCost = 0;

                            if (success.containsKey(type)) {
                                for (MonitorMeta meta : success.get(type)) {
                                    totalCost += meta.getDataComputeCost();
                                    nsqCost += meta.getNSQCost();
                                    aesDecryptCost += meta.getAESDecryptCost();
                                }
                            }

                            long averageTotalCost = successNum == 0 ? 0 : totalCost/successNum;
                            long averageNsqCost = successNum == 0 ?  0 : nsqCost/successNum;
                            long averageAesDecryptCost = successNum == 0 ? 0 : aesDecryptCost/successNum;

                            sendRequestMessageInfo(type, successNum, failNum, averageTotalCost, averageNsqCost, averageAesDecryptCost);
                        }

                        if (RequestResponseType.isResponse(type)) {
                            long totalCost = 0;
                            long aesEncryptCost = 0;

                            if (success.containsKey(type)) {
                                for (MonitorMeta meta : success.get(type)) {
                                    totalCost += meta.getTcpSendCost();
                                    aesEncryptCost += meta.getAESEncrytCost();
                                }
                            }

                            long averageTotalCost = successNum == 0 ? 0 : totalCost/successNum;
                            long averageEncryptCost = successNum == 0 ? 0 : aesEncryptCost/successNum;

                            sendResponseMessageInfo(type, successNum, failNum, averageTotalCost, averageEncryptCost);
                        }
                    }

                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }, DURATION, DURATION, DURATION_UNIT);
    }

    public void addSuccessTreat(RequestResponseType type, MonitorMeta meta) {
        try {
            ConcurrentLinkedQueue<MonitorMeta> hash = successTreat.get(type);

            if (hash == null) {
                successTreat.putIfAbsent(type, new ConcurrentLinkedQueue<MonitorMeta>());
                hash = successTreat.get(type);
            }

            hash.add(meta);

            if (RequestResponseType.isRequest(type)) {
                logger.debug("Message Type: {}. Total Cost: {}. NSQ Cost: {}. AES Decrypt Cost: {}. ", type.getName(), meta.getDataComputeCost(), meta.getNSQCost(), meta.getAESDecryptCost());
            }

            if (RequestResponseType.isResponse(type)) {
                logger.debug("Message Type: {}. Time stay in Outbox: {}. Total Cost: {}. AES Encrypt Cost: {}", type.getName(), meta.getStayInOutboxTime(), meta.getTcpSendCost(), meta.getAESEncrytCost());
            }
        } catch (Exception e) {
            logger.error("Exception occurred when add success monitor data for type: {}. Monitor data: {}", type == null ? "null" : type.getName(), JSON.toJSONString(meta));
        }
    }

    public void addFailCount(RequestResponseType type) {
        try {
            AtomicLong atom = failCount.get(type);

            if (atom == null) {
                failCount.putIfAbsent(type, new AtomicLong(0));
                atom = failCount.get(type);
            }

            long count = atom.incrementAndGet();
            logger.debug("Type: {}. Total Fail Count: {}", type.getName(), count);
        } catch (Exception e) {
            logger.error("Exception occurred when add fail monitor data for type: {}.", type == null ? "null" : type.getName());
        }
    }

    private void sendRequestMessageInfo(RequestResponseType type, long successCount, long failCount, long totalCost, long nsqCost, long aesDecryptCost) {
        logger.debug("Type: {}. Success: {}. Fail: {}. Average Total Cost: {}. Average NSQ Cost: {}. Average AES Decrypt Cost: {}", type.getName(), successCount, failCount, totalCost, nsqCost, aesDecryptCost);
    }

    private void sendResponseMessageInfo(RequestResponseType type, long successCount, long failCount, long totalCost, long aesEncryptCost) {
        logger.debug("Type: {}. Success: {}. Fail: {}. Average Total Cost: {}. Average AES Encrypt Cost: {}", type.getName(), successCount, failCount, totalCost, aesEncryptCost);
    }
}
