package com.linbox.im.message;

/**
 * Created by lrsec on 7/3/15.
 */
public enum MessageType {
    All(1, "all"),
    Session(2, "session"),
    Group(3, "group");

    private int value;
    private String name;

    private MessageType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static MessageType parse(int v) {
        MessageType type = null;

        for(MessageType t : MessageType.values()) {
            if (t.getValue() == v) {
                type = t;
            }
        }

        return type;
    }
}
