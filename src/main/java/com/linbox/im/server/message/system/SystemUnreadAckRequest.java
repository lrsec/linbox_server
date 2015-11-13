package com.linbox.im.server.message.system;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lrsec on 9/23/15.
 */
public class SystemUnreadAckRequest {
    /** 消息发送时的 rId */
    @JSONField(name = "r_id")
    public long rId;

    /** 用户 id */
    @JSONField(name = "user_id")
    public String userId;

    /** 未读数类型 */
    @JSONField(name = "type")
    public int type;
}
