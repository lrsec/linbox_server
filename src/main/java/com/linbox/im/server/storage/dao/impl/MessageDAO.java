package com.linbox.im.server.storage.dao.impl;

import com.linbox.im.exceptions.IMException;
import com.linbox.im.server.constant.RedisKey;
import com.linbox.im.server.storage.dao.IMessageDAO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by lrsec on 12/29/15.
 */
@Service
public class MessageDAO implements IMessageDAO {
    private static Logger logger = LoggerFactory.getLogger(MessageDAO.class);
    
    @Autowired
    private JedisPool jedisPool;

    private AtomicLong atomicLong = new AtomicLong(0);

    @Override
    public long getNewMessageCount(String userId) {
        long newMessageCount = 0;
//
//        try (Jedis jedis = jedisPool.getResource()) {
//            newMessageCount = jedis.llen(RedisKey.getUnreadMessageNotifyKey(Long.parseLong(userId)));
//        }
//
        return newMessageCount;
    }

    @Override
    public long getNewFriendCount(String userId) {
        long newFriendsCount = 0;
//
//        try (Jedis jedis = jedisPool.getResource()) {
//            newFriendsCount = jedis.hlen(RedisKey.getFriendRequestsKey(Long.parseLong(userId)));
//        }
//
        return newFriendsCount;
    }

    @Override
    //TODO 目前使用 redis 实现，之后可以考虑使用 zk 提高可用性，避免由于 redis crash 导致的 id 不一致问题
    public long generateMsgId(String key) {
        return atomicLong.incrementAndGet();

//        if (StringUtils.isBlank(key)) {
//            logger.error("Key should not be empty.");
//            throw new IMException("Key should not be empty.");
//        }
//
//        key = RedisKey.getMsgIDKey(key);
//
//        try(Jedis jedis = jedisPool.getResource()) {
//            return jedis.incr(key);
//        }
    }
}
