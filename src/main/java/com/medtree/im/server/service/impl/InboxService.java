package com.medtree.im.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.medtree.im.message.Message;
import com.medtree.im.message.UnreadMsg;
import com.medtree.im.server.constant.RedisKey;
import com.medtree.im.server.service.IInboxService;
import com.medtree.im.server.service.StopcockService;
import com.medtree.im.server.storage.UnreadLoopData;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by lrsec on 7/2/15.
 */
@Service
public class InboxService extends StopcockService<String> implements IInboxService {
    public static final Logger logger = LoggerFactory.getLogger(InboxService.class);

    @Autowired
    private JedisPool jedisPool;

    public void updateSessionMsg(String id, String sessionId, Message msg) {
        updateMsg(id, sessionId, msg);
    }

    public void updateGroupMsg(String id, String groupId, Message msg) {
        updateMsg(id, groupId, msg);
    }

    private void updateMsg(String id, String field, Message msg) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(field)) {
            logger.error("Get empty id or field when updating unread message. id: {}. field: {}", StringUtils.trimToEmpty(id), StringUtils.trimToEmpty(field));
            return;
        }

        String redisKey = RedisKey.getInboxKey(id);
        String json = JSON.toJSONString(msg);

        if (StringUtils.isBlank(json)) {
            logger.error("Message format is not correct. Get a empty json string when updating unread for user: {}. field: {}", StringUtils.trimToEmpty(id), StringUtils.trimToEmpty(field));
            return;
        }

        lock(redisKey);

        try {
            try (Jedis jedis = jedisPool.getResource()) {
                String oldMsg = jedis.hget(redisKey, field);
                UnreadMsg oldUnreadMsg = JSON.parseObject(oldMsg, UnreadMsg.class);

                UnreadMsg newUnreadMsg;
                if (oldUnreadMsg == null || (oldUnreadMsg.msgId < msg.msgId)) {
                    newUnreadMsg = new UnreadMsg();
                    newUnreadMsg.msg = msg;
                    newUnreadMsg.msgId = msg.msgId;
                    newUnreadMsg.count = oldUnreadMsg == null ? 1 : (oldUnreadMsg.count + msg.msgId - oldUnreadMsg.msgId);
                    newUnreadMsg.userId = (StringUtils.equals(id, msg.fromUserId)) ? msg.fromUserId : msg.toUserId;
                    newUnreadMsg.remoteId = (StringUtils.equals(id, msg.fromUserId)) ? msg.toUserId : msg.fromUserId;
                    newUnreadMsg.type = msg.type;
                } else {
                    newUnreadMsg = oldUnreadMsg;
                }

                if (newUnreadMsg == null) {
                    logger.error("The message is stale. Message in redis: {}. Message received: {}", JSON.toJSONString(oldUnreadMsg), JSON.toJSONString(msg));
                    return;
                }

                jedis.hset(redisKey, field, JSON.toJSONString(newUnreadMsg));
            }
        } finally {
            unlock(redisKey);
        }
    }

    public void removeSessionMsg (String id, String sessionId, long msgId) {
        removeMsg(id, sessionId, msgId);
    }

    public void removeGroupMsg(String id, String groupId, long msgId) {
        removeMsg(id, groupId, msgId);
    }

    private void removeMsg(String id, String field, long msgId) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(field)) {
            logger.error("Get empty id or field when removing unread message. id: {}. field: {}", StringUtils.trimToEmpty(id), StringUtils.trimToEmpty(field));
            return;
        }

        String redisKey = RedisKey.getInboxKey(id);

        lock(redisKey);

        try {

            try(Jedis jedis = jedisPool.getResource()) {
                String json = jedis.hget(redisKey, field);
                UnreadMsg msg = JSON.parseObject(json, UnreadMsg.class);

                if (msg != null) {
                    if (msg.msgId <= msgId) {
                        jedis.hdel(redisKey, field);
                    } else {
                        msg.count = msg.msgId - msgId;
                        jedis.hset(redisKey, field, JSON.toJSONString(msg));
                    }
                }
            }
        } catch(Exception e) {
            logger.error("Remove message fail.", e);
        } finally {
            unlock(redisKey);
        }
    }

    public String getSessionJson(String id, String sessionId) {
        return getJson(id, sessionId);
    }

    public String getGroupJson(String id, String groupId) {
        return getJson(id, groupId);
    }

    public UnreadLoopData getAllJson(String id, long offset, int limit) {
        UnreadLoopData data = new UnreadLoopData();

        if (StringUtils.isBlank(id)) {
            logger.error("Get empty id when getting all unread message. id: {}.", StringUtils.trimToEmpty(id));
            return data;
        }

        String redisKey = RedisKey.getInboxKey(id);

        lock(redisKey);

        try {
            try(Jedis jedis = jedisPool.getResource()) {
                long cusor = offset;
                int currentLimit = limit;

                ScanParams params = new ScanParams();
                do {
                    params.count(currentLimit);

                    ScanResult<Map.Entry<String, String>> result = jedis.hscan(redisKey, Long.toString(cusor), params);

                    List<Map.Entry<String, String>> entities = result.getResult();
                    if (entities != null) {
                        for(Map.Entry<String, String> e : entities) {
                            data.unreads.add(e.getValue());
                        }
                    }

                    cusor =  Long.parseLong(result.getStringCursor());
                    currentLimit = limit - data.unreads.size();
                } while (data.unreads.size() < limit && cusor != 0);
            }
        } finally {
            unlock(redisKey);
        }

        return data;
    }

    private String getJson(String id, String field) {
        String result = null;

        if (StringUtils.isBlank(id) || StringUtils.isBlank(field)) {
            logger.error("Get empty id or session_id when getting group unread message. user id: {}. field: {}", StringUtils.trimToEmpty(id), StringUtils.trimToEmpty(field));
            return null;
        }

        String redisKey = RedisKey.getInboxKey(id);

        lock(redisKey);

        try {
            try (Jedis jedis = jedisPool.getResource()) {
                result = jedis.hget(redisKey, field);
            }
        } finally {
            unlock(redisKey);
        }

        return result;
    }

    @Override
    public int getTotalUnreadCount(String id) {
        int count = 0;

        if (StringUtils.isBlank(id)) {
            logger.error("Id is empty when getting total unread count");
            return count;
        }

        List<String> unreads = new LinkedList<>();

        String redisKey = RedisKey.getInboxKey(id);

        lock(redisKey);

        try {
            try(Jedis jedis = jedisPool.getResource()) {
                unreads.addAll(jedis.hvals(redisKey));

                for (String s : unreads) {
                    UnreadMsg msg = JSON.parseObject(s, UnreadMsg.class);

                    if (msg == null) {
                        logger.error("A unread record could not be parsed to UnreadMsg. JSON: {}. User id: {}", s, id);
                        continue;
                    }

                    count += msg.count;
                }
            }
        } finally {
            unlock(redisKey);
        }

        return count;
    }
}
