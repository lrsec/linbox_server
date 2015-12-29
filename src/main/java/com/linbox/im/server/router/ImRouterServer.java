package com.linbox.im.server.router;

import com.linbox.im.exceptions.IMConsumerException;
import com.linbox.im.server.constant.MessageTopic;
import com.linbox.im.server.router.handlers.Handler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by lrsec on 7/2/15.
 */
public class ImRouterServer {
    private static Logger logger = LoggerFactory.getLogger(ImRouterServer.class);

    @Value("${bootstrap.servers}")
    private String bootstrapServers;

    @Value("${group.id}")
    private String groupId;

    @Value("${enable.auto.commit}")
    private String autoCommit;

    @Value("${auto.commit.interval.ms}")
    private String autoCommitInterval;

    @Value("${session.timeout.ms}")
    private String sessionTimeoutMs;

    @Value("${key.deserializer}")
    private String keyDeserializer;

    @Value("${value.deserializer}")
    private String valueDeserializer;

    @Autowired
    @Qualifier("syncUnreadHandler")
    private Handler syncUnreadHandler;

    @Autowired
    @Qualifier("readAckHandler")
    private Handler readAckHandler;

    @Autowired
    @Qualifier("pullOldMsgHandler")
    private Handler pullOldMsgHandler;

    @Autowired
    @Qualifier("sendMsgHandler")
    private Handler sendMsgHandler;

    @Autowired
    @Qualifier("pingHandler")
    private Handler pingHandler;

    @Autowired
    @Qualifier("syncSystemUnreadHandler")
    private Handler syncSystemUnreadHandler;

    @Autowired
    @Qualifier("dispatchToSingleHandler")
    private Handler dispatchToSingleHandler;

    @Autowired
    @Qualifier("dispatchToGroupHandler")
    private Handler dispatchToGroupHandler;

    public void run() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", groupId);
        props.put("enable.auto.commit", autoCommit);
        props.put("auto.commit.interval.ms", autoCommitInterval);
        props.put("session.timeout.ms", sessionTimeoutMs);
        props.put("key.deserializer", keyDeserializer);
        props.put("value.deserializer", valueDeserializer);


        logger.info("Start IM Router Server");

        logger.info("Start Sync Unread Handler");
        new Thread(new ConsumerTask(props, MessageTopic.TOPIC_SYNC_UNREAD, syncUnreadHandler)).start();

        logger.info("Start Read ACK Handler");
        new Thread(new ConsumerTask(props, MessageTopic.TOPIC_READ_ACK, readAckHandler)).start();

        logger.info("Start Pull Old Msg Handler");
        new Thread(new ConsumerTask(props, MessageTopic.TOPIC_PULL_OLD_MEG, pullOldMsgHandler)).start();

        logger.info("Start Send Msg Handler");
        new Thread(new ConsumerTask(props, MessageTopic.TOPIC_SEND_MSG, sendMsgHandler)).start();

        logger.info("Start Ping Msg Handler");
        new Thread(new ConsumerTask(props, MessageTopic.TOPIC_PING, pingHandler)).start();

        logger.info("Start Sync System Unread Handler");
        new Thread(new ConsumerTask(props, MessageTopic.TOPIC_SYNC_SYSTEM_UNREAD, syncSystemUnreadHandler)).start();

        logger.debug("Start Single Send Dispatch Consumer");
        new Thread(new ConsumerTask(props, MessageTopic.TOPIC_DISPATCH_SEND_SINGLE, dispatchToSingleHandler)).start();

        logger.debug("Start Group Send Dispatch Consumer");
        new Thread(new ConsumerTask(props, MessageTopic.TOPIC_DISPATCH_SEND_GROUP, dispatchToGroupHandler)).start();
    }

    private static class ConsumerTask implements Runnable{
        private String topic;
        private Properties props;
        private Handler handler;

        ConsumerTask(Properties props, String topic, Handler handler) {
            this.topic = topic;
            this.props = props;
            this.handler = handler;
        }

        @Override
        public void run() {
            logger.info("Thread for {} consumer started", topic);

            KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
            consumer.subscribe(Arrays.asList(topic));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(1000);
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        handler.handle(record);
                    } catch (IMConsumerException e1) {
                        logger.error("IMConsumerException in consumer for Topic: " + topic + ". Message: + " + e1.getTopicMessage() + ".", e1);
                    } catch (Exception e2) {
                        logger.error("Exception in consumer for topic: " + topic, e2);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("spring/router.xml");

            ImRouterServer server = (ImRouterServer) appContext.getBean("imRouterServer");
            server.run();
        } catch (Exception e) {
            logger.error("Exception in im router server.", e);
        }
    }
}
