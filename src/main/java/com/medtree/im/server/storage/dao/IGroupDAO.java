package com.medtree.im.server.storage.dao;

import java.util.List;

/**
 * Created by lrsec on 7/4/15.
 */
public interface IGroupDAO {
    String getGroupId(String groupChatId);
    String getGroupChatId(String groupId);
    List<String> getGroupMembers(String groupId);
}
