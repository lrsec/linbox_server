package com.medtree.im.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lrsec on 6/29/15.
 */
public class PullOldMsgResponse extends ByteCreator {
    // client request id
    @JSONField(name = "r_id")
    public long rId;

    // user chat id
    @JSONField(name = "user_chat_id")
    public String userChatId;

    // remote chatter id
    @JSONField(name = "remote_chat_id")
    public String remoteChatId;

    @JSONField(name = "group_chat_id")
    public String groupChatId;

    // 消息
    @JSONField(name = "msgs")
    public Message[] msgs;

    // 状态码, 200 = success
    @JSONField(name = "status")
    public int status;

    // 错误信息
    @JSONField(name = "err_msg")
    public String errMsg;

    // 消息类型
    @JSONField(name = "type")
    public int type;

    // request 中携带的 max message id 信息，冗余仅用于简化客户端操作
    @JSONField(name = "max_msg_id_in_request")
    public long maxMsgIdInRequest;

    // request type ，冗余，仅用于简化客户端操作
    @JSONField(name = "request_type")
    public int requestType;
}
