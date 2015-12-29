package com.linbox.im.server.connector.tcp.handler;

import com.alibaba.fastjson.JSON;
import com.linbox.im.message.*;
import com.linbox.im.server.connector.tcp.constant.HandlerName;
import com.linbox.im.server.service.IOutboxService;
import com.linbox.im.server.storage.dao.IServerDAO;
import com.linbox.im.server.storage.dao.IUserDAO;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by lrsec on 6/28/15.
 */
public class AuthHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(AuthHandler.class);

    private ScheduledExecutorService executor;
    private int loopRatio = 500;
    private int maxHandleTimeInMills = 5 * 1000;
    private ScheduledFuture task;

    private long rId = -1;
    private String userId = null;
    private AES aes = null;
    private Base64.Encoder base64Encoder = null;

    private IOutboxService outboxService;
    private IUserDAO userDAO;
    private IServerDAO serverDAO;

    public AuthHandler(ClassPathXmlApplicationContext applicationContext, ScheduledExecutorService executor, int loopRatio, int maxHandleTimeInMills, AES aes) {
        this.executor = executor;
        this.loopRatio = loopRatio;
        this.maxHandleTimeInMills = maxHandleTimeInMills;
        this.aes = aes;
        this.outboxService = (IOutboxService)applicationContext.getBean("outboxService");
        this.userDAO = (IUserDAO) applicationContext.getBean("userDAO");
        this.serverDAO = (IServerDAO) applicationContext.getBean("serverDAO");
        this.base64Encoder = Base64.getEncoder();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception in AuthHandler. Close the connection for user: " + StringUtils.trimToEmpty(userId), cause);
        if (rId >= 0) {
            sendFailResponse(ctx, 500, cause.getMessage());
        }

        ctx.close();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("Received AuthRequest. Message: {}", JSON.toJSONString(msg));

        MessageWrapper wrapper = (MessageWrapper) msg;

        if (wrapper == null) {
            logger.error("Get an null message wrapper in message handler");
            ctx.close();
            return;
        }

        RequestResponseType type = wrapper.type;

        if (type == null || type != RequestResponseType.AuthRequestMsg) {
            logger.error("The first message from client: {} is not AuthRequest. Close the connection. Message type is {}", ctx.channel().remoteAddress(), msg.getClass().getName());
            ctx.close();
            return;
        }

        AuthRequest authRequest = (AuthRequest) wrapper.content;
        rId = authRequest.rId;
        boolean authenticated = isCertified(authRequest);

        if (authenticated) {
            logger.debug("User {} authenticated.", userId);

            InetSocketAddress address = (InetSocketAddress)ctx.channel().localAddress();
            String addressRecord = address.toString() + Long.toString(System.currentTimeMillis());
            serverDAO.registerConnection(userId, addressRecord);
            logger.debug("Create connection for user: {}. Remote address: {}.", userId, ctx.channel().remoteAddress());

            //TODO 动态密码的生成策略,在测试时关闭
//            resetPassword(ctx);

            task = executor.scheduleAtFixedRate(new SendMessageChecker(ctx.channel(), addressRecord), 0, loopRatio, TimeUnit.MILLISECONDS);

            IMMessageHandler imMsgHandler = (IMMessageHandler) ctx.pipeline().get(HandlerName.HANDLER_MSG);
            imMsgHandler.setUserId(userId);
            IMIdleStateHandler imIdleStateHandler = (IMIdleStateHandler) ctx.pipeline().get(HandlerName.HANDLER_IDLE);
            imIdleStateHandler.setUserId(userId);

            ctx.pipeline().remove(this);

            sendSuccessResponse(ctx);

            return;
        } else {
            logger.error("User {} is not certified. Close the connection.", userId);
            sendFailResponse(ctx, 401, "User is not certified");

            ctx.close();
            return;
        }
    }

    private boolean isCertified(AuthRequest request) {
        userId = request.userId;
        return userDAO.isUserValid(request.userId, request.token);
    }

    private void resetPassword(final ChannelHandlerContext ctx) {
        String password = serverDAO.getPassword(Long.parseLong(userId));

        if (StringUtils.isBlank(password)) {
            logger.error("Can not get im password for user: {}", userId);
            sendFailResponse(ctx, 400, "Can not find user im password");
            return;
        } else {
            aes.resetPassword(password);
        }
    }

    private class SendMessageChecker implements Runnable {
        private Logger logger = LoggerFactory.getLogger(SendMessageChecker.class);

        private volatile boolean isConnectionClosed = false;
        private Channel ch;
        private String linkRecord;

        public SendMessageChecker(Channel ch, String linkRecord) {
            this.ch = ch;
            this.linkRecord = linkRecord;
        }

        public void run() {
            long start = System.currentTimeMillis();
            while ((System.currentTimeMillis() - start <= maxHandleTimeInMills)) {
                try {
                    if(shouldClose()) {
                        terminate();
                        return;
                    }

                    final String msg = outboxService.get(userId);

                    if (StringUtils.isBlank(msg)) {
                        return;
                    }

                    final MessageWrapper wrapper = JSON.parseObject(msg, MessageWrapper.class);

                    ChannelFuture future = ch.writeAndFlush(wrapper);

                    logger.debug("Tcp sender send message for {}. Message type: {}. Message body: {}", userId, wrapper.type, msg);

                    future.addListener(new GenericFutureListener() {
                        public void operationComplete(Future future) throws Exception {
                            if (!future.isSuccess()) {
                                logger.info("Network I/O write fail. Should close connection for user {}.", userId);
                                isConnectionClosed = true;

                                Throwable t = future.cause();
                                if (t != null) {
                                    logger.info("Send message fail", t);
                                }
                            } else {
                                logger.debug("Tcp sender send message success. user: {}. Message type: {}. Message body: {}", userId, wrapper.type, msg);
                            }
                        }
                    });

                } catch (Exception e) {
                    logger.error("Exception in sending task for user: " + userId + ". But the sending loop will continue to work", e);
                    return;
                }
            }
        }

        private boolean shouldClose() {
            if (!ch.isActive()) {
                logger.warn("Connection for user {} is inactive, should terminate the sending task.", userId);
                return true;
            }

            if(isConnectionClosed) {
                logger.warn("Connection for user {} is closed, should terminate the sending task.", userId);
                return true;
            }

            String record = serverDAO.getConnection(userId);

            if(!StringUtils.equals(record, linkRecord)) {
                logger.warn("Connection is updated, should terminate the sending task for user {}. Local address {}. New connection address: {}", userId, StringUtils.trimToEmpty(linkRecord), StringUtils.trimToEmpty(record));

                sendOfflineInfo();
                return true;
            }

            return false;
        }

        private void terminate() {
            logger.info("Terminate sending task for user {}", userId);

            task.cancel(false);

            try {
                ch.close();
            } catch (Exception e) {
                logger.error("Exception when terminate sending task", e);
            }
        }

        private void sendOfflineInfo() {
            OfflineInfo offlineInfo = new OfflineInfo();
            offlineInfo.userId = userId;

            MessageWrapper wrapper = offlineInfo.toWrapper();
            ch.writeAndFlush(wrapper);
        }
    }

    private void sendSuccessResponse(ChannelHandlerContext ctx) {
        AuthResponse response = new AuthResponse();
        response.rId = rId;
        response.userId = userId;
        response.status = 200;
        response.sendTime = System.currentTimeMillis();

        MessageWrapper responseWrapper = response.toWrapper();
        ctx.channel().writeAndFlush(responseWrapper);
    }

    private void sendFailResponse(ChannelHandlerContext ctx, int status, String errCode) {

        AuthResponse response = new AuthResponse();
        response.rId = rId;
        response.userId = userId;
        response.status = status;
        response.errMsg = errCode;
        response.sendTime = System.currentTimeMillis();

        MessageWrapper responseWrapper = response.toWrapper();
        ctx.channel().writeAndFlush(responseWrapper);
    }
}
