package com.linbox.im.server.router.handlers;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Created by lrsec on 12/15/15.
 */
public interface Handler<T,V> {
    void handle(ConsumerRecord<T, V> record);
}
