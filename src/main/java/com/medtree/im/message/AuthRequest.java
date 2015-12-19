package com.medtree.im.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lrsec on 6/29/15.
 */

public class AuthRequest extends ByteCreator{
    // 客户端 request id
    @JSONField(name = "r_id")
    public long rId;

    // user chat id
    @JSONField(name="user_chat_id")
    public String userChatId;

    // user token
    @JSONField(name = "token")
    public String token;

    // user ip
    @JSONField(name = "ip")
    public String ip;

    // user port
    @JSONField(name = "port")
    public int port;

    // device type
    @JSONField(name = "device")
    public String device;
}
