package com.linbox.im.server.storage.dao.impl;

import com.linbox.im.message.Message;
import com.linbox.im.server.storage.dao.ISessionMessageDAO;
import com.linbox.im.server.storage.entity.SessionMessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lrsec on 7/2/15.
 */
@Service
public class SessionMessageDAO implements ISessionMessageDAO {

    @Autowired
    private Sql2o sql2o;

    @Override
    public SessionMessageEntity insert(Message msg) {
        String sql = "insert into im_session_message set RId = :rId, SessionId = :sessionId, MsgID = :msgId, FromUserID = :fromUserId, ToUserID = :toUserId, MimeType = :mimeType, Content = :content, SendTime = :sendTime, Created = :created";

        SessionMessageEntity dao = SessionMessageEntity.convertToDao(msg);
        try (Connection conn = sql2o.open()) {
            conn.createQuery(sql)
                    .addParameter("rId", dao.RId)
                    .addParameter("sessionId", dao.SessionId)
                    .addParameter("msgId", dao.MsgID)
                    .addParameter("fromUserId", dao.FromUserID)
                    .addParameter("toUserId", dao.ToUserID)
                    .addParameter("mimeType", dao.MimeType)
                    .addParameter("content", dao.Content)
                    .addParameter("sendTime", dao.SendTime)
                    .addParameter("created", dao.Created)
                    .executeUpdate();
        }

        return dao;
    }

    @Override
    public List<SessionMessageEntity> findMsg(String sessionId, long maxMsgId, long minMsgId, int limit) {
        String sql = "select * from im_session_message where SessionId = :sessionId  AND MsgID <= :maxMsgId AND MsgID > :minMsgId ORDER BY MsgID DESC LIMIT :limit";

        List<SessionMessageEntity> daos = new LinkedList<SessionMessageEntity>();

        try(Connection conn = sql2o.open()) {
            List<SessionMessageEntity> result = conn.createQuery(sql)
                    .addParameter("sessionId", sessionId)
                    .addParameter("maxMsgId", maxMsgId)
                    .addParameter("minMsgId", minMsgId)
                    .addParameter("limit", limit)
                    .executeAndFetch(SessionMessageEntity.class);

            daos.addAll(result);
        }

        return daos;
    }

    @Override
    public SessionMessageEntity findMsgByRId(long rId, String fromUserId, String toUserId) {
        String sql = "select * from im_session_message where RId = :rid AND FromUserID = :fromUserId AND ToUserID = :toUserId";

        try(Connection conn = sql2o.open()) {
            return conn.createQuery(sql)
                    .addParameter("rid", rId)
                    .addParameter("fromUserId", fromUserId)
                    .addParameter("toUserId", toUserId)
                    .executeAndFetchFirst(SessionMessageEntity.class);

        }
    }

}
