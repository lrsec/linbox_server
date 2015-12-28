package com.linbox.im.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lrsec on 7/3/15.
 */
public class IMUtils {
    public static final Logger logger = LoggerFactory.getLogger(IMUtils.class);

    public static String getSessionKey(String id1, String id2) {
        if(StringUtils.isBlank(id1) || StringUtils.isBlank(id2)) {
            logger.error("id should not be empty for create session_id. id1: {}. id2: {}", StringUtils.trimToEmpty(id1), StringUtils.trimToEmpty(id2));
            throw new IllegalArgumentException("id should not be empty for create session_id");
        }

        return id1.compareTo(id2) < 0 ? concrat(id1, id2) : concrat(id2, id1);
    }

    private static String concrat(String id1, String id2) {
        return id1 + "_" + id2;
    }
}
