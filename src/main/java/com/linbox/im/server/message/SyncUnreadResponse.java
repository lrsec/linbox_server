package com.linbox.im.server.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lrsec on 6/29/15.
 */
public class SyncUnreadResponse extends ByteCreator {
    // request id
    @JSONField(name="r_id")
    public long rId;

    // user chat id
    @JSONField(name="user_id")
    public String userId;

    // remote chatter id
    // 非必填项，填写则指定拉取一组对话的未读数信息。不填则拉取所有未读数信息
    @JSONField(name = "remote_id")
    public String remoteId;

    @JSONField(name = "group_id")
    public String groupId;

    @JSONField(name = "type")
    public int type = 1;

    // unread messages
    @JSONField(name="unreads")
    public UnreadMsg[] unreads;

    // 返回本次所取数据的起始 offset
    @JSONField(name = "offset")
    public long offset;

    // status. 200 = success
    @JSONField(name="status")
    public int status;

    // error message
    @JSONField(name="err_msg")
    public String errMsg;
}
