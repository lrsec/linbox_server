package com.linbox.im.server.storage.dao.impl;

import com.linbox.im.exceptions.IMException;
import com.linbox.im.server.constant.RedisKey;
import com.linbox.im.server.storage.dao.IServerDAO;
import com.linbox.im.utils.AESUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by lrsec on 12/29/15.
 */
@Service
public class ServerDAO implements IServerDAO {
    private static Logger logger = LoggerFactory.getLogger(ServerDAO.class);

    @Autowired
    private JedisPool jedisPool;

    @Value("${im.ip}")
    private String ip;

    @Value("${im.port}")
    private String port;

    @Override
    public void registerServer() {
        if (StringUtils.isBlank(ip) || StringUtils.isBlank(port)) {
            logger.error("ip or port is empty when insert ServierInfoDao. ip: {}. port: {}", StringUtils.trimToEmpty(ip), StringUtils.trimToEmpty(port));
            throw new IMException("ip or port could not be empty. ip: " + StringUtils.trimToEmpty(ip) + ". port: " + StringUtils.trimToEmpty(port));
        }

        String info = StringUtils.trim(ip) + ":" + StringUtils.trim(port);

        logger.debug("Register server: {}", info);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.zadd(RedisKey.IP_REGISTRY, 0, info);
        }
    }

    @Override
    public Set<String> getServers() {
        Set<String> result = new LinkedHashSet<>();

        try (Jedis jedis = jedisPool.getResource()) {
            result.addAll(jedis.zrange(RedisKey.IP_REGISTRY, 0, -1));
        } catch (Exception e) {
            logger.error("Get IM server list from redis fail.", e);
        }

        return result;
    }

    @Override
    public String generatePassword(long userId) {
        String token = AESUtils.generatePassword();

        String key= RedisKey.getIMPassword(userId);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key,3600, token);
        }

        return token;
    }

    @Override
    public String getPassword(long userId) {
        String password = null;
        try (Jedis jedis = jedisPool.getResource()) {
            password = jedis.get(RedisKey.getIMPassword(userId));
        }

        return password;
    }

    @Override
    public void registerConnection(String userId, String address) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(RedisKey.CONNECT_REGISTRY, userId, address);
        }
    }

    @Override
    public String getConnection(String userId) {
        String record = null;

        try (Jedis jedis = jedisPool.getResource()) {
            record = jedis.hget(RedisKey.CONNECT_REGISTRY, userId);
        }

        return record;
    }
}
