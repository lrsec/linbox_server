package com.medtree.im.server.connector.tcp.handler;

import com.alibaba.fastjson.JSON;
import com.medtree.im.message.*;
import com.medtree.im.message.system.SyncSystemUnreadRequest;
import com.medtree.im.server.constant.MessageTopic;
import com.medtree.im.server.monitor.IConnectorMonitor;
import com.medtree.im.server.storage.dao.IUserDAO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by lrsec on 6/25/15.
 */
public class IMMessageHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(IMMessageHandler.class);
    private String userId = null;

    private IConnectorMonitor connectorMonitorService;
    private KafkaProducer<String,String> kafkaProducer;

    public IMMessageHandler(ClassPathXmlApplicationContext appContext) {
        connectorMonitorService = (IConnectorMonitor)appContext.getBean("connectorMonitor");
        kafkaProducer = (KafkaProducer)appContext.getBean("kafkaProducer");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        connectorMonitorService.incrConnCount();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        connectorMonitorService.decConnCount();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageWrapper wrapper = (MessageWrapper) msg;

        if (wrapper == null) {
            logger.error("Get an null message wrapper in message handler");
            return;
        }

        RequestResponseType type = wrapper.type;

        if (type == null) {
            logger.error("Illegal message type for user: {}. Message type: {}", userId, msg.getClass().getName());
            return;
        }

        String json = JSON.toJSONString(wrapper);

        logger.debug("Handle {}. Message content: {}", type.getName(), json);
        switch(type) {
            case SyncUnreadRequestMsg:

                if (!StringUtils.equalsIgnoreCase(userId, ((SyncUnreadRequest)wrapper.content).userId)) {
                    logger.error("The chat id is not match for {}. Authenticated user id: {}. Message content: {}", type.getName(), userId, json);
                    return;
                }

                kafkaProducer.send(new ProducerRecord<String, String>(MessageTopic.TOPIC_SYNC_UNREAD, json));
                break;

            case ReadAckRequestMsg:

                if (!StringUtils.equalsIgnoreCase(userId, ((ReadAckRequest)wrapper.content).userId)) {
                    logger.error("The chat id is not match for {}. Authenticated user id: {}. Message content: {}", type.getName(), userId, json);
                    return;
                }

                kafkaProducer.send(new ProducerRecord<String, String>(MessageTopic.TOPIC_READ_ACK, json));
                break;

            case PullOldMsgRequestMsg:

                if (!StringUtils.equalsIgnoreCase(userId, ((PullOldMsgRequest)wrapper.content).userId)) {
                    logger.error("The chat id is not match for {}. Authenticated user id: {}. Message content: {}", type.getName(), userId, json);
                    return;
                }

                kafkaProducer.send(new ProducerRecord<String, String>(MessageTopic.TOPIC_PULL_OLD_MEG, json));
                break;

            case SendMsgRequestMsg:

                if (!StringUtils.equalsIgnoreCase(userId, ((SendMsgRequest)wrapper.content).userId)) {
                    logger.error("The chat id is not match for {}. Authenticated user id: {}. Message content: {}", type.getName(), userId, json);
                    return;
                }

                kafkaProducer.send(new ProducerRecord<String, String>(MessageTopic.TOPIC_SEND_MSG,json));
                break;

            case Ping:

                if (!StringUtils.equalsIgnoreCase(userId, ((Ping)wrapper.content).userId)) {
                    logger.error("The chat id is not match for {}. Authenticated user id: {}. Message content: {}", type.getName(), userId, json);
                    return;
                }

                kafkaProducer.send(new ProducerRecord<String, String>(MessageTopic.TOPIC_PING, json));
                break;

            case SyncSystemUnreadRequestMsg:

                if (!StringUtils.equalsIgnoreCase(userId, ((SyncSystemUnreadRequest)wrapper.content).userId)) {
                    logger.error("The chat id is not match for {}. Authenticated user id: {}. Message content: {}", type.getName(), userId, json);
                    return;
                }

                kafkaProducer.send(new ProducerRecord<String, String>(MessageTopic.TOPIC_SYNC_SYSTEM_UNREAD, json));
                break;

            default:
                logger.error("{} is not handled right now. Message content: {}", type.getName(), JSON.toJSONString(msg));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("", cause);
    }

    void setUserId(String u) {
        userId = u;
    }
}
