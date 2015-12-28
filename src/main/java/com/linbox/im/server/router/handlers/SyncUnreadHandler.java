package com.linbox.im.server.router.handlers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linbox.im.exceptions.IMConsumerException;
import com.linbox.im.exceptions.IMException;
import com.linbox.im.message.*;
import com.linbox.im.server.service.IInboxService;
import com.linbox.im.server.service.IOutboxService;
import com.linbox.im.utils.IMUtils;
import com.linbox.im.server.storage.UnreadLoopData;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lrsec on 7/2/15.
 */
@Service
@Qualifier("syncUnreadHandler")
public class SyncUnreadHandler implements Handler<String, String> {

    private static Logger logger = LoggerFactory.getLogger(SyncUnreadHandler.class);

    @Autowired
    private IInboxService inboxService;

    @Autowired
    private IOutboxService outboxService;

    @Override
    public void handle(ConsumerRecord<String, String> record) {
        String json = record.value();
        
        try {
            logger.debug("Start handling SyncUnreadRequest: {}", json);

            MessageWrapper wrapper = JSON.parseObject(json, MessageWrapper.class);

            SyncUnreadRequest request = JSON.parseObject(((JSONObject)wrapper.content).toJSONString(), SyncUnreadRequest.class);
            wrapper.content = request;

            if(request == null) {
                logger.error("SyncUnreadRequest format is not correct. Json string: {}", json);
                return;
            }

            String userId = request.userId;
            if(StringUtils.isBlank(userId)) {
                logger.error("Can not find avaiable user id for SyncUnreadRequest {}", json);
                return;
            }

            try {
                MessageType type = MessageType.parse(request.type);
                if (type == null) {
                    logger.error("Can not parse MessageType in SyncUnreadRequest {}", json);

                    throw new IMException("Unknown MessageType " + Integer.toString(request.type));
                }

                List<String> unreadMsgs = new LinkedList<>();
                long nextOffset = 0;

                switch(type) {
                    case All:

                        UnreadLoopData data = inboxService.getAllJson(userId, request.offset, request.limit);
                        unreadMsgs.addAll(data.unreads);
                        nextOffset = data.nextOffset;

                        break;

                    case Session:

                        if(request.remoteId == null) {
                            logger.error("The remote chat id is null when getting session unread message for user {}", userId);
                            throw new IMException("The remote chat id is null when getting session unread message for user " + userId);
                        } else {
                            String remoteId = request.remoteId;

                            if (StringUtils.isBlank(remoteId)) {
                                logger.error("Can not find remote id for SyncUnreadRequest {}", json);
                                throw new IMException("Non existing SyncUnreadRequest " + json);
                            }

                            unreadMsgs.add(inboxService.getSessionJson(userId, IMUtils.getSessionKey(userId, remoteId)));
                        }

                        break;

                    case Group:

                        if (request.groupId == null) {
                            logger.error("The group id is null when getting group unread message for user {}", userId);
                            throw new IMException("The group id is null when getting group unread message for user " + userId);
                        } else {
                            String groupId = request.groupId;

                            if (StringUtils.isBlank(groupId)) {
                                logger.error("Can not find group id for SyncUnreadRequest " + json);

                                throw new IMException("Non existing SyncUnreadRequest " + json);
                            }

                            unreadMsgs.add(inboxService.getGroupJson(userId, groupId));
                        }

                        break;
                    default:
                        logger.error("Message type {} is not handled in SyncUnreadCallback. SyncUnreadRequest: {}", type.getValue(), json);
                        throw new IMException("Unhandled MessageType " + type.getValue() + " for SyncUnreadCallback");
                }

                sendSuccessResponse(userId, request, unreadMsgs, nextOffset);

            } catch(Exception e) {
                logger.error("Exception when handling SyncUnreadCallback.", e);

                sendFailResponse(userId, request, e.getMessage());
            }

        } catch (Exception e) {
            throw new IMConsumerException(e, json);
        }
    }

    private void sendSuccessResponse(String userId, SyncUnreadRequest request, List<String> result, long nextOffset) {
        List<UnreadMsg> msgs = new ArrayList<>(result.size());

        for (String r : result) {
            UnreadMsg m = JSON.parseObject(r, UnreadMsg.class);

            if (m == null) {
                logger.error("Can not parse UnreadMsg from json: {}", r);
                continue;
            }

            msgs.add(m);
        }

        SyncUnreadResponse response = new SyncUnreadResponse();
        response.rId = request.rId;
        response.userId = request.userId;
        response.remoteId = request.remoteId;
        response.groupId = request.groupId;
        response.type = request.type;
        response.offset = nextOffset;
        response.unreads = msgs.toArray(new UnreadMsg[0]);
        response.status = 200;

        outboxService.put(userId, response.toWrapperJson());
    }

    private void sendFailResponse(String userId, SyncUnreadRequest request, String errMsg) {
        SyncUnreadResponse errResp = new SyncUnreadResponse();
        errResp.rId = request.rId;
        errResp.userId = request.userId;
        errResp.remoteId = request.remoteId;
        errResp.groupId = request.groupId;
        errResp.type = request.type;
        errResp.offset = 0;
        errResp.unreads = new UnreadMsg[0];
        errResp.status = 500;
        errResp.errMsg = errMsg;

        outboxService.put(userId, errResp.toWrapperJson());
    }
}
