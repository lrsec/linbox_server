package com.linbox.im.server.message;

import com.alibaba.fastjson.annotation.JSONField;
import com.linbox.im.server.message.enums.MessageType;
import com.linbox.im.server.storage.entity.SessionMessageEntity;

/**
 * Created by lrsec on 6/29/15.
 */
public class Message {

    // 消息发送时的 rId
    @JSONField(name = "r_id")
    public long rId;

    // 发送方 id
    @JSONField(name = "from_user_id")
    public String fromUserId;

    // 目的方 id
    @JSONField(name = "to_user_id")
    public String toUserId;

    // 群组 id
    @JSONField(name = "group_id")
    public String groupId;

    // message id
    @JSONField(name = "msg_id")
    public long msgId;

    // 消息体类型
    @JSONField(name = "mime_type")
    public String mimeType;

    // 消息体内容
    @JSONField(name = "content")
    public String content;

    // 服务器端接收到消息的时间
    @JSONField(name = "send_time")
    public long sendTime;

    // 消息的类型
    @JSONField(name = "type")
    public int type;

    public static Message convertToMsg(SessionMessageEntity entity) {
        Message m = new Message();

        m.rId = entity.RId;
        m.fromUserId = Long.toString(entity.FromUserID);
        m.toUserId = Long.toString(entity.ToUserID);
        m.msgId = entity.MsgID;
        m.mimeType = entity.MimeType;
        m.content = entity.Content;
        m.sendTime = entity.SendTime;
        m.type = MessageType.Session.getValue();

        return m;
    }

    public static Message convertToMsg(SessionMessageEntity entity, String fromUserId, String groupId) {
        Message m = new Message();
        m.rId = entity.RId;
        m.fromUserId = fromUserId;
        m.groupId = groupId;
        m.msgId = entity.MsgID;
        m.mimeType = entity.MimeType;
        m.content = entity.Content;
        m.sendTime = entity.SendTime;
        m.type = MessageType.Group.getValue();

        return m;
    }
}
