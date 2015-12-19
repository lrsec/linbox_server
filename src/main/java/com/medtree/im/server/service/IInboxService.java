package com.medtree.im.server.service;

import com.medtree.im.message.Message;
import com.medtree.im.server.storage.UnreadLoopData;

import java.util.List;

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
