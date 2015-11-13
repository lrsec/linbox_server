package com.linbox.im.server.message;

import com.linbox.im.server.message.enums.RequestResponseType;

/**
 * Created by lrsec on 7/10/15.
 */
public class MessageWrapper {
    public MonitorMeta monitorMeta;
    public RequestResponseType type;
    public Object content;
}
