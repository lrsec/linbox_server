package com.medtree.im.server.storage.dao;

/**
 * Created by lrsec on 7/3/15.
 */
public interface IUserDAO {
    String getUserName(String userId);
    Boolean isUserValid(String userID, String token);
}
