package com.medtree.im.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lrsec on 7/9/15.
 */
public class OfflineInfo extends ByteCreator{
    // user chat id
    @JSONField(name="user_chat_id")
    public String userChatId;

    // 返回信息,客户端显示给用户下线原因
    @JSONField(name = "reason")
    public String reason;
}
