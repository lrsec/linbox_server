package com.medtree.im.message;

import com.alibaba.fastjson.annotation.JSONField;
import com.medtree.im.server.storage.entity.GroupMessageEntity;
import com.medtree.im.server.storage.entity.SessionMessageEntity;
import org.apache.commons.lang.StringUtils;

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
    @JSONField(name = "mine_type")
    public String mineType;

    // 消息体内容
    @JSONField(name = "content")
    public String content;

    // 服务器端接收到消息的时间
    @JSONField(name = "send_time")
    public long sendTime;

    // 消息的类型
    @JSONField(name = "type")
    public int type;

    public static Message convertToMsg(SessionMessageEntity dao, String userId) {
        Message m = new Message();
        m.rId = dao.RId;
        m.fromUserId = Long.toString(dao.FromUserID);
        m.toUserId = Long.toString(dao.ToUserID);
        m.msgId = dao.MsgID;
        m.mineType = dao.MineType;
        m.content = dao.Content;
        m.sendTime = dao.SendTime;
        m.type = MessageType.Session.getValue();

        return m;
    }

    public static Message convertToMsg(GroupMessageEntity dao, String fromUserId, String groupId) {
        Message m = new Message();
        m.rId = dao.RId;
        m.fromUserId = fromUserId;
        m.groupId = groupId;
        m.msgId = dao.MsgID;
        m.mineType = dao.MineType;
        m.content = dao.Content;
        m.sendTime = dao.SendTime;
        m.type = MessageType.Group.getValue();

        return m;
    }
}
