package com.medtree.im.server.service.impl;

import com.medtree.im.exceptions.IMException;
import com.medtree.im.server.constant.RedisKey;
import com.medtree.im.server.service.IIDService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by lrsec on 7/4/15.
 */
@Service
public class IDService implements IIDService {

    private static Logger logger = LoggerFactory.getLogger(IDService.class);

    @Autowired
    private JedisPool jedisPool;

    // 目前使用 redis 实现，之后可以考虑使用 zk 提高可用性，避免由于 redis crash 导致的 id 不一致问题
    public long generateMsgId(String key) {
        if (StringUtils.isBlank(key)) {
            logger.error("Key should not be empty.");
            throw new IMException("Key should not be empty.");
        }

        key = RedisKey.getMsgIDKey(key);

        try(Jedis jedis = jedisPool.getResource()) {
            return jedis.incr(key);
        }
    }

    @Override
    // 目前使用独立 key 实现，以后可以考虑使用 id pool 形式实现
    public long generateGroupId() {
        try (Jedis jedis = jedisPool.getResource()) {
            if(!jedis.exists(RedisKey.GROUP_ID)) {
                throw new IMException( RedisKey.GROUP_ID + "不存在，无法获取合法的 Group Id");
            }

            return jedis.incr(RedisKey.GROUP_ID);
        }
    }
}
