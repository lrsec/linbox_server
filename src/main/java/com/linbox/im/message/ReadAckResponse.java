package com.linbox.im.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lrsec on 6/29/15.
 */
public class ReadAckResponse extends ByteCreator {

    @JSONField(name = "r_id")
    public long rId;

    @JSONField(name = "user_id")
    public String userId;

    @JSONField(name = "remote_id")
    public String remoteId;

    @JSONField(name = "group_id")
    public String groupId;

    // 消息类型
    @JSONField(name = "type")
    public int type;

    @JSONField(name = "status")
    public int status;

    @JSONField(name = "err_code")
    public String errCode;
}
