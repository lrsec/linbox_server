package com.medtree.im.server.service;

import com.medtree.im.message.Message;

/**
 * Created by lrsec on 7/16/15.
 */
public interface IPushService {
    void sendPush(Message message);
}
