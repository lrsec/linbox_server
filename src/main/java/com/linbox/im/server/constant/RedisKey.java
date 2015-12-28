package com.linbox.im.server.constant;

import com.google.common.base.CharMatcher;
import org.apache.commons.lang.StringUtils;

/**
 * Created by lrsec on 7/1/15.
 */
public class RedisKey {

    public static final String CONNECT_REGISTRY = "im:connection:registry";
    public static final String IP_REGISTRY = "im:ip";
    public static final String GROUP_ID = "im:group:id";
    public static final String IM_USER_WHITE_LIST = "im:user:white:list";

    public static String getInboxKey(String id) {
        return "im:inbox:" + StringUtils.trim(id);
    }

    public static String getFriendRequestsKey(long userId){
        return "unread_count:"+userId+":friend_requests";
    }

    public static String getUserUnReadCountsKey(long userId) {
        return "unread_count:"+userId;
    }

    public static String getUnreadMessageNotifyKey(long userID) {
        return "message:notify:" + userID;
    }

    public static String getIMPassword(long userId) {
        return "im:password:" + Long.toString(userId);
    }

    public static String getMsgIDKey(String key) {
        return "im:message:id:" + CharMatcher.WHITESPACE.trimFrom(key);
    }

    public static String getOutboxKey (String id) {
        return "im:outbox:" + CharMatcher.WHITESPACE.trimFrom(id);
    }
}
