package com.linbox.im.server.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lrsec on 6/29/15.
 */
public class AuthResponse extends ByteCreator {
    // 客户端 request id
    @JSONField(name = "r_id")
    public long rId;

    // user id
    @JSONField(name = "user_id")
    public String userId;

    // status code, 200 = success
    @JSONField(name = "status")
    public int status;

    // error message
    @JSONField(name = "err_msg")
    public String errMsg;

    // 发送该消息的服务器时间，毫秒
    @JSONField(name = "send_time")
    public long sendTime;
}
