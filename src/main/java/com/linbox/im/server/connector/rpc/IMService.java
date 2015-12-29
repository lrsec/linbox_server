package com.linbox.im.server.connector.rpc;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.linbox.im.interfaces.IIMService;
import com.linbox.im.message.Message;
import com.linbox.im.message.MessageType;
import com.linbox.im.message.MessageWrapper;
import com.linbox.im.message.SendMsgRequest;
import com.linbox.im.message.system.SystemMessage;
import com.linbox.im.server.constant.MessageTopic;
import com.linbox.im.server.service.IOutboxService;
import com.linbox.im.server.storage.dao.IGroupDAO;
import com.linbox.im.server.storage.dao.IServerDAO;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * Created by lrsec on 7/27/15.
 */
public class IMService implements IIMService {
    private static Logger logger = LoggerFactory.getLogger(IMService.class);

    @Autowired
    private KafkaProducer<String,String> kafkaProducer;

    @Autowired
    private IOutboxService outboxService;

    @Autowired
    private IServerDAO serverDAO;

    @Autowired
    private IGroupDAO groupDAO;

    @Override
    public Set<String> getIMServerList() {
        return serverDAO.getServers();
    }

    @Override
    public String getPassword(long userId) {
        return serverDAO.generatePassword(userId);
    }

    @Override
    public void sendMessage(String fromUserId, String toId, String mineType, String content, int messageType, String creatorId) {
        logger.debug("Received message from rpc call. From: {}. To: {}. MimeType: {}. Content: {}. MessageType: {}. CreatorID: {}", fromUserId, toId, mineType, content, messageType, creatorId);

        if (Strings.isNullOrEmpty(fromUserId)) {
            logger.error("Sender user id could not be blank");
            return;
        }

        if (Strings.isNullOrEmpty(fromUserId)) {
            logger.error("Target user id could not be blank");
            return;
        }

        if (!isLegalSender(fromUserId)) {
            logger.warn("User {} is not allowed to send message through im rpc service", fromUserId);
            return;
        }

        long current = System.currentTimeMillis();

        String toUserId = null;
        String toGroupId = null;

        MessageType type = MessageType.parse(messageType);
        if (type == null) {
            logger.warn("Unknown message type {} for user {}. Stop sending messages", messageType, fromUserId);
            return;
        }

        switch (type) {
            case Session:
                toUserId = toId;
                break;
            case Group:
                toGroupId = toId;
                break;
            default:
                logger.warn("Unknown message type {} in sending message through rpc service.", type.getName());
                return;
        }

        Message msg = new Message();
        msg.rId = current;
        msg.fromUserId = fromUserId;
        msg.toUserId = toUserId;
        msg.groupId = toGroupId;
        msg.msgId = -1;
        msg.mimeType = mineType;
        msg.content = content;
        msg.sendTime = -1;
        msg.type = messageType;

        SendMsgRequest request = new SendMsgRequest();
        request.rId = current;
        request.userId = fromUserId;
        request.remoteId = toUserId;
        request.groupId = toGroupId;
        request.msg = msg;
        request.type = messageType;

        MessageWrapper wrapper = request.toWrapper();

        String json = JSON.toJSONString(wrapper);

        kafkaProducer.send(new ProducerRecord(MessageTopic.TOPIC_SEND_MSG, json));
    }

    // only allow users in white list to send message
    private boolean isLegalSender(String userId) {
        return true;
    }

    @Override
    public long generateGroupId() {
        return groupDAO.generateGroupId();
    }

    @Override
    public void sendSystemMessage(String targetUserId, int systemType, String content) {
        SystemMessage systemMessage = new SystemMessage();
        systemMessage.systemType = systemType;
        systemMessage.content = content;

        outboxService.put(targetUserId, systemMessage.toWrapperJson());
    }
}
