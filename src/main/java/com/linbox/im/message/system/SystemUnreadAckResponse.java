package com.linbox.im.message.system;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lrsec on 9/23/15.
 */
public class SystemUnreadAckResponse {
    // 消息发送时的 rId
    @JSONField(name = "r_id")
    public long rId;
}
