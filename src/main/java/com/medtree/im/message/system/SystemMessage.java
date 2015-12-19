package com.medtree.im.message.system;

import com.alibaba.fastjson.annotation.JSONField;
import com.medtree.im.message.ByteCreator;

/**
 * Created by lrsec on 9/7/15.
 */
public class SystemMessage extends ByteCreator {

    @JSONField(name = "system_type")
    public int systemType;

    @JSONField(name = "content")
    public String content;
}
