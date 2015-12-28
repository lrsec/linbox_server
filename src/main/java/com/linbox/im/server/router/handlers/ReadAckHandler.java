package com.linbox.im.server.router.handlers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linbox.im.exceptions.IMConsumerException;
import com.linbox.im.exceptions.IMException;
import com.linbox.im.message.MessageType;
import com.linbox.im.server.service.IOutboxService;
import com.linbox.im.message.MessageWrapper;
import com.linbox.im.message.ReadAckRequest;
import com.linbox.im.message.ReadAckResponse;
import com.linbox.im.server.service.IInboxService;
import com.linbox.im.utils.IMUtils;
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
    private IInboxService inboxService;

    @Autowired
    private IOutboxService outboxService;

    @Override
    public void handle(ConsumerRecord<String, String> record) {
        String json = record.value();

        try {
            logger.debug("Start handling ReadAckRequest: {}", json);

            MessageWrapper wrapper = JSON.parseObject(json, MessageWrapper.class);

            ReadAckRequest request = JSON.parseObject(((JSONObject)wrapper.content).toJSONString(), ReadAckRequest.class);
            wrapper.content = request;

            String userId = request.userId;
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
                        String remoteId = request.remoteId;

                        if (StringUtils.isBlank(remoteId)) {
                            logger.error("Can not find remote id for ReadAckRequest {}", json);

                            throw new IMException("Non existing ReadAckRequest " + json);
                        }

                        inboxService.removeSessionMsg(userId, IMUtils.getSessionKey(userId, remoteId), request.msgId);

                        sendSuccessResponse(userId, request);

                        break;
                    case Group:
                        String groupId = request.groupId;

                        if (StringUtils.isBlank(groupId)) {
                            logger.error("Can not find group id for ReadAckRequest {}", json);

                            throw new IMException("Non existing ReadAckRequest " + json);
                        }

                        inboxService.removeGroupMsg(userId, groupId, request.msgId);

                        sendSuccessResponse(userId, request);

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
            throw new IMConsumerException(e, json);
        }
    }

    private void sendSuccessResponse(String userId, ReadAckRequest request) {

        ReadAckResponse response = new ReadAckResponse();
        response.rId = request.rId;
        response.userId = request.userId;
        response.remoteId = request.remoteId;
        response.groupId = request.groupId;
        response.type = request.type;
        response.status = 200;

        outboxService.put(userId, response.toWrapperJson());
    }

    private void sendFailResponse(String userId, ReadAckRequest request, String errMsg) {

        ReadAckResponse response = new ReadAckResponse();
        response.rId = request.rId;
        response.userId = request.userId;
        response.remoteId = request.remoteId;
        response.groupId = request.groupId;
        response.type = request.type;
        response.status = 500;
        response.errCode = errMsg;

        outboxService.put(userId, response.toWrapperJson());
    }
}
