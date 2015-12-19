package com.medtree.im.server.router.handlers.dispatcher;

import com.alibaba.fastjson.JSON;
import com.medtree.im.exceptions.IMException;
import com.medtree.im.message.Message;
import com.medtree.im.message.MessageType;
import com.medtree.im.server.constant.MessageTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lrsec on 8/26/15.
 */
@Service
public class SendDispatcher {
    private static Logger logger = LoggerFactory.getLogger(SendDispatcher.class);

    @Autowired
    private KafkaProducer<String, String> kafkaProducer;

    // dispatch to single user
    public void dispatchToSingle(String userId, String userChatId, String remoteChatId, String sessionKey, MessageType type, Message msg) {
        try {
            SendDispatchMessage message = new SendDispatchMessage();
            message.setUserId(userId);
            message.setUserChatId(userChatId);
            message.setRemoteChatId(remoteChatId);
            message.setSessionKey(sessionKey);
            message.setMessage(msg);
            message.setType(type);

            String json = JSON.toJSONString(message);
            if (json == null) {
                throw new IMException("Message to user " + userId + "can not be parsed to json correctly.");
            }

            kafkaProducer.send(new ProducerRecord<String, String>(MessageTopic.TOPIC_DISPATCH_SEND_SINGLE, json));
        } catch (Exception e) {
            logger.error("Dispatch send message to single user " + userId + "fail.", e);
        }
    }

    //dispatch to group
    public void dispatchToGroup(String groupId, Message message) {
        try {
            String json = JSON.toJSONString(message);
            if (json == null) {
                throw new IMException("Message to group " + groupId + "can not be parsed to json correctly.");
            }

            kafkaProducer.send(new ProducerRecord<String, String>(MessageTopic.TOPIC_DISPATCH_SEND_GROUP, json));
        } catch (Exception e) {
            logger.error("Dispatch send message to group " + groupId + " fail.", e);
        }
    }

}
