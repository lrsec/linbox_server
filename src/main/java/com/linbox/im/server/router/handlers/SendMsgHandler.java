package com.linbox.im.server.router.handlers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linbox.im.exceptions.IMConsumerException;
import com.linbox.im.exceptions.IMException;
import com.linbox.im.message.*;
import com.linbox.im.server.router.handlers.dispatcher.SendDispatcher;
import com.linbox.im.server.service.IOutboxService;
import com.linbox.im.server.storage.dao.IGroupMessageDAO;
import com.linbox.im.server.storage.dao.IMessageDAO;
import com.linbox.im.server.storage.dao.ISessionMessageDAO;
import com.linbox.im.server.storage.entity.SessionMessageEntity;
import com.linbox.im.utils.IMUtils;
import com.linbox.im.server.storage.entity.GroupMessageEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by lrsec on 7/2/15.
 */
@Service
@Qualifier("sendMsgHandler")
public class SendMsgHandler implements Handler<String, String> {
    private static Logger logger = LoggerFactory.getLogger(SendMsgHandler.class);

    @Autowired
    private ISessionMessageDAO sessionMessageDAO;

    @Autowired
    private IGroupMessageDAO groupMessageDAO;

    @Autowired
    private IMessageDAO messageDAO;

    @Autowired
    private IOutboxService outboxService;

    @Autowired
    private SendDispatcher dispatcher;

    @Override
    public void handle(ConsumerRecord<String, String> record) {
        String json = record.value();

        try {
            logger.debug("Start handling SendMsgRequest: {}", json);

            MessageWrapper wrapper = JSON.parseObject(json, MessageWrapper.class);

            SendMsgRequest request = JSON.parseObject(((JSONObject)wrapper.content).toJSONString(), SendMsgRequest.class);
            wrapper.content = request;

            if(request == null) {
                logger.error("SendMsgRequest format is not correct. Json string: {}", json);
                return;
            }

            String userId = request.userId;
            if(StringUtils.isBlank(userId)) {
                logger.error("Can not find avaiable user id for SendMsgRequest {}", json);
                return;
            }

            try {
                MessageType type = MessageType.parse(request.type);
                if (type == null) {
                    logger.error("Can not parse MessageType in SendMsgRequest {}", json);

                    throw new IMException("Unknown MessageType " + Integer.toString(request.type));
                }

                switch(type) {
                    case Session:

                        String remoteId = request.remoteId;
                        if (StringUtils.isBlank(remoteId)) {
                            logger.error("Can not find corresponding remote id for SendMsgRequest: {}", json);

                            throw new IMException("Cannot find remote id for SendMsgRequest: " + json);
                        }

                        Message body = request.msg;
                        if (body == null) {
                            logger.error("No msg in SendMsgRequest: {}", json);

                            throw new IMException("Empty message between " + userId + " and " + remoteId + ". rid: " + request.rId);
                        }

                        SessionMessageEntity dao = sessionMessageDAO.findMsgByRId(body.rId, body.fromUserId, body.toUserId);
                        if (dao == null) {
                            String sessionKey = IMUtils.getSessionKey(userId, remoteId);

                            body.msgId = messageDAO.generateMsgId(sessionKey);
                            body.sendTime = System.currentTimeMillis();
                            dao = sessionMessageDAO.insert(body);

                            logger.debug("Save SessionMessageDao into DB. Message: {}. Dao: {}", JSON.toJSONString(body), JSON.toJSONString(dao));

                            dispatcher.dispatchToSingle(remoteId, userId, sessionKey, MessageType.Session, body);

                        } else {
                            logger.debug("Find existing SessionMessageDao for message: {}. Dao: {}", JSON.toJSONString(body), JSON.toJSONString(dao));
                        }

                        sendSuccessResponse(userId, request, dao);

                        break;
                    case Group:

                        String groupId = request.groupId;
                        if (StringUtils.isBlank(groupId)) {
                            logger.error("Can not find corresponding group id for SendMsgRequest: {}", json);

                            throw new IMException("Cannot find group id for SendMsgRequest: " + json);
                        }

                        Message groupMsgBody = request.msg;
                        if (groupMsgBody == null) {
                            logger.error("No msg in SendMsgRequest: {}", json);

                            throw new IMException("Empty message between " + userId + " and group " + groupId + ". rid: " + request.rId);
                        }

                        GroupMessageEntity groupMsgDao = groupMessageDAO.findMsgByRId(groupMsgBody.rId, groupMsgBody.fromUserId, groupMsgBody.groupId);
                        if (groupMsgDao == null) {
                            groupMsgBody.msgId = messageDAO.generateMsgId(groupId);
                            groupMsgBody.sendTime = System.currentTimeMillis();
                            groupMsgDao = groupMessageDAO.insert(groupMsgBody);

                            logger.debug("Save GroupMessageDao into DB. Message: {}. Dao: {}", JSON.toJSONString(groupMsgBody), JSON.toJSONString(groupMsgDao));

                            dispatcher.dispatchToGroup(groupId, groupMsgBody);

                        } else {
                            logger.debug("Find existing GroupMessageDao for message: {}. Dao: {}", JSON.toJSONString(groupMsgBody), JSON.toJSONString(groupMsgDao));
                        }

                        sendSuccessResponse(userId, request, groupMsgDao);

                        break;
                    default:
                        logger.error("Message type {} is not handled in SendMsgRequest. SendMsgRequest: {}", type.getValue(), json);
                        throw new IMException("Unhandled MessageType " + type.getValue() + " for SendMsgCallback");
                }
            } catch (Exception e) {
                logger.error("Exception when handling SendMsgRequest.", e);
                sendFailResponse(userId, request, e.getMessage());
            }

        } catch (Exception e) {
            throw new IMConsumerException(e, json);
        }
    }

    private void sendSuccessResponse(String userId, SendMsgRequest request, SessionMessageEntity sessionEntity) {
        SendMsgResponse resp = new SendMsgResponse();
        resp.rId = request.rId;
        resp.msgRId = sessionEntity.RId;
        resp.userId = request.userId;
        resp.remoteId = request.remoteId;
        resp.groupId = request.groupId;
        resp.msgId = sessionEntity.MsgID;
        resp.sendTime = sessionEntity.SendTime;
        resp.type = request.type;
        resp.status = 200;

        outboxService.put(userId, resp.toWrapperJson());
    }

    private void sendSuccessResponse(String userId, SendMsgRequest request, GroupMessageEntity groupEntity) {
        SendMsgResponse resp = new SendMsgResponse();
        resp.rId = request.rId;
        resp.msgRId = groupEntity.RId;
        resp.userId = request.userId;
        resp.remoteId = request.remoteId;
        resp.groupId = request.groupId;
        resp.msgId = groupEntity.MsgID;
        resp.sendTime = groupEntity.SendTime;
        resp.type = request.type;
        resp.status = 200;

        outboxService.put(userId, resp.toWrapperJson());
    }

    private void sendFailResponse(String userId, SendMsgRequest request, String errMsg) {

        SendMsgResponse errResp = new SendMsgResponse();
        errResp.rId = request.rId;
        errResp.msgRId = request.msg.rId;
        errResp.userId = request.userId;
        errResp.remoteId = request.remoteId;
        errResp.groupId = request.groupId;
        errResp.type = request.type;
        errResp.status = 500;
        errResp.errMsg = errMsg;

        outboxService.put(userId, errResp.toWrapperJson());
    }


}
