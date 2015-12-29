package com.linbox.im.server.storage.dao;

/**
 * Created by lrsec on 12/29/15.
 */
public interface IMessageDAO {
    long getNewMessageCount(String userId);
    long getNewFriendCount(String userId);

    long generateMsgId(String key);
}
