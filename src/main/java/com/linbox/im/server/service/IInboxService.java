package com.linbox.im.server.service;

import com.linbox.im.message.Message;
import com.linbox.im.server.storage.UnreadLoopData;

/**
 * Created by lrsec on 7/2/15.
 */
public interface IInboxService {
    void updateSessionMsg(String id, String sessionId, Message msg);
    void updateGroupMsg(String id, String groupId, Message msg) ;

    void removeSessionMsg(String id, String sessionId, long msgId);
    void removeGroupMsg(String id, String groupId, long msgId);

    UnreadLoopData getAllJson(String id, long offset, int limit);
    String getSessionJson(String id, String sessionId);
    String getGroupJson(String id, String groupId);

    int getTotalUnreadCount(String id);
}
