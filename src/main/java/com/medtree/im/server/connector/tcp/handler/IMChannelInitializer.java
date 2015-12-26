package com.medtree.im.server.connector.tcp.handler;

import com.medtree.im.server.connector.tcp.constant.HandlerName;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by lrsec on 7/19/15.
 */
public class IMChannelInitializer extends ChannelInitializer<SocketChannel> {
    private static final int SENDER_LOOP_RATIO_MILLIS = 500;
    private static final int MAX_HANDLE_TIME_MILLIS = 5 * 1000;
    private static final int IDLE_LOOP_IN_SEC = 60;

    private ClassPathXmlApplicationContext appContext;
    private ScheduledExecutorService executorService;

    public IMChannelInitializer (ClassPathXmlApplicationContext appContext, ScheduledExecutorService executorService) {
        this.appContext = appContext;
        this.executorService = executorService;
    }

    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        AES aes = new AES();

        ChannelPipeline p = sc.pipeline();

        p.addLast(HandlerName.HANDLER_DECODER, new IMMessageDecoder(aes));

        p.addLast(HandlerName.HANDLER_AUTH, new AuthHandler(appContext,executorService, SENDER_LOOP_RATIO_MILLIS, MAX_HANDLE_TIME_MILLIS, aes));
        p.addLast(HandlerName.HANDLER_MSG, new IMMessageHandler(appContext));

        p.addLast(new IdleStateHandler(0, 0, IDLE_LOOP_IN_SEC));
        p.addLast(HandlerName.HANDLER_IDLE, new IMIdleStateHandler());

        p.addLast(new IMMessageEncoder(aes));
    }
}
