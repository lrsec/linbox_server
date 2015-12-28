package com.linbox.im.server.constant;

/**
 * Created by lrsec on 7/1/15.
 */
public interface MessageTopic {
    String TOPIC_SYNC_UNREAD = "IMSyncUnread";
    String TOPIC_READ_ACK = "IMReadAck";
    String TOPIC_PULL_OLD_MEG = "IMPullOldMsg";
    String TOPIC_SEND_MSG = "IMSendMsg";
    String TOPIC_PING = "IMPing";
    String TOPIC_SYNC_SYSTEM_UNREAD = "IMSyncSysUnread";

    String TOPIC_DISPATCH_SEND_SINGLE = "IMSendSingleDispatch";
    String TOPIC_DISPATCH_SEND_GROUP = "IMSendGroupDispatch";
}

