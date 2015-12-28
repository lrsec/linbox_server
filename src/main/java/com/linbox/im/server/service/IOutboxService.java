package com.linbox.im.server.service;

/**
 * Created by lrsec on 7/3/15.
 */
public interface IOutboxService {
    void put(String userId, String msg);
    String get(String userId);
}
