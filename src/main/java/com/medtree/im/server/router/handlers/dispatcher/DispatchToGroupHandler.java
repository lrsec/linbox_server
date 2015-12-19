package com.medtree.im.server.router.handlers.dispatcher;

import com.alibaba.fastjson.JSON;
import com.medtree.im.exceptions.IMConsumerException;
import com.medtree.im.exceptions.IMException;
import com.medtree.im.message.Message;
import com.medtree.im.message.MessageType;
import com.medtree.im.server.router.handlers.Handler;
import com.medtree.im.server.storage.dao.IGroupDAO;
import com.medtree.im.server.storage.dao.IUserDAO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lrsec on 8/26/15.
 */
@Service
@Qualifier("dispatchToGroupHandler")
public class DispatchToGroupHandler implements Handler<String, String> {
    private static final Logger logger = LoggerFactory.getLogger(DispatchToGroupHandler.class);
    private final ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    @Autowired
    private IGroupDAO groupDAO;

    @Autowired
    private IUserDAO userDAO;

    @Autowired
    private SendDispatcher dispatcher;

    @Override
    public void handle(ConsumerRecord<String, String> record) {
        String json = record.value();

        try {
            final Message groupMessage = JSON.parseObject(json, Message.class);

            if (groupMessage == null) {
                throw new IMException("Message could not be parsed correctly. Message: " + json);
            }

            final String groupId = groupMessage.groupId;
            List<String> members = groupDAO.getGroupMembers(groupId);

            for(final String userId : members) {

                if(userId == null || userId.equalsIgnoreCase(groupMessage.fromUserId)) {
                    continue;
                }

                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        String userChatId = userDAO.getUserChatId(userId);
                        dispatcher.dispatchToSingle(userId, userChatId, groupId, groupId, MessageType.Group, groupMessage);
                    }
                });
            }
        } catch (Exception e) {
            throw new IMConsumerException(e, json);
        }
    }
}
