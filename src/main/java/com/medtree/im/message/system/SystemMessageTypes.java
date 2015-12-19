package com.medtree.im.message.system;

/**
 * Created by lrsec on 9/23/15.
 */
public enum SystemMessageTypes {

    /** 有添加好友信息时，通知客户端，客户端更新新朋友未读数 */
    NewFriend(10, "新朋友"),
    /** 有新消息时，通知客户端，用于消息中心中，新通知未读数的更新 */
    NoticeNotify(30, "新通知"),
    /** 添加或者删除好友时，通知对方，刷新好友列表，通知客户端，用于人脉关系刷新 */
    RelationChange(100, "人脉关系变动"),
    /** 身份认证状态发生变化时，发出通知 */
    Certification(110, "身份认证信息"),
    /** 动态有更新 */
    FeedNotify(120, "动态更新"),
    /** 活动信息有更新 */
    ActivityNotify(130, "活动更新"),
    /** 招聘信息相关系统消息类型 */
    JobNotifify(140, "招聘信息更新");


    private int value;
    private String name;

    private SystemMessageTypes(int v, String n) {
        this.value = v;
        this.name = n;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
