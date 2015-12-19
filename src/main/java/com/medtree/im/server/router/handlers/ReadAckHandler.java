package com.medtree.im.server.router.handlers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.medtree.im.exceptions.IMConsumerException;
import com.medtree.im.exceptions.IMException;
import com.medtree.im.message.*;
import com.medtree.im.server.monitor.MonitorMeta;
import com.medtree.im.server.service.IConsumerMonitorService;
import com.medtree.im.server.service.IInboxService;
import com.medtree.im.server.service.IOutboxService;
import com.medtree.im.server.storage.dao.IGroupDAO;
import com.medtree.im.server.storage.dao.IUserDAO;
import com.medtree.im.utils.IMUtils;
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
@Qualifier("readAckHandler")
public class ReadAckHandler implements Handler<String, String> {
    private static Logger logger = LoggerFactory.getLogger(ReadAckHandler.class);

    @Autowired
    private IConsumerMonitorService consumerMonitorService;

    @Autowired
    private IUserDAO userDAO;

    @Autowired
    private IInboxService inboxService;

    @Autowired
    private IGroupDAO groupDAO;

    @Autowired
    private IOutboxService outboxService;

    @Override
    public void handle(ConsumerRecord<String, String> record) {
        long nsqEndTime = System.currentTimeMillis();
        String json = record.value();

        try {
            logger.debug("Start handling ReadAckRequest: {}", json);

            MessageWrapper wrapper = JSON.parseObject(json, MessageWrapper.class);
            wrapper.monitorMeta.setNsqEnd(nsqEndTime);

            ReadAckRequest request = JSON.parseObject(((JSONObject)wrapper.content).toJSONString(), ReadAckRequest.class);
            wrapper.content = request;

            String userId = userDAO.getUserId(request.userChatId);
            if(StringUtils.isBlank(userId)) {
                logger.error("Can not find avaiable user id for ReadAckRequest {}", json);
                return;
            }

            try {
                MessageType type = MessageType.parse(request.type);
                if (type == null) {
                    logger.error("Can not parse MessageType in ReadAckRequest {}", json);

                    throw new IMException("Unknown MessageType " + Integer.toString(request.type));
                }

                switch(type) {
                    case Session:
                        String remoteId = userDAO.getUserId(request.remoteChatId);

                        if (StringUtils.isBlank(remoteId)) {
                            logger.error("Can not find remote id for ReadAckRequest {}", json);

                            throw new IMException("Non existing remote_chat_id " + StringUtils.trimToEmpty(request.remoteChatId));
                        }

                        inboxService.removeSessionMsg(userId, IMUtils.getSessionKey(userId, remoteId), request.msgId);

                        sendSuccessResponse(userId, request, wrapper.monitorMeta);

                        break;
                    case Group:
                        String groupId = groupDAO.getGroupId(request.groupChatId);

                        if (StringUtils.isBlank(groupId)) {
                            logger.error("Can not find group id for ReadAckRequest {}", json);

                            throw new IMException("Non existing group_chat_id " + StringUtils.trimToEmpty(request.groupChatId));
                        }

                        inboxService.removeGroupMsg(userId, groupId, request.msgId);

                        sendSuccessResponse(userId, request, wrapper.monitorMeta);

                        break;
                    default:
                        logger.error("Message type {} is not handled in ReadAckCallback. ReadAckRequest: {}", type.getValue(), json);
                        throw new IMException("Unhandled MessageType " + type.getValue() + " for ReadAckCallback");
                }

            } catch (Exception e) {
                logger.error("Exception when handling ReadAckRequest.", e);

                sendFailResponse(userId, request, e.getMessage());
            }

        } catch (Exception e) {
            consumerMonitorService.addFailCount(RequestResponseType.ReadAckRequestMsg);
            throw new IMConsumerException(e, json);
        }
    }

    private void sendSuccessResponse(String userId, ReadAckRequest request, MonitorMeta meta) {
        long current = System.currentTimeMillis();
        meta.setDataComputeEnd(current);

        ReadAckResponse response = new ReadAckResponse();
        response.rId = request.rId;
        response.userChatId = request.userChatId;
        response.remoteChatId = request.remoteChatId;
        response.groupChatId = request.groupChatId;
        response.type = request.type;
        response.status = 200;

        outboxService.put(userId, response.toWrapperJson(meta));

        consumerMonitorService.addSuccessTreat(RequestResponseType.ReadAckRequestMsg, meta);
    }

    private void sendFailResponse(String userId, ReadAckRequest request, String errMsg) {
        MonitorMeta meta = new MonitorMeta();

        ReadAckResponse response = new ReadAckResponse();
        response.rId = request.rId;
        response.userChatId = request.userChatId;
        response.remoteChatId = request.remoteChatId;
        response.groupChatId = request.groupChatId;
        response.type = request.type;
        response.status = 500;
        response.errCode = errMsg;

        outboxService.put(userId, response.toWrapperJson(meta));

        consumerMonitorService.addFailCount(RequestResponseType.ReadAckRequestMsg);
    }
}
