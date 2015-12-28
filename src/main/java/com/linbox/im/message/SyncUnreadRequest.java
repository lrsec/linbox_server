package com.linbox.im.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lrsec on 6/29/15.
 */
public class SyncUnreadRequest extends ByteCreator{
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

    // 分页数据，本次分页起始偏移量
    @JSONField(name = "offset")
    public long offset;

    // 分页数据，分业内数据量
    @JSONField(name = "limit")
    public int limit;

    // 消息类型
    // 非必填项，不填写则拉取所有类型的信息
    // 如果填写，则必须包含对应类型的 remoteChatId 或者 groupChatId
    @JSONField(name = "type")
    public int type = 1;
}

