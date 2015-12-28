package com.linbox.im.exceptions;

/**
 * Created by lrsec on 12/15/15.
 */
public class IMConsumerException extends RuntimeException {
    private String topicMessage;

    public IMConsumerException(Exception e, String topicMessage) {
        super(e);
        topicMessage = topicMessage;
    }

    public String getTopicMessage() {
        return topicMessage;
    }
}
