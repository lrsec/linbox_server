package com.medtree.im.server.service.impl;

import com.medtree.im.exceptions.IMException;
import com.medtree.im.server.constant.RedisKey;
import com.medtree.im.server.service.IServerService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by lrsec on 7/11/15.
 */
@Service
public class ServerService implements IServerService {
    private static Logger logger = LoggerFactory.getLogger(ServerService.class);

    @Autowired
    private JedisPool jedisPool;

    @Value("${im.ip}")
    private String ip;

    @Value("${im.port}")
    private String port;

    @Override
    public void register() {
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
}
