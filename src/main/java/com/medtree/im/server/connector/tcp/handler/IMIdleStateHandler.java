package com.medtree.im.server.connector.tcp.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lrsec on 7/8/15.
 */
public class IMIdleStateHandler extends ChannelDuplexHandler {

    private static Logger logger = LoggerFactory.getLogger(IMIdleStateHandler.class);
    private String userId = null;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;

            logger.info("Can not receive heartbeat from user {}. Close the connection.", userId);

            ctx.close();
        }
    }

    void setUserId(String u) {
        userId = u;
    }
}
