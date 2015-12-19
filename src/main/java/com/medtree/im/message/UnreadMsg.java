package com.medtree.im.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lrsec on 6/29/15.
 */
public class UnreadMsg {
    // user chat id
    @JSONField(name = "user_chat_id")
    public String userChatId;

    // remote chatter id
    @JSONField(name = "remote_chat_id")
    public String remoteChatId;

    @JSONField(name = "group_chat_id")
    public String groupChatId;

    // 消息类型
    @JSONField(name = "type")
    public int type;

    // last message id
    @JSONField(name = "msg_id")
    public long msgId;

    // unread count
    @JSONField(name = "count")
    public long count;

    // last message
    @JSONField(name = "msg")
    public Message msg;
}