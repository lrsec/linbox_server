package com.medtree.im.server.router.handlers.dispatcher;

import com.medtree.im.message.Message;
import com.medtree.im.message.MessageType;

/**
 * Created by lrsec on 8/26/15.
 */
public class SendDispatchMessage {

    private String userId;
    private String userChatId;
    private String remoteChatId;
    private String sessionKey;
    private Message message;
    private MessageType type;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserChatId() {
        return userChatId;
    }

    public void setUserChatId(String userChatId) {
        this.userChatId = userChatId;
    }

    public String getRemoteChatId() {
        return remoteChatId;
    }

    public void setRemoteChatId(String remoteChatId) {
        this.remoteChatId = remoteChatId;
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
