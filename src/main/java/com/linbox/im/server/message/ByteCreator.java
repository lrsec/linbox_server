package com.linbox.im.server.message;

import com.alibaba.fastjson.JSON;
import com.linbox.exceptions.IMException;
import com.linbox.im.server.message.enums.RequestResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lrsec on 7/4/15.
 *
 * 提供了消息体自生成可用于 netty 发送的 byte[] 的共更能
 */
public class ByteCreator {
    private static Logger logger = LoggerFactory.getLogger(ByteCreator.class);

    public MessageWrapper toWrapper(MonitorMeta meta) {
        MessageWrapper wrapper = new MessageWrapper();

        RequestResponseType type = RequestResponseType.parse(this);

        if (type == null) {
            logger.error("无法解析当前类型 {}", this.getClass().getName());
            throw new IMException("无法解析当前类型 " + this.getClass().getName());
        }

        wrapper.type = type;
        wrapper.monitorMeta = meta;
        wrapper.content = this;

        return wrapper;
    }

    public String toWrapperJson(MonitorMeta meta) {
        return JSON.toJSONString(toWrapper(meta));
    }
}
