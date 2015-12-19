package com.medtree.im.interfaces;

import java.util.Set;

/**
 * Created by lrsec on 8/21/15.
 */
public interface IIMService {
    Set<String> getIMServerList();
    String getPassword(long userId);

    void sendMessage(String fromUserId, String toId, String mineType, String content, int messageType, String creatorId);
    void sendSystemMessage(String targetUserId, int systemType, String content);

    long generateGroupId();
}
