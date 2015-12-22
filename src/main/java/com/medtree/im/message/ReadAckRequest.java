package com.medtree.im.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lrsec on 6/29/15.
 */
public class ReadAckRequest extends ByteCreator{

    @JSONField(name = "r_id")
    public long rId;

    @JSONField(name = "user_id")
    public String userId;

    @JSONField(name = "remote_id")
    public String remoteId;

    @JSONField(name = "group_id")
    public String groupId;

    @JSONField(name = "msg_id")
    public long msgId;

    // 消息类型
    @JSONField(name = "type")
    public int type;
}

