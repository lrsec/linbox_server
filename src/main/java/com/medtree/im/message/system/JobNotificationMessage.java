package com.medtree.im.message.system;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lrsec on 11/9/15.
 */
public class JobNotificationMessage {
    @JSONField(name = "unread")
    public int unreadCount;
}
