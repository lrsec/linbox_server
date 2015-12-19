package com.medtree.im.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lrsec on 7/6/15.
 */
public class Pong extends ByteCreator{
    // 客户端 request id
    @JSONField(name = "r_id")
    public long rId;
}
