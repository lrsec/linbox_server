package com.linbox.im.server.storage.dao;

import java.util.List;

/**
 * Created by lrsec on 7/4/15.
 */
public interface IGroupDAO {
    List<String> getGroupMembers(String groupId);
}
