package com.linbox.im.server.service;

/**
 * Created by lrsec on 7/4/15.
 */
public interface IIDService {
    long generateMsgId(String key);
    long generateGroupId();
}
