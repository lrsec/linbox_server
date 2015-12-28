package com.linbox.im.server.storage.dao.impl;

import com.linbox.im.server.storage.dao.IGroupMessageDAO;
import com.linbox.im.server.storage.entity.GroupMessageEntity;
import com.linbox.im.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by lrsec on 7/4/15.
 */
@Service
public class GroupMessageDao implements IGroupMessageDAO {

    @Autowired
    private Sql2o sql2o;

    @Override
    public GroupMessageEntity insert(Message msg) {
        String sql = "INSERT INTO im_group_message set RId = :rId, GroupId = :groupId, FromUserID = :fromUserId, MsgID = :msgId, MineType = :mimeType, Content = :content, SendTime = :sendTime, Created = :created";

        GroupMessageEntity dao = GroupMessageEntity.convertToGroupMsgDao(msg);

        try (Connection conn = sql2o.open()) {
            conn.createQuery(sql)
                    .addParameter("rId", dao.RId)
                    .addParameter("groupId", dao.GroupId)
                    .addParameter("fromUserId", dao.FromUserID)
                    .addParameter("msgId", dao.MsgID)
                    .addParameter("mimeType", dao.MineType)
                    .addParameter("content", dao.Content)
                    .addParameter("sendTime", dao.SendTime)
                    .addParameter("created", dao.Created)
                    .executeUpdate();
        }

        return dao;

    }

    @Override
    public List<GroupMessageEntity> findMsg(String groupId, long maxMsgId, long minMsgId, int limit) {

        String sql = "select * from im_group_message where GroupId = :groupId  AND MsgID <= :maxMsgId AND MsgID > :minMsgId  ORDER BY MsgID DESC LIMIT :limit";

        List<GroupMessageEntity> daos = new LinkedList<GroupMessageEntity>();

        try(Connection conn = sql2o.open()) {
            List<GroupMessageEntity> result = conn.createQuery(sql)
                    .addParameter("groupId", groupId)
                    .addParameter("maxMsgId", maxMsgId)
                    .addParameter("minMsgId", minMsgId)
                    .addParameter("limit", limit)
                    .executeAndFetch(GroupMessageEntity.class);

            daos.addAll(result);
        }

        return daos;

    }

    @Override
    public GroupMessageEntity findMsgByRId(long rId, String fromUserId, String groupId) {
        String sql = "SELECT * FROM im_group_message WHERE RId = :rId AND FromUserID = :fromUserId AND GroupId = :groupId";

        try(Connection conn = sql2o.open()) {
            return conn.createQuery(sql)
                    .addParameter("rId", rId)
                    .addParameter("fromUserId", fromUserId)
                    .addParameter("groupId", groupId)
                    .executeAndFetchFirst(GroupMessageEntity.class);

        }
    }
}
