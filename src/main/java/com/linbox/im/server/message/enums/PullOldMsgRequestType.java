package com.linbox.im.server.message.enums;

/**
 * Created by lrsec on 8/4/15.
 */

import com.alibaba.fastjson.JSON;

/**
 * 冗余类型，仅用于简化客户端操作
 */
public enum PullOldMsgRequestType {
    LATEST(1, "最新数据"),
    OLD(2, "历史数据"),
    POINTED(3,"指定数据")
    ;

    private int value;
    private String name;

    private PullOldMsgRequestType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public int valueOf() {
        return value;
    }

    public static void main(String[] args) {
        class A {
            public PullOldMsgRequestType type = LATEST;
        }

        A a = new A();

        String json = JSON.toJSONString(a);

        A b = JSON.parseObject(json, A.class);

        System.out.println(json);
        System.out.println(b.type.getName());
    }
}


