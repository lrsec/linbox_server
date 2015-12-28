package com.linbox.im.server.connector.tcp.handler;

import com.alibaba.fastjson.JSON;
import com.linbox.im.message.MessageWrapper;
import com.linbox.im.message.RequestResponseType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lrsec on 7/10/15.
 */
public class IMMessageEncoder extends MessageToByteEncoder<MessageWrapper> {
    private static Logger logger = LoggerFactory.getLogger(IMMessageEncoder.class);

    private static short CURRENT_VERISON = 1;

    private AES aes;

    public IMMessageEncoder(AES aes) {
        this.aes = aes;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageWrapper messageWrapper, ByteBuf byteBuf) throws Exception {
        try {
            RequestResponseType type = messageWrapper.type;

            long encodeStart = System.currentTimeMillis();

            byte[] content = aes.encrypt(JSON.toJSONString(messageWrapper.content));

            long encodeEnd = System.currentTimeMillis();

            byteBuf.writeShort(CURRENT_VERISON);
            byteBuf.writeShort(type.getValue());
            byteBuf.writeInt(content.length);
            byteBuf.writeBytes(content);
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
