package com.linbox.im.server.service.impl;

import com.linbox.im.server.constant.RedisKey;
import com.linbox.im.server.service.IOutboxService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by lrsec on 7/3/15.
 */
@Service
public class OutboxService implements IOutboxService {
    private static Logger logger = LoggerFactory.getLogger(OutboxService.class);

//    @Autowired
//    private JedisPool jedisPool;

    @Override
    public void put(String userId, String msg) {
//        if(StringUtils.isBlank(userId)) {
//            logger.error("Get empty user id when saving outbox message");
//            return;
//        }
//
//        String key = RedisKey.getOutboxKey(userId);
//
//        try(Jedis jedis = jedisPool.getResource()) {
//            jedis.lpush(key, msg);
//            logger.debug("Save message to outbox for {}. Message: {}", userId, StringUtils.trimToEmpty(msg));
//        }
    }

    @Override
    public String get(String userId) {
        String result = null;
//
//        if(StringUtils.isBlank(userId)) {
//            logger.error("Get empty user id when getting outbox message");
//            return null;
//        }
//
//        String key = RedisKey.getOutboxKey(userId);
//
//        try(Jedis jedis = jedisPool.getResource()) {
//            result = jedis.lpop(key);
//            logger.trace("Get message from outbox for {}. Message: {}. Thread: {}", userId, StringUtils.trimToEmpty(result), Thread.currentThread().getId());
//
//        }
//
        return result;
    }
}
