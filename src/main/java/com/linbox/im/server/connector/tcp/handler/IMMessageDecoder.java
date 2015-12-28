package com.linbox.im.server.connector.tcp.handler;

import com.alibaba.fastjson.JSON;
import com.linbox.im.message.MessageWrapper;
import com.linbox.im.message.RequestResponseType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by lrsec on 6/29/15.
 */
public class IMMessageDecoder extends ByteToMessageDecoder{
    private static Logger logger = LoggerFactory.getLogger(IMMessageDecoder.class);

    private short version = -1;
    private RequestResponseType requestResponseType = null;
    private int contentSize = -1;

    private AES aes;

    public IMMessageDecoder(AES aes) {
        this.aes = aes;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            if (version < 0) {
                if (in.readableBytes() >= 2) {
                    version = in.readShort();

                    if (version < 0) {
                        logger.error("Unknow IM Message Version for {}", version);

                        reset();
                        return;
                    }

                    logger.debug("Version: {}", version);
                }
            }

            if (requestResponseType == null) {
                if (in.readableBytes() >= 2) {
                    int value = in.readShort();
                    requestResponseType = RequestResponseType.parse(value);

                    if (requestResponseType == null) {
                        logger.error("Unknown IM message type for {}.", value);

                        reset();
                        return;
                    }

                    logger.debug("Request type: {}", requestResponseType.getName());
                } else {
                    return;
                }
            }

            if (requestResponseType != null && contentSize < 0) {
                if (in.readableBytes() >= 4) {
                    contentSize = in.readInt();

                    if(contentSize <= 0) {
                        logger.error("Get illegal IM message content size: {} for message type: {}", contentSize, requestResponseType.getName());

                        reset();
                        return;
                    }

                    logger.debug("Request type: {}. Content Length: {}", requestResponseType.getName(), contentSize);
                } else {
                    return;
                }
            }

            if (requestResponseType != null && contentSize > 0) {
                if (in.readableBytes() >= contentSize) {

                    try {
                        byte[] buf = in.readBytes(contentSize).array();

                        long startTime = System.currentTimeMillis();

                        String json = aes.decrypt(buf);

                        long decryptEndTime = System.currentTimeMillis();

                        Object message = JSON.parseObject(json, requestResponseType.getClazz());

                        if (message == null) {
                            logger.error("Can not parse message content from json string. Message Type: {}. Message Content Size: {}. Message Content: {}.", requestResponseType.getName());
                        } else {
                            logger.debug("Request type: {}. Content Length: {}. Request: {}", requestResponseType.getName(), contentSize, new String(buf));

                            MessageWrapper wrapper = new MessageWrapper();
                            wrapper.type = requestResponseType;
                            wrapper.content = message;

                            out.add(wrapper);
                        }
                    } catch (Exception e) {
                        logger.error("Parse request body fail. ", e);
                    } finally {
                        reset();
                    }
                }

                return;
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private void reset() {
        version = -1;
        requestResponseType = null;
        contentSize = -1;
    }
}
