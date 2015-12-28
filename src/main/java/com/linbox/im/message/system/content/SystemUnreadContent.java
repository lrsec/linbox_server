package com.linbox.im.message.system.content;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lrsec on 9/23/15.
 */
public class SystemUnreadContent {

    /** 未读数 */
    @JSONField(name = "unread")
    public long unread;
}
