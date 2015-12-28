package com.linbox.im.message.system;

import com.alibaba.fastjson.annotation.JSONField;
import com.linbox.im.message.ByteCreator;

/**
 * Created by lrsec on 9/23/15.
 */
public class SyncSystemUnreadResponse extends ByteCreator {
    // 客户端 request id
    @JSONField(name = "r_id")
    public long rId;

    /** 用户 id */
    @JSONField(name = "user_id")
    public String userId;

    /** 系统未读数消息，直接复用了 SystemMessage */
    @JSONField(name = "unreads")
    public SystemMessage[] unreads;

    // status. 200 = success
    @JSONField(name="status")
    public int status;

    // error message
    @JSONField(name="err_msg")
    public String errMsg;
}
