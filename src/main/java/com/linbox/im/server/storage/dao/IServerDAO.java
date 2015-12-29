package com.linbox.im.server.storage.dao;

import java.util.Set;

/**
 * Created by lrsec on 12/29/15.
 */
public interface IServerDAO {
    void registerServer();
    Set<String> getServers();
    String generatePassword(long userId);
    String getPassword(long userId);
    void registerConnection(String userId, String address);
    String getConnection(String userId);
}
