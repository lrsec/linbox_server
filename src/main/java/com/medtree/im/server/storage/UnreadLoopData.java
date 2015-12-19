package com.medtree.im.server.storage;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lrsec on 7/23/15.
 * 用于分页获取 unread 数据时使用的中间数据局。
 */
public class UnreadLoopData {

    // 下一次分页请求所需要使用的 offset 信息
    public int nextOffset = 0;

    // 本次分页的数据
    public List<String> unreads = new LinkedList<>();
}
