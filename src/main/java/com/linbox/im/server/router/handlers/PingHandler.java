package com.linbox.im.server.router.handlers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linbox.im.exceptions.IMConsumerException;
import com.linbox.im.message.MessageWrapper;
import com.linbox.im.message.Ping;
import com.linbox.im.message.Pong;
import com.linbox.im.server.service.IOutboxService;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by lrsec on 7/7/15.
 */
@Service
@Qualifier("pingHandler")
public class PingHandler implements Handler<String, String> {
    private static Logger logger = LoggerFactory.getLogger(PingHandler.class);

    @Autowired
    private IOutboxService outboxService;


    @Override
    public void handle(ConsumerRecord<String, String> record) {
        String json = record.value();

        try {
            logger.debug("Start handling Ping: {}", json);

            MessageWrapper wrapper = JSON.parseObject(json, MessageWrapper.class);

            Ping ping = JSON.parseObject(((JSONObject)wrapper.content).toJSONString(), Ping.class);
            wrapper.content = ping;

            String userId = ping.userId;
            if(StringUtils.isBlank(userId)) {
                logger.error("Can not find avaiable user id for PullOldMsgRequest {}", json);
                return;
            }

            sendPong(userId, wrapper);
        } catch (Exception e) {
            throw new IMConsumerException(e, json);
        }
    }

    private void sendPong(String userId, MessageWrapper wrapper) {
        Pong pong = new Pong();
        pong.rId = ((Ping)(wrapper.content)).rId;
        outboxService.put(userId, pong.toWrapperJson());
    }
}
