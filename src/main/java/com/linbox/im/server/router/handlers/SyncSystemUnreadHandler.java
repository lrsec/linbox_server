package com.linbox.im.server.router.handlers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linbox.im.exceptions.IMConsumerException;
import com.linbox.im.message.MessageWrapper;
import com.linbox.im.message.system.SyncSystemUnreadRequest;
import com.linbox.im.message.system.SyncSystemUnreadResponse;
import com.linbox.im.message.system.SystemMessage;
import com.linbox.im.message.system.content.SystemUnreadContent;
import com.linbox.im.server.constant.RedisKey;
import com.linbox.im.server.service.IOutboxService;
import com.linbox.im.message.system.SystemMessageTypes;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lrsec on 9/24/15.
 */
@Service
@Qualifier("syncSystemUnreadHandler")
public class SyncSystemUnreadHandler implements Handler<String, String> {
    private static Logger logger = LoggerFactory.getLogger(SyncSystemUnreadHandler.class);

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private IOutboxService outboxService;

    @Override
    public void handle(ConsumerRecord<String, String> record) {
        String json = record.value();
        
        try {
            logger.debug("Start handling SyncSystemUnreadRequest: {}", json);

            MessageWrapper wrapper = JSON.parseObject(json, MessageWrapper.class);

            SyncSystemUnreadRequest request = JSON.parseObject(((JSONObject)wrapper.content).toJSONString(), SyncSystemUnreadRequest.class);
            wrapper.content = request;

            if (request == null) {
                logger.error("SyncSystemUnreadCallback format is not correct. Json string: {}", json);
                return;
            }

            String userId = request.userId;

            try {
                long newFriendsCount = 0;
                long newMessageCount = 0;

                try (Jedis jedis = jedisPool.getResource()) {
                    newFriendsCount = jedis.hlen(RedisKey.getFriendRequestsKey(Long.parseLong(userId)));
                    newMessageCount = jedis.llen(RedisKey.getUnreadMessageNotifyKey(Long.parseLong(userId)));
                }

                List<SystemMessage> messages = new ArrayList<>(2);

                SystemUnreadContent newFriendsContent = new SystemUnreadContent();
                newFriendsContent.unread = newFriendsCount;
                SystemMessage newFriends = new SystemMessage();
                newFriends.systemType = SystemMessageTypes.NewFriend.getValue();
                newFriends.content = JSON.toJSONString(newFriendsContent);
                messages.add(newFriends);

                SystemUnreadContent newMessageContent = new SystemUnreadContent();
                newMessageContent.unread = newMessageCount;
                SystemMessage newMessages = new SystemMessage();
                newMessages.systemType = SystemMessageTypes.NoticeNotify.getValue();
                newMessages.content = JSON.toJSONString(newMessageContent);
                messages.add(newMessages);

                sendSuccessResponse(userId, messages, request.rId );
            } catch (Exception e) {
                logger.error("Exception in SyncSystemUnreadCallback", e);

                sendFailResponse(userId, e.toString(), request.rId);
            }
        } catch (Exception e) {
            throw new IMConsumerException(e, json);
        }
    }

    private void sendSuccessResponse(String userId, List<SystemMessage> messages, long rid) {

        SyncSystemUnreadResponse response = new SyncSystemUnreadResponse();
        response.status = 200;
        response.errMsg = "";
        response.userId = userId;
        response.rId = rid;
        response.unreads = messages.toArray(new SystemMessage[0]);

        outboxService.put(userId, response.toWrapperJson());
    }

    private void sendFailResponse(String userId, String errMsg, long rid) {
        SyncSystemUnreadResponse response = new SyncSystemUnreadResponse();
        response.status = 500;
        response.errMsg = errMsg;
        response.userId = userId;
        response.rId = rid;
        response.unreads = new SystemMessage[0];

        outboxService.put(userId, response.toWrapperJson());
    }
}
