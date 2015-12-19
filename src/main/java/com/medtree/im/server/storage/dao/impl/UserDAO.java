package com.medtree.im.server.storage.dao.impl;

import com.google.common.base.Strings;
import com.medtree.im.server.storage.dao.IUserDAO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

/**
 * Created by lrsec on 7/3/15.
 */
@Service
public class UserDAO implements IUserDAO {

    public static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    @Autowired
    private Sql2o sql2o;

    public String getUserId(String userChatId) {
        if (StringUtils.isBlank(userChatId)) {
            logger.warn("Query user id for null or empty userChatId");
            return null;
        }

        String sql = "select ID from mdt_account where ChatID = :chatId";

        String userId = null;

        try (Connection conn = sql2o.open()) {
            Long id = conn.createQuery(sql)
                    .addParameter("chatId", userChatId)
                    .executeAndFetchFirst(Long.class);

            if (id != null) {
                userId = id.toString();
            } else {
                logger.error("Can not find corresponding user id for chat id: {}", userChatId);
            }
        }

        return userId;
    }

    public String getUserChatId(String userId) {
        if (StringUtils.isBlank(userId)) {
            logger.warn("Query chat id for null or empty user Id");
            return null;
        }

        String sql = "select ChatID from mdt_account where ID = :id";

        String userChatId;

        try (Connection conn = sql2o.open()) {
            userChatId = conn.createQuery(sql)
                    .addParameter("id", userId)
                    .executeAndFetchFirst(String.class);
        }

        return userChatId;
    }

    public String getUserName(String userId) {
        if (StringUtils.isBlank(userId)) {
            logger.warn("Query user name for null or empty user id");
            return "";
        }

        String sql = "select RealName from mdt_profile where AccountID = :accountId";


        String realName;

        try (Connection conn = sql2o.open()) {
            realName = conn.createQuery(sql)
                    .addParameter("accountId", userId)
                    .executeAndFetchFirst(String.class);
        }

        return Strings.nullToEmpty(realName);
    }
}
