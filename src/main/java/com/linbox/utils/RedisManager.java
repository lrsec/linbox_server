package com.linbox.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by lrsec on 11/11/15.
 *
 *
 * How to use:
 *
 * 1. for try-with resource:
 *
 * try(RedisManager.getPool().getResource()) {
 *     ...
 * }
 *
 * 2. for try-catch-finally:
 *
 * Jedis jedis = null;
 * try {
 *   jedis = RedisManager.getPool().getResource();
 *   ....
 *
 * } finally {
 *    if (jedis != null) {
 *        jedis.close();
 *    }
 * }
 *
 */
public class RedisManager {

    private static final String CONFIG_FILE_NAME = "redis.properties";
    private static final String CONFIG_HOST = "host";
    private static final String CONFIG_PORT = "port";
    private static final String CONFIG_MAX_ACTIVE = "maxActive";
    private static final String CONFIG_MAX_IDLE = "maxIdle";
    private static final String CONFIG_MAX_WAIT_MILLI = "maxWaitMilli";
    private static final String CONFIG_TIMEOUT = "timeout";

    private static Logger logger = LoggerFactory.getLogger(RedisManager.class);
    private static volatile JedisPool pool;

    static {
        Properties props = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(CONFIG_FILE_NAME);
            props.load(input);

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(Integer.parseInt(props.getProperty(CONFIG_MAX_ACTIVE)));
            config.setMaxIdle(Integer.parseInt(props.getProperty(CONFIG_MAX_IDLE)));
            config.setMaxWaitMillis(Integer.parseInt(props.getProperty(CONFIG_MAX_WAIT_MILLI)));

            pool = new JedisPool(config, props.getProperty(CONFIG_HOST), Integer.parseInt(props.getProperty(CONFIG_PORT)), Integer.parseInt(props.getProperty(CONFIG_TIMEOUT)));

        } catch (Exception e) {
            logger.error("Can not initialize redis pool.", e);
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    logger.error("Can not close InputStream for redis config file",e);
                }
            }
        }
    }

    public static JedisPool getPool() {
        if (pool == null) {
            logger.error("Redis Pool is not initialized correctly");
            throw new RuntimeException("Redis Pool is not initialized correctly");
        }

        return pool;
    }
}
