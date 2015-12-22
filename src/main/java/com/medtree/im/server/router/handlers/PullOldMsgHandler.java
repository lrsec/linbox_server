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
import com.medtree.im.server.storage.dao.IGroupMessageDAO;
import com.medtree.im.server.storage.dao.ISessionMessageDAO;
import com.medtree.im.server.storage.dao.IUserDAO;
import com.medtree.im.server.storage.entity.GroupMessageEntity;
import com.medtree.im.server.storage.entity.SessionMessageEntity;
import com.medtree.im.utils.IMUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lrsec on 7/2/15.
 */
@Service
@Qualifier("pullOldMsgHandler")
public class PullOldMsgHandler implements Handler<String, String> {
    public static final Logger logger = LoggerFactory.getLogger(PullOldMsgHandler.class);

    @Autowired
    private IOutboxService outboxService;

    @Autowired
    private IConsumerMonitorService consumerMonitorService;

    @Autowired
    private ISessionMessageDAO sessionMessageDAO;

    @Autowired
    private IInboxService inboxService;

    @Autowired
    private IGroupDAO groupDAO;

    @Autowired
    private IGroupMessageDAO groupMessageDAO;

    @Override
    public void handle(ConsumerRecord<String, String> record) {
        long nsqEndTime = System.currentTimeMillis();
        String json = record.value();

        try {
            logger.debug("start handling PullOldMsgRequest: {}", json);
            MessageWrapper wrapper = JSON.parseObject(json, MessageWrapper.class);
            wrapper.monitorMeta.setNsqEnd(nsqEndTime);

            PullOldMsgRequest request = JSON.parseObject(((JSONObject)wrapper.content).toJSONString(), PullOldMsgRequest.class);
            wrapper.content = request;

            boolean shouldCleanUnread = false;
            if (request.maxMsgId == PullOldMsgRequest.MAX) {
                request.maxMsgId = Long.MAX_VALUE;
                shouldCleanUnread = true;
            }

            if (request.minMsgId < 0) {
                request.minMsgId = 0;
            }

            String userId = request.userId;
            if (StringUtils.isBlank(userId)) {
                logger.error("Can not find avaiable user id for PullOldMsgRequest {}", json);
                return;
            }

            try {
                MessageType type = MessageType.parse(request.type);
                if (type == null) {
                    logger.error("Can not parse MessageType in PullOldMsgRequest {}", json);

                    throw new IMException("Unknown MessageType " + Integer.toString(request.type));
                }

                List<Message> msgs = new LinkedList<Message>();

                switch (type) {
                    case Session:

                        String remoteId = request.remoteId;
                        if (StringUtils.isBlank(remoteId)) {
                            logger.error("Can not find corresponding remote id for PullOldMsgRequest: {}", json);

                            throw new IMException("Cannot find remote id for PullOldMsgRequest: " + json);
                        }

                        String sessionId = IMUtils.getSessionKey(userId, remoteId);
                        List<SessionMessageEntity> sessionDaos = sessionMessageDAO.findMsg(sessionId, request.maxMsgId, request.minMsgId, request.limit);

                        for (SessionMessageEntity dao : sessionDaos) {
                            Message m = Message.convertToMsg(dao, userId);
                            msgs.add(m);
                        }

                        if (shouldCleanUnread && !msgs.isEmpty()) {
                            inboxService.removeSessionMsg(userId, IMUtils.getSessionKey(userId, remoteId), msgs.get(0).msgId);
                        }

                        break;
                    case Group:

                        String groupId = request.groupId;
                        if (StringUtils.isBlank(groupId)) {
                            logger.error("Can not find corresponding groupId id for PullOldMsgRequest: {}", json);

                            throw new IMException("Cannot find groupId id for PullOldMsgRequest: " + json);
                        }

                        List<GroupMessageEntity> groupDaos = groupMessageDAO.findMsg(groupId, request.maxMsgId, request.minMsgId, request.limit);

                        for (GroupMessageEntity dao : groupDaos) {

                            String fromUserId = Long.toString(dao.FromUserID);
                            Message m = Message.convertToMsg(dao, fromUserId, groupId);

                            msgs.add(m);
                        }

                        if (shouldCleanUnread && !msgs.isEmpty()) {
                            inboxService.removeGroupMsg(userId, groupId, msgs.get(0).msgId);
                        }

                        break;
                    default:
                        logger.error("Message type {} is not handled in SyncUnreadCallback. PullOldMsgRequest: {}", type.getValue(), json);
                        throw new IMException("Unhandled MessageType " + type.getValue() + " for PullOldMsgCallback");
                }

                sendSuccessResponse(userId, request, msgs, wrapper.monitorMeta);

            } catch (Exception e) {
                logger.error("Pull old message handler fail with exception. Send fail response. Message: " + json, e);
                sendFailResponse(userId, request, e.getMessage());
            }
        } catch (Exception e) {
            consumerMonitorService.addFailCount(RequestResponseType.PullOldMsgRequestMsg);
            throw new IMConsumerException(e, json);
        }
    }

    private void sendSuccessResponse(String userId, PullOldMsgRequest request, List<Message> result, MonitorMeta meta) {
        long current = System.currentTimeMillis();
        meta.setDataComputeEnd(current);

        PullOldMsgResponse response = new PullOldMsgResponse();
        response.rId = request.rId;
        response.userId = request.userId;
        response.remoteId = request.remoteId;
        response.groupId = request.groupId;
        response.msgs = result.toArray(new Message[0]);
        response.type = request.type;
        response.maxMsgIdInRequest = (request.maxMsgId == Long.MAX_VALUE) ? -1 : request.maxMsgId;
        response.requestType = request.requestType;
        response.status = 200;
        outboxService.put(userId, response.toWrapperJson(meta));

        consumerMonitorService.addSuccessTreat(RequestResponseType.PullOldMsgRequestMsg, meta);
    }

    private void sendFailResponse(String userId, PullOldMsgRequest request, String errMsg) {
        MonitorMeta meta = new MonitorMeta();

        PullOldMsgResponse errResp = new PullOldMsgResponse();

        errResp.rId = request.rId;
        errResp.userId = request.userId;
        errResp.remoteId = request.remoteId;
        errResp.groupId = request.groupId;

        errResp.msgs = new Message[0];
        errResp.type = request.type;
        errResp.maxMsgIdInRequest = (request.maxMsgId == Long.MAX_VALUE) ? -1 : request.maxMsgId;
        errResp.requestType = request.requestType;
        errResp.status = 500;
        errResp.errMsg = errMsg;

        outboxService.put(userId, errResp.toWrapperJson(meta));

        consumerMonitorService.addFailCount(RequestResponseType.PullOldMsgRequestMsg);
    }

}
