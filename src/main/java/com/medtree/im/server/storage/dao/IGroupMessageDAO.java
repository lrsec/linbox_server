package com.medtree.im.server.storage.dao;

import com.medtree.im.message.Message;
import com.medtree.im.server.storage.entity.GroupMessageEntity;

import java.util.List;

/**
 * Created by lrsec on 7/4/15.
 */
public interface IGroupMessageDAO {
    GroupMessageEntity insert(Message msg);
    List<GroupMessageEntity> findMsg(String groupId, long maxMsgId, long minMsgId, int limit);
    GroupMessageEntity findMsgByRId(long rId, String fromUserId, String groupId);
}
