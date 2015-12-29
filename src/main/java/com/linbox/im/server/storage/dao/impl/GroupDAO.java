package com.linbox.im.server.storage.dao.impl;

import com.linbox.im.exceptions.IMException;
import com.linbox.im.server.constant.RedisKey;
import com.linbox.im.server.storage.dao.IGroupDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by lrsec on 7/4/15.
 */
@Service
public class GroupDAO implements IGroupDAO {
    private static Logger logger = LoggerFactory.getLogger(GroupDAO.class);

//    @Autowired
//    private Sql2o sql2o;
//
//    @Autowired
//    private JedisPool jedisPool;

    //TODO TEST
    private AtomicLong atomicLong = new AtomicLong(0);

    @Override
    public List<String> getGroupMembers(String groupId) {
        List<String> members = new LinkedList<>();
//
//        String sql = "SELECT AccountId FROM group_members WHERE ConsultationId = :groupId ";
//
//        try(Connection conn = sql2o.open()) {
//            members.addAll(conn.createQuery(sql)
//                    .addParameter("groupId", groupId)
//                    .executeAndFetch(String.class));
//        }
//
        return members;
    }

    @Override
    public long generateGroupId() {

        return atomicLong.incrementAndGet();

//        try (Jedis jedis = jedisPool.getResource()) {
//            if(!jedis.exists(RedisKey.GROUP_ID)) {
//                throw new IMException( RedisKey.GROUP_ID + "不存在，无法获取合法的 Group Id");
//            }
//
//            return jedis.incr(RedisKey.GROUP_ID);
//        }


    }
}
