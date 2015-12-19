package com.medtree.im.message;

import com.medtree.im.message.system.*;

/**
 * Created by lrsec on 6/29/15.
 */
public enum RequestResponseType {

    // 客户端鉴权，同时用于连接建立后试探网络环境
    // 鉴权信息必须是网络联通后客户端发送的第一个信息，否则连接被关闭
    AuthRequestMsg(1, "AuthRequest", AuthRequest.class),
    AuthResponseMsg(2, "AuthResponse", AuthResponse.class),

    // 客户端同步未读信息
    SyncUnreadRequestMsg(3, "SyncUnreadRequest", SyncUnreadRequest.class),
    SyncUnreadResponseMsg(4, "SyncUnreadResponse", SyncUnreadResponse.class),

    // 客户端确认未读信息已被读取
    ReadAckRequestMsg(5, "ReadAckRequest", ReadAckRequest.class),
    ReadAckResponseMsg(6, "ReadAckResponse", ReadAckResponse.class),

    // 客户端以反序分页拉取信息
    PullOldMsgRequestMsg(7, "PullOldMsgRequest", PullOldMsgRequest.class),
    PullOldMsgResponseMsg(8, "PullOldMsgResponse", PullOldMsgResponse.class),

    // 客户端发送信息
    SendMsgRequestMsg(9, "SendMsgRequest", SendMsgRequest.class),
    SendMsgResponseMsg(10, "SendMsgResponse", SendMsgResponse.class),

    // 新消息通知
    NewMsgInfo(11, "NewMessage", NewMessage.class),
    OfflineInfo(12, "OfflineInfo", OfflineInfo.class),

    // Heartbeat
    Ping(13, "Ping", Ping.class),
    Pong(14, "Pong", Pong.class),

    // 系统消息
    SystemMsgInfo(15, "SystemMessage", SystemMessage.class),
    SyncSystemUnreadRequestMsg(16, "SyncSystemUnreadRequest", SyncSystemUnreadRequest.class),
    SyncSystemUnreadResponseMsg(17, "SyncSystemUnreadResponse", SyncSystemUnreadResponse.class),
    SystemUnreadAckRequestMsg(18, "SystemUnreadAckRequest", SystemUnreadAckRequest.class),
    SystemUnreadAckResponseMsg(19, "SystemUnreadAckResponse", SystemUnreadAckResponse.class)

    ;

    private int value;
    private String name;
    private Class clazz ;

    private RequestResponseType(int value, String name, Class clazz) {
        this.value = value;
        this.clazz = clazz;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public static RequestResponseType parse(int value) {
        RequestResponseType type = null;

        for(RequestResponseType t : RequestResponseType.values()) {
            if(t.getValue() == value) {
                type = t;
                break;
            }
        }

        return type;

    }

    public static RequestResponseType parse(Object o) {
        RequestResponseType type = null;

        for (RequestResponseType t : RequestResponseType.values()) {
            if (t.clazz.isInstance(o)) {
                type = t;
                break;
            }
        }

        return type;
    }

    public static boolean isRequest(RequestResponseType t) {
        return t==AuthRequestMsg
                || t==SyncUnreadRequestMsg
                || t==ReadAckRequestMsg
                || t==PullOldMsgRequestMsg
                || t==SendMsgRequestMsg
                || t==Ping;
    }

    public static boolean isResponse(RequestResponseType t) {
        return t==AuthResponseMsg
                || t==SyncUnreadResponseMsg
                || t==ReadAckResponseMsg
                || t==PullOldMsgResponseMsg
                || t==SendMsgResponseMsg
                || t==NewMsgInfo
                || t==OfflineInfo
                || t==Pong;
    }
}
