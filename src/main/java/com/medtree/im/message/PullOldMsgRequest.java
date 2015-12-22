package com.medtree.im.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lrsec on 6/29/15.
 */
public class PullOldMsgRequest extends ByteCreator{

    public static final long MAX = -1l;

    // 客户端 request id
    @JSONField(name = "r_id")
    public long rId;

    // user id
    @JSONField(name = "user_id")
    public String userId;

    // remote id
    @JSONField(name = "remote_id")
    public String remoteId;

    @JSONField(name = "group_id")
    public String groupId;

    // max message id want to pull
    @JSONField(name = "max_msg_id")
    public long maxMsgId;

    // min message id for this pulling
    @JSONField(name = "min_msg_id")
    public long minMsgId;

    // pull page size
    @JSONField(name = "limit")
    public int limit;

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

