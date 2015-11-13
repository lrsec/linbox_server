package com.linbox.im.server.message.system;

import com.alibaba.fastjson.annotation.JSONField;
import com.linbox.im.server.message.ByteCreator;

/**
 * Created by lrsec on 9/23/15.
 */
public class SyncSystemUnreadRequest extends ByteCreator {
    // 客户端 request id
    @JSONField(name = "r_id")
    public long rId;

    /** 要拉取未读数的用户 id */
    @JSONField(name = "user_id")
    public String userId;
}
