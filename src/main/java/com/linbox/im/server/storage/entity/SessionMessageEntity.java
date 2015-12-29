package com.linbox.im.server.storage.entity;

import com.linbox.im.utils.IMUtils;
import com.linbox.im.message.Message;

/**
 * Created by lrsec on 7/3/15.
 */
public class SessionMessageEntity {
    // 消息发送方的 rId
    public long RId;

    public String SessionId;

    // 发送方 id
    public long FromUserID;

    // 目的方 id
    public long ToUserID;

    // message id
    public long MsgID;

    // 消息体类型
    public String MimeType;

    // 消息体内容
    public String Content;

    // 服务器端接收到消息的时间
    public long SendTime;

    // 记录创建时间
    public long Created;

    public static SessionMessageEntity convertToDao(Message msg) {
        if (msg == null) {
            return null;
        }

        SessionMessageEntity dao = new SessionMessageEntity();

        dao.RId = msg.rId;
        dao.SessionId = IMUtils.getSessionKey(msg.fromUserId, msg.toUserId);
        dao.FromUserID = Long.parseLong(msg.fromUserId);
        dao.ToUserID = Long.parseLong(msg.toUserId);
        dao.MsgID = msg.msgId;
        dao.MimeType = msg.mimeType;
        dao.Content = msg.content;
        dao.SendTime = msg.sendTime == 0 ? System.currentTimeMillis() : msg.sendTime;
        dao.Created = dao.SendTime;

        return dao;
    }
}
