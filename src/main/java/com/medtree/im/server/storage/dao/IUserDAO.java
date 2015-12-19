package com.medtree.im.server.storage.dao;

/**
 * Created by lrsec on 7/3/15.
 */
public interface IUserDAO {
    String getUserId(String userChatId);
    String getUserChatId(String userId);
    String getUserName(String userId);
}
