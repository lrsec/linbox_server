package com.medtree.im.server.storage.dao.impl;

import com.medtree.im.server.storage.dao.IGroupDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class GroupDAO implements IGroupDAO {
    private static Logger logger = LoggerFactory.getLogger(GroupDAO.class);

    @Autowired
    private Sql2o sql2o;

    @Override
    public List<String> getGroupMembers(String groupId) {
        List<String> members = new LinkedList<>();

        String sql = "SELECT AccountId FROM Consult_Person WHERE ConsultationId = :groupId ";

        try(Connection conn = sql2o.open()) {
            members.addAll(conn.createQuery(sql)
                    .addParameter("groupId", groupId)
                    .executeAndFetch(String.class));
        }

        return members;
    }
}
