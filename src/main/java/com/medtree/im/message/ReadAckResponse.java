package com.medtree.im.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lrsec on 6/29/15.
 */
public class ReadAckResponse extends ByteCreator {

    @JSONField(name = "r_id")
    public long rId;

    @JSONField(name = "user_chat_id")
    public String userChatId;

    @JSONField(name = "remote_chat_id")
    public String remoteChatId;

    @JSONField(name = "group_chat_id")
    public String groupChatId;

    // 消息类型
    @JSONField(name = "type")
    public int type;

    @JSONField(name = "status")
    public int status;

    @JSONField(name = "err_code")
    public String errCode;
}
