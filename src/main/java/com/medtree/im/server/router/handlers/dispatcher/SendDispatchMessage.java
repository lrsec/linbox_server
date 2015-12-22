package com.medtree.im.server.router.handlers.dispatcher;

import com.medtree.im.message.Message;
import com.medtree.im.message.MessageType;

/**
 * Created by lrsec on 8/26/15.
 */
public class SendDispatchMessage {

    private String userId;
    private String remoteId;
    private String sessionKey;
    private Message message;
    private MessageType type;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
